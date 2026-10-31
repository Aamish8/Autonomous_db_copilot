package com.aamish.db_copilot.controller;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.aamish.db_copilot.entity.ChatHistory;
import com.aamish.db_copilot.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class ChatHistoryController {
 private final HistoryRepository historyRepository;
 @GetMapping
 public List<ChatHistory> find(@RequestParam Long connectionId){
	 return historyRepository.findTop50ByConnectionIdOrderByCreatedAtDesc(connectionId);
 }
 @DeleteMapping("/{id}")
 public void deleteById(@PathVariable Long id) {
	 historyRepository.deleteById(id);
 }
 @Transactional
 @DeleteMapping
 public void deleteAll(@RequestParam Long connectionId) {
	 historyRepository.deleteByConnectionId(connectionId);
 }
}
