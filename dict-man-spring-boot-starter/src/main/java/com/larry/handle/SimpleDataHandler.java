package com.larry.handle;

import com.larry.service.DictService;
import com.larry.spring.DictAop;
import org.noear.snack.ONode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;


public class SimpleDataHandler extends HandleChain {


    public void handle(DictAop.DictHelper dictHelper, ONode data, DictService dictService, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {

        String bindValue = dictHelper.dictValue.value();
        if ("".equals(bindValue)) {
            Class<?> dictParseClass = dictHelper.dictParseClass;
            String key = dictHelper.key;
            Object item = data.select("$." + key).toObject(dictParseClass);

            String invoke = (String) dictHelper.declaredMethod.invoke(item);

            String value = dictHelper.dictMap.get(invoke);
            dictHelper.declaredMethodSet.invoke(item, value == null ? "" : value);
            // 设置数据
            data.set("data", ONode.load(item));
        } else {
            this.nextHandle(dictHelper, data, dictService, field);
        }

    }

    @Override
    public void handleBatch(DictAop.DictHelper dictHelper, ONode data, DictService dictService, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {

        String bindValue = dictHelper.dictValue.value();
        if ("".equals(bindValue)) {
            Class<?> dictParseClass = dictHelper.dictParseClass;
            String key = dictHelper.key;
            List<?> list1 = data.select("$." + key).toObjectList(dictParseClass);

            // 获取字典值,并且设置
            for (Object item1 : list1) {
                String invoke = (String) dictHelper.declaredMethod.invoke(item1);
                String value = dictHelper.dictMap.get(invoke);
                dictHelper.declaredMethodSet.invoke(item1, value == null ? "" : value);
            }

            // 设置数据
            int index = -1;
            for (int i = key.length() - 1; i > 0; i--) {
                char c = key.charAt(i);
                if (c == '.') {
                    index = i;
                }
            }
            String path = key.substring(0, index);
            String dataKey = key.substring(index + 1);

            data.select("$." + path).set(dataKey, ONode.load(list1));
        } else {
            this.nextBatchHandle(dictHelper, data, dictService, field);
        }
    }
}
