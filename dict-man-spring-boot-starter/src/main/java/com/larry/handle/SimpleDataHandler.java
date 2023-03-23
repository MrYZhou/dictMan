package com.larry.handle;

import com.larry.service.DictService;
import com.larry.spring.DictAop;
import org.noear.snack.ONode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SimpleDataHandler extends HandleChain {


    public void handle(DictAop.DictHelper dictHelper, ONode data, DictService dictService, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {

        if ("".equals(dictHelper.dictValue.value())) {

            Object item = data.select("$." + dictHelper.key).toObject(dictHelper.dictParseClass);
            if(item == null) return;

            // 设置数据
            String invoke = (String) dictHelper.declaredMethod.invoke(item);
            String value = dictHelper.dictMap.get(invoke);
            String newKey = dictHelper.dictValue.newKey();
            if ("".equals(newKey)) {
                dictHelper.declaredMethodSet.invoke(item, value == null ? "" : value);

            } else {
                Map<String, String> tempMap = DictService.getTempMap();
                tempMap.put(newKey, value);
            }

            data.set("data", ONode.load(item));
        } else {
            this.nextHandle(dictHelper, data, dictService, field);
        }

    }

    @Override
    public void handleBatch(DictAop.DictHelper dictHelper, ONode data, DictService dictService, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {

        if ("".equals(dictHelper.dictValue.value())) {
            List<?> list = data.select("$." + dictHelper.key).toObjectList(dictHelper.dictParseClass);
            String newKey = dictHelper.dictValue.newKey();
            ArrayList<Object> objects = new ArrayList<>();
            for (Object item : list) {
                String invoke = (String) dictHelper.declaredMethod.invoke(item);
                String value = dictHelper.dictMap.get(invoke);
                if ("".equals(newKey)) {
                    dictHelper.declaredMethodSet.invoke(item, value == null ? "" : value);
                } else {
                    // 设置新key
                    ONode load = ONode.load(item);
                    Map o = (Map)  load.toData();
                    o.put(newKey,value);
                    objects.add( ONode.load(o));
                }
            }
            // 设置数据
            if (!"".equals(newKey)) {
                dictService.setResultList(objects);
                return;
            }
            RelationTableHandler.setData(data, dictHelper.key, list);

        } else {
            this.nextBatchHandle(dictHelper, data, dictService, field);
        }
    }
}
