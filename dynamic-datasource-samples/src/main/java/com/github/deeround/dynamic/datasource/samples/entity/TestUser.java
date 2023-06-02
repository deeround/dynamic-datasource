package com.github.deeround.dynamic.datasource.samples.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 测试用户表
 *
 * @TableName test_user
 */
@Data
public class TestUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private String name;
    /**
     *
     */
    private String id;
}
