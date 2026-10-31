package com.aamish.db_copilot.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.aamish.db_copilot.entity.DbConnection;
import com.aamish.db_copilot.repository.DbConnectionRepository;
import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class ConnectionManager {
  private final DbConnectionRepository repository;
  private final CryptoService cryptoService;
  private ConcurrentHashMap<Long,JdbcClient>map=new ConcurrentHashMap<>();
  public JdbcClient clientFor(Long id) {
	  if(map.containsKey(id))return map.get(id);
	  DbConnection connection=repository.findById(id).orElseThrow(()->
	  new IllegalArgumentException("Connection Not FOund"));
	  HikariDataSource dataSource=new HikariDataSource();
	  dataSource.setMaximumPoolSize(3);
	  dataSource.setJdbcUrl(connection.getUrl());
	  dataSource.setUsername(connection.getUsername());
	  dataSource.setPassword(cryptoService.decrypt(connection.getPassword()));
	  JdbcClient jdbc=JdbcClient.create(dataSource);
	  map.put(id,jdbc);
	  return jdbc; 
  }
  public void testConnection(String url,String username,String password) {
	  try(Connection c=DriverManager.getConnection(url, username, password)){
		  
	  }
	  catch(SQLException e) {
		  throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Could not connect"+e.getMessage());
	  }
  }
}
