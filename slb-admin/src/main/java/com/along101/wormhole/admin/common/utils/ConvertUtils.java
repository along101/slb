package com.along101.wormhole.admin.common.utils;

import com.alibaba.fastjson.JSON;
import com.along.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.PageDto;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by yinzuolong on 2017/7/17.
 */
public class ConvertUtils {

    public static <S, T> T convert(S s, Class<T> tClass) {
        try {
            T t = tClass.newInstance();
            BeanUtils.copyProperties(s, t);
            return t;
        } catch (Exception e) {
            throw SlbServiceException.newException("convert error from %s to %s", s, tClass, e);
        }
    }

    public static <S, T> T convert(S s, T t) {
        try {
            BeanUtils.copyProperties(s, t);
            return t;
        } catch (Exception e) {
            throw SlbServiceException.newException("convert error from %s to %s", s, t, e);
        }
    }

    public static <S, T> List<T> convert(Iterable<S> iterable, Class<T> tClass) {
        return convert(iterable, s -> ConvertUtils.convert(s, tClass));
    }

    public static <S, T> List<T> convert(Iterable<S> iterable, Function<? super S, ? extends T> mapper) {
        return StreamSupport.stream(iterable.spliterator(), false).map(mapper).collect(Collectors.toList());
    }

    public static <S, T> PageDto<T> convertPage(Page<S> page, Function<? super S, ? extends T> mapper) {
        List<S> contents = page.getContent();
        List<T> tContents = convert(contents, mapper);
        PageDto<T> pageDto = new PageDto<>();
        pageDto.setContent(tContents);
        pageDto.setFirst(page.isFirst());
        pageDto.setLast(page.isLast());
        pageDto.setNumber(page.getNumber());
        pageDto.setNumberOfElements(page.getNumberOfElements());
        pageDto.setTotalPages(page.getTotalPages());
        pageDto.setTotalElements(page.getTotalElements());
        pageDto.setSize(page.getSize());
        return pageDto;
    }

    public static <S, T> PageDto<T> convertPage(Page<S> page, Class<T> tClass) {
        return convertPage(page, s -> convert(s, tClass));
    }

    public static <T> T deepClon(T t, Class<T> tClass) {
        String json = JSON.toJSONString(t);
        return JSON.parseObject(json, tClass);
    }
}
