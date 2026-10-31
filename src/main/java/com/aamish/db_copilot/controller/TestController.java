package com.aamish.db_copilot.controller;

import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aamish.db_copilot.dto.AskRequest;
import com.aamish.db_copilot.service.CopilotService;
import com.aamish.db_copilot.service.SchemaIndexer;
import com.aamish.db_copilot.service.SchemaReader;
import com.aamish.db_copilot.service.SchemaRetriever;
import com.aamish.db_copilot.service.SqlGenerator;

import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.JSQLParserException;

@RestController
@RequiredArgsConstructor
public class TestController {
 private final SchemaReader schemaReader;
 private final SchemaIndexer schemaIndexer;
 private final SchemaRetriever schemaRetriever;
 private final SqlGenerator sqlGenerator;
 private final CopilotService copilotService;
 @GetMapping("/tables/{id}")
 public String listTables(@PathVariable Long id) {
	 return schemaReader.listTables(id).toString();
 }
 @GetMapping("/columns/{id}/{table}")
 public List<Map<String, @Nullable Object>> listColumns(@PathVariable Long id,@PathVariable String table) {
	 return schemaReader.listColumns(id,table);
 }
 @GetMapping("/describe//{id}/{table}")
 public String describeTable(@PathVariable Long id,@PathVariable String table) {
	 return schemaReader.describeTable(id,table);
 }
 @GetMapping("/search/{id}")
 public List<String> search(@PathVariable Long id,@RequestParam String query){
	 return schemaRetriever.retrieveTopK(id,query);
 }
 @GetMapping("/sql/{id}")
 public String generate(@PathVariable Long id,@RequestParam String query) {
	 return sqlGenerator.generate(id,query,null,null);
 }
 @GetMapping("/ask/{id}")
 public String ask(@PathVariable Long id,@RequestBody AskRequest request) throws JSQLParserException {
	 return copilotService.ask(id,request).getAnswer();
 }
}
