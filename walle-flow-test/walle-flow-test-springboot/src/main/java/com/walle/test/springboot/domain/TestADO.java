package com.walle.test.springboot.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author harley.shi
 * @date 2024/10/30
 */
@Getter
@Setter
@TableName("test_a")
public class TestADO {
    @TableId(value = "id")
    private Long id;

    @TableField("name")
    private String name;

    public TestADO(String name) {
        this.name = name;
    }
}
