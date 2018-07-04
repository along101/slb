package com.along101.slbcoordinate.app;

import com.along101.slbcoordinate.dto.SlbInstanceDto;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.along101.slbcoordinate.dto.AppSiteServerDto;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SlbSynTest {
	@Autowired
	private SlbSynApplication slbSynApplication;
	@SuppressWarnings("serial")
	private Type type = new TypeToken<ArrayList<SlbInstanceDto>>() {
	}.getType();
	private Type type1 = new TypeToken<ArrayList<AppSiteServerDto>>() {
	}.getType();

	public String test() {
		StringBuilder rs = new StringBuilder();
		rs.append(test1());
		rs.append("\n");
		rs.append(test2());
		rs.append("\n");
		rs.append(test3());
		rs.append("\n");
		return rs.toString();
	}

	private String test1() {
		List<SlbInstanceDto> slbData = Lists.newArrayList();
		List<SlbInstanceDto> eurekaData = Lists.newArrayList();
		Gson gson = new Gson();
		String json = "", json1 = "";
		json = readAllString("slb1.json");
		slbData = getSlbData(gson.fromJson(json, type1));
		json1 = readAllString("eureka1.json");
		eurekaData = gson.fromJson(json1, type);

		// 数据对比
		List<SlbInstanceDto> rs = slbSynApplication.checkData(slbData, eurekaData);
		Optional<SlbInstanceDto> up = rs.stream().filter(p -> p.getStatus() == 1).findFirst();
		Optional<SlbInstanceDto> down = rs.stream().filter(p -> p.getStatus() == 0).findFirst();
		if (up.isPresent() && !down.isPresent()) {
			if (!up.get().getIp().equals("192.168.1.2") || (up.get().getPort().intValue() != 80)
					|| up.get().getAppSiteId() != 1) {
				return json + " " + json1 + "测试异常！";
			} else {
				return "test1正常！";
			}
		}
		return "测试1:" +json + " " + json1 + "测试异常！";
	}

	private List<SlbInstanceDto> getSlbData(ArrayList<AppSiteServerDto> lstAppServer) {
		List<SlbInstanceDto> lstSlb=Lists.newArrayList();
		lstAppServer.forEach(p->{
			SlbInstanceDto slbInstanceDto=new SlbInstanceDto();
			slbInstanceDto.setAppSiteId(p.getAppSiteId());
			slbInstanceDto.setIp(p.getIp());
			slbInstanceDto.setName(p.getName());
			if (p.getStatusDetail() != null && p.getStatusDetail().containsKey("ops_health_check")) {
				slbInstanceDto.setStatus(p.getStatusDetail().get("ops_health_check"));
			} else {
				slbInstanceDto.setStatus(0);
			}
			lstSlb.add(slbInstanceDto);
		});
		return lstSlb;
		
	}

	private String readAllString(String path) {
		InputStream in = null;
		try {
			in = SlbSynTest.class.getClassLoader().getResourceAsStream(path);
			return IOUtils.toString(in, Charsets.toCharset("utf-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} finally {
			try {
				IOUtils.closeQuietly(in);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private String test2() {
		List<SlbInstanceDto> slbData = Lists.newArrayList();
		List<SlbInstanceDto> eurekaData = Lists.newArrayList();
		Gson gson = new Gson();
		String json = "", json1 = "";

		json = readAllString("slb2.json");
		slbData = getSlbData(gson.fromJson(json, type1));
		json1 = readAllString("eureka1.json");
		eurekaData = gson.fromJson(json1, type);

		// 数据对比
		List<SlbInstanceDto> rs = slbSynApplication.checkData(slbData, eurekaData);
		Optional<SlbInstanceDto> up = rs.stream().filter(p -> p.getStatus() == 1).findFirst();
		Optional<SlbInstanceDto> down = rs.stream().filter(p -> p.getStatus() == 0).findFirst();
		if (up.isPresent() && down.isPresent()) {
			if (!up.get().getIp().equals("192.168.1.2") || (up.get().getPort().intValue() != 80)
					|| up.get().getAppSiteId() != 1) {
				return json + " " + json1 + "测试异常！";
			} else if (!down.get().getName().equals("test1") || !StringUtils.isEmpty(down.get().getIp())
					|| down.get().getPort() != null) {
				return json + " " + json1 + "测试异常！";
			} else {
				return "test2正常！";
			}
		}
		return "测试2:" + json + " " + json1 + "测试异常！";
	}

	private String test3() {
		List<SlbInstanceDto> slbData = Lists.newArrayList();
		List<SlbInstanceDto> eurekaData = Lists.newArrayList();
		Gson gson = new Gson();
		String json = "", json1 = "";

		json = readAllString("slb1.json");
		slbData = getSlbData(gson.fromJson(json, type1));

		// 数据对比
		List<SlbInstanceDto> rs = slbSynApplication.checkData(slbData, eurekaData);
		List<SlbInstanceDto> down = rs.stream().filter(p -> p.getStatus() == 0).collect(Collectors.toList());
		if (down.size() == slbData.size()-1) {
			return "test3正常！";
		}
		return "测试3:" +json + " " + json1 + "测试异常！";
	}
}
