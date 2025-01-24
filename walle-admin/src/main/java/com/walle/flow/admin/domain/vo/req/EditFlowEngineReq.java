package com.walle.flow.admin.domain.vo.req;

import lombok.Data;

import java.util.Date;

/**
 * @author harley.shi
 * @date 2024/10/30
 */
@Data
public class EditFlowEngineReq {
    private Long id;

    private String name;

    private String description;

    private String content;

    private String executionMode;

    private Date updateTime;
}
