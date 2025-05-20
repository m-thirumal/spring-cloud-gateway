package in.thirumal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
/**
 * 
 * @author Thirumal
 *
 */
@RestController
@RequestMapping("/fallback")
public class GatewayFallback {

	@GetMapping("/1")
	public Mono<ResponseEntity<String>> client1Fallback() {
		return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Client 1 - Service Not available"));
	}
	
	@GetMapping("/2")
	public Mono<ResponseEntity<String>> client2Fallback() {
		return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Client 2 - Service Not available"));
	}
	
    @GetMapping("/3")
    public Mono<ResponseEntity<String>> googleFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Google - Service Not available"));
    }  
	
	@GetMapping("/default")
	public Mono<ResponseEntity<String>> defaultFallback() {
		return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Not able to find the route!"));
	}
	
}
