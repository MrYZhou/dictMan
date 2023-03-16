package com.example.springdemo.spring.book;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dict")
public class DictEntity {
    @TableId(value = "id")
    private String id;

    @TableField("name")
    private String name;


}
