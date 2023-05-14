/**
 * 
 */
package in.thirumal.logging;

import java.io.ByteArrayOutputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author Thirumal
 *
 */
@Component
public class LoggingWebFilter implements WebFilter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        // log the request body
        ServerHttpRequest decoratedRequest = getDecoratedRequest(request);
        // log the response body
        ServerHttpResponseDecorator decoratedResponse = getDecoratedResponse(response, request, dataBufferFactory);
        return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build());
	}
	
	private ServerHttpResponseDecorator getDecoratedResponse(ServerHttpResponse response, ServerHttpRequest request, DataBufferFactory dataBufferFactory) {
		return new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {
            	if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        DefaultDataBuffer joinedBuffers = new DefaultDataBufferFactory().join(dataBuffers);
                        byte[] content = new byte[joinedBuffers.readableByteCount()];
                        joinedBuffers.read(content);
                        String responseBody = new String(content, StandardCharsets.UTF_8);//MODIFY RESPONSE and Return the Modified response
                        logger.info("Response decorate ==> RequestId: {}, method: {}, url: {}, response body :{}", request.getId(), request.getMethod(), request.getURI(), responseBody);
                        return dataBufferFactory.wrap(responseBody.getBytes());
                    })
                    .switchIfEmpty(Flux.defer(() -> {
                        System.out.println("Write to database here");
                        return Flux.just();
                    }))
                    ).onErrorResume(err -> {
                    	logger.error("error while decorating Response: {}", err.getMessage());
                        return Mono.empty();
                    });
                } else {
                    System.out.println("2000000000");
                }
                return super.writeWith(body);
            }
        };
    }
	
	private ServerHttpRequest getDecoratedRequest(ServerHttpRequest request) {
        return new ServerHttpRequestDecorator(request) {
            @Override
            public Flux<DataBuffer> getBody() {
            	logger.info("requestId: {}, method: {} , url: {}", request.getId(), request.getMethod(), request.getURI());
                return super.getBody().publishOn(Schedulers.boundedElastic()).doOnNext(dataBuffer -> {
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                        String body = baos.toString(StandardCharsets.UTF_8);
                        logger.info("Request Decorate ==> For requestId: {}, the request body is :{}", request.getId(), body);
                    } catch (Exception e) {
                    	logger.error(e.getMessage());
                    }
                });
            }
        };
    }

}
