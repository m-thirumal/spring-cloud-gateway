package in.thirumal.recaptcha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.BadRequestException;
import reactor.core.publisher.Mono;

/**
 * @author Thirumal
 *
 */
@Component
public class PreGlobalFilter implements GlobalFilter, Ordered {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CaptchaService captchaService;
	/**
	 * Highest precedence is the first in the “pre”-phase 
	 * i.e The lower the order the higher the priority it has.
	 */
	@Override
	public int getOrder() {
		return -1;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		logger.debug("Pre filter for captcha");
		if (exchange.getRequest().getQueryParams().containsKey("recaptcha")) {
			logger.debug("Started validating ===> re-captha");
			String captchaVerifyMessage = captchaService.isCaptchaValid(exchange.getRequest().getQueryParams().getFirst("recaptcha"));
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			try {
				ReCaptchaResponse reCaptchaResponse = objectMapper.readValue(captchaVerifyMessage, ReCaptchaResponse.class);
				logger.debug("Captcha response {}", captchaVerifyMessage);
				if (!reCaptchaResponse.isSuccess()) {
					String errorMessage = "Recaptcha validation failed";
					if (reCaptchaResponse.getErrorCodes().contains("timeout-or-duplicate")) {
						errorMessage = "ReCaptcha Expired \u231B, Try Again \u21BA";
					} else if (reCaptchaResponse.getErrorCodes().contains("invalid-keys")) {
						errorMessage = "Invalid re-Captcha: Site Key Mismatch \u21BA ";
					}
					throw new BadRequestException(errorMessage);
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} 
		}
		return chain.filter(exchange);
	}

//	private ResponseEntity<?> verifyCaptcha(String recaptchaResponse, String ip) {
//		String captchaVerifyMessage = captchaService.isCaptchaValid(recaptchaResponse);
//		logger.debug("Re-Captcha response from G:=> {} ", captchaVerifyMessage);
//		if (org.apache.commons.lang3.StringUtils.isNotEmpty(captchaVerifyMessage)) {
//			Map<String, Object> response = new HashMap<>();
//			response.put("message", captchaVerifyMessage);
//			return ResponseEntity.badRequest().body(response);
//		}
//		return ResponseEntity.badRequest().body(Boolean.TRUE);
//	}
//	
//	private String getIpAddress(ServerWebExchange exchange) {
//		XForwardedRemoteAddressResolver resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(1);
//        InetSocketAddress inetSocketAddress = resolver.resolve(exchange);
//        return inetSocketAddress.getAddress().getHostAddress();
//	}
//
//	@Bean
//	public GlobalFilter responseFilter(){
//	   return new PreGlobalFilter();
//	}

}
