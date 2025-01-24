package com.walle.test.springboot.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author harley.shi
 * @date 2024/10/30
 */
@Getter
@Setter
@AllArgsConstructor
@TableName("test_b")
public class TestBDO {
    @TableId(value = "id")
    private Long id;

    @TableField("name")
    private String name;

    public TestBDO(String name) {
        this.name = name;
    }
}
