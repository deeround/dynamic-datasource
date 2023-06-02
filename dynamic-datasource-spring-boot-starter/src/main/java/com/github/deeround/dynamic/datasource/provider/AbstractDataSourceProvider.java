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

import com.github.deeround.dynamic.datasource.creator.DataSourceCreatorFactory;
import com.github.deeround.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.github.deeround.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author TaoYu
 */
@Slf4j
public abstract class AbstractDataSourceProvider implements DynamicDataSourceProvider {

    protected DynamicDataSourceProperties dynamicDataSourceProperties;

    public AbstractDataSourceProvider(DynamicDataSourceProperties dynamicDataSourceProperties) {
        this.dynamicDataSourceProperties = dynamicDataSourceProperties;
    }

    protected LinkedHashMap<String, DataSource> createDataSourceMap(LinkedHashMap<String, DataSourceProperty> dataSourcePropertiesMap) {
        LinkedHashMap<String, DataSource> dataSourceMap = new LinkedHashMap<>(16);
        for (Map.Entry<String, DataSourceProperty> item : dataSourcePropertiesMap.entrySet()) {
            String dsName = item.getKey();
            DataSourceProperty dataSourceProperty = item.getValue();
            String poolName = dataSourceProperty.getPoolName();
            if (StringUtils.isEmpty(poolName)) {
                poolName = dsName;
            }
            dataSourceProperty.setPoolName(poolName);
            dataSourceMap.put(dsName, DataSourceCreatorFactory.createDataSourceCreator(this.dynamicDataSourceProperties, dataSourceProperty).createDataSource(dataSourceProperty));
        }
        return dataSourceMap;
    }
}
