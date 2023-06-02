package com.github.deeround.dynamic.datasource.spring.boot.autoconfigure;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * @author wanghao 913351190@qq.com
 * @create 2023/2/23 17:29
 */
@ConfigurationProperties(prefix = DynamicDataSourceProperties.PREFIX)
@Slf4j
@Data
public class DynamicDataSourceProperties {

    public static final String PREFIX = "spring.datasource.dynamic";

    /**
     * 必须设置默认的库,默认master
     */
    private String primary = "master";
    /**
     * 连接池类型
     */
    private Class<? extends DataSource> type;
    /**
     * 每一个数据源
     */
    private LinkedHashMap<String, DataSourceProperty> datasource = new LinkedHashMap<>();
    /**
     * hikari 连接池全局参数
     */
    @NestedConfigurationProperty
    private Properties hikari = new Properties();
    /**
     * druid 连接池全局参数
     */
    @NestedConfigurationProperty
    private Properties druid = new Properties();
}
