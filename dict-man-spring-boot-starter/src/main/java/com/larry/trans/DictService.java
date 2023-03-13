package com.larry.trans;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


public class DictService {
    private static final ThreadLocal<Map<String, Map<String, String>>> dictType = new ThreadLocal<>();

    // 比如字典是id转换名称,不需要加类别,因为雪花id之类生成策略已经保证分布式下也唯一。
    public void putDictItem(String key, String value) {
        Map<String, String> dictMan = getDict("dictMan");
        if (dictMan != null && dictMan.size() > 0) {
            dictMan.put(key, value);
            putDictType("dictMan",dictMan );

        }else{
            putDictType("dictMan",new HashMap() {{
                put(key, value);
            }} );
        }

    }

    // 分类
    public void putDictType(String key, Map<String, String> item) {
        Map<String, Map<String, String>> stringMapMap = dictType.get();
        stringMapMap.put(key, item);
        dictType.set(stringMapMap);

    }

    public Map<String, String> getDict(String key) {
        Map<String, Map<String, String>> stringMapMap = dictType.get();
        return stringMapMap.get(key);
    }
}
