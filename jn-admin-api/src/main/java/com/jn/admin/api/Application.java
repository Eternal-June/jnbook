package com.jn.admin.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = { "com.jn.db", "com.jn.core",
		"com.jn.admin" })
@MapperScan({ "com.jn.db.dao", "com.jn.db.dao.ex" })
@EnableTransactionManagement
//事务管理
@EnableScheduling
//对计划任务的开启
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}