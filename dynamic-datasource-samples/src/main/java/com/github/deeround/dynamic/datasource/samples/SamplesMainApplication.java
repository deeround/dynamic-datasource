package com.github.deeround.dynamic.datasource.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author wanghao 913351190@qq.com
 * @create 2023/5/24 16:43
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SamplesMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SamplesMainApplication.class, args);
    }
}
