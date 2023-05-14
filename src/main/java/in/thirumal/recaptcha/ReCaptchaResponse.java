/**
 * 
 */
package in.thirumal.recaptcha;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Thirumal
 *
 */
@NoArgsConstructor@AllArgsConstructor
@Getter@Setter
@ToString
public class ReCaptchaResponse implements Serializable {

	private static final long serialVersionUID = 454042018838271607L;

	private boolean success;
	@JsonAlias("error-codes")
	private List<String> errorCodes;
}
