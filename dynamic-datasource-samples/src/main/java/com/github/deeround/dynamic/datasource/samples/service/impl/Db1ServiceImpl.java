package com.github.deeround.dynamic.datasource.samples.service.impl;

import com.github.deeround.dynamic.datasource.annotation.DS;
import com.github.deeround.dynamic.datasource.samples.service.Db1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author wanghao 913351190@qq.com
 * @create 2023/3/1 9:06
 */
@Service
@DS("db1")
public class Db1ServiceImpl implements Db1Service {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void delete() {
        this.jdbcTemplate.update("delete from test_user");
    }

    @Override
    public void insert1() {
        String id = "db1" + System.currentTimeMillis();
        this.jdbcTemplate.update("insert into test_user(id,name) values('" + id + "','db1_insert1')");
    }

    @Override
    public void insert2() {
        String id = "db1" + System.currentTimeMillis();
        this.jdbcTemplate.update("insert into test_user(id,name) values('" + id + "','db1_insert2')");
    }
}
