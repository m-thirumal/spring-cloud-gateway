/**
 * 
 */
package in.thirumal.throttle;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * @author Thirumal
 * @since 13/07/2019
 * @version 1.0
 */
@Component
public class RemoteAddressKeyResolver implements KeyResolver {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/* (non-Javadoc)
	 * @see org.springframework.cloud.gateway.filter.ratelimit.KeyResolver#resolve(org.springframework.web.server.ServerWebExchange)
	 */
	@Override
	public Mono<String> resolve(ServerWebExchange exchange) {
		//findPattern(exchange);
		//checkTime(exchange);
		//return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
		XForwardedRemoteAddressResolver resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(1);
        InetSocketAddress inetSocketAddress = resolver.resolve(exchange);
        return Mono.just(inetSocketAddress.getAddress().getHostAddress());
	}

	@SuppressWarnings("unused")
	private void checkTime(ServerWebExchange exchange) {
		logger.debug(this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			//exchange
			String time = exchange.getRequest().getHeaders().get("Request-Time").get(0);
			System.out.println(generateKey(time));
		} catch (NullPointerException e) {
			logger.error("Null Pointer exception {} ", e.getCause());
		}
	}
	

	private String generateKey(String date) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			byte[] bytes = digest.digest(date.getBytes(StandardCharsets.UTF_8));
			return String.format("%032x", new BigInteger(1, bytes));
		} catch (NoSuchAlgorithmException nsae) {
			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).", nsae);
		}
	}

	@SuppressWarnings("unused")
	private void findPattern(ServerWebExchange exchange) {//TODO Block the remote IP address
		String token = exchange.getRequest().getHeaders().get("Authorization").get(0);
		System.out.println("Token: " + token.replace("Bearer ", ""));
		String ipAddress = exchange.getRequest().getRemoteAddress().getHostName();
		System.out.println("Ip-Address: " + ipAddress);
		int port = exchange.getRequest().getRemoteAddress().getPort();
		System.out.println("Port: " + port);
		String method = exchange.getRequest().getMethod().name();
		System.out.println("Method: " + method);
		String path = exchange.getRequest().getPath().value();
		System.out.println("Path: " + path);
		String userAgent = exchange.getRequest().getHeaders().get("User-Agent").get(0);
		System.out.println("User Agent: " + userAgent);
	}

}
