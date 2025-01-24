package com.walle.flow.admin.domain.entity;

import com.walle.flow.admin.common.OpsNodeType;
import lombok.Data;

/**
 * @author harley.shi
 * @date 2024/10/28
 */
@Data
public class OperatorsDO {
    /**
     * 节点id
     */
    private Long id;

    /**
     * 组件描述
     */
    private String label;

    /**
     * 节点类型
     * @see OpsNodeType
     */
    private String type;

    /**
     * 算子
     */
    private String operator;

    /**
     * 算子参数
     */
    private String params;

    /**
     * 回滚算子
     */
    private String rollback;

    /**
     * 回滚算子参数
     */
    private String rollbackParams;

    /**
     * 超时时间
     */
    private Integer timeout;

    /**
     * 是否忽略异常
     */
    private Boolean ignoreException = Boolean.FALSE;

    /**
     * 节点坐标
     */
    private Position position;

    /**
     * 节点坐标
     */
    @Data
    public static class Position{
        /**
         * x坐标
         */
        private Integer x;

        /**
         * y坐标
         */
        private Integer y;
    }
}
