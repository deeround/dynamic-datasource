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

import javax.sql.DataSource;

/**
 * 抽象连接池创建器
 * <p>
 * 这里主要处理一些公共逻辑，如脚本和事件等
 *
 * @author TaoYu
 */
@Slf4j
public abstract class AbstractDataSourceCreator implements DataSourceCreator {

    /**
     * 子类去实际创建连接池
     *
     * @param dataSourceProperty 数据源信息
     * @return 实际连接池
     */
    public abstract DataSource doCreateDataSource(DataSourceProperty dataSourceProperty);

    @Override
    public DataSource createDataSource(DataSourceProperty dataSourceProperty) {
        DataSource dataSource = this.doCreateDataSource(dataSourceProperty);
        return dataSource;
    }
}
