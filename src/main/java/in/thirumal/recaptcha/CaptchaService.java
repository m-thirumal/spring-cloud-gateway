/**
 * 
 */
package in.thirumal.recaptcha;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.ws.rs.BadRequestException;

/**
 * @author Thirumal
 *
 */
@Service("captchaService")
public class CaptchaService  {

	private static final Logger logger = LoggerFactory.getLogger(CaptchaService.class);
	// latest
	@Value("${google.recaptcha.key.secret}") 
	String recaptchaSecret;
	
	private static final String GOOGLE_RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
	private static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
	
	private boolean responseSanityCheck(final String response) {
		return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
	}
	
	/**
	 * Validates Google reCAPTCHA V2 or Invisible reCAPTCHA.
	 *
	 * @param secretKey Secret key (key given for communication between your
	 * site and Google)
	 * @param response reCAPTCHA response from client side.
	 * (g-recaptcha-response)
	 * @return true if validation successful, false otherwise.
	 */
	public String isCaptchaValid(String response) {
		logger.debug("Validating catpcha {}", response);
		if (!responseSanityCheck(response)) {
			throw new BadRequestException("Invalid recaptcha format!");
		}
	    try {
	        String url = GOOGLE_RECAPTCHA_VERIFY_URL,
	                params = "secret=" + recaptchaSecret + "&response=" + response;

	        HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
	        http.setDoOutput(true);
	        http.setRequestMethod("POST");
	        http.setRequestProperty("Content-Type",
	                "application/x-www-form-urlencoded; charset=UTF-8");
	        OutputStream out = http.getOutputStream();
	        out.write(params.getBytes(StandardCharsets.UTF_8));
	    
	        out.flush();
	        out.close();

	        InputStream res = http.getInputStream();
	        BufferedReader rd = new BufferedReader(new InputStreamReader(res, StandardCharsets.UTF_8));

	        StringBuilder sb = new StringBuilder();
	        int cp;
	        while ((cp = rd.read()) != -1) {
	            sb.append((char) cp);
	        }
	        res.close();
	        return sb.toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return org.apache.commons.lang3.StringUtils.EMPTY;
	}	

}
