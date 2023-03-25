package com.example.springdemo.spring.book.model;


import com.larry.trans.DictValue;

import lombok.Data;

@Data
public class BookInfo2 {

   String id;
   String name;

   String tag;
   private String did;

    // 1. 普通字典翻译,带类别,增加别名
    @DictValue(ref="book",newKey = "bookName3")
    private String type;
}
