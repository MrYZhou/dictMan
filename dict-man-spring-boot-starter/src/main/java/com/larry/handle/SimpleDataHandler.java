package com.larry.handle;

import com.larry.service.DictService;
import com.larry.spring.DictAop;
import com.larry.trans.DictValue;
import org.noear.snack.ONode;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class SimpleDataHandler extends HandleChain {


    public void handle(DictAop.DictHelper dictHelper, ONode data, DictService dictService, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {

        Class<?> dictParseClass = dictHelper.dictParseClass;
        String key = dictHelper.key;
        Object item = data.select("$." + key).toObject(dictParseClass);


        DictValue annotation = field.getAnnotation(DictValue.class);
        if (annotation == null) {
            return;
        }
        String bindValue = annotation.value();
        if ("".equals(bindValue)) {
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
            // 设置数据
            data.set("data", ONode.load(item));
        }else{
            this.nextHandle(dictHelper, data, dictService, field);
        }

    }
}
