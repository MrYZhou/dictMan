package com.larry.trans;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Repeatable(RelationTables.class)
public @interface RelationTable {

    Class<?> target();
    // 大部分情况认为外键是关联表的id
    String bindKey() default "id";


}
