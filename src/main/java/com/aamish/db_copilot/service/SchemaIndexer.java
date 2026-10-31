package com.aamish.db_copilot.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import java.util.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchemaIndexer {
private final SchemaReader schemaReader;
private final VectorStore vectorStore;
  public int indexSchema(Long id) {
	  vectorStore.delete("connectionID == " +id);
	  List<Document>document=new ArrayList<>();
	  List<String>tables=schemaReader.listTables(id);
	 	  for(String table:tables) {
		  if(table.contains("vector_store"))continue;
		  String doc=schemaReader.describeTable(id,table);
		  document.add(new Document(doc,Map.of("connectionId",id)));
	  }
	  vectorStore.add(document);
	  return document.size();
  }
}
