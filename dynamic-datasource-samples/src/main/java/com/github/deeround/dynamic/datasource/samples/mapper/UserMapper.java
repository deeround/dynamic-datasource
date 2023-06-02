package com.github.deeround.dynamic.datasource.samples.mapper;

import com.github.deeround.dynamic.datasource.annotation.DS;
import com.github.deeround.dynamic.datasource.samples.entity.TestUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wanghao 913351190@qq.com
 * @create 2023/3/22 15:32
 */
@DS("db3")
@Mapper
public interface UserMapper {
    void insert1(TestUser user);

    void insert2(TestUser user);
}
