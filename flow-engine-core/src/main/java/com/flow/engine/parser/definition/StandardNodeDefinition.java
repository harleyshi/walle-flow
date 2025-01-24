package com.flow.engine.parser.definition;

import com.flow.engine.common.enums.NodeType;
import com.flow.engine.parser.DefinitionVisitor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author harley.shi
 * @date 2024/10/28
 */
@Getter
@Setter
public class StandardNodeDefinition extends NodeDefinition {
    /**
     * 节点版本号
     */
    private String version;

    /**
     * 算子参数
     */
    private String params;

    /**
     * 超时时间（毫秒）
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

    public StandardNodeDefinition(String code, String name) {
        super(code, name);
    }

    @Override
    public void validate() {

    }

    @Override
    public void doVisit(DefinitionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType nodeType() {
        return NodeType.COMPONENT;
    }
}
