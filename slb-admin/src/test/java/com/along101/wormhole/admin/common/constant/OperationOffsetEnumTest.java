package com.along101.wormhole.admin.common.constant;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

/**
 * Created by yinzuolong on 2017/7/18.
 */
public class OperationOffsetEnumTest {
    @Test
    public void test() throws Exception {
        Optional<OperationOffsetEnum> exist = OperationOffsetEnum.findByOperation("ops_manual");
        Assert.assertTrue(exist.isPresent());
        Optional<OperationOffsetEnum> notExist = OperationOffsetEnum.findByOperation("tt");
        Assert.assertFalse(notExist.isPresent());
    }
}
