package in.thirumal.filter;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * @author ThirumalM
 */
@Component
public class IpWhitelistFilter implements GlobalFilter, Ordered {

    private static final List<String> IP_WHITELIST = List.of(
            "192.168.1.10",
            "203.0.113.5",
            "0:0:0:0:0:0:0:1", // IPv6 localhost
            "127.0.0.1" // IPv4 localhost
        );
    
	@Override
	public int getOrder() {
		return -1; //// ensure it runs early
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
	    String clientIp = getClientIp(exchange);
        if (!IP_WHITELIST.contains(clientIp)) {
            System.out.println(clientIp + " is not allowed to access this service.");
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
	}
	
	 private String getClientIp(ServerWebExchange exchange) {
	        String forwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
	        if (forwardedFor != null) {
	            return forwardedFor.split(",")[0].trim(); // in case of multiple IPs
	        }
	        return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
	 }

}
