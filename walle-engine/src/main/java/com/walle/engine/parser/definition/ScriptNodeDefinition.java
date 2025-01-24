package com.walle.engine.parser.definition;

import com.walle.engine.parser.DefinitionVisitor;
import com.walle.operator.common.enums.NodeType;
import com.walle.operator.utils.AssertUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @author harley.shi
 * @date 2024/10/28
 */
@Getter
@Setter
public class ScriptNodeDefinition extends NodeDefinition {

    /**
     * 脚本语言，如：groovy、javascript
     */
    private String scriptLang;

    /**
     * 脚本内容
     */
    private String script;

    public ScriptNodeDefinition(String identify, String name) {
        super(identify, name);
    }

    @Override
    public void validate() {
        AssertUtil.notBlank(scriptLang, String.format("%s if [scriptLang] cannot be null", scriptLang));
        AssertUtil.notBlank(script, String.format("%s [script] cannot be empty", script));
    }

    @Override
    public void doVisit(DefinitionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType nodeType() {
        return NodeType.SCRIPT;
    }
}
