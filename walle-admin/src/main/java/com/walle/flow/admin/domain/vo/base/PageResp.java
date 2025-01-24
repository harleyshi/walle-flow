package com.walle.flow.admin.domain.vo.base;

import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页请求出参
 */
@SuppressWarnings("all")
@Data
public class PageResp<T> implements Serializable {
    private Long total;

    private List<T> records;

    public PageResp() {
        this.total = 0L;
        this.records = Lists.newArrayList();
    }

    public PageResp(List<T> list, Long total) {
        this.records = list;
        this.total = total;
    }

    public static <T> PageResp<T> empty() {
        PageResp p = new PageResp();
        p.records = Lists.newArrayList();
        p.total = 0L;
        return p;
    }

}
