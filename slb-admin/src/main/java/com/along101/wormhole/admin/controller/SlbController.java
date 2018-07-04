package com.along101.wormhole.admin.controller;

import com.along101.wormhole.admin.dto.Response;
import com.along101.wormhole.admin.dto.slb.SlbDto;
import com.along101.wormhole.admin.dto.slb.SlbServerDto;
import com.along101.wormhole.admin.service.SlbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@RestController
@RequestMapping("/web/slbs")
@Slf4j
public class SlbController {

    @Autowired
    private SlbService slbService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Response<List<SlbDto>> getAll() {
        List<SlbDto> slbs = slbService.getAllSlbs();
        return Response.success(slbs);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Response<SlbDto> createSlb(@RequestBody SlbDto slb) {
        SlbDto newSlb = slbService.createSlb(slb);
        return Response.success(newSlb);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public Response<SlbDto> updateSlb(@RequestBody SlbDto slb) {
        SlbDto newSlb = slbService.updateSlb(slb);
        return Response.success(newSlb);
    }

    @RequestMapping(value = "/{slbId}", method = RequestMethod.GET)
    public Response<SlbDto> getSlbById(@PathVariable(value = "slbId") Long slbId) {
        Optional<SlbDto> slb = slbService.getSlbById(slbId);
        return Response.success(slb.orElse(null));
    }

    @RequestMapping(value = "/conditions", method = RequestMethod.GET)
    public Response<List<SlbDto>> getSlbByIdOrName(@RequestParam(required = false) String field,
                                                @RequestParam(required=false) String value) {

        List<SlbDto> slbDtos = new ArrayList<>();
        Optional<SlbDto> slb = null;
        if(field == null || value == null || value.equals("")) {
            slbDtos = slbService.getAllSlbs();
            //return Response.success(slbDtos);
        }else if(field.equals("ID")) { //field 为 ID
            slb = slbService.getSlbById(Long.parseLong(value));
            slbDtos.add(slb.orElse(null));
        }else if(field.equals("集群名")){ //field 为 模糊名称
            slbDtos = slbService.queryByName(value);
        }
        return Response.success(slbDtos);
    }

    @RequestMapping(value = "/{slbId}", method = RequestMethod.DELETE)
    public Response deleteSlbById(@PathVariable(value = "slbId") Long slbId) {
        slbService.deleteSlb(slbId);
        return Response.success("");
    }

    //增加server
    @RequestMapping(value = "/{slbId}/servers", method = RequestMethod.POST)
    public Response<SlbServerDto> addSlbServer(@PathVariable(value = "slbId") Long slbId, @RequestBody SlbServerDto slbServer) {
        SlbServerDto newSlbServer = slbService.addServer(slbId, slbServer);
        return Response.success(newSlbServer);
    }

    //删除server
    @RequestMapping(value = "/{slbId}/servers/{serverId}", method = RequestMethod.DELETE)
    public Response deleteSlbServer(@PathVariable(value = "slbId") Long slbId, @PathVariable(value = "serverId") Long serverId) {
        slbService.deleteServer(slbId, serverId);
        return Response.success("");
    }

}
