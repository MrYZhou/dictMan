package com.example.springdemo.base;

import org.noear.snack.ONode;

public class CommonUtil {
    public static <T> T toBean(Object obj, Class<?> tClass) {
        return ONode.deserialize(ONode.stringify(obj), tClass);
    }

    public static <T> T toBean(Object obj) {
        return ONode.deserialize(ONode.stringify(obj));
    }

    public static <T> T toBean(String str, Class<?> tClass) {
        return ONode.deserialize(str, tClass);
    }

    public static <T> T toBean(String str) {
        return ONode.deserialize(str);
    }

}
