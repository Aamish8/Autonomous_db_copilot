package com.aamish.db_copilot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import com.aamish.db_copilot.dto.AskRequest;
import com.aamish.db_copilot.dto.AskResponse;
import com.aamish.db_copilot.service.CopilotService;
import com.aamish.db_copilot.service.QueryRunner;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * Runs every question in eval.json through the copilot and compares the
 * result rows with the rows of the hand-written gold SQL.
 * Needs the app database, the target database and Ollama running.
 * Run it from Eclipse: right-click -> Run As -> JUnit Test.
 */
@Tag("eval")
@SpringBootTest
class EvalRunner {

    private static final long CONNECTION_ID = 3L; // your sales_dw connection id

    record EvalCase(String question, String goldSql) {}

    @Autowired CopilotService copilotService;
    @Autowired QueryRunner queryRunner;
    @Autowired ObjectMapper objectMapper;

    @Test
    void runEvaluation() throws Exception {
        List<EvalCase> cases = objectMapper.readValue(
                new ClassPathResource("eval.json").getInputStream(),
                new TypeReference<List<EvalCase>>() {});

        int passed = 0;
        for (EvalCase c : cases) {
            try {
                AskRequest request = new AskRequest();
                request.setQuestion(c.question());
                AskResponse response = copilotService.ask(CONNECTION_ID, request);

                List<String> actual = normalize(response.getRows());
                List<String> expected = normalize(queryRunner.run(CONNECTION_ID, c.goldSql()));

                if (actual.equals(expected)) {
                    passed++;
                    System.out.println("PASS  " + c.question());
                } else {
                    System.out.println("FAIL  " + c.question());
                    System.out.println("      expected: " + expected);
                    System.out.println("      actual:   " + actual);
                    System.out.println("      sql:      " + response.getSql());
                }
            } catch (Exception e) {
                System.out.println("FAIL  " + c.question() + "  (error: " + e.getMessage() + ")");
            }
        }
        System.out.println("\nSCORE: " + passed + "/" + cases.size() + " passed");
    }

    // Turn rows into sorted text so column names, column order and row order do not matter.
    private static List<String> normalize(List<? extends Map<String, ?>> rows) {
        return rows.stream()
                .map(row -> row.values().stream()
                        .map(EvalRunner::cell)
                        .sorted()
                        .collect(Collectors.joining("|")))
                .sorted()
                .toList();
    }

    private static String cell(Object value) {
        if (value == null) return "null";
        if (value instanceof Number) {
            return new BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
        return value.toString();
    }
}