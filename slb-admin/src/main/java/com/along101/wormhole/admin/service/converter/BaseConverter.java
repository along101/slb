package com.along101.wormhole.admin.service.converter;

import com.along101.wormhole.admin.common.utils.ConvertUtils;

/**
 * Created by yinzuolong on 2017/7/17.
 */
public abstract class BaseConverter<S, T> {

    public T convertS2T(S s, Class<T> tClass) {
        T t = ConvertUtils.convert(s, tClass);
        manualConvertS2T(s, t);
        return t;
    }

    public void convertS2T(S s, T t) {
        ConvertUtils.convert(s, t);
        manualConvertS2T(s, t);
    }

    public S convertT2S(T t, Class<S> sClass) {
        S s = ConvertUtils.convert(t, sClass);
        manualConvertT2S(t, s);
        return s;
    }

    public void convertT2S(T t, S s) {
        ConvertUtils.convert(t, s);
        manualConvertT2S(t, s);
    }

    protected abstract void manualConvertT2S(T t, S s);

    protected abstract void manualConvertS2T(S s, T t);
}
