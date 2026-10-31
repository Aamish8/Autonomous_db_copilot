package com.aamish.db_copilot.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aamish.db_copilot.entity.ChatHistory;
@Repository
public interface HistoryRepository extends JpaRepository<ChatHistory,Long>{
  List<ChatHistory>findTop50ByConnectionIdOrderByCreatedAtDesc(Long connectionId);

void deleteByConnectionId(Long connectionId);
}
