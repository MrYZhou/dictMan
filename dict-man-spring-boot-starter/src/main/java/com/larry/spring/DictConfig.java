package com.larry.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class DictConfig {

    @Value("ref")
    private String ref;

    // 初始化字典服务
    @Bean
    @Primary
    public MybatisDictService dictService() {

        return new MybatisDictService();
    }
    // 切面
    @Bean
    public DictAop dictAop() {
        return new DictAop();
    }
}
