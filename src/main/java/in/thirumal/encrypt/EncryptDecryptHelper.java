/**
 * 
 */
package in.thirumal.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thirumal
 *
 */
public class EncryptDecryptHelper {

	private static final Logger logger = LoggerFactory.getLogger(EncryptDecryptHelper.class);
	
	private static final String SECRET_KEY = "Thirumal";

	private static SecretKeySpec secretKey;
	private static byte[] key;
	
	private EncryptDecryptHelper() {
		super();
	}

	public static String encrypt(String strToEncrypt){
		return encrypt(strToEncrypt, SECRET_KEY);
	}

	public static String decrypt(String strToDecrypt){
		return decrypt(strToDecrypt, SECRET_KEY);
	}
	
	public static String decrypt(String strToDecrypt, String secret) {
		if (!isBase64(strToDecrypt)) {
			return strToDecrypt;
		}
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		}
		catch (Exception e) {
			logger.error("Error while decryptin {}", e.getMessage());
			return strToDecrypt;
		}
	}

	public static String encrypt(String strToEncrypt, String secret) {
		try	{
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
		}
		catch (Exception e)	{
			logger.error("Error while decryptin {}", e.getMessage());
			return strToEncrypt;
		}
	}

	private static void setKey(String myKey) {
		MessageDigest sha = null;
		try {
			key = myKey.getBytes(StandardCharsets.UTF_8);
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Check the data is in encrypted in Base64 or not
	 * @param data
	 * @return whether 
	 */
	public static boolean isBase64(String data) {
        String pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        Pattern r = Pattern.compile(pattern);
        return r.matcher(data).find();
    }
}
