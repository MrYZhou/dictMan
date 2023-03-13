package com.larry.trans;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DictOne {
    // 翻译解析类
    Class<?> value();

    // 数据解析的key,默认解析data
    String key() default "data";
}
