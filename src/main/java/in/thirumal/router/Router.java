/**
 * 
 */
package in.thirumal.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import in.thirumal.throttle.AuthorizationKeyResolver;
import in.thirumal.throttle.RemoteAddressKeyResolver;

/**
 * @author Thirumal
 */
@Component
public class Router {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${spring.profiles.active}")
	private String activeProfile;
	@Autowired
	RemoteAddressKeyResolver remoteAddressKeyResolver;
	@Autowired
	AuthorizationKeyResolver authorizationKeyResolver;

	@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
		logger.debug(this.getClass().getSimpleName() + ": " + Thread.currentThread().getStackTrace()[1].getMethodName());
		final String client_1_lb = "lb://EUREKA-CLIENT-1";
		final String client_2_lb = "lb://eureka-client-2";
		final String defaultLb ="lb://go-to-hell";
//		if (Set.of("DEV").contains(activeProfile)) {
//			lb = "lb://eureka-client-1";
//		} else {
//			lb = "lb://eureka-client-1";
//		}
		return routeLocatorBuilder.routes()
			/*	.route("client2", r -> r.path("/api/oauth/token")
						.filters(f -> f.filter((exchange, chain) -> {
							String path = "/error";
							ServerHttpRequest request = exchange.getRequest().mutate()
									.path(path)
									.build();
							return chain.filter(exchange.mutate().request(request).build());
						})).uri(lb))*/
				.route("client_1", r -> r.path("/1/**")
					.filters(f -> 
						f.requestRateLimiter()
								.rateLimiter(RedisRateLimiter.class, c -> c.setBurstCapacity(10).setReplenishRate(4))
								// .configure(c -> c.setKeyResolver(exchange ->
								// Mono.just(HttpHeaders.AUTHORIZATION)))
								.configure(c -> c.setKeyResolver(authorizationKeyResolver)
										.setKeyResolver(remoteAddressKeyResolver)).retry(3)
								.secureHeaders()
								.addResponseHeader("app", "client1")
								.rewritePath("1/", "") //Rewrite the path
								//.addResponseHeader("response-time", LocalDateTime.now().toString())
							//	.hystrix(h -> h.setName("gateway Fallback").setFallbackUri("forward:/default-gateway"))
								.retry(3)//retry
								.circuitBreaker(c -> c.setName("myCircuitBreaker").setFallbackUri("forward:/fallback/1"))
						) // add response header
						
						// .route(r -> r.header("X-Request-Id", "\\d+")
						.uri(client_1_lb))
				.route("client_2", r -> r.path("/2/**")
						.filters(f -> 
							f.requestRateLimiter()
									.rateLimiter(RedisRateLimiter.class, c -> c.setBurstCapacity(10).setReplenishRate(4))
									// .configure(c -> c.setKeyResolver(exchange ->
									// Mono.just(HttpHeaders.AUTHORIZATION)))
									.configure(c -> c.setKeyResolver(authorizationKeyResolver)
											.setKeyResolver(remoteAddressKeyResolver)).retry(3)
									.secureHeaders()
									.addResponseHeader("app", "client2")
									.rewritePath("2/", "") //Rewrite the path
									//.addResponseHeader("response-time", LocalDateTime.now().toString())
								//	.hystrix(h -> h.setName("gateway Fallback").setFallbackUri("forward:/default-gateway"))
									.circuitBreaker(c -> c.setName("myCircuitBreaker").setFallbackUri("forward:/fallback/2"))
							) // add response header
						
							// .route(r -> r.header("X-Request-Id", "\\d+")
							.uri(client_2_lb))
				.route("default", r -> r.path("/**")//.filters(f -> f//.rewritePath("/*", "/default-icms")
						//.hystrix(h -> h.setName("gateway Fallback").setFallbackUri("forward:/fallback/default-gateway")))
						.filters(f -> f.circuitBreaker(c -> c.setName("myCircuitBreaker").setFallbackUri("forward:/fallback/default")))
						.uri(defaultLb))
				.build();
				
	}
	
	@GetMapping("/fallback/default-gateway")
	public String defaultGateway() {
		return "Message from default gateway";
	}
	
}
