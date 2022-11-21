package in.thirumal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.web.WebAppConfiguration;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
@WebAppConfiguration
public class GatewayCircuitBreakerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(GatewayCircuitBreakerTest.class);

    @Autowired
    TestRestTemplate template;
    int i = 0;

    @BeforeAll
    public static void init() {

    }

    @Test
    @BenchmarkOptions(warmupRounds = 0, concurrency = 1, benchmarkRounds = 200)
    void testAccountService() {
        int gen = 1 + (i++ % 2);
        ResponseEntity<Object> r = template.exchange("/account/{id}", HttpMethod.GET, null, Object.class, gen);
        LOGGER.info("{}. Received: status->{}, payload->{}, call->{}", i, r.getStatusCodeValue(), r.getBody(), gen);
    }
}
