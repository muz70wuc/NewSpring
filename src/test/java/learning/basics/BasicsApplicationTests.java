package learning.basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BasicsApplicationTests {

	@Test
	@DisplayName("Sollte den Spring-Anwendungskontext fehlerfrei starten")
	void contextLoads() {
	}

}
