package com.aamish.db_copilot.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConnectionInfo {
  private Long connectionId;
  private String name;
}
