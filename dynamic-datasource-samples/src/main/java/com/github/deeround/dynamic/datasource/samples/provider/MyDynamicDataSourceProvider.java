package com.github.deeround.dynamic.datasource.samples.provider;

import com.github.deeround.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;
import com.github.deeround.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.github.deeround.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

/**
 * @author wanghao 913351190@qq.com
 * @create 2023/3/15 16:52
 */
//@Component
public class MyDynamicDataSourceProvider extends AbstractJdbcDataSourceProvider {

    public MyDynamicDataSourceProvider(DynamicDataSourceProperties dynamicDataSourceProperties) {
        super(dynamicDataSourceProperties);
    }

    @Override
    protected LinkedHashMap<String, DataSourceProperty> executeStmt(Statement statement) throws SQLException {

        statement.executeQuery("select * from test_user");//从默认数据源动态读取其他数据源，然后动态添加数据源

        LinkedHashMap<String, DataSourceProperty> ds = new LinkedHashMap<>();
        DataSourceProperty db3 = new DataSourceProperty();
        db3.setDriverClassName("com.mysql.cj.jdbc.Driver");
        db3.setUrl("jdbc:mysql://127.0.0.1:3306/db3?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useUnicode=true&useSSL=false");
        db3.setUsername("root");
        db3.setPassword("root");
        ds.put("db3", db3);
        return ds;
    }

}
