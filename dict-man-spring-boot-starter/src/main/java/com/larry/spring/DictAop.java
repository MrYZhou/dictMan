package com.larry.spring;

import com.larry.handle.HandleChain;
import com.larry.handle.RelationTableHandler;
import com.larry.handle.RelationTablesHandler;
import com.larry.handle.SimpleDataHandler;
import com.larry.service.DictService;
import com.larry.trans.DictMany;
import com.larry.trans.DictOne;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.noear.snack.ONode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Aspect
@Component
public class DictAop {

    static DictHelper dictHelper = new DictHelper();
    static HandleChain chain = null;

    static {
        chain = new SimpleDataHandler();
        HandleChain chain2 = chain.setNextHandleChain(new RelationTablesHandler());
        chain2.setNextHandleChain(new RelationTableHandler());

    }

    @Autowired
    DictService dictService;

    @Around("@annotation(com.larry.trans.DictOne)")
    public Object transOne(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed;
        try {
            ONode data = ONode.load(joinPoint.proceed());

            dictHelper.initParserClass(joinPoint, "1");
            // 获取解析类中需要解析的字段
            Field[] declaredFields = dictHelper.dictParseClass.getDeclaredFields();
            for (Field field : declaredFields) {
                chain.handle(dictHelper, data , dictService,field);
            }

            proceed = ONode.deserialize(ONode.stringify(data), dictHelper.returnType);

        } catch (Throwable e) {
            throw new Exception("解析失败");
        }
        return proceed;
    }

    @Around("@annotation(com.larry.trans.DictMany)")
    public Object transMany(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed;
        try {
            ONode data = ONode.load(joinPoint.proceed());

            dictHelper.initParserClass(joinPoint, "2");
            // 获取解析类中需要解析的字段
            Field[] declaredFields = dictHelper.dictParseClass.getDeclaredFields();
            for (Field field : declaredFields) {
                chain.handleBatch(dictHelper, data , dictService,field);
            }

            proceed = ONode.deserialize(ONode.stringify(data), dictHelper.returnType);

        } catch (Throwable e) {
            throw new Exception("解析失败");
        }
        return proceed;
    }

    public static class DictHelper {
        public ProceedingJoinPoint joinPoint;

        public Class<?> dictParseClass;

        public Class<?> returnType;

        public String key;

        public void initParserClass(ProceedingJoinPoint joinPoint, String type) throws NoSuchMethodException, InstantiationException, IllegalAccessException {
            this.joinPoint = joinPoint;
            Class<?> targetCls = joinPoint.getTarget().getClass();
            // 得到当前方法签名上面的解析类
            MethodSignature ms = (MethodSignature) joinPoint.getSignature();
            Method targetMethod = targetCls.getDeclaredMethod(ms.getName(), ms.getParameterTypes());
            Class<?> dictClass;
            String key;

            // 获取解析类
            if ("1".equals(type)) {
                DictOne annotation = targetMethod.getAnnotation(DictOne.class);
                key = annotation.key();
                dictClass = annotation.value();
            } else {
                DictMany annotation = targetMethod.getAnnotation(DictMany.class);
                key = annotation.key();
                dictClass = annotation.value();
            }
            this.key = key;
            this.dictParseClass = dictClass;
            this.returnType = targetMethod.getReturnType();
        }

        public ProceedingJoinPoint getJoinPoint() {
            return joinPoint;
        }

        public void setJoinPoint(ProceedingJoinPoint joinPoint) {
            this.joinPoint = joinPoint;
        }
    }

}
