/**
 * 
 */
package in.thirumal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * @author Thirumal
 *
 */
@Configuration
public class PostGlobalFilter implements GlobalFilter, Ordered {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		return chain.filter(exchange).then(Mono.fromRunnable(()->{
            ServerHttpResponse response = exchange.getResponse();
            logger.debug("Response code from the service ==> {}", response.getStatusCode());
        }));
	}
	
	@Bean
	public GlobalFilter responseFilter(){
	   return new PostGlobalFilter();
	}

}
