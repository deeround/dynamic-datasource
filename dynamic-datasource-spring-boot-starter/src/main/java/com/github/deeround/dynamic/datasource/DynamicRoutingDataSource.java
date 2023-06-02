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
package com.github.deeround.dynamic.datasource;

import com.github.deeround.dynamic.datasource.ds.AbstractRoutingDataSource;
import com.github.deeround.dynamic.datasource.provider.DynamicDataSourceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 核心动态数据源组件
 *
 * @author TaoYu Kanyuxia
 * @since 1.0.0
 */
@Slf4j
public class DynamicRoutingDataSource extends AbstractRoutingDataSource implements InitializingBean, DisposableBean {

    /**
     * 所有数据库
     */
    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    private String primary;

    @Override
    public String getPrimary() {
        return this.primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    @Override
    public DataSource determineDataSource() {
        String dsKey = DynamicDataSourceContextHolder.peek();
        if (StringUtils.isEmpty(dsKey)) {
            dsKey = this.primary;
            log.debug("dynamic-datasource current datasource is null switch primary datasource named [{}]", this.primary);
        }
        DataSource dataSource = this.getDataSource(dsKey);
        log.debug("dynamic-datasource current datasource named [{}]", dsKey);
        if (dataSource == null) {
            throw new RuntimeException("dynamic-datasource can not find datasource named [" + dsKey + "]");
        }
        return dataSource;
    }


    private DataSource determinePrimaryDataSource() {
        return this.getDataSource(this.primary);
    }

    /**
     * 获取所有的数据源
     *
     * @return 当前所有数据源
     */
    public Map<String, DataSource> getDataSources() {
        return this.dataSourceMap;
    }

    /**
     * 获取数据源
     *
     * @param ds 数据源名称
     * @return 数据源
     */
    public DataSource getDataSource(String ds) {
        if (this.dataSourceMap.containsKey(ds)) {
            return this.dataSourceMap.get(ds);
        }
        return null;
    }

    /**
     * 添加数据源
     *
     * @param ds         数据源名称
     * @param dataSource 数据源
     */
    public synchronized void addDataSource(String ds, DataSource dataSource) {
        log.info("dynamic-datasource add datasource named [{}]", ds);
        DataSource oldDataSource = this.dataSourceMap.put(ds, dataSource);
        // 关闭老的数据源
        if (oldDataSource != null) {
            this.closeDataSource(ds, oldDataSource);
        }
    }

    /**
     * 删除数据源
     *
     * @param ds 数据源名称
     */
    public synchronized void removeDataSource(String ds) {
        log.info("dynamic-datasource remove datasource named [{}]", ds);
        if (this.dataSourceMap.containsKey(ds)) {
            DataSource dataSource = this.dataSourceMap.remove(ds);
            this.closeDataSource(ds, dataSource);
        }
    }

    /**
     * close db
     *
     * @param ds         dsName
     * @param dataSource db
     */
    private void closeDataSource(String ds, DataSource dataSource) {
        try {
            Method closeMethod = ReflectionUtils.findMethod(dataSource.getClass(), "close");
            if (closeMethod != null) {
                closeMethod.invoke(dataSource);
            }
        } catch (Exception e) {
            log.error("dynamic-datasource closed datasource named [{}] failed", ds, e);
        }
    }

    @Override
    public void destroy() {
        for (Map.Entry<String, DataSource> item : this.dataSourceMap.entrySet()) {
            this.closeDataSource(item.getKey(), item.getValue());
        }
    }

    @Autowired
    private List<DynamicDataSourceProvider> providers;

    @Override
    public void afterPropertiesSet() {
        log.info("dynamic-datasource initialization starting...");
        LinkedHashMap<String, DataSource> dataSources = new LinkedHashMap<>(16);
        for (DynamicDataSourceProvider provider : this.providers) {
            LinkedHashMap<String, DataSource> dataSourceMap = provider.loadDataSources();
            log.info("dynamic-datasource provider {} loadDataSources {}", provider.getClass().getName(), dataSourceMap != null ? dataSourceMap.keySet() : null);
            if (dataSourceMap != null) {
                dataSources.putAll(dataSourceMap);
            }
        }
        if (dataSources.size() > 0) {
            for (Map.Entry<String, DataSource> dsItem : dataSources.entrySet()) {
                this.addDataSource(dsItem.getKey(), dsItem.getValue());
            }
        } else {
            log.warn("dynamic-datasource not load any datasource");
        }
        log.info("dynamic-datasource initialization completed.");
    }
}