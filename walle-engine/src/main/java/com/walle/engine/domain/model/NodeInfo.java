package com.walle.engine.domain.model;

import lombok.Data;

/**
 * @author harley.shi
 * @date 2024/11/20
 */
@Data
public class NodeInfo {
    /**
     * 节点id
     */
    private String id;

    /**
     * 节点名称
     */
    private String label;

    /**
     * 节点类型
     * @see com.walle.operator.common.enums.NodeType
     */
    private String type;

    /**
     * 节点配置
     */
    private NodeConfig config;

    /**
     * 节点坐标
     */
    private Position position;

    /**
     * 节点版本号
     */
    private String version;

    public NodeInfo() {
    }

    public NodeInfo(String id) {
        this.id = id;
    }

    /**
     * 高级配置
     */
    @Data
    public static class NodeConfig{
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

    }

    /**
     * 节点坐标
     */
    @Data
    public static class Position{
        /**
         * x坐标
         */
        private Float x;

        /**
         * y坐标
         */
        private Float y;
    }
}
