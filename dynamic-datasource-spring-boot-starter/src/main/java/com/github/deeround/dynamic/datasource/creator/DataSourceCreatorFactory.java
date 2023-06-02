package com.github.deeround.dynamic.datasource.creator;

import com.github.deeround.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.github.deeround.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;

import javax.sql.DataSource;

/**
 * @author wanghao 913351190@qq.com
 * @create 2023/3/13 14:55
 */
public class DataSourceCreatorFactory {
    public static DataSourceCreator createDataSourceCreator(DynamicDataSourceProperties dynamicDataSourceProperties, DataSourceProperty dataSourceProperty) {
        DataSourcePoolType dataSourcePoolType = getDataSourcePoolType(dynamicDataSourceProperties.getType(), dataSourceProperty.getType());
        switch (dataSourcePoolType) {
            case DRUID: {
                return new DruidDataSourceCreator(dynamicDataSourceProperties.getDruid());
            }
            case HIKARI: {
                return new HikariDataSourceCreator(dynamicDataSourceProperties.getHikari());
            }
            case BASIC:
            default: {
                return new BasicDataSourceCreator();
            }
        }
    }

    private static DataSourcePoolType getDataSourcePoolType(Class<? extends DataSource> globalType, Class<? extends DataSource> itemType) {
        if (globalType == null && itemType == null) {
            return DataSourcePoolType.BASIC;
        }
        Class<? extends DataSource> targetType = itemType != null ? itemType : globalType;
        String targetTypeName = targetType.getTypeName();
        if (targetTypeName.startsWith("com.alibaba.druid.")) {
            return DataSourcePoolType.DRUID;
        } else if (targetTypeName.startsWith("com.zaxxer.hikari.")) {
            return DataSourcePoolType.HIKARI;
        } else {
            return DataSourcePoolType.BASIC;
        }
    }
}
