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
package com.github.deeround.dynamic.datasource.ds;

import com.github.deeround.dynamic.datasource.DynamicDataSourceContextHolder;
import com.github.deeround.dynamic.datasource.tx.ConnectionFactory;
import com.github.deeround.dynamic.datasource.tx.ConnectionProxy;
import com.github.deeround.dynamic.datasource.tx.TransactionContext;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 抽象动态获取数据源
 *
 * @author TaoYu
 * @since 2.2.0
 */
public abstract class AbstractRoutingDataSource extends AbstractDataSource {

    /**
     * 抽象获取连接池
     *
     * @return 连接池
     */
    protected abstract DataSource determineDataSource();

    /**
     * 获取默认数据源名称
     *
     * @return 名称
     */
    protected abstract String getPrimary();

    @Override
    public Connection getConnection() throws SQLException {
        String xid = TransactionContext.getXID();
        if (StringUtils.isEmpty(xid)) {
            return this.determineDataSource().getConnection();
        } else {
            String ds = DynamicDataSourceContextHolder.peek();
            ds = StringUtils.isEmpty(ds) ? this.getPrimary() : ds;
            ConnectionProxy connection = ConnectionFactory.getConnection(ds);
            return connection == null ? this.getConnectionProxy(ds, this.determineDataSource().getConnection()) : connection;
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        String xid = TransactionContext.getXID();
        if (StringUtils.isEmpty(xid)) {
            return this.determineDataSource().getConnection(username, password);
        } else {
            String ds = DynamicDataSourceContextHolder.peek();
            ds = StringUtils.isEmpty(ds) ? this.getPrimary() : ds;
            ConnectionProxy connection = ConnectionFactory.getConnection(ds);
            return connection == null ? this.getConnectionProxy(ds, this.determineDataSource().getConnection(username, password))
                    : connection;
        }
    }

    private Connection getConnectionProxy(String ds, Connection connection) {
        ConnectionProxy connectionProxy = new ConnectionProxy(connection, ds);
        ConnectionFactory.putConnection(ds, connectionProxy);
        return connectionProxy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return this.determineDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface.isInstance(this) || this.determineDataSource().isWrapperFor(iface));
    }
}
