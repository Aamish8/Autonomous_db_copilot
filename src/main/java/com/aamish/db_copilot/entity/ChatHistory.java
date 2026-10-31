package com.aamish.db_copilot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name="chat_history")
public class ChatHistory {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
    
	@Column(columnDefinition="text")
    private String rowsJson;
  
	@Column(columnDefinition="text")
    private String question;
    
	@Column(columnDefinition="text")
    private String answer;
    
	@Column(columnDefinition="text")
    private String sql;
    @Builder.Default
	private LocalDateTime createdAt=LocalDateTime.now();
    private Long connectionId;
}
