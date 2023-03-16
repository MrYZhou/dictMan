package com.larry.handle;

import com.baomidou.mybatisplus.annotation.TableName;
import com.larry.service.DictService;
import com.larry.spring.DictAop;
import com.larry.trans.DictValue;
import com.larry.trans.RelationTable;
import org.noear.snack.ONode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationTableHandler extends HandleChain implements DictHandler {
    RelationTable relationTable = null;

    public RelationTable getRelationTable() {
        return relationTable;
    }

    public void setRelationTable(RelationTable relationTable) {
        this.relationTable = relationTable;
    }

    @Override
    public void handle(DictAop.DictHelper dictHelper, ONode data, DictService dictService, Field field) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, SQLException {

        // 获取解析类中需要解析的字段
        Class<?> dictParseClass = dictHelper.dictParseClass;
        //  单关联表
        RelationTable relationTable = this.relationTable != null ? this.relationTable : dictParseClass.getDeclaredAnnotation(RelationTable.class);

        String key = dictHelper.key;
        Object item = data.select("$." + key).toObjectList(dictHelper.dictParseClass);
        if (relationTable != null) {
            Class<?> target = relationTable.target();
            String primaryKey = relationTable.primaryKey();
            String tableName = target.getDeclaredAnnotation(TableName.class).value();
            String dictName = field.getDeclaredAnnotation(DictValue.class).value();
            List<String> list = data.select("$..dictId").toObjectList(String.class);
            List<Map<String, Object>> result = dictService.getDb().table(tableName).whereIn(primaryKey, list).selectMapList(primaryKey + "," + dictName);
            // 转成map字典
            HashMap<String, String> dictMap = new HashMap<>();
            for (Map<String, Object> map : result) {
                Object[] objects = map.values().toArray();
                dictMap.put((String) objects[0], (String) objects[1]);
            }
            // 获取字典值,并且设置
            String invoke = (String) dictHelper.declaredMethod.invoke(item);
            String value = dictMap.get(invoke);
            dictHelper.declaredMethodSet.invoke(item, value == null ? "" : value);
            data.set("data", ONode.load(item));

        }


    }

    @Override
    public void handleBatch(DictAop.DictHelper dictHelper, ONode data, DictService dictService, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {
        // 获取解析类中需要解析的字段
        Class<?> dictParseClass = dictHelper.dictParseClass;
        //  单关联表
        RelationTable relationTable = this.relationTable != null ? this.relationTable : dictParseClass.getDeclaredAnnotation(RelationTable.class);

        String key = dictHelper.key;
        List<?> item = data.select("$." + key).toObjectList(dictHelper.dictParseClass);
        if (relationTable != null) {
            Class<?> target = relationTable.target();
            String primaryKey = relationTable.primaryKey();
            String tableName = target.getDeclaredAnnotation(TableName.class).value();
            String dictName = field.getDeclaredAnnotation(DictValue.class).value();
            List<String> list = data.select("$..dictId").toObjectList(String.class);
            List<Map<String, Object>> result = dictService.getDb().table(tableName).whereIn(primaryKey, list).selectMapList(primaryKey + "," + dictName);
            // 转成map字典
            HashMap<String, String> dictMap = new HashMap<>();
            for (Map<String, Object> map : result) {
                Object[] objects = map.values().toArray();
                dictMap.put((String) objects[0], (String) objects[1]);
            }
            // 获取字典值,并且设置
            String invoke = (String) dictHelper.declaredMethod.invoke(item);
            String value = dictMap.get(invoke);
            dictHelper.declaredMethodSet.invoke(item, value == null ? "" : value);
            data.set("data", ONode.load(item));

        }
    }


}
