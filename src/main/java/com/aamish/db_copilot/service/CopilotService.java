package com.aamish.db_copilot.service;
import java.util.*;
import org.springframework.stereotype.Service;

import com.aamish.db_copilot.dto.AskRequest;
import com.aamish.db_copilot.dto.AskResponse;
import com.aamish.db_copilot.entity.ChatHistory;
import com.aamish.db_copilot.repository.HistoryRepository;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class CopilotService {
    private final SqlGenerator sqlGen;
    private final SqlValidator sqlVal;
    private final QueryRunner qRunner;
    private final AnswerSummarizer summarizes;
    private final ObjectMapper objectMapper;
    private final HistoryRepository historyRepository;
    public AskResponse ask(Long id,AskRequest request) {
    	String error=null;
    	String previousSql=null;
    	String sql=cleanSql(sqlGen.generate(id,request.getQuestion(),previousSql,error));
    	List<Map<String,Object>>rows=null;
    	for(int i=0;i<3;i++) {
    	try {
    	sqlVal.validate(sql);
    	rows=qRunner.run(id,sql);
    	break;
    	}
    	catch(Exception e){
    		Throwable root=e;
    		while(root.getCause()!=null)root=root.getCause();
    		error=root.getMessage();
    		if(i<2)sql=cleanSql(sqlGen.generate(id,request.getQuestion(),sql,error));
    	}
    	}
    	if(rows==null)throw new IllegalArgumentException("Result Not Found>>: "+error);
    	String answer=summarizes.summarize(request.getQuestion(),rows);
    	String rowJson=objectMapper.writeValueAsString(rows);
    	ChatHistory history=ChatHistory.builder()
    			.answer(answer)
    			.question(request.getQuestion())
    			.sql(sql)
    			.rowsJson(rowJson)
    			.connectionId(id)
    			.build();
    	historyRepository.save(history);
    	return AskResponse.builder()
    			.answer(answer)
    			.sql(sql)
    			.rows(rows)
    			.build();
    }
    private String cleanSql(String raw) {
    	String sql=raw.replace("```sql","").replace("```", "").trim();
    	if(sql.endsWith(";"))sql=sql.substring(0,sql.length()-1);
    	return sql;
    }
}
