package com.github.deeround.dynamic.datasource.samples.service.impl;

import com.github.deeround.dynamic.datasource.annotation.DS;
import com.github.deeround.dynamic.datasource.samples.service.Db2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author wanghao 913351190@qq.com
 * @create 2023/3/1 9:06
 */
@Service
@DS("db2")
public class Db2ServiceImpl implements Db2Service {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void delete() {
        this.jdbcTemplate.update("delete from test_user");
    }

    @Override
    public void insert1() {
        String id = "db2" + System.currentTimeMillis();
        this.jdbcTemplate.update("insert into test_user(id,name) values('" + id + "','db2_insert1')");
    }
}
