package com.along01.slb.agent.common;

import com.along101.dropwizard.metrics.TaggedMetricRegistry;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    @Autowired
    private TaggedMetricRegistry metricRegistry;
    @Autowired
    private Environment env;
    private String ip;

    @PostConstruct
    private void initail() {
        ip = env.getProperty("spring.cloud.client.ipAddress");
    }

    public String getIp() {
        return ip;
    }

    public void metricMeter(String name, String tag) {
        Map<String, String> tagMap = new HashMap<>();
        tagMap.put("url", tag.replaceAll(":", "："));
        metricRegistry.taggedMeter(name, tagMap).mark();
    }

    public String getSlbGetUrl() {
        return env.getProperty("agent.slbUrl", "");
    }

    public String getSlbUpdateUrl() {
        return env.getProperty("agent.slbUpdateUrl", "");
    }

    public String getSlbHeartUrl() {
        return env.getProperty("agent.slbHeartUrl", "");
    }

    public int getSlbDyupPort() {
        return Integer.parseInt(env.getProperty("agent.slbDyupPort", "81"));
    }

    public long checkInterval() {
        return Long.parseLong(env.getProperty("agent.checkInterval", "2"));
    }

    public long reportInterval() {
        return Long.parseLong(env.getProperty("agent.reportInterval", "60"));
    }

    public boolean enableAgent() {
        return Boolean.parseBoolean(env.getProperty("agent.enable", "true"));
    }

    public boolean agentTest() {
        return Boolean.parseBoolean(env.getProperty("agent.test", "false"));
    }

    public String getTestConfReload() {
        return env.getProperty("agent.testConf.reload", "");
    }

    public String getTestConfDyups() {
        return env.getProperty("agent.testConf.dyUps", "");
    }

    public String getTestConfDyupsError() {
        return env.getProperty("agent.testConf.dyUps.error", "");
    }

    public String getTestConfDyupsError1() {
        return env.getProperty("agent.testConf.dyUps.error1", "");
    }

    public int getTestConfStep() {
        return Integer.parseInt(env.getProperty("agent.step", "1"));
    }

    public long getFoceTaskId() {
        return Integer.parseInt(env.getProperty("agent.foceTaskId", "0"));
    }

    //设置缓存文件路径
    public String getCacheConfPath() {
        return env.getProperty("agent.cacheConfPath", System.getProperty("user.dir"));
    }

    public String shell(String command) {
        Transaction transaction = null;
        try {
            transaction = Cat.newTransaction("slb-agent", "shell");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            // 命令行处理
            CommandLine commandline = CommandLine.parse(command);
            // 进行执行体
            DefaultExecutor exec = new DefaultExecutor();
            exec.setExitValues(null);
            // 利用监视狗来设置超时
            ExecuteWatchdog watchdog = new ExecuteWatchdog(12000);
            exec.setWatchdog(watchdog);

            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
            exec.setStreamHandler(streamHandler);
            int t = exec.execute(commandline);// 执行
            String error = errorStream.toString("gbk");
            if (!StringUtils.isEmpty(error)) {
                logger.info("执行shell:{} 出现错误或者警告{}", command, error);
            }
            if (t == 1) {
                error = "执行shell:" + command + " 出现错误" + error + "";
            }
            if (t == 0) {
                transaction.setStatus(Transaction.SUCCESS);
                return "";
            } else {
                transaction.setStatus(error);
                return error;
            }

        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("执行shell报错", e);
            transaction.setStatus(e);
            return e.getMessage();
        } finally {
            transaction.complete();
        }
    }

}
