package com.aamish.db_copilot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.zaxxer.hikari.HikariDataSource;

@Configuration

public class TargetDbConfig {
	@Bean
 public JdbcClient targetDb(@Value("${copilot.target.url}")String url,
		 					@Value("${copilot.target.username}")String username,
 							@Value("${copilot.target.password}")String password){
 	      HikariDataSource ds=new HikariDataSource();	
 	      ds.setJdbcUrl(url);
 	      ds.setUsername(username);
 	      ds.setPassword(password);
 	      return JdbcClient.create(ds);
 	}
}
