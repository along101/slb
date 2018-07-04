package com.along01.slb.agent.dao;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.along01.slb.agent.common.Util;
import com.along01.slb.agent.dto.taskconfget.TaskAppDto;
import com.along01.slb.agent.dto.taskconfget.TaskConfGetRequest;
import com.along01.slb.agent.dto.taskconfget.TaskConfGetResponse;
import com.along01.slb.agent.dto.taskstatusupdate.TaskStatusUpdateRequest;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Repository
public class TaskDao {
	private static final Logger logger = LoggerFactory.getLogger(TaskDao.class);
	private OkHttpClient httpclient = new OkHttpClient();
	private MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
	@Autowired
	private Util util;
	private long foceTaskId = 0;

	public TaskConfGetResponse getTaskConf(long lastTaskId, boolean testFlag) {
		Gson gson = new Gson();
		if (testFlag) {
			String json = "";
			if (util.getTestConfStep() == 1) {
				json = util.getTestConfReload();
			} else if (util.getTestConfStep() == 2) {
				json = util.getTestConfDyups();
			} else if (util.getTestConfStep() == 3) {
				json = util.getTestConfDyupsError();
			} else {
				json = util.getTestConfDyupsError1();
			}
			return gson.fromJson(json, TaskConfGetResponse.class);
		} else {
			TaskConfGetRequest request = new TaskConfGetRequest();
			request.setIp(util.getIp());
			if (util.getFoceTaskId() != 0 && util.getFoceTaskId() != foceTaskId) {
				foceTaskId = util.getFoceTaskId();
				request.setLastTaskId(foceTaskId);
			} else {
				request.setLastTaskId(lastTaskId);
			}

			TaskConfGetResponse resp = null;
			try {
				String par = gson.toJson(request);
				Response response = httpclient.newCall(
						new Request.Builder().url(util.getSlbGetUrl()).post(RequestBody.create(mediaType, par)).build())
						.execute();
				String json=response.body().string();
				resp = gson.fromJson(json, TaskConfGetResponse.class);
				response.close();
				if(!resp.isSuc()){
					logger.info(json);
				}
			} catch (JsonSyntaxException | IOException e) {
				// TODO Auto-generated catch block
				logger.error("获取conf异常", e);
			}
			return resp;
		}
	}

	public void updateStatus(List<TaskAppDto> taskApps) {
		Transaction transaction = Cat.newTransaction("slb-agent", "updateStatus");
		try {
			TaskStatusUpdateRequest request = new TaskStatusUpdateRequest();
			request.setTaskApps(taskApps);
			request.setIp(util.getIp());
			Gson gson = new Gson();
			String par = gson.toJson(request);
			httpclient.newCall(
					new Request.Builder().url(util.getSlbUpdateUrl()).post(RequestBody.create(mediaType, par)).build())
					.execute();
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			logger.error("更新状态异常", e);
			transaction.setStatus(e);
		} finally {
			transaction.complete();
		}
	}

	public void heart() {
		try {
			httpclient.newCall(new Request.Builder().url(util.getSlbHeartUrl() + "?ip=" + util.getIp()).post(RequestBody.create(mediaType, "")).build())
					.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			logger.error("心跳异常", e);
		}
	}
}
