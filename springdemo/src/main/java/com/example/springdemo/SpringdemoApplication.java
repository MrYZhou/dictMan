package com.example.springdemo;

import com.larry.spring.MybatisDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class SpringdemoApplication implements ApplicationListener<ApplicationStartedEvent> {
    //    // 初始化字典的数据
    @Autowired
    MybatisDictService dictService;

    public static void main(String[] args) {
        SpringApplication.run(SpringdemoApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        Map<String, String> transMap = new HashMap<>();
        transMap.put("0", "书籍1");
        transMap.put("1", "书籍2");
        dictService.putDictType("book", transMap);
    }

}
