package com.larry.handle;

import com.baomidou.mybatisplus.annotation.TableName;
import com.larry.service.DictService;
import com.larry.spring.DictAop;
import com.larry.trans.DictValue;
import com.larry.trans.RelationTable;
import org.noear.snack.ONode;
import org.noear.wood.utils.AssertUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationTableHandler extends HandleChain implements DictHandler {
    RelationTable relationTable = null;

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

    public RelationTable getRelationTable() {
        return relationTable;
    }

    public void setRelationTable(RelationTable relationTable) {
        this.relationTable = relationTable;
    }

    @Override
    public void handle(DictAop.DictHelper dictHelper, ONode data, DictService dictService, Field field) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, SQLException {
        // 获取响应数据
        Object item = data.select("$." + dictHelper.key).toObject(dictHelper.dictParseClass);
        if(item ==null){
            return;
        }
        // 获取字典值,并且设置
        HashMap<String, String> dictMap = this.getDictMap(dictHelper, data, field);
        String invoke = (String) dictHelper.declaredMethod.invoke(item);
        String value ;
        if(dictMap.size()==0){
            value = "";
        }else{
            value = dictMap.get(invoke);
        }
        String newKey = dictHelper.dictValue.newKey();
        if("".equals(newKey)){
            dictHelper.declaredMethodSet.invoke(item, value);
        }else{
            Map<String, String> tempMap = DictService.getTempMap();
            tempMap.put(newKey,value);
        }


        data.set("data", ONode.load(item));

    }

    @Override
    public void handleBatch(DictAop.DictHelper dictHelper, ONode data, DictService dictService, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {

        // 获取响应数据
        List<?> list = data.select("$." + dictHelper.key).toObjectList(dictHelper.dictParseClass);

        // 获取字典值,并且设置数据
        HashMap<String, String> dictMap = this.getDictMap(dictHelper, data, field);
        if(dictMap.size()==0){
            return;
        }
        for (Object item : list) {
            String invoke = (String) dictHelper.declaredMethod.invoke(item);
            String value = dictMap.get(invoke);
            dictHelper.declaredMethodSet.invoke(item, value == null ? "" : value);
        }

        setData(data, dictHelper.key, list);
    }

    private HashMap<String, String> getDictMap(DictAop.DictHelper dictHelper, ONode data, Field field) throws SQLException {

        String tableName = getTableName();
        String primaryKey = relationTable.primaryKey();

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

    private String getTableName() {
        Class<?> target = relationTable.target();
        String ormTag = "";
        String tableName ;
        switch (ormTag){
            case "jpa":
//                tableName = target.getDeclaredAnnotation(Table.class).name();
                tableName = "";
                break;
            default:
                // 默认是mybatis
                tableName = target.getDeclaredAnnotation(TableName.class).value();
        }
        AssertUtils.notEmpty(tableName,"关联表的表名未设置");
        return  tableName;
    }

}
