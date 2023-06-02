package com.github.deeround.dynamic.datasource;

import com.github.deeround.dynamic.datasource.samples.service.Db1Service;
import com.github.deeround.dynamic.datasource.samples.service.Db2Service;
import com.github.deeround.dynamic.datasource.samples.service.DbTransService;
import com.github.deeround.dynamic.datasource.samples.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author wanghao 913351190@qq.com
 * @create 2023/5/24 17:25
 */
@SpringBootTest
public class Tests {

    @Autowired
    Db1Service db1Service;

    @Autowired
    Db2Service db2Service;

    @Autowired
    UserService userService;

    @Autowired
    DbTransService dbTransService;

    @Test
    void test01() {
        db1Service.insert1();
    }

}
