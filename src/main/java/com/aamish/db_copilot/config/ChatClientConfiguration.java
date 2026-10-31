package com.aamish.db_copilot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfiguration {
	@Bean
  public ChatClient chatClient(ChatClient.Builder builder) {
	  return builder.build();
  }
}
