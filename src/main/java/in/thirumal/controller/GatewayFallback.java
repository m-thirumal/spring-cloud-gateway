package in.thirumal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 
 * @author Thirumal
 *
 */
@RestController
@RequestMapping("/fallback")
public class GatewayFallback {

	@GetMapping("/1")
	public String client1Fallback() {
		return "Client 1 - Service Not available";
	}
	
	@GetMapping("/2")
	public String client2Fallback() {
		return "Client 2 - Service Not available";
	}
	
	@GetMapping("/default")
	public String defaultFallback() {
		return "Not able to find the route!";
	}
	
}
