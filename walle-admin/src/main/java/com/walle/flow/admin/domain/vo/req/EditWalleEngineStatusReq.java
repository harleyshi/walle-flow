package com.walle.flow.admin.domain.vo.req;

import lombok.Data;

/**
 * @author harley.shi
 * @date 2024/10/30
 */
@Data
public class EditWalleEngineStatusReq {
    private Long id;

    private String status;
}
