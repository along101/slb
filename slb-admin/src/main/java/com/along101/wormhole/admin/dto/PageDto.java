package com.along101.wormhole.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by yinzuolong on 2017/8/18.
 */
@Data
public class PageDto<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;
    private int numberOfElements;
    private int size;
    private int number;
}
