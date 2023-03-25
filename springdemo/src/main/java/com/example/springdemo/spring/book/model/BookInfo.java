package com.example.springdemo.spring.book.model;

import com.larry.trans.DictValue;

import lombok.Data;

@Data
public class BookInfo {

    private String id;
    private String name;
    private String tag;

    // 1. 普通字典翻译,不带类别
    @DictValue
    private String type;

}
