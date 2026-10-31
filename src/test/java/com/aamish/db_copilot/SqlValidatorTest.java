package com.aamish.db_copilot;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.aamish.db_copilot.service.SqlValidator;
public class SqlValidatorTest {
 private SqlValidator validator=new SqlValidator();
 @Test
 public void allowPlainSelect() {
	 assertDoesNotThrow(()->validator.validate("SELECT 1"));
 }
 @Test
 public void blocksSleep() {
	 assertThrows(IllegalArgumentException.class,()->validator.validate("SELECT pg_sleep(5)"));
 }
 @Test
 public void blocksSelectInto() {
	 assertThrows(IllegalArgumentException.class,()->validator.validate("SELECT * INTO x FROM orders"));
 }
 @Test
 public void blocksDelete() {
	 assertThrows(IllegalArgumentException.class,()->validator.validate("DELETE FROM orders"));
 }
 @Test
 public void blocksTwoStatements() {
	 assertThrows(IllegalArgumentException.class,()->validator.validate("SELECT 1; DROP TABLE orders"));
 }
}
