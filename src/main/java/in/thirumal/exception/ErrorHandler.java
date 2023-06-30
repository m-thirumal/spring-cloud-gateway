/**
 * 
 */
package in.thirumal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Thirumal
 *
 */
@ControllerAdvice
@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
    public BadRequestException handleBadRequestException(BadRequestException badRequestException) {
		return badRequestException;
    }
	
}
