package com.along01.slb.agent.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.along01.slb.agent.common.Util;
import com.along01.slb.agent.dao.TaskDao;
import com.along01.slb.agent.domain.TaskAppStatusDo;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.along01.slb.agent.domain.TaskDomainStatusDo;
import com.along01.slb.agent.dto.taskconfget.TaskAppDto;
import com.along01.slb.agent.dto.taskconfget.TaskConfGetResponse;
import com.along01.slb.agent.dto.taskconfget.TaskDomainDto;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class TaskService {
	private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
	private MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
	// key为domainport,TaskDomain
	private TaskAppStatusDo taskApps = new TaskAppStatusDo();
	private String slbbin = "", slbconf = "";
	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
	private OkHttpClient httpclient = new OkHttpClient();
	@Autowired
	private Util util;
	@Autowired
	private TaskDao taskDao;
	private boolean testFlag = false;

	public void start() {
		try {
			doInit();
		} catch (Exception e) {
			logger.error("初始化异常", e);
		}
		// 定时执行更新任务
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (util.enableAgent()) {
						Transaction transaction = Cat.newTransaction("slb-agent", "updateConf");
						try {
							doStart();
							transaction.setStatus(Transaction.SUCCESS);
						} catch (Exception e) {
							transaction.setStatus(e);
							logger.error("更新conf异常", e);
							// TODO: handle exception
						} finally {
							transaction.complete();
						}
					}
					try {
						TimeUnit.SECONDS.sleep(util.checkInterval());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}, 2, TimeUnit.SECONDS);
		// 定时发送心跳
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (util.enableAgent()) {
						try {
							taskDao.heart();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					try {
						TimeUnit.SECONDS.sleep(util.checkInterval());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}, 2, TimeUnit.SECONDS);
		// 定时上报心跳
		// executorService.schedule(new Runnable() {
		// @Override
		// public void run() {
		// while (true) {
		// if (util.enableAgent()) {
		// try {
		// reportStatus();
		//
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// }
		// try {
		// TimeUnit.SECONDS.sleep(util.reportInterval());
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }
		// }, 2, TimeUnit.SECONDS);
	}

	private void reportStatus() {
		Transaction transaction = Cat.newTransaction("slb-agent", "reportStatus");
		try {
			List<TaskAppDto> current = Lists.newArrayList();
			if (taskApps != null && taskApps.getTaskDomains() != null) {
				for (Map.Entry<String, TaskDomainStatusDo> entry : taskApps.getTaskDomains().entrySet()) {
					if (entry.getValue() != null && entry.getValue().getTaskApps() != null) {
						current.addAll(entry.getValue().getTaskApps().values());
					}
				}
			}
			if (!CollectionUtils.isEmpty(current)) {
				taskDao.updateStatus(current);
			}
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			transaction.setStatus(e);
		} finally {
			transaction.complete();
		}

	}

	public TaskAppStatusDo getData() {
		return taskApps;
	}

	// 自检接口
	public String check() {
		try {
			File file = new File(util.getCacheConfPath(), "slbagent_cachedata_1");
			FileUtils.writeStringToFile(file, "test", Charset.forName("utf-8"), false);
			FileUtils.deleteQuietly(file);
		} catch (Exception e) {
			// logger.error("自检失败", e);
			return "自检失败" + e.getMessage();
		}
		return "";
	}

	private void doInit() throws IOException {
		try {
			File file = new File(util.getCacheConfPath(), "slbagent_cachedata");
			Gson gson = new Gson();
			try {
				if (file.exists()) {
					String data = FileUtils.readFileToString(file, Charset.forName("utf-8"));
					if (!StringUtils.isEmpty(data)) {
						taskApps = gson.fromJson(data, TaskAppStatusDo.class);
						slbbin = taskApps.getNginxSbin();
						slbconf = taskApps.getNginxConf();
					}
				}
			} catch (IOException e) {
				logger.error("slbdata初始化数据异常", e);
				return;
			}
			// 清理备份数据
			// clearBackup();
			if (taskApps != null && !StringUtils.isEmpty(slbbin)) {
				String shellRs = util.shell(slbbin + "/nginx -t");
				if (StringUtils.isEmpty(shellRs)) {
					util.shell(slbbin + "/nginx -s reload");
				}
			}
		} catch (Exception e) {
			logger.error("初始化失败", e);
		}

	}

	private void doStart() throws IOException {
		testFlag = util.agentTest();
		TaskConfGetResponse response = taskDao.getTaskConf(taskApps.getTaskId(), testFlag);
		if (response == null || !response.isSuc()) {
			logger.info("获取信息失败");
			return;
		}
		if (CollectionUtils.isEmpty(response.getTaskDomains())) {
			// modifyStatus(response);
			logger.info("已经是最新的了，获取TaskDomains为空！");
			return;
		}
		if (!checkSlbPath(response)) {
			logger.info("slbconf：{}或者slbbin:{}路径不存在或者不对", slbconf, slbbin);
			return;
		}
		TaskConfGetResponse reloadResponse = new TaskConfGetResponse();
		TaskConfGetResponse updateResponse = new TaskConfGetResponse();
		// 将reload和 update 分离
		splitGetResponse(response, reloadResponse, updateResponse);
		// 记录每个站点底下的状态appid
		Map<String, TaskAppDto> mapTaskApp = Maps.newHashMap();
		boolean reload = false;
		for (TaskDomainDto domain : reloadResponse.getTaskDomains()) {
			if (backupAndUpdate(domain, mapTaskApp, true)) {
				reload = true;
			}
		}
		if (reload) {
			util.shell(slbbin + "/nginx -s reload");
		}
		for (TaskDomainDto domain : updateResponse.getTaskDomains()) {
			backupAndUpdate(domain, mapTaskApp, false);
		}
		if (!testFlag) {
			// 不管成功与taskid一直往前走
			taskApps.setTaskId(response.getLastTaskId());
			save();
			taskDao.updateStatus(Lists.newArrayList(mapTaskApp.values()));
		}
	}

	// 分离reload和update response
	private void splitGetResponse(TaskConfGetResponse response, TaskConfGetResponse reloadResponse,
			TaskConfGetResponse updateResponse) {
		reloadResponse.setLastTaskId(reloadResponse.getLastTaskId());
		reloadResponse.setNginxConf(response.getNginxConf());
		reloadResponse.setNginxSbin(response.getNginxSbin());
		reloadResponse.setSuc(response.isSuc());

		updateResponse.setLastTaskId(reloadResponse.getLastTaskId());
		updateResponse.setNginxConf(response.getNginxConf());
		updateResponse.setNginxSbin(response.getNginxSbin());
		updateResponse.setSuc(response.isSuc());

		if (!CollectionUtils.isEmpty(response.getTaskDomains())) {
			for (TaskDomainDto taskDomainDto : response.getTaskDomains()) {
				if (taskDomainDto.getOptType() == 1) {
					reloadResponse.getTaskDomains().add(taskDomainDto);
				} else {
					updateResponse.getTaskDomains().add(taskDomainDto);
				}
			}
		}

	}

	private boolean checkSlbPath(TaskConfGetResponse response) {		
		slbconf = response.getNginxConf();
		slbbin = response.getNginxSbin();
		File fconf = new File(slbconf);
		File fbin = new File(slbbin);
		if (fconf.isDirectory() && fbin.isDirectory()) {
			if (StringUtils.endsWithIgnoreCase(slbconf, "/")) {
				slbconf = slbconf.substring(0, slbconf.length() - 1);
				response.setNginxConf(slbconf);
			}
			if (StringUtils.endsWithIgnoreCase(slbbin, "/")) {
				slbbin = slbbin.substring(0, slbconf.length() - 1);
				response.setNginxSbin(slbbin);
			}
			taskApps.setNginxConf(response.getNginxConf());
			taskApps.setNginxSbin(response.getNginxSbin());
			return true;
		}
		return false;
	}

	private boolean backupAndUpdate(TaskDomainDto domain, Map<String, TaskAppDto> mapTaskApp, boolean isReload) {
		boolean flag = true;
		Transaction transaction = Cat.newTransaction("slb-agent", domain.getDomainPort());
		try {
			// 更新domain conf文件
			if (!taskApps.getTaskDomains().containsKey(domain.getDomainPort())) {
				taskApps.getTaskDomains().put(domain.getDomainPort(), new TaskDomainStatusDo());
			}
			taskApps.getTaskDomains().get(domain.getDomainPort()).setConf(domain.getConf());
			taskApps.getTaskDomains().get(domain.getDomainPort()).setDomainPort(domain.getDomainPort());
			backupFile(domain);
			// 如果conf文件为空则说明需要reload
			if (StringUtils.isEmpty(domain.getConf())) {
				clearnConf(domain, mapTaskApp);
				transaction.setStatus(Transaction.SUCCESS);
				return true;
			}
			if (!updateConf(domain)) {
				transaction.setStatus(Transaction.SUCCESS);
				return false;
			}
			String shellRs = util.shell(slbbin + "/nginx -t");
			if (!StringUtils.isEmpty(shellRs)) {
				// 回滚
				rollbackFile(domain);
				flag = false;
				// 更新版本和taskid为上一次成功的taskid
				updatetaskIdAndAppSiteVersionAndFailMsg(domain, mapTaskApp, shellRs);
			} else {				
				if (!isReload) {
					// 热更新
					updateUpstream(domain, mapTaskApp);
				} else {
					TaskDomainStatusDo taskDomainStatusDo = taskApps.getTaskDomains().get(domain.getDomainPort());
					if (!CollectionUtils.isEmpty(domain.getTaskApps())) {
						for (TaskAppDto taskAppDto : domain.getTaskApps()) {
							// taskAppDto.setDeleted(false);
							taskAppDto.setMsg("");
							taskAppDto.setStatus(1);
							mapTaskApp.put(taskAppDto.getAppId(), taskAppDto);
							taskDomainStatusDo.getTaskApps().put(taskAppDto.getAppId(), taskAppDto);
						}
					}
				}
				try {
					File backup = new File(slbconf + "/app/" + domain.getDomainPort() + "/server.backup");
					if (backup.exists()) {
						backup.delete();
					}
				} catch (Exception e) {
					logger.error("删除备份文件报错", e);
				}
			}
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			transaction.setStatus(e);
		} finally {
			transaction.complete();
		}
		return flag;
	}

	private boolean updateConf(TaskDomainDto domain) {
		Transaction transaction = Cat.newTransaction("slb-agent", "updateConf");
		try {
			File serverConf = new File(slbconf + "/app/" + domain.getDomainPort() + "/server.conf");
			FileUtils.writeStringToFile(serverConf, domain.getConf(), Charset.forName("utf-8"), false);
			transaction.setStatus(Transaction.SUCCESS);
			return true;
		} catch (IOException e) {
			logger.info("更新conf文件失败！", e);
			transaction.setStatus(e);
			// 回滚
			rollbackFile(domain);
			return false;
		} finally {
			transaction.complete();
		}
	}

	private void clearnConf(TaskDomainDto domain, Map<String, TaskAppDto> mapTaskApp) {
		Transaction transaction = Cat.newTransaction("slb-agent", "clearnConf");
		try {
			File serverConf = new File(slbconf + "/app/" + domain.getDomainPort() + "/server.conf");
			if (serverConf.exists())
				serverConf.delete();
			File serverBackUpConf = new File(slbconf + "/app/" + domain.getDomainPort() + "/server.backup");
			serverBackUpConf.delete();
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			logger.error("clearConf异常", e);
			transaction.setStatus(e);
		} finally {
			transaction.complete();
		}	
		TaskDomainStatusDo taskDomainStatusEntity = taskApps.getTaskDomains().get(domain.getDomainPort());
		taskDomainStatusEntity.setConf(domain.getConf());
		taskDomainStatusEntity.setDomainPort(domain.getDomainPort());
		if (taskDomainStatusEntity.getTaskApps() == null) {
			taskDomainStatusEntity.setTaskApps(Maps.newConcurrentMap());
		}
		if (!CollectionUtils.isEmpty(domain.getTaskApps())) {
			for (TaskAppDto taskAppDto : domain.getTaskApps()) {
				taskAppDto.setMsg("");
				taskAppDto.setStatus(1);
				mapTaskApp.put(taskAppDto.getAppId(), taskAppDto);
				taskDomainStatusEntity.getTaskApps().put(taskAppDto.getAppId(), taskAppDto);
			}
		} else {
			logger.warn("domain port 对应的conf为空，同事domain port 底下的task apps 也为空！");
		}
	}

	private void updateUpstream(TaskDomainDto domain, Map<String, TaskAppDto> mapTaskApp) {
		if (!CollectionUtils.isEmpty(domain.getTaskApps())) {
			TaskDomainStatusDo taskDomainStatusDo = taskApps.getTaskDomains().get(domain.getDomainPort());
			for (TaskAppDto taskAppDto : domain.getTaskApps()) {
				taskAppDto.setMsg("");
				taskAppDto.setStatus(1);
				mapTaskApp.put(taskAppDto.getAppId(), taskAppDto);
				taskDomainStatusDo.getTaskApps().put(taskAppDto.getAppId(), taskAppDto);
				if (StringUtils.isEmpty(taskAppDto.getUpstreamName())
						|| StringUtils.isEmpty(taskAppDto.getUpstreamConf())) {
					continue;
				}
				Transaction transaction = Cat.newTransaction("slb-agent", taskAppDto.getUpstreamName());
				try {
					Response response = httpclient
							.newCall(new Request.Builder()
									.url("http://" + util.getIp() + ":" + util.getSlbDyupPort() + "/upstream/"
											+ taskAppDto.getUpstreamName())
									.post(RequestBody.create(mediaType, taskAppDto.getUpstreamConf())).build())
							.execute();
					if (!response.isSuccessful()) {
						String msg = String.format("更新upstreamname：%s,UpstreamConf:%s,异常:%s",
								taskAppDto.getUpstreamName(), taskAppDto.getUpstreamConf(), response.message());
						logger.error(msg);
						transaction.setStatus(msg);
						// 更新上次失败的
						updatetaskIdAndAppSiteVersionAndFailMsg(domain, taskAppDto, msg);
					} else {
						taskDomainStatusDo.getTaskApps().put(taskAppDto.getAppId(), taskAppDto);
						transaction.setStatus(Transaction.SUCCESS);
					}
				} catch (IOException e) {
					transaction.setStatus(e);
					String msg = String.format("更新upstreamname：%s,UpstreamConf:%s,异常:%s", taskAppDto.getUpstreamName(),
							taskAppDto.getUpstreamConf(), e.getMessage());
					logger.error(msg, e);
					updatetaskIdAndAppSiteVersionAndFailMsg(domain, taskAppDto, msg);
					transaction.setStatus(e);
				} finally {
					transaction.complete();
				}
			}
		}
	}

	// 更新taskid，app_site版本和错误信息
	private void updatetaskIdAndAppSiteVersionAndFailMsg(TaskDomainDto domain, Map<String, TaskAppDto> mapTaskApp,
			String shellRs) {
		if (!CollectionUtils.isEmpty(domain.getTaskApps())) {
			// 标记某个domain底下的taskapp 错误信息
			for (TaskAppDto taskAppDto : domain.getTaskApps()) {
				updatetaskIdAndAppSiteVersionAndFailMsg(domain, taskAppDto, shellRs);
				mapTaskApp.put(taskAppDto.getAppId(), taskAppDto);
			}
		}
	}

	// 将taskAppDto 更新到历史版本
	private void updatetaskIdAndAppSiteVersionAndFailMsg(TaskDomainDto domain, TaskAppDto taskAppDto, String shellRs) {
		
//		taskAppDto.setMsg(shellRs);
//		taskAppDto.setStatus(0);
//		taskAppDto.setVersion(0L);
//		taskAppDto.setTaskId(0L);
//		if (taskApps.getTaskDomains().containsKey(domain.getDomainPort())) {
//			if (taskApps.getTaskDomains().get(domain.getDomainPort()).getTaskApps()
//					.containsKey(taskAppDto.getAppId())) {
//				TaskAppDto temp = taskApps.getTaskDomains().get(domain.getDomainPort()).getTaskApps()
//						.get(taskAppDto.getAppId());
//				taskAppDto.setVersion(temp.getVersion());
//				taskAppDto.setTaskId(temp.getTaskId());
//				temp.setMsg(shellRs);
//				temp.setStatus(0);
//			}
//		}
		taskAppDto.setMsg(shellRs);
		taskAppDto.setStatus(0);		
		taskApps.getTaskDomains().get(domain.getDomainPort()).getTaskApps().put(taskAppDto.getAppId(),taskAppDto);
	}

	private void rollbackFile(TaskDomainDto domain) {
		Transaction transaction = Cat.newTransaction("slb-agent", "rollbackFile");
		try {
			File backup = new File(slbconf + "/app/" + domain.getDomainPort() + "/server.backup");
			if (backup.exists()) {
				File file = new File(slbconf + "/app/" + domain.getDomainPort() + "/server.conf");
				if (file.exists()) {
					file.delete();
				}
				FileUtils.moveFile(backup, file);
			}
			transaction.setStatus(Transaction.SUCCESS);
		} catch (IOException e) {
			logger.error("还原站点失败", e);
			transaction.setStatus(e);
		} finally {
			transaction.complete();
		}
	}

	private void backupFile(TaskDomainDto domain) {
		Transaction transaction = Cat.newTransaction("slb-agent", "rollbackFile");
		try {
			File dir = new File(slbconf, "/app/" + domain.getDomainPort());
			if (!dir.exists()) {
				dir.mkdir();
			}
			File file = new File(slbconf + "/app/" + domain.getDomainPort() + "/server.conf");
			if (file.exists()) {
				File backup = new File(slbconf + "/app/" + domain.getDomainPort() + "/server.backup");
				if (backup.exists()) {
					backup.delete();
				}
				try {
					FileUtils.moveFile(file, backup);
					transaction.setStatus(Transaction.SUCCESS);
				} catch (IOException e) {
					logger.error("备份文件失败", e);
					transaction.setStatus(e);
				}
			} else {
				transaction.setStatus(Transaction.SUCCESS);
			}
		} catch (Exception e) {
			transaction.setStatus(e);
		} finally {
			transaction.complete();
		}
	}

	// private void save(TaskConfGetResponse response, Map<String, TaskAppDto>
	// mapTaskApp) {
	private void save() {
		Transaction transaction = Cat.newTransaction("slb-agent", "save");
		try {
			// for (TaskDomainDto taskDomainDto : response.getTaskDomains()) {
			// if (taskDomainDto != null &&
			// !CollectionUtils.isEmpty(taskDomainDto.getTaskApps())) {
			// TaskDomainStatusDo taskDomainStatusEntity = null;
			// if
			// (!taskApps.getTaskDomains().containsKey(taskDomainDto.getDomainPort()))
			// {
			// taskApps.getTaskDomains().put(taskDomainDto.getDomainPort(), new
			// TaskDomainStatusDo());
			// }
			// taskDomainStatusEntity =
			// taskApps.getTaskDomains().get(taskDomainDto.getDomainPort());
			// boolean flag = true;
			// for (TaskAppDto taskAppDto : taskDomainDto.getTaskApps()) {
			// flag = updateVersionAndTask(taskDomainStatusEntity, taskAppDto);
			// }
			// if (flag) {
			// taskDomainStatusEntity.setConf(taskDomainDto.getConf());
			// taskDomainStatusEntity.setDomainPort(taskDomainDto.getDomainPort());
			// }
			// }
			// }
			File file = new File(util.getCacheConfPath(), "slbagent_cachedata");
			Gson gson = new Gson();
			FileUtils.writeStringToFile(file, gson.toJson(taskApps), Charset.forName("utf-8"), false);
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			logger.error("持久化文件失败", e);
			transaction.setStatus(e);
		} finally {
			transaction.complete();
		}
	}

	// private boolean updateVersionAndTask(TaskDomainStatusDo
	// taskDomainStatusEntity, TaskAppDto taskAppDto) {
	// boolean flag;
	// if
	// (!taskDomainStatusEntity.getTaskApps().containsKey(taskAppDto.getAppId()))
	// {
	// taskDomainStatusEntity.getTaskApps().put(taskAppDto.getAppId(),
	// taskAppDto);
	// if (!StringUtils.isEmpty(taskAppDto.getMsg())) {
	// taskAppDto.setVersion(0L);
	// taskAppDto.setTaskId(0L);
	// }
	// }
	// TaskAppDto temp =
	// taskDomainStatusEntity.getTaskApps().get(taskAppDto.getAppId());
	// // temp.setDeleted(taskAppDto.isDeleted());
	// // temp.setHasChanged(true);
	// if (!StringUtils.isEmpty(taskAppDto.getMsg())) {
	// temp.setMsg(taskAppDto.getMsg());
	// temp.setStatus(0);
	// flag = false;
	// } else {
	// // 成功则更新app版本号
	// temp.setMsg("");
	// temp.setStatus(1);
	// flag = true;
	// temp.setVersion(taskAppDto.getVersion());
	// temp.setTaskId(taskAppDto.getTaskId());
	// }
	// return flag;
	// }
}
