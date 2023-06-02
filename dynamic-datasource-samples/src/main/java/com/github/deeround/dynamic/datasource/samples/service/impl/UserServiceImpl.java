package com.github.deeround.dynamic.datasource.samples.service.impl;

import com.github.deeround.dynamic.datasource.annotation.DS;
import com.github.deeround.dynamic.datasource.samples.entity.TestUser;
import com.github.deeround.dynamic.datasource.samples.mapper.UserMapper;
import com.github.deeround.dynamic.datasource.samples.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wanghao 913351190@qq.com
 * @create 2023/3/22 16:14
 */
@Service
@DS("db3")
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public void insert1() {
        String id = "db3" + System.currentTimeMillis();
        TestUser testUser = new TestUser();
        testUser.setId(id);
        testUser.setName("db3_insert1");
        this.userMapper.insert1(testUser);
    }

    @Override
    public void insert2() {
        String id = "db3" + System.currentTimeMillis();
        TestUser testUser = new TestUser();
        testUser.setId(id);
        testUser.setName("db3_insert2");
        this.userMapper.insert2(testUser);
    }
}
