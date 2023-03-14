package com.larry.trans;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface RelationTables {
    RelationTable[] value();
}
