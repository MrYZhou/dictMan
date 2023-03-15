package com.larry.handle;

import com.larry.service.DictService;
import com.larry.spring.DictAop;
import org.noear.snack.ONode;

import java.lang.reflect.InvocationTargetException;

public interface DictHandler {

    public void handle(DictAop.DictHelper dictHelper, ONode data, DictService dictService) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}
