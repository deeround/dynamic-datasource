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
package com.github.deeround.dynamic.datasource.tx;

import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author funkye
 */
@Slf4j
public class ConnectionFactory {

    private static final ThreadLocal<Map<String, ConnectionProxy>> CONNECTION_HOLDER = ThreadLocal.withInitial(() -> new ConcurrentHashMap(0));

    public static void putConnection(String ds, ConnectionProxy connection) {
        Map<String, ConnectionProxy> concurrentHashMap = CONNECTION_HOLDER.get();
        if (!concurrentHashMap.containsKey(ds)) {
            try {
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                log.error(e.getLocalizedMessage(), e);
            }
            concurrentHashMap.put(ds, connection);
        }
    }

    public static ConnectionProxy getConnection(String ds) {
        return CONNECTION_HOLDER.get().get(ds);
    }

    public static void notify(Boolean state) throws Exception {
        Exception exception = null;
        try {
            Map<String, ConnectionProxy> concurrentHashMap = CONNECTION_HOLDER.get();
            for (ConnectionProxy connectionProxy : concurrentHashMap.values()) {
                try {
                    connectionProxy.notify(state);
                } catch (SQLException e) {
                    exception = e;
                }
            }
        } finally {
            CONNECTION_HOLDER.remove();
            if (exception != null) {
                throw exception;
            }
        }
    }

}
