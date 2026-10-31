package com.aamish.db_copilot.dto;
import java.util.*;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class AskResponse {
private String answer;
private String sql;
private List<Map<String,Object>>rows;
}
