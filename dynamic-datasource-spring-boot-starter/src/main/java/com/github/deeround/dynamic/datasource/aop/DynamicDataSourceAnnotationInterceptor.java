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
package com.github.deeround.dynamic.datasource.aop;

import com.github.deeround.dynamic.datasource.DataSourceClassResolver;
import com.github.deeround.dynamic.datasource.DynamicDataSourceContextHolder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Core Interceptor of Dynamic Datasource
 *
 * @author TaoYu
 * @since 1.2.0
 */
public class DynamicDataSourceAnnotationInterceptor implements MethodInterceptor {

    private static final DataSourceClassResolver dataSourceClassResolver = new DataSourceClassResolver();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String key = this.determineDatasourceKey(invocation);
        DynamicDataSourceContextHolder.push(key);
        try {
            return invocation.proceed();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    private String determineDatasourceKey(MethodInvocation invocation) {
        return dataSourceClassResolver.findKey(invocation.getMethod(), invocation.getThis());
    }
}
