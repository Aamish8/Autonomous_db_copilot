package com.aamish.db_copilot.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.aamish.db_copilot.entity.DbConnection;
import com.aamish.db_copilot.repository.DbConnectionRepository;
import com.aamish.db_copilot.service.CryptoService;
import com.aamish.db_copilot.service.ExampleService;
import com.aamish.db_copilot.service.SchemaIndexer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * On first start (no saved connections yet) registers the demo database, indexes its schema
 * and loads the example queries. Only runs when COPILOT_DEMO_URL is set (Docker does this).
 * It retries for a few minutes, because Ollama may still be starting or downloading models.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DemoDataSeeder implements ApplicationRunner {

    private final DbConnectionRepository repository;
    private final CryptoService crypto;
    private final SchemaIndexer indexer;
    private final ExampleService examples;
    private final ObjectMapper objectMapper;

    @Value("${copilot.demo.url:}") private String url;
    @Value("${copilot.demo.username:copilot_ro}") private String username;
    @Value("${copilot.demo.password:copilot_ro}") private String password;

    @Override
    public void run(ApplicationArguments args) {
        if (url.isBlank() || repository.count() > 0) return;
        Thread thread = new Thread(this::seed, "demo-seeder");
        thread.setDaemon(true);
        thread.start();
    }

    private void seed() {
        DbConnection saved = repository.save(DbConnection.builder()
                .name("sales_dw")
                .url(url)
                .username(username)
                .password(crypto.encrypt(password))
                .build());

        for (int attempt = 1; attempt <= 40; attempt++) {
            try {
                int tables = indexer.indexSchema(saved.getId());
                List<ExampleService.Example> list = objectMapper.readValue(
                        new ClassPathResource("examples-sales_dw.json").getInputStream(),
                        new TypeReference<List<ExampleService.Example>>() {});
                int count = examples.save(saved.getId(), list);
                log.info("Demo database ready: {} tables indexed, {} examples loaded", tables, count);
                return;
            } catch (Exception e) {
                log.warn("Demo setup attempt {} failed (is Ollama up and are the models pulled?): {}",
                        attempt, e.getMessage());
                try { Thread.sleep(15_000); } catch (InterruptedException ie) { return; }
            }
        }
        log.error("Demo setup gave up. Use Re-index in the page once Ollama is ready.");
    }
}
