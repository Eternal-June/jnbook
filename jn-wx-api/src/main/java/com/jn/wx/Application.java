package com.jn.wx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.jn.db", "com.jn.core",
        "com.jn.wx"})
@MapperScan({"com.jn.db.dao", "com.jn.db.dao.ex"})
@EnableTransactionManagement
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}