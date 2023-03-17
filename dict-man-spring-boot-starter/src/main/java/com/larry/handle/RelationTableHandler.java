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
//            Class<?> target = relationTable.target();
//            String primaryKey = relationTable.primaryKey();
//            String tableName = target.getDeclaredAnnotation(TableName.class).value();
//            String dictName = field.getDeclaredAnnotation(DictValue.class).value();
//            List<String> keylist = data.select("$..dictId").toObjectList(String.class);
//            List<Map<String, Object>> result = dictHelper.dbContext.table(tableName).whereIn(primaryKey, keylist).selectMapList(primaryKey + "," + dictName);
//            // 转成map字典
//            HashMap<String, String> dictMap = new HashMap<>();
//            for (Map<String, Object> map : result) {
//                Object[] objects = map.values().toArray();
//                dictMap.put((String) objects[0], (String) objects[1]);
//            }
            HashMap<String, String> dictMap  = this.getDictMap(dictHelper,data,field);
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
        if(relationTable==null){
            return;
        }
        // 获取响应数据
        String key = dictHelper.key;
        List<?> list = data.select("$." + key).toObjectList(dictHelper.dictParseClass);


        // 获取字典值,并且设置数据
        HashMap<String, String> dictMap  = this.getDictMap(dictHelper,data,field);
        for (Object item1 : list) {
            String invoke = (String) dictHelper.declaredMethod.invoke(item1);
            String value = dictMap.get(invoke);
            dictHelper.declaredMethodSet.invoke(item1, value == null ? "" : value);
        }

        setData(data, key, list);
    }

    private HashMap<String, String> getDictMap(DictAop.DictHelper dictHelper, ONode data,  Field field) throws SQLException {
        Class<?> target = relationTable.target();
        String primaryKey = relationTable.primaryKey();
        String tableName = target.getDeclaredAnnotation(TableName.class).value();
        String dictName = field.getDeclaredAnnotation(DictValue.class).value();
        List<String> keylist = data.select("$..dictId").toObjectList(String.class);
        List<Map<String, Object>> result = dictHelper.dbContext.table(tableName).whereIn(primaryKey, keylist).selectMapList(primaryKey + "," + dictName);
        // 转成map字典
        HashMap<String, String> dictMap = new HashMap<>();
        for (Map<String, Object> map : result) {
            Object[] objects = map.values().toArray();
            dictMap.put((String) objects[0], (String) objects[1]);
        }
        return dictMap;
    }

    public static void setData(ONode data, String key, List<?> list) {
        int index = -1;
        for (int i = key.length() - 1; i > 0; i--) {
            char c = key.charAt(i);
            if (c == '.') {
                index = i;
            }
        }
        String path = key.substring(0, index);
        String dataKey = key.substring(index + 1);
        data.select("$." + path).set(dataKey, ONode.load(list));
    }


}
