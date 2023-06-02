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
package com.github.deeround.dynamic.datasource.provider;

import com.github.deeround.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.LinkedHashMap;

/**
 * YML数据源提供者
 *
 * @author TaoYu Kanyuxia
 * @since 1.0.0
 */
@Slf4j
public class YmlDynamicDataSourceProvider extends AbstractDataSourceProvider {

    public YmlDynamicDataSourceProvider(DynamicDataSourceProperties dynamicDataSourceProperties) {
        super(dynamicDataSourceProperties);
    }

    @Override
    public LinkedHashMap<String, DataSource> loadDataSources() {
        return this.createDataSourceMap(this.dynamicDataSourceProperties.getDatasource());
    }
}
