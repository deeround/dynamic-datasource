package com.github.deeround.dynamic.datasource.spring.boot.autoconfigure;

import com.github.deeround.dynamic.datasource.DynamicRoutingDataSource;
import com.github.deeround.dynamic.datasource.annotation.DS;
import com.github.deeround.dynamic.datasource.annotation.DSTransactional;
import com.github.deeround.dynamic.datasource.aop.DynamicDataSourceAnnotationAdvisor;
import com.github.deeround.dynamic.datasource.aop.DynamicDataSourceAnnotationInterceptor;
import com.github.deeround.dynamic.datasource.aop.DynamicLocalTransactionInterceptor;
import com.github.deeround.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.github.deeround.dynamic.datasource.provider.YmlDynamicDataSourceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author wanghao 913351190@qq.com
 * @create 2023/2/23 16:52
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@AutoConfigureBefore(value = DataSourceAutoConfiguration.class, name = "com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure")
@ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class DynamicDataSourceAutoConfiguration {

    @Autowired
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @Bean
    @Order(0)
    public DynamicDataSourceProvider ymlDynamicDataSourceProvider() {
        return new YmlDynamicDataSourceProvider(this.dynamicDataSourceProperties);
    }

    @Bean
    public DynamicRoutingDataSource dynamicRoutingDataSource() {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        dataSource.setPrimary(this.dynamicDataSourceProperties.getPrimary());
        return dataSource;
    }

    @Bean
    public Advisor dynamicDatasourceAnnotationAdvisor() {
        DynamicDataSourceAnnotationInterceptor interceptor = new DynamicDataSourceAnnotationInterceptor();
        DynamicDataSourceAnnotationAdvisor advisor = new DynamicDataSourceAnnotationAdvisor(interceptor, DS.class);
        advisor.setOrder(Integer.MIN_VALUE);
        return advisor;
    }

    @Bean
    public Advisor dynamicTransactionAdvisor() {
        DynamicLocalTransactionInterceptor interceptor = new DynamicLocalTransactionInterceptor();
        return new DynamicDataSourceAnnotationAdvisor(interceptor, DSTransactional.class);
    }

}
