package in.thirumal.exception;

import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author Thirumal
 *
 */
@ControllerAdvice
public class ServiceNotFoundHandler {
   
	@ExceptionHandler(NotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleUnavailableService(NotFoundException e) {
    	//System.out.println("ji");
        return "service unavailable";
    }
    
}