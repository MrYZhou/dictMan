package com.larry.service;

import org.noear.wood.DbContext;

import java.util.HashMap;
import java.util.Map;


public class DictService {
    private final Map<String, Map<String, String>> dictType = new HashMap<>();
    Map<String, DbContext> db = new HashMap<>();

    public Map<String, DbContext> getDb() {
        return db;
    }

    public void setDb(Map<String, DbContext> db) {
        this.db = db;
    }

    public DbContext getDb(String tag) {
        return db.get(tag);
    }

    // 比如字典是id转换名称,不需要加类别,因为雪花id之类生成策略已经保证分布式下也唯一。
    public void putDictItem(String key, String value) {
        Map<String, String> dictMan = dictType.get("dictMan");
        if (dictMan == null) {
            dictMan = new HashMap<>();
        }
        dictMan.put(key, value);
        putDictType("dictMan", dictMan);
    }

    // 分类
    public void putDictType(String key, Map<String, String> item) {
        dictType.put(key, item);

    }

    public Map<String, String> getDict(String key) {
        Map<String, String> map = dictType.get(key);
        if (map == null) map = new HashMap<>();
        return map;
    }


}

