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

import com.github.deeround.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.github.deeround.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

/**
 * JDBC数据源提供者(抽象)
 *
 * @author TaoYu
 * @since 2.1.2
 */
@Slf4j
public abstract class AbstractJdbcDataSourceProvider extends AbstractDataSourceProvider implements DynamicDataSourceProvider {

    /**
     * JDBC driver
     */
    private final String driverClassName;
    /**
     * JDBC url 地址
     */
    private final String url;
    /**
     * JDBC 用户名
     */
    private final String username;
    /**
     * JDBC 密码
     */
    private final String password;


    public AbstractJdbcDataSourceProvider(DynamicDataSourceProperties dynamicDataSourceProperties, String url, String username, String password) {
        this(dynamicDataSourceProperties, null, url, username, password);
    }

    public AbstractJdbcDataSourceProvider(DynamicDataSourceProperties dynamicDataSourceProperties, String driverClassName, String url, String username, String password) {
        super(dynamicDataSourceProperties);
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public AbstractJdbcDataSourceProvider(DynamicDataSourceProperties dynamicDataSourceProperties, DataSourceProperty dataSourceProperty) {
        this(dynamicDataSourceProperties, dataSourceProperty.getDriverClassName(), dataSourceProperty.getUrl(), dataSourceProperty.getUsername(), dataSourceProperty.getPassword());
    }

    public AbstractJdbcDataSourceProvider(DynamicDataSourceProperties dynamicDataSourceProperties, String poolName) {
        this(dynamicDataSourceProperties, dynamicDataSourceProperties.getDatasource().get(poolName));
    }

    public AbstractJdbcDataSourceProvider(DynamicDataSourceProperties dynamicDataSourceProperties) {
        this(dynamicDataSourceProperties, dynamicDataSourceProperties.getDatasource().get(dynamicDataSourceProperties.getPrimary()));
    }

    @Override
    public LinkedHashMap<String, DataSource> loadDataSources() {
        Connection conn = null;
        Statement stmt = null;
        try {
            // 由于 SPI 的支持，现在已无需显示加载驱动了
            // 但在用户显示配置的情况下，进行主动加载
            if (!StringUtils.isEmpty(this.driverClassName)) {
                Class.forName(this.driverClassName);
                log.info("dynamic-datasource load datasource from jdbc load driver success");
            }
            conn = DriverManager.getConnection(this.url, this.username, this.password);
            log.info("dynamic-datasource load datasource from jdbc get connection success");
            stmt = conn.createStatement();
            LinkedHashMap<String, DataSourceProperty> dataSourcePropertiesMap = this.executeStmt(stmt);
            return this.createDataSourceMap(dataSourcePropertiesMap);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(e.getLocalizedMessage());
        } finally {
            JdbcUtils.closeConnection(conn);
            JdbcUtils.closeStatement(stmt);
        }
    }

    /**
     * 执行语句获得数据源参数
     *
     * @param statement 语句
     * @return 数据源参数
     * @throws SQLException sql异常
     */
    protected abstract LinkedHashMap<String, DataSourceProperty> executeStmt(Statement statement)
            throws SQLException;
}
