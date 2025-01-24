package com.walle.flow.admin.domain.vo.base;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * 通用分页请求对象
 * @author harley.shi
 */
@Data
public abstract class PageReq {

    /**
     * 页码
     */
    private Integer pageNo = 1;

    /**
     * 页展示条数
     */
    private Integer pageSize = 20;


    public <T> Page<T> buildPage() {
        return new Page<>(pageNo, pageSize);
    }

    /**
     * 获取是否首页 目前只按页码确认是否首页
     * @return ture 是  false 否
     */
    public Boolean obtFirstPage(){
     return this.pageNo != null && pageNo == 1 ;
    }
}
