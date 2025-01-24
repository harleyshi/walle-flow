package com.walle.engine.domain.model;

import lombok.Data;

/**
 * @author harley.shi
 * @date 2024/11/20
 */
@Data
public class EdgeInfo {
    /**
     * 边的唯一标识
     */
    private String id;

    /**
     * 边的类型
     */
    private String type;

    /**
     * 边的源节点id
     */
    private String source;

    /**
     * 边的目标节点id
     */
    private String target;

    /**
     * 边的源节点的处理器id
     */
    private String sourceHandle;

    /**
     * 边的目标节点的处理器id
     */
    private String targetHandle;

    /**
     * 边的标签
     */
    private String label;

    /**
     * 边的额外数据
     */
    private Object data;

    /**
     * 边的箭头类型
     */
    private String markerEnd;

    /**
     * 边的源节点的坐标x
     */
    private Float sourceX;

    /**
     * 边的源节点的坐标y
     */
    private Float sourceY;

    /**
     * 边的目标节点的坐标x
     */
    private Float targetX;

    /**
     * 边的目标节点的坐标y
     */
    private Float targetY;
}
