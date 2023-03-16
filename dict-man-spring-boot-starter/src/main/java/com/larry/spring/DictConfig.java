package com.larry.spring;

import com.larry.service.DictService;
import org.noear.wood.DbContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class DictConfig {

    @Value("${dictman.primaryKey}")
    private String primaryKey;
    @Value("${dictman.resultList}")
    private String resultList;

    @Value("${spring.datasource.username}")
    private String name;

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    // 初始化字典服务
    @Bean
    @Primary
    public DictService mybatisDictService() {
        DictService dictService = new DictService();
        DbContext db  = new DbContext("study",url,username,password);
        dictService.setDb(db);
        return dictService;
    }

    // 切面
    @Bean
    public DictAop dictAop() {
        return new DictAop();
    }
}
