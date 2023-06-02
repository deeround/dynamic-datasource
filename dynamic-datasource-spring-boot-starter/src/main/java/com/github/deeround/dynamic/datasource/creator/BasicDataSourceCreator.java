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
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

/**
 * 基础数据源创建器
 *
 * @author TaoYu
 * @since 2020/1/21
 */
@Slf4j
public class BasicDataSourceCreator extends AbstractDataSourceCreator implements DataSourceCreator {
    /**
     * 创建基础数据源
     *
     * @param dataSourceProperty 数据源参数
     * @return 数据源
     */
    @Override
    public DataSource doCreateDataSource(DataSourceProperty dataSourceProperty) {
        DataSourceBuilder<?> builder = DataSourceBuilder.create();
        builder.type(dataSourceProperty.getType());
        builder.driverClassName(dataSourceProperty.getDriverClassName());
        builder.username(dataSourceProperty.getUsername());
        builder.password(dataSourceProperty.getPassword());
        builder.url(dataSourceProperty.getUrl());
        return builder.build();
    }
}
