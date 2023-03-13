package com.larry.spring;

import com.larry.trans.DictMany;
import com.larry.trans.DictOne;
import com.larry.trans.DictService;
import com.larry.trans.DictValue;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.noear.snack.ONode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class DictAop {

    static DictHelper dictHelper = new DictHelper();
    @Autowired
    DictService dictService;

    @Around("@annotation(com.larry.trans.DictOne)")
    public Object transOne(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed;
        try {
            dictHelper.initParserClass(joinPoint, "1");

            // 获取解析类中需要解析的字段
            Class<?> returnType = dictHelper.returnType;
            Class<?> dictParseClass = dictHelper.dictParseClass;
            Field[] declaredFields = dictParseClass.getDeclaredFields();
            proceed = joinPoint.proceed();
            ONode data = ONode.load(proceed);
            String key = dictHelper.key;
            Object item = data.select("$." + key).toObject(dictParseClass);
            Map<String, String> dictMap;
            for (Field field : declaredFields) {

                DictValue annotation = field.getAnnotation(DictValue.class);
                if (annotation == null) {
                    continue;
                }
                String ref = annotation.ref();
                dictMap = dictService.getDict(ref);
                // 字段名
                String name = field.getName();
                // 获取方法名
                Method declaredMethod = dictParseClass.getDeclaredMethod("get" + StringUtils.capitalize(name));
                Method declaredMethodSet = dictParseClass.getDeclaredMethod("set" + StringUtils.capitalize(name), String.class);
                // 获取字典值,并且设置
                String invoke = (String) declaredMethod.invoke(item);
                if(dictMap == null) dictMap = new HashMap<>();
                String value = dictMap.get(invoke);
                declaredMethodSet.invoke(item, value == null ? "" : value);
            }
            // 设置数据
            data.set("data", ONode.load(item));
            proceed = ONode.deserialize(ONode.stringify(data), returnType);

        } catch (Throwable e) {
            throw new Exception("解析失败");
        }
        return proceed;
    }

    @Around("@annotation(com.larry.trans.DictMany)")
    public Object transMany(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed;
        try {

            dictHelper.initParserClass(joinPoint, "2");

            // 获取解析类中需要解析的字段
            Class<?> returnType = dictHelper.returnType;
            Class<?> dictParseClass = dictHelper.dictParseClass;
            String key = dictHelper.key;
            Field[] declaredFields = dictParseClass.getDeclaredFields();

            proceed = joinPoint.proceed();

            ONode data = ONode.load(proceed);
            List<?> list1 = data.select("$." + key).toObjectList(dictParseClass);
            Map<String, String> dictMap;
            for (Field field : declaredFields) {
                if (!field.isAnnotationPresent(DictValue.class)) {
                    continue;
                }
                DictValue annotation = field.getAnnotation(DictValue.class);
                String ref = annotation.ref();
                dictMap = dictService.getDict(ref);
                // 字段名
                String name = field.getName();
                // 获取方法
                Method declaredMethod = dictParseClass.getDeclaredMethod("get" + StringUtils.capitalize(name));
                Method declaredMethodSet = dictParseClass.getDeclaredMethod("set" + StringUtils.capitalize(name), String.class);

                if(dictMap == null) dictMap = new HashMap<>();
                // 获取字典值,并且设置
                for (Object item : list1) {
                    String invoke = (String) declaredMethod.invoke(item);
                    String value = dictMap.get(invoke);
                    declaredMethodSet.invoke(item, value == null ? "" : value);
                }
            }


            // 设置数据
            int index = -1;
            for (int i = key.length() - 1; i > 0; i--) {
                char c = key.charAt(i);
                if (c == '.') {
                    index = i;
                }
            }
            String path = key.substring(0, index);
            String dataKey = key.substring(index + 1);

            data.select("$." + path).set(dataKey, ONode.load(list1));
            proceed = ONode.deserialize(ONode.stringify(data), returnType);

        } catch (Throwable e) {
            throw new Exception("解析失败");
        }
        return proceed;
    }

    static class DictHelper {
        ProceedingJoinPoint joinPoint;

        Class<?> dictParseClass;

        Class<?> returnType;

        String key;

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
