package com.example.springdemo.spring.book.model;


import com.example.springdemo.spring.book.DictEntity;
import com.larry.trans.DictValue;
import com.larry.trans.RelationTable;
import lombok.Data;

@Data
//@RelationTables({
//        @RelationTable(primaryKey = "id",target = DictEntity.class)
//})

@RelationTable(primaryKey = "id",target = DictEntity.class)
public class BookInfo2 {

//    String id;
//    String name;
//
//    String tag;
//    private String did;

//    1. 普通字典翻译
    @DictValue(ref="book",newKey = "bookName3")
    String type;

    // 2. 关联表id的翻译建议通过newKey加个别名
    @DictValue(value = "name",newKey = "bookName4")
    String dictId;
}
