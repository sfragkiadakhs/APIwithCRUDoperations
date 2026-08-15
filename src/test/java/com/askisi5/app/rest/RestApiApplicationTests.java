package com.askisi5.app.rest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Uses the "test" profile (in-memory H2, see application-test.properties) so the
// context can load without a live MySQL instance being available.
@SpringBootTest
@ActiveProfiles("test")
class RestApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
