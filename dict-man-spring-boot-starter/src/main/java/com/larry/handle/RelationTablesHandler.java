package com.larry.handle;

import com.larry.service.DictService;
import com.larry.spring.DictAop;
import com.larry.trans.RelationTable;
import com.larry.trans.RelationTables;
import org.noear.snack.ONode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class RelationTablesHandler extends  HandleChain implements DictHandler{

    @Override
    public void handle(DictAop.DictHelper dictHelper, ONode data, DictService dictService, Field field) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, SQLException {
        // 获取解析类中需要解析的字段
        Class<?> dictParseClass = dictHelper.dictParseClass;
        // 多关联表
        RelationTables relationTables = dictParseClass.getDeclaredAnnotation(RelationTables.class);
        if(relationTables != null){
            RelationTable[] list = relationTables.value();
            RelationTableHandler handler = new RelationTableHandler();
            for (RelationTable relationTable : list) {
                handler.setRelationTable(relationTable);
                this.setNextHandleChain(handler);
                this.nextHandle(dictHelper,data,dictService, field);
            }

        }else{
            this.nextHandle(dictHelper,data,dictService, field);
        }

    }

    @Override
    public void handleBatch(DictAop.DictHelper dictHelper, ONode data, DictService dictService, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {

    }


}
