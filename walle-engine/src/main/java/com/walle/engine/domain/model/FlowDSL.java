package com.walle.engine.domain.model;

import lombok.Data;

import java.util.List;

/**
 * @author harley.shi
 * @date 2024/11/20
 */
@Data
public class FlowDSL {

    /**
     * 引擎id
     */
    private Long id;

    /**
     * 引擎名称
     */
    private String name;

    /**
     * 引擎描述
     */
    private String description;

    /**
     * 执行方式
     */
    private String executionMode;

    /**
     * 引擎内容
     */
    private Graph content;

    /**
     * 引擎版本号
     */
    private String version;

    /**
     * 图
     */
    @Data
    public static class Graph {
        /**
         * 节点
         */
        private List<NodeInfo> nodes;

        /**
         * 边
         */
        private List<EdgeInfo> edges;
    }
}
