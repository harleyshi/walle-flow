package com.flow.engine.model;

import com.flow.engine.common.enums.StatusEnum;
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
     * 引擎状态
     */
    private StatusEnum status;

    /**
     * 引擎内容
     */
    private Graph content;

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
