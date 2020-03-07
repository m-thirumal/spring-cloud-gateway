/**
 * 
 */
package in.thirumal.router;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

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
		final String lb;
		final String defaultLb ="lb://go-to-hell";
		if (Set.of("DEV").contains(activeProfile)) {
			lb = "http://localhost:8082";
		} else {
			lb = "lb://eureka-client-2";
		}
		return routeLocatorBuilder.routes()
			/*	.route("client2", r -> r.path("/api/oauth/token")
						.filters(f -> f.filter((exchange, chain) -> {
							String path = "/error";
							ServerHttpRequest request = exchange.getRequest().mutate()
									.path(path)
									.build();
							return chain.filter(exchange.mutate().request(request).build());
						})).uri(lb))*/
				.route("client2", r -> r.path("/**")
					.filters(f -> 
						f.requestRateLimiter()
								.rateLimiter(RedisRateLimiter.class, c -> c.setBurstCapacity(10).setReplenishRate(4))
								// .configure(c -> c.setKeyResolver(exchange ->
								// Mono.just(HttpHeaders.AUTHORIZATION)))
								.configure(c -> c.setKeyResolver(authorizationKeyResolver)
										.setKeyResolver(remoteAddressKeyResolver)).retry(3)
								.secureHeaders()
								.addResponseHeader("app", "client2")
								//.addResponseHeader("response-time", LocalDateTime.now().toString())
								.hystrix(h -> h.setName("gateway Fallback").setFallbackUri("forward:/default-gateway"))
						) // add response header

						// .route(r -> r.header("X-Request-Id", "\\d+")
						.uri(lb))
				.route("default", r -> r.path("/**").filters(f -> f//.rewritePath("/*", "/default-icms")
						.hystrix(h -> h.setName("gateway Fallback").setFallbackUri("forward:/default-gateway")))
						.uri(defaultLb))
				.build();
				
	}
	
	@RequestMapping("/default-gateway")
	public String defaultGateway() {
		return "Message from default gateway";
	}
	
}
