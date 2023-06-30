/**
 * 
 */
package in.thirumal.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import in.thirumal.cryptography.EncryptDecryptHelper;
import reactor.core.publisher.Mono;

/**
 * @author Thirumal
 *
 */
@Component
@Order(-2) // Ensure this custom exception handler is executed before the default exception handler
public class ExceptionHandlerForEncryption implements WebExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(ExceptionHandlerForEncryption.class);
	
	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		ServerHttpRequest request = exchange.getRequest();
	    ServerHttpResponse response = exchange.getResponse();
	    logger.debug("Request {}", request.getPath());
		String[] subPaths = exchange.getRequest().getPath().toString().split("/");		
	    if (subPaths.length > 2 && subPaths[1].endsWith("-api") && ex instanceof BadRequestException) {
	    	logger.debug("The request {} is encrypted", exchange.getRequest().getPath());
	    	// Set response
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            // Encrypt the response body
            String responseBody = "{\"status\":" + HttpStatus.BAD_REQUEST.value() + ", \"message\":\"" + ex.getMessage() + "\"}";
            String encryptedMessage = EncryptDecryptHelper.encrypt(responseBody);
            byte[] encryptedBytes = encryptedMessage.getBytes();
            //
            return response.writeWith(Mono.just(response.bufferFactory().wrap(encryptedBytes)));
	    } else {
	    	logger.debug("The request {} is plain text", exchange.getRequest().getPath());
	    	return Mono.error(ex);
	    }
	}

}
