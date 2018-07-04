package com.along101.wormhole.admin.service.converter;

import com.along101.wormhole.admin.common.constant.OperationOffsetEnum;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.entity.AppSiteServerEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/7/17.
 */
@Component
public class AppSiteServerConverter extends BaseConverter<AppSiteServerDto, AppSiteServerEntity> {

    public static final String STATUS = "1111111111";

    @Override
    protected void manualConvertS2T(AppSiteServerDto appSiteServerDto, AppSiteServerEntity appSiteServerEntity) {
        String status = STATUS;
        if (!CollectionUtils.isEmpty(appSiteServerDto.getStatusDetail())) {
            status = computeStatus(appSiteServerDto.getStatusDetail());
        }
        appSiteServerEntity.setStatus(status);
    }

    @Override
    protected void manualConvertT2S(AppSiteServerEntity appSiteServerEntity, AppSiteServerDto appSiteServerDto) {
        Map<String, Integer> statusDetail = computeStatus(appSiteServerEntity.getStatus());
        appSiteServerDto.setStatusDetail(statusDetail);
        appSiteServerDto.setStatus(getServerFinalStatus(appSiteServerEntity.getStatus()));
    }

    /**
     * 获取最终状态
     *
     * @param status
     * @return
     */
    public static Integer getServerFinalStatus(String status) {
        return status.indexOf('0') < 0 ? 1 : 0;
    }

    public static Integer getServerFinalStatus(Map<String, Integer> statusDetail) {
        return getServerFinalStatus(computeStatus(statusDetail));
    }

    public static String computeStatus(String resultStatus, OperationOffsetEnum operationOffset, Integer status) {
        StringBuilder result = new StringBuilder(resultStatus);
        result.setCharAt(operationOffset.getOffset(), status == 1 ? '1' : '0');
        return result.toString();
    }

    //根据statusDetail 获取status
    public static String computeStatus(Map<String, Integer> statusDetail) {
        StringBuilder status = new StringBuilder(STATUS);
        statusDetail.forEach((key, value) -> {
            Optional<OperationOffsetEnum> offsetEnum = OperationOffsetEnum.findByOperation(key);
            offsetEnum.ifPresent(operationOffset ->
                    status.setCharAt(operationOffset.getOffset(), value == 1 ? '1' : '0'));
        });
        return status.toString();
    }

    //根据status获取statusDetail
    public static Map<String, Integer> computeStatus(String status) {
        return Arrays.stream(OperationOffsetEnum.values())
                .collect(Collectors.toMap(OperationOffsetEnum::getOperation, operationOffsetEnum ->
                        status.charAt(operationOffsetEnum.getOffset()) == '1' ? 1 : 0));
    }
}
