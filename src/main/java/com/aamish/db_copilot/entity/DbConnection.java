package com.aamish.db_copilot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DbConnection {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long   id;
  private String name;
  private String url;
  private String username;
  private String password;
}
