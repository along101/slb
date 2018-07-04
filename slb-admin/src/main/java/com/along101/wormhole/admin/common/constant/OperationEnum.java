package com.along101.wormhole.admin.common.constant;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by yinzuolong on 2017/7/20.
 */
public enum OperationEnum {

    DEPLOY_APPSITE(OperationTypeEnum.UP, null),
    UNDEPLOY_APPSITE(OperationTypeEnum.DOWN, null),
    PULL_IN(OperationTypeEnum.UP, OperationOffsetEnum.OPS_PULL),
    PULL_OUT(OperationTypeEnum.DOWN, OperationOffsetEnum.OPS_PULL),
    HEALTH_CHECK_UP(OperationTypeEnum.UP, OperationOffsetEnum.OPS_HEALTH_CHECK),
    HEALTH_CHECK_DOWN(OperationTypeEnum.DOWN, OperationOffsetEnum.OPS_HEALTH_CHECK),
    MANUAL_UP(OperationTypeEnum.UP, OperationOffsetEnum.OPS_MANUAL),
    MANUAL_DOWN(OperationTypeEnum.DOWN, OperationOffsetEnum.OPS_MANUAL);

    private OperationTypeEnum operationType;
    private OperationOffsetEnum operationOffset;

    OperationEnum(OperationTypeEnum operationType, OperationOffsetEnum operationOffset) {
        this.operationType = operationType;
        this.operationOffset = operationOffset;
    }

    public OperationTypeEnum getOperationType() {
        return this.operationType;
    }

    public OperationOffsetEnum getOperationOffset() {
        return operationOffset;
    }

    public static Optional<OperationEnum> findByTypeOffset(int operationType, String offset) {
        return Arrays.stream(OperationEnum.values())
                .filter(operationEnum -> operationEnum.getOperationType().getCode() == operationType
                        && operationEnum.getOperationOffset() != null
                        && operationEnum.getOperationOffset().getOperation().equals(offset))
                .findAny();
    }
}
