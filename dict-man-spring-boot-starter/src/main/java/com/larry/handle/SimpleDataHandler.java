package com.larry.handle;

import com.larry.service.DictService;
import com.larry.spring.DictAop;
import com.larry.trans.DictValue;
import org.noear.snack.ONode;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class SimpleDataHandler extends HandleChain {


    public void handle(DictAop.DictHelper dictHelper, ONode data, DictService dictService) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 判断是不是有ref属性

        // 获取解析类中需要解析的字段
        Class<?> dictParseClass = dictHelper.dictParseClass;
        Field[] declaredFields = dictParseClass.getDeclaredFields();

        String key = dictHelper.key;
        Object item = data.select("$." + key).toObject(dictParseClass);

        for (Field field : declaredFields) {
            DictValue annotation = field.getAnnotation(DictValue.class);
            if (annotation == null) {
                continue;
            }
            String type = annotation.type();
            if("simple".equals(type)){
                String ref = annotation.ref();
                Map<String, String> dictMap = dictService.getDict(ref);
                // 字段名
                String name = field.getName();
                // 获取方法名
                Method declaredMethod = dictParseClass.getDeclaredMethod("get" + StringUtils.capitalize(name));
                Method declaredMethodSet = dictParseClass.getDeclaredMethod("set" + StringUtils.capitalize(name), String.class);
                // 获取字典值,并且设置
                String invoke = (String) declaredMethod.invoke(item);
                if (dictMap == null) dictMap = new HashMap<>();
                String value = dictMap.get(invoke);
                declaredMethodSet.invoke(item, value == null ? "" : value);
            }
        }
        // 设置数据
        data.set("data", ONode.load(item));

        this.nextHandle(dictHelper, data, dictService);
    }
}
