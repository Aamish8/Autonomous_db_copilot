package com.aamish.db_copilot.service;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryRunner {
     private final ConnectionManager manager;
     private final static int maxLimit=100;
     public List<Map<String, @Nullable Object>> run(Long id,String sql){
    	 String limited="SELECT * FROM (" +sql+ ") AS q LIMIT "+ maxLimit;
    	 return manager.clientFor(id).sql(limited).query().listOfRows();
     }
}
