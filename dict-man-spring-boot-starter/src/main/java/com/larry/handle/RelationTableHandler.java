package com.larry.handle;

import com.larry.service.DictService;
import com.larry.spring.DictAop;
import com.larry.trans.RelationTable;
import com.larry.trans.RelationTables;
import org.noear.snack.ONode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class RelationTableHandler extends  HandleChain implements DictHandler{

    @Override
    public void handle(DictAop.DictHelper dictHelper, ONode data, DictService dictService) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        // 获取解析类中需要解析的字段
        Class<?> returnType = dictHelper.returnType;
        Class<?> dictParseClass = dictHelper.dictParseClass;
        Field[] declaredFields = dictParseClass.getDeclaredFields();

        // 多关联表
        RelationTables relationTables = dictParseClass.getDeclaredAnnotation(RelationTables.class);
        if(relationTables ==null){
             this.nextHandle(dictHelper,data,dictService);
        }
        // 单关联表
        RelationTable relationTable = dictParseClass.getDeclaredAnnotation(RelationTable.class);
        Class<?> target = relationTable.target();
    }
}
