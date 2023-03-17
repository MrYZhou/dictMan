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

        if ("".equals(dictHelper.dictValue.value())) {

            Object item = data.select("$." + dictHelper.key).toObject(dictHelper.dictParseClass);

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

        if ("".equals(dictHelper.dictValue.value())) {
            List<?> list = data.select("$." + dictHelper.key).toObjectList(dictHelper.dictParseClass);

            for (Object item : list) {
                String invoke = (String) dictHelper.declaredMethod.invoke(item);
                String value = dictHelper.dictMap.get(invoke);
                dictHelper.declaredMethodSet.invoke(item, value == null ? "" : value);
            }

            // 设置数据
            RelationTableHandler.setData(data, dictHelper.key, list);
        } else {
            this.nextBatchHandle(dictHelper, data, dictService, field);
        }
    }
}
