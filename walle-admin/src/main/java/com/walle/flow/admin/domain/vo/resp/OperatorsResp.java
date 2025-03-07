package com.walle.flow.admin.domain.vo.resp;

import com.walle.flow.admin.common.OpsNodeType;
import lombok.Data;

/**
 * @author harley.shi
 * @date 2024/10/28
 */
@Data
public class OperatorsResp {
    /**
     * 节点id
     */
    private Long id;

    /**
     * 组件名称
     */
    private String label;

    /**
     * 节点类型
     * @see OpsNodeType
     */
    private String type;

    /**
     * 配置参数
     */
    private AdvancedConfig config;

    /**
     * 节点坐标
     */
    private Position position;

    /**
     * 版本号
     */
    private String version;

    /**
     * 高级配置
     */
    @Data
    public static class AdvancedConfig{
        /**
         * 算子参数
         */
        private String params;

        /**
         * 超时时间
         */
        private Integer timeout;

        /**
         * 是否忽略异常
         */
        private Boolean ignoreException = Boolean.FALSE;

        /**
         * 是否异步
         */
        private Boolean async = Boolean.FALSE;

    }

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
