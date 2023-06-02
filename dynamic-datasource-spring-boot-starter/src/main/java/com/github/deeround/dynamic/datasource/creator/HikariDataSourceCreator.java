/*
 * Copyright © 2018 organization baomidou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.deeround.dynamic.datasource.creator;

import com.github.deeround.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.github.deeround.dynamic.datasource.utils.PropertyUtil;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Hikari数据源创建器
 *
 * @author TaoYu
 * @since 2020/1/21
 */
@Slf4j
public class HikariDataSourceCreator extends AbstractDataSourceCreator implements DataSourceCreator {

    private Properties globalConfig;

    public HikariDataSourceCreator(Properties globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public DataSource doCreateDataSource(DataSourceProperty dataSourceProperty) {
        HikariDataSource dataSource = new HikariDataSource();

        Properties properties = new Properties();
        if (this.globalConfig != null) {
            PropertyUtil.putAll(properties, this.globalConfig);
        }
        if (dataSourceProperty.getHikari() != null) {
            PropertyUtil.putAll(properties, dataSourceProperty.getHikari());
        }
        PropertyUtil.setTargetFromProperties(dataSource, properties);

        dataSource.setUsername(dataSourceProperty.getUsername());
        dataSource.setPassword(dataSourceProperty.getPassword());
        dataSource.setJdbcUrl(dataSourceProperty.getUrl());
        dataSource.setPoolName(dataSourceProperty.getPoolName());
        String driverClassName = dataSourceProperty.getDriverClassName();
        if (!StringUtils.isEmpty(driverClassName)) {
            dataSource.setDriverClassName(driverClassName);
        }

        dataSource.validate();

        return dataSource;
    }


}
