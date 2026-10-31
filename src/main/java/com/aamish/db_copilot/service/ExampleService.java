package com.aamish.db_copilot.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Stores example question -> SQL pairs in the vector store (tagged per connection)
 * and finds the ones most similar to a new question.
 */
@Service
@RequiredArgsConstructor
public class ExampleService {

    private final VectorStore vectorStore;

    public record Example(String question, String sql) {}

    // Replaces all examples of this connection with the given list.
    public int save(Long connectionId, List<Example> examples) {
        vectorStore.delete("connectionId == " + connectionId + " && type == 'example'");
        List<Document> docs = examples.stream()
                .map(e -> new Document(e.question(), Map.<String, Object>of(
                        "connectionId", connectionId,
                        "type", "example",
                        "sql", e.sql())))
                .toList();
        vectorStore.add(docs);
        return docs.size();
    }

    public List<Example> findSimilar(Long connectionId, String question) {
        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(3)
                .filterExpression("connectionId == " + connectionId + " && type == 'example'")
                .build();
        return vectorStore.similaritySearch(request).stream()
                .map(d -> new Example(d.getText(), String.valueOf(d.getMetadata().get("sql"))))
                .toList();
    }
}