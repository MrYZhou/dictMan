package com.larry.spring;

import com.larry.service.DictService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class DictConfig {

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
        return new DictService();
    }

    // 切面
    @Bean
    public DictAop dictAop() {
        return new DictAop();
    }
}
