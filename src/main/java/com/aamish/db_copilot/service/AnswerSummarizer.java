package com.aamish.db_copilot.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerSummarizer {
  private final ChatClient chatClient;
  public String summarize(String question,List<Map<String,Object>>row) {
	  if(row==null||row.isEmpty())return "No data found";
	  String prompt = """
		        Answer the question in one or two plain sentences, using only the data below.
		        Each row is a result of the query. If there is a single row with a number
		        (such as a count or total), that number is the answer. Mention names and values
		        from the data. Do not invent anything.

		        Question:
		        %s

		        Data:
		        %s
		        """.formatted(question, row);
	  System.out.println("SUMMARY PROMPT >>>\n" + prompt);
	  return chatClient.prompt().user(prompt).call().content();
  }
}
