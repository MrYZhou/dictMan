package com.larry.spring;

import com.larry.handle.HandleChain;
import com.larry.handle.RelationTableHandler;
import com.larry.handle.RelationTablesHandler;
import com.larry.handle.SimpleDataHandler;
import com.larry.service.DictService;
import com.larry.trans.DictContext;
import com.larry.trans.DictMany;
import com.larry.trans.DictOne;
import com.larry.trans.DictValue;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.wood.DbContext;
import org.noear.wood.utils.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class DictAop {

    static DictHelper dictHelper = new DictHelper();
    static HandleChain chain = null;
    private static String primaryKey;
    private static String resultKey;

    static {
        chain = new SimpleDataHandler();
        HandleChain chain2 = chain.setNextHandleChain(new RelationTablesHandler());
        chain2.setNextHandleChain(new RelationTableHandler());

    }

    @Autowired
    DictService dictService;


    @Value("${dictman.primaryKey:id}")
    public void setPrimaryKey(String value) {
        primaryKey = value;
    }

    @Value("${dictman.resultKey:data.list}")
    public void setResultKey(String value) {
        resultKey = value;
    }

    private final Options opts = Options.def().add(Feature.SerializeNulls);

    @Around("@annotation(com.larry.trans.DictOne)")
    public Object transOne(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed;
        try {

            ONode data = ONode.load(joinPoint.proceed(), opts);

            dictHelper.initParserClass(joinPoint, dictService, "1");
            // 获取解析类中需要解析的字段
            Field[] declaredFields = dictHelper.dictParseClass.getDeclaredFields();
            for (Field field : declaredFields) {
                DictValue annotation = field.getAnnotation(DictValue.class);
                if (annotation == null) {
                    continue;
                }
                dictHelper.setDictValue(annotation);
                // 字段名
                String name = field.getName();
                // 获取方法名
                Method declaredMethod = dictHelper.dictParseClass.getDeclaredMethod("get" + StringUtils.capitalize(name));
                Method declaredMethodSet = dictHelper.dictParseClass.getDeclaredMethod("set" + StringUtils.capitalize(name), String.class);
                dictHelper.setDeclaredMethod(declaredMethod);
                dictHelper.setDeclaredMethodSet(declaredMethodSet);
                chain.handle(dictHelper, data, dictService, field);
            }

            // 设置新key
            Map<String, String> tempMap = DictService.getTempMap();
            if (tempMap.size() > 0) {
                Object item = data.select("$." + dictHelper.key).toObject(dictHelper.dictParseClass);
                ONode load = ONode.load(item);
                Map o = (Map) load.toData();
                tempMap.forEach((k, v) -> {
                    o.put(k, v);
                });
                data.set("data", ONode.load(o));
            }

            proceed = ONode.deserialize(ONode.load(data, opts).toJson(), dictHelper.returnType);

        } catch (Throwable e) {
            throw new Exception("解析失败:" + e.getMessage());
        }
        return proceed;
    }

    @Around("@annotation(com.larry.trans.DictMany)")
    public Object transMany(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed;
        try {
            ONode data = ONode.load(joinPoint.proceed());

            dictHelper.initParserClass(joinPoint, dictService, "2");
            // 获取解析类中需要解析的字段
            Field[] declaredFields = dictHelper.dictParseClass.getDeclaredFields();
            for (Field field : declaredFields) {
                DictValue annotation = field.getAnnotation(DictValue.class);
                if (annotation == null) {
                    continue;
                }
                dictHelper.setDictValue(annotation);
                // 字段名
                String name = field.getName();
                // 获取方法名
                Method declaredMethod = dictHelper.dictParseClass.getDeclaredMethod("get" + StringUtils.capitalize(name));
                Method declaredMethodSet = dictHelper.dictParseClass.getDeclaredMethod("set" + StringUtils.capitalize(name), String.class);
                dictHelper.setDeclaredMethod(declaredMethod);
                dictHelper.setDeclaredMethodSet(declaredMethodSet);
                chain.handleBatch(dictHelper, data, dictService, field);
            }

            // 设置新key
            List<?> list = dictService.getResultList();
            if (list.size() > 0) {
                List objects = new ArrayList();
                List<?> result = data.select("$." + dictHelper.key).toObjectList(dictHelper.dictParseClass);
                for (int i = 0; i < result.size(); i++) {
                    Object item = result.get(i);
                    ONode load = ONode.load(item);
                    Map o = (Map) load.toData();

                    // 获取对应记录顺序的属性
                    Object o1 = list.get(i);
                    Map<String, String> map = (Map<String, String>) o1;
                    map.forEach((k, v) -> {
                        o.put(k, v);
                    });
                    objects.add(ONode.load(o));
                }
                RelationTableHandler.setData(data, dictHelper.key, objects);
            }

            proceed = ONode.deserialize(ONode.load(data, opts).toJson(), dictHelper.returnType);

        } catch (Throwable e) {
            throw new Exception("解析失败:" + e.getMessage());
        }
        return proceed;
    }

    public static class DictHelper {
        public Map<String, String> dictMap;

        public DbContext dbContext;

        public DictValue dictValue;
        public Method declaredMethod;
        public Method declaredMethodSet;

        public ProceedingJoinPoint joinPoint;

        public Class<?> dictParseClass;

        public Class<?> returnType;

        public String key;
        public static String primaryKey;
        DictService dictService;

        public void initParserClass(ProceedingJoinPoint joinPoint, DictService dictService, String type) throws NoSuchMethodException, InstantiationException, IllegalAccessException {

            Class<?> targetCls = joinPoint.getTarget().getClass();
            // 得到当前方法签名上面的解析类
            MethodSignature ms = (MethodSignature) joinPoint.getSignature();
            Method targetMethod = targetCls.getDeclaredMethod(ms.getName(), ms.getParameterTypes());

            // 配置数据源
            DictContext dbSource = targetMethod.getAnnotation(DictContext.class);
            dbContext = dictService.getDb(dbSource == null ? "main" : dbSource.value());
            AssertUtils.notNull(dbContext, "数据源不存在");

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
                // 如果是空的,取配置文件属性
                if ("".equals(key)) {
                    key = resultKey;
                }
                //还为空,则处理为data.list
                if ("".equals(key)) {
                    key = "data.list";
                }
                dictClass = annotation.value();
            }
            DictHelper.primaryKey = DictAop.primaryKey;
            // 清空缓存的key
            DictService.getTempMap().clear();
            DictService.resultList = new ArrayList<>();

            this.key = key;
            this.dictParseClass = dictClass;
            this.joinPoint = joinPoint;
            this.dictService = dictService;
            this.returnType = targetMethod.getReturnType();
        }


        public ProceedingJoinPoint getJoinPoint() {
            return joinPoint;
        }

        public void setJoinPoint(ProceedingJoinPoint joinPoint) {
            this.joinPoint = joinPoint;
        }

        public DictService getDictService() {
            return dictService;
        }

        public void setDictService(DictService dictService) {
            this.dictService = dictService;
        }

        public DictValue getDictValue() {
            return dictValue;
        }

        public void setDictValue(DictValue dictValue) {
            this.dictValue = dictValue;
            String ref = dictValue.ref();
            dictMap = dictService.getDict(ref);
            if (dictMap == null) dictMap = new HashMap<>();
        }

        public Method getDeclaredMethod() {
            return declaredMethod;
        }

        public void setDeclaredMethod(Method declaredMethod) {
            this.declaredMethod = declaredMethod;
        }

        public Method getDeclaredMethodSet() {
            return declaredMethodSet;
        }

        public void setDeclaredMethodSet(Method declaredMethodSet) {
            this.declaredMethodSet = declaredMethodSet;
        }

        public Map<String, String> getDictMap() {
            return dictMap;
        }

        public void setDictMap(Map<String, String> dictMap) {
            this.dictMap = dictMap;
        }

        public DbContext getDbContext() {
            return dbContext;
        }

        public void setDbContext(DbContext dbContext) {
            this.dbContext = dbContext;
        }
    }

}
