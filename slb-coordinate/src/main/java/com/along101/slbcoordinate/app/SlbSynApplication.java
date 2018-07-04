package com.along101.slbcoordinate.app;

import com.along101.slbcoordinate.common.Util;
import com.along101.slbcoordinate.dto.SlbInstanceDto;
import com.along101.slbcoordinate.service.SlbLockService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.along101.slbcoordinate.dto.AppSiteServerDto;
import com.along101.slbcoordinate.dto.PageDto;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SlbSynApplication {
    @Autowired
    private SlbLockService slbLockService;
    @Autowired
    private Util util;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(100);
    @SuppressWarnings("serial")
    private Type type = new TypeToken<ArrayList<SlbInstanceDto>>() {
    }.getType();

    private Type typeSlb = new TypeToken<com.along101.slbcoordinate.dto.Response<PageDto<AppSiteServerDto>>>() {
        private static final long serialVersionUID = 100000L;
    }.getType();

    private volatile boolean isMaster = false;
    private MediaType mediaType = MediaType.parse("application/json");
    private OkHttpClient okHttpClient = new OkHttpClient();
    // 是否调试
    private boolean debug = false;

    @PostConstruct
    public void start() {
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (util.checkEnabled()) {
                            doSyn();
                        }
                    } catch (Exception e) {
                        log.error("数据同步器异常", e);
                    }
                    try {
                        TimeUnit.SECONDS.sleep(util.checkInterval());
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }, 20, TimeUnit.SECONDS);
        // 发送心跳
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (util.checkEnabled() && isMaster) {
                            slbLockService.updateHeatTime();
                        }
                    } catch (Exception e) {
                        log.error("更新心跳时间异常", e);
                    }
                    try {
                        TimeUnit.SECONDS.sleep(util.checkHeartbeatInterval());
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            }

        }, 1, TimeUnit.SECONDS);
    }

    private void doSyn() {
        // 判断是否获取到了分配器
        if (!debug && !slbLockService.isMaster()) {
            isMaster = false;
            return;
        }
        isMaster = true;
        try {
            List<SlbInstanceDto> slbData = getSlbData();
            List<SlbInstanceDto> eurekaData = getEurekaData();
            if (CollectionUtils.isEmpty(slbData) || eurekaData == null) {
                return;
            }
            // 数据对比
            List<SlbInstanceDto> rs = checkData(slbData, eurekaData);
            // 发送结果
            updateResult(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateResult(List<SlbInstanceDto> rs) {
        try {
            List<SlbInstanceDto> dataUp = rs.stream().filter(p -> p.getStatus() == 1).collect(Collectors.toList());
            List<SlbInstanceDto> dataDown = rs.stream().filter(p -> p.getStatus() == 0).collect(Collectors.toList());
            // Collections.sort(dataUp);
            dataUp.sort(new Comparator<SlbInstanceDto>() {
                @Override
                public int compare(SlbInstanceDto o1, SlbInstanceDto o2) {
                    if (o1.getAppSiteId() < o2.getAppSiteId())
                        return -1;
                    else if (o1.getAppSiteId() == o2.getAppSiteId()) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
            dataDown.sort(new Comparator<SlbInstanceDto>() {
                @Override
                public int compare(SlbInstanceDto o1, SlbInstanceDto o2) {
                    if (o1.getAppSiteId() < o2.getAppSiteId())
                        return -1;
                    else if (o1.getAppSiteId() == o2.getAppSiteId()) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });

            List<List<SlbInstanceDto>> dataUps = util.split(dataUp, 500);
            List<List<SlbInstanceDto>> dataDowns = util.split(dataDown, 500);
            CountDownLatch latch = new CountDownLatch(dataUps.size() + dataDowns.size());
            for (List<SlbInstanceDto> t : dataUps) {
                doUpdateResult(t, true, latch);
            }
            for (List<SlbInstanceDto> t : dataDowns) {
                doUpdateResult(t, false, latch);
            }
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doUpdateResult(List<SlbInstanceDto> t, boolean b, CountDownLatch latch) {
        Gson gson = new Gson();
        String json = gson.toJson(t);
        String url = util.getSlbCheckUpInUrl();
        if (!b) {
            url = util.getSlbCheckDownInUrl();
        }
        final String url1 = url;
        executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    okHttpClient
                            .newCall(new Request.Builder().url(url1).post(RequestBody.create(mediaType, json)).build())
                            .execute();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
                return null;
            }
        });

    }

    public List<SlbInstanceDto> checkData(List<SlbInstanceDto> slbData, List<SlbInstanceDto> eurekaData) {
        Map<String, SlbInstanceDto> mapSlb = Maps.newHashMap();
        Map<String, SlbInstanceDto> mapEuk = Maps.newHashMap();
        List<SlbInstanceDto> rs = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(slbData)) {
            slbData.forEach(p -> {
                if (!StringUtils.isEmpty(p.getName())) {
                    mapSlb.put(p.getName(), p);
                }
            });
            eurekaData.forEach(p -> {
                if (!StringUtils.isEmpty(p.getName())) {
                    mapEuk.put(p.getName(), p);
                }
            });
        }
        for (String name : mapSlb.keySet()) {
            if (mapEuk.containsKey(name)) {
                if (!mapEuk.get(name).getIpPort().equals(mapSlb.get(name).getIpPort())) {
                    // ip和端口发生了变更
                    mapSlb.get(name).setStatus(1);
                    mapSlb.get(name).setIp(mapEuk.get(name).getIp());
                    mapSlb.get(name).setPort(mapEuk.get(name).getPort());
                    rs.add(mapSlb.get(name));
                    mapEuk.remove(name);
                } else if (mapSlb.get(name).getStatus() == 0) {
                    mapSlb.get(name).setStatus(1);
                    rs.add(mapSlb.get(name));
                    mapEuk.remove(name);
                }
            } else if (mapSlb.get(name).getStatus() == 1) {
                // 说明下线了
                mapSlb.get(name).setStatus(0);
                mapSlb.get(name).setIp("");
                mapSlb.get(name).setPort(null);

                rs.add(mapSlb.get(name));
            }
        }
        // 剩下的说明是新增的
        // for (String name : mapEuk.keySet()) {
        // mapEuk.get(name).setStatus(1);
        // rs.add(mapEuk.get(name));
        // }
        return rs;

    }

    private List<SlbInstanceDto> getEurekaData() {
        Response response = null;
        try {
            String eurekaInfoUrl = util.getEurekaInfoUrl();
            //获取 slb siteserver 列表
            response = okHttpClient.newCall(new Request.Builder().url(eurekaInfoUrl).get().build()).execute();

            String data = response.body().string();
            Gson gson = new Gson();
            List<SlbInstanceDto> rs = gson.fromJson(data, type);
            return rs;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //注意次行代码是必须的，不可删除
            throw new RuntimeException(e);
        } finally {
            try {
                if (response != null) {
                    response.body().close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<SlbInstanceDto> getSlbData() {
        Response response = null;
        try {
            Request.Builder request = new Request.Builder().url(util.getSlbInfoUrl());
            response = okHttpClient.newCall(request.get().build()).execute();
            String data = response.body().string();
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            //Gson gson = builder.create();
            com.along101.slbcoordinate.dto.Response<PageDto<AppSiteServerDto>> rsData = gson.fromJson(data, typeSlb);
            List<SlbInstanceDto> rs = Lists.newArrayList();
            if (rsData != null && rsData.getDetail() != null
                    && !CollectionUtils.isEmpty(rsData.getDetail().getContent())) {
                rsData.getDetail().getContent().forEach(p -> {
                    SlbInstanceDto slbInstanceDto = new SlbInstanceDto();
                    slbInstanceDto.setIp(p.getIp());
                    slbInstanceDto.setPort(p.getPort());
                    slbInstanceDto.setName(p.getName());
                    slbInstanceDto.setAppSiteId(p.getId());
                    if (p.getStatusDetail() != null && p.getStatusDetail().containsKey("ops_health_check")) {
                        slbInstanceDto.setStatus(p.getStatusDetail().get("ops_health_check"));
                    } else {
                        slbInstanceDto.setStatus(0);
                    }
                    rs.add(slbInstanceDto);
                });
            }
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            //注意次行代码是必须的，不可删除
            throw new RuntimeException(e);
        } finally {
            try {
                if (response != null) {
                    response.body().close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
