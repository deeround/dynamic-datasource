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

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.*;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallFilter;
import com.github.deeround.dynamic.datasource.creator.druid.DruidLogConfigUtil;
import com.github.deeround.dynamic.datasource.creator.druid.DruidStatConfigUtil;
import com.github.deeround.dynamic.datasource.creator.druid.DruidWallConfigUtil;
import com.github.deeround.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.github.deeround.dynamic.datasource.utils.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Hikari数据源创建器
 *
 * @author TaoYu
 * @since 2020/1/21
 */
@Slf4j
public class DruidDataSourceCreator extends AbstractDataSourceCreator implements DataSourceCreator {

    private Properties globalConfig;

    public DruidDataSourceCreator(Properties globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public DataSource doCreateDataSource(DataSourceProperty dataSourceProperty) {
        DruidDataSource dataSource = new DruidDataSource();

        Properties properties = new Properties();
        if (this.globalConfig != null) {
            PropertyUtil.putAll(properties, this.globalConfig);
        }
        if (dataSourceProperty.getDruid() != null) {
            PropertyUtil.putAll(properties, dataSourceProperty.getDruid());
        }

        //filters
        List<Filter> proxyFilters = this.initFilters(dataSourceProperty);
        dataSource.setProxyFilters(proxyFilters);
        PropertyUtil.remove("filters", properties);
        PropertyUtil.remove("filter", properties);

        PropertyUtil.setTargetFromProperties(dataSource, properties);

        dataSource.setUsername(dataSourceProperty.getUsername());
        dataSource.setPassword(dataSourceProperty.getPassword());
        dataSource.setUrl(dataSourceProperty.getUrl());
        dataSource.setName(dataSourceProperty.getPoolName());
        String driverClassName = dataSourceProperty.getDriverClassName();
        if (!StringUtils.isEmpty(driverClassName)) {
            dataSource.setDriverClassName(driverClassName);
        }

        return dataSource;
    }

    private List<Filter> initFilters(DataSourceProperty dataSourceProperty) {
        String filters = PropertyUtil.getStr("filters", dataSourceProperty.getDruid());
        if (StringUtils.isEmpty(filters)) {
            filters = PropertyUtil.getStr("filters", this.globalConfig);
        }
        List<Filter> proxyFilters = new ArrayList<>(2);
        if (!StringUtils.isEmpty(filters)) {
            String[] filterItems = filters.split(",");
            for (String filter : filterItems) {
                switch (filter) {
                    case "stat": {
                        StatFilter statFilter = DruidStatConfigUtil.toStatFilter(PropertyUtil.getMap("filter.stat", dataSourceProperty.getDruid()), PropertyUtil.getMap("filter.stat", this.globalConfig));
                        if (statFilter != null) {
                            proxyFilters.add(statFilter);
                        }
                    }
                    break;
                    case "wall": {
                        WallFilter wallFilter = DruidWallConfigUtil.toWallConfig(PropertyUtil.getMap("filter.wall", dataSourceProperty.getDruid()), PropertyUtil.getMap("filter.wall", this.globalConfig));
                        if (wallFilter != null) {
                            proxyFilters.add(wallFilter);
                        }
                    }
                    break;
                    case "slf4j": {
                        LogFilter logFilter = DruidLogConfigUtil.initFilter(Slf4jLogFilter.class, PropertyUtil.getMap("filter.slf4j", dataSourceProperty.getDruid()), PropertyUtil.getMap("filter.slf4j", this.globalConfig));
                        if (logFilter != null) {
                            proxyFilters.add(logFilter);
                        }
                    }
                    break;
                    case "commons-log":
                    case "commonsLog": {
                        LogFilter logFilter = DruidLogConfigUtil.initFilter(CommonsLogFilter.class, PropertyUtil.getMap("filter.commons-log", dataSourceProperty.getDruid()), PropertyUtil.getMap("filter.commons-log", this.globalConfig));
                        if (logFilter != null) {
                            proxyFilters.add(logFilter);
                        }
                    }
                    break;
                    case "log4j": {
                        LogFilter logFilter = DruidLogConfigUtil.initFilter(Log4jFilter.class, PropertyUtil.getMap("filter.log4j", dataSourceProperty.getDruid()), PropertyUtil.getMap("filter.log4j", this.globalConfig));
                        if (logFilter != null) {
                            proxyFilters.add(logFilter);
                        }
                    }
                    break;
                    case "log4j2": {
                        LogFilter logFilter = DruidLogConfigUtil.initFilter(Log4j2Filter.class, PropertyUtil.getMap("filter.log4j2", dataSourceProperty.getDruid()), PropertyUtil.getMap("filter.log4j2", this.globalConfig));
                        if (logFilter != null) {
                            proxyFilters.add(logFilter);
                        }
                    }
                    break;
                    default:
                        log.error("dynamic-datasource current not support [{}]", filter);
                }
            }
        }
        return proxyFilters;
    }
}
