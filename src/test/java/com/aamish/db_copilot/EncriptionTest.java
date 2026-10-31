package com.aamish.db_copilot;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.aamish.db_copilot.service.CryptoService;
public class EncriptionTest {
@Test
public void roundTrip() {
	CryptoService service=new CryptoService("any_long_password","050c54a9e58382f1");
	String secret=service.encrypt("copilot_ro");
	assertNotEquals("copilot_ro",secret);
	assertEquals("copilot_ro",service.decrypt(secret));
}
}
