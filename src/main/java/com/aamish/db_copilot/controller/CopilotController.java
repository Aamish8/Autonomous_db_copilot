package com.aamish.db_copilot.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aamish.db_copilot.dto.AskRequest;
import com.aamish.db_copilot.dto.AskResponse;
import com.aamish.db_copilot.service.CopilotService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CopilotController {
private final CopilotService service;
@PostMapping("/ask/{id}")
public AskResponse askQuerry(@PathVariable Long id,@RequestBody AskRequest request) {
	return service.ask(id,request);
}
}
