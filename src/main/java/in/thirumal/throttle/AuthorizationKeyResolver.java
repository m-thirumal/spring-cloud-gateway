/**
 * 
 */
package in.thirumal.throttle;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * @author Thirumal
 * @since 13/07/2019
 * @version 1.0
 */
@Component
@Primary
public class AuthorizationKeyResolver implements KeyResolver {


	/* (non-Javadoc)
	 * @see org.springframework.cloud.gateway.filter.ratelimit.KeyResolver#resolve(org.springframework.web.server.ServerWebExchange)
	 */
	@Override
	public Mono<String> resolve(ServerWebExchange exchange) {
		//exchange.getRequest().getHeaders();
		// TODO Block the user
		//System.out.println(HttpHeaders.AUTHORIZATION);
		return Mono.just(HttpHeaders.AUTHORIZATION);
	}

}
