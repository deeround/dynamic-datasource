package com.github.deeround.dynamic.datasource.samples.service.impl;

import com.github.deeround.dynamic.datasource.annotation.DSTransactional;
import com.github.deeround.dynamic.datasource.samples.service.Db1Service;
import com.github.deeround.dynamic.datasource.samples.service.Db2Service;
import com.github.deeround.dynamic.datasource.samples.service.DbTransService;
import com.github.deeround.dynamic.datasource.samples.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wanghao 913351190@qq.com
 * @create 2023/3/1 10:41
 */
@Service

public class DbTransServiceImpl implements DbTransService {

    @Autowired
    Db1Service db1Service;

    @Autowired
    Db2Service db2Service;

    @Autowired
    UserService userService;

    /**
     * 测试单库事务
     */
    @DSTransactional
    @Override
    public void test01() {
        this.db1Service.insert1();
        this.db1Service.insert2();
    }

    /**
     * 测试多库事务
     */
    @DSTransactional
    @Override
    public void test02() {
        this.db1Service.insert1();
        this.db2Service.insert1();
        this.userService.insert1();
    }
}
