package com.larry.spring;

import com.larry.service.MybatisDictService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
    @ConditionalOnClass(SqlSessionFactory.class)
    public MybatisDictService mybatisDictService() {
        return new MybatisDictService();
    }
    // 切面
    @Bean
    public DictAop dictAop() {
        return new DictAop();
    }
}
