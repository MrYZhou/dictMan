package com.larry.handle;

import com.larry.service.DictService;
import com.larry.spring.DictAop;
import org.noear.snack.ONode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SimpleDataHandler extends HandleChain {


    public void handle(DictAop.DictHelper dictHelper, ONode data, DictService dictService, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {
        Object item = data.select("$." + dictHelper.key).toObject(dictHelper.dictParseClass);
        if (item == null) return;

        // 如果不是简单类型直接放行
        if (!"".equals(dictHelper.dictValue.value())) {
            this.nextHandle(dictHelper, data, dictService, field);
            return;
        }

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

        if ("".equals(dictHelper.dictValue.value())) {
            data.set("data", ONode.load(item));
        }


    }

    @Override
    public void handleBatch(DictAop.DictHelper dictHelper, ONode data, DictService dictService, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {
        // 如果不是简单类型直接放行
        if (!"".equals(dictHelper.dictValue.value())) {
            this.nextBatchHandle(dictHelper, data, dictService, field);
            return;
        }

        List<?> list = data.select("$." + dictHelper.key).toObjectList(dictHelper.dictParseClass);
        String newKey = dictHelper.dictValue.newKey();
        List objects = dictService.getResultList();
        for (int i = 0; i < list.size() ; i++) {
            Object item = list.get(i);
            String invoke = (String) dictHelper.declaredMethod.invoke(item);
            String value = dictHelper.dictMap.get(invoke);
            dictHelper.declaredMethodSet.invoke(item, value == null ? "" : value);
            if (!"".equals(newKey)) {
                if(objects.size() < i+1 ) objects.add(new HashMap<>());
                Map o = (Map) objects.get(i);
                o.put(newKey, value == null ? "" : value);
            }
        }
        // 别名设置
        dictService.setResultList(objects);
        // 本名设置
        RelationTableHandler.setData(data, dictHelper.key, list);

    }
}
