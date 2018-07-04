package com.along101.wormhole.admin.service.converter;

import com.alibaba.fastjson.JSON;
import com.along101.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.entity.AppSiteArchiveEntity;
import org.springframework.stereotype.Component;

/**
 * Created by yinzuolong on 2017/7/20.
 */
@Component
public class AppSiteArchiveConverter extends BaseConverter<AppSiteArchiveDto, AppSiteArchiveEntity> {

    @Override
    protected void manualConvertT2S(AppSiteArchiveEntity appSiteArchiveEntity, AppSiteArchiveDto appSiteArchiveDto) {
        appSiteArchiveDto.setAppSiteDto(JSON.parseObject(appSiteArchiveEntity.getAppSiteContent(), AppSiteDto.class));
        appSiteArchiveDto.setDomainAppSiteDtos(JSON.parseArray(appSiteArchiveEntity.getDomainContent(), AppSiteDto.class));
    }

    @Override
    protected void manualConvertS2T(AppSiteArchiveDto appSiteArchiveDto, AppSiteArchiveEntity appSiteArchiveEntity) {
        appSiteArchiveEntity.setAppSiteContent(JSON.toJSONString(appSiteArchiveDto.getAppSiteDto()));
        appSiteArchiveEntity.setDomainContent(JSON.toJSONString(appSiteArchiveDto.getDomainAppSiteDtos()));
    }
}
