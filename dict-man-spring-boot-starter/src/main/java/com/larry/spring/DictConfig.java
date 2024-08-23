package com.larry.spring;

import com.larry.service.DictService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class DictConfig {

    // 初始化字典服务
    @Bean
    @Primary
    public DictService dictService() {
        return new DictService();
    }

}
