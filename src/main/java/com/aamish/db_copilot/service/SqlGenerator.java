package com.aamish.db_copilot.service;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class SqlGenerator {
  private final SchemaRetriever schemaRetriever;
  private final ChatClient chatClient;
	private final ExampleService exampleService;
  public String generate(Long id,String question,String previousSql,String error) {
	  String example=exampleService.findSimilar(id, question)
			  .stream()
			  .map(e->"Question: "+e.question()+ "\nSQL: "+e.sql())
			  .collect(Collectors.joining("\n\n"));
	  String schema=String.join("\n",schemaRetriever.retrieveTopK(id,question));
	  String retry="";
	  if(previousSql!=null) {
		  retry="\nYour previous SQL was: "+previousSql
				  +"\nIt failed with: "+error
				  +"\nReturn a corrected SQL only.";
	  }
	  String prompt="""
	  		 Write one PostgreSQL SELECT statement. Return only the SQL, no explanation.

        Rules:
        - Only SELECT is allowed. Never modify data.
        - Use only the tables and columns in the schema below. Do not invent columns or joins.
        - Join tables only through the foreign keys described in the schema.
        - Return only what the question asks for. Do not add counts, aggregates or extra columns.
        - If the question asks to show or list rows, use a plain SELECT with no aggregates.
        - If the question asks for the most, least, top or highest, also include the value used for ranking as a column.
	  	- For "most", "highest" or "top" questions about a group (a region, a category), aggregate with SUM or COUNT and GROUP BY that group, then ORDER BY the aggregate DESC.
	  	- Return only what the question asks for. Do not add extra columns.
        - If the question asks for a total, sum, count, average or "how many", use an aggregate (SUM, COUNT, AVG) and return a single value.
	  	- Use columns as they are. Do not multiply or combine columns unless the question asks for a calculation.
	  		Examples of correct queries:\n
	  		%s
	  		example
	  		Schema:
	  		%s
	  		Question:
	  		%s
	  		""".formatted(example,schema,question)+retry;
	  return chatClient.prompt().user(prompt).call().content();
  }
}
