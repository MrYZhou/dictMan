package com.example.springdemo.spring.book;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("book")
public class BookEntity {
    @TableId(value = "id")
    private String id;

    @TableField("name")
    private String name;


    @TableField("type")
    private Integer type;


    @TableField(value = "tag", fill = FieldFill.INSERT)
    private String tag;
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(value = "modifyTime", fill = FieldFill.INSERT)
    private LocalDateTime modifyTime;


}
