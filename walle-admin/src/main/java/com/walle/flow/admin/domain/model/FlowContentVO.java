package com.walle.flow.admin.domain.model;

import com.walle.flow.admin.domain.vo.resp.OperatorsResp;
import lombok.Data;

import java.util.List;

/**
 * @author harley.shi
 * @date 2024/10/28
 */
@Data
public class FlowContentVO {
    /**
     * 节点信息
     */
    private List<OperatorsResp> nodes;

    /**
     * 节点连线信息
     */
    private List<EdgeInfo> edges;

    /**
     * 连线信息
     */
    @Data
    public static class EdgeInfo{

        private String id;

        private String type;

        private String source;

        private String target;

        private String sourceHandle;

        private String targetHandle;

        private String label;

        private String markerEnd;

        private Float sourceX;

        private Float sourceY;

        private Float targetX;

        private Float targetY;
    }
}
