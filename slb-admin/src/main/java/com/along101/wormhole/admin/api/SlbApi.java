package com.along101.wormhole.admin.api;

import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.Response;
import com.along101.wormhole.admin.dto.slb.SlbDto;
import com.along101.wormhole.admin.service.SlbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@RestController
@RequestMapping("/api/slb")
@Slf4j
public class SlbApi {

    @Autowired
    private SlbService slbService;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public Response<List<SlbDto>> getAll() {
        try {
            List<SlbDto> slbDtos = slbService.getAllSlbs();
            log.info("get slb, result={}", slbDtos);
            return Response.success(slbDtos);
        } catch (SlbServiceException e) {
            log.warn("get slb error.", e);
            return Response.error(e);
        } catch (Throwable e) {
            log.error("get slb error.", e);
            return Response.error(e);
        }
    }
}
