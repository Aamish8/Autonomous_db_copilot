package com.aamish.db_copilot.service;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import java.util.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchemaRetriever {
	private final VectorStore vectorStore;
	public List<String> retrieveTopK(Long id,String question){
	  SearchRequest request=SearchRequest.builder()
			  .query(question).topK(4).filterExpression("connectionId == "+ id).build();
	  return vectorStore.similaritySearch(request).stream()
			  .map(Document::getText).toList();
	}
}
