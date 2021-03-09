/**
 * 
 */
package in.thirumal.config;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

/**
 * @author Thirumal
 *
 */
@Configuration
public class Resilience4JConfig {

	/**
	 * The very simple configuration contains default circuit breaker settings and and defines timeout duration using TimeLimiterConfig.
	 * @return
	 */
	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
//				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(5)
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .failureRateThreshold(50.0F)
                        .waitDurationInOpenState(Duration.ofMillis(30))
//                        .slowCallDurationThreshold(Duration.ofMillis(200))
//                        .slowCallRateThreshold(50.0F)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofMillis(200)).build())
                .build());
	}
	
}
