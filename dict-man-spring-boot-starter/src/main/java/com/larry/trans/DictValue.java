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
    // 翻译类型
    String type() default "simple";

    // 关联表的字段
    String value() default "";

    // 多张联表需要额外指定表名，避免相同属性冲突
    String table() default "";

    // 使用新字段保存翻译结果
    String newKey() default "";

    // 是字典类别的key
    String ref() default "dictMan";

}
