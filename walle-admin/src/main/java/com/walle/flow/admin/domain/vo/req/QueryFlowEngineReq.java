package com.walle.flow.admin.domain.vo.req;

import com.walle.flow.admin.domain.vo.base.PageReq;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author harley.shi
 * @date 2024-07-08 10:32:25
 */
@Data
public class QueryFlowEngineReq extends PageReq implements Serializable{

    private String name;
}
