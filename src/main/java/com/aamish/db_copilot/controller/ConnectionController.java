package com.aamish.db_copilot.controller;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.aamish.db_copilot.dto.ConnectionInfo;
import com.aamish.db_copilot.dto.ConnectionRequest;
import com.aamish.db_copilot.entity.DbConnection;
import com.aamish.db_copilot.repository.DbConnectionRepository;
import com.aamish.db_copilot.service.ConnectionManager;
import com.aamish.db_copilot.service.CryptoService;
import com.aamish.db_copilot.service.ExampleService;
import com.aamish.db_copilot.service.SchemaIndexer;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/connections")
public class ConnectionController {
	private final SchemaIndexer schemaIndexer;
	private final ConnectionManager manager;
	private final CryptoService cryptoService;
	private final ExampleService exampleService;
private final DbConnectionRepository repository;
  @GetMapping
  public List<ConnectionInfo>getInfo(){
	  return repository.findAll().stream()
		        .map(c -> ConnectionInfo.builder().connectionId(c.getId()).name(c.getName()).build())
		        .toList();
  }
  @PostMapping
  public ConnectionInfo save(@RequestBody ConnectionRequest request) {
	  manager.testConnection(request.getUrl(),request.getUsername(),request.getPassword());
	  DbConnection db=DbConnection.builder()
			  			.name(request.getName())
			  			.url(request.getUrl())
			  			.username(request.getUsername())
			  			.password(cryptoService.encrypt(request.getPassword()))
			  			.build();
	 DbConnection saved=repository.save(db);
	  return ConnectionInfo.builder().connectionId(saved.getId()).name(saved.getName()).build();
  }
  @PostMapping("/{id}/index")
  public int indexSchema(@PathVariable Long id) {
 	 return schemaIndexer.indexSchema(id);
  }
  @PostMapping("/{id}/example")
  public int saveExample(@PathVariable Long id,@RequestBody List<ExampleService.Example>examples) {
	  return exampleService.save(id, examples);
  }
}
