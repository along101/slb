package com.along101.wormhole.admin.common.constant;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by yinzuolong on 2017/7/11.
 */
public enum OperationOffsetEnum {

    OPS_MANUAL("ops_manual", 0), OPS_PULL("ops_pull", 1), OPS_HEALTH_CHECK("ops_health_check", 2);
    private int offset;
    private String operation;

    OperationOffsetEnum(String operation, int offset) {
        this.operation = operation;
        this.offset = offset;
    }

    public int getOffset() {
        return this.offset;
    }

    public String getOperation() {
        return operation;
    }

    public static Optional<OperationOffsetEnum> findByOperation(String operation) {
        return Arrays.stream(OperationOffsetEnum.values())
                .filter(offsetEnum -> offsetEnum.getOperation().equals(operation))
                .findFirst();
    }
}
