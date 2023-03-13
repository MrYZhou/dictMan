package com.larry.trans;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DictValue {
    // 需要联表的类
    Class<?> targetTable() default Object.class;

    // 联表的字段
    String targetField() default "";

    // 返回的json中要翻译的
    String sourceField() default "";

    // 是字典类别的key
    String ref() default "dictMan";

}
