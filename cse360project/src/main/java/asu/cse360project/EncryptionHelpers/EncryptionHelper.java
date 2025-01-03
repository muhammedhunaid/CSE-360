package asu.cse360project.EncryptionHelpers;

import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * <p> EncryptionHelper. </p>
 * 
 * <p> Description: A class to contain helper functions for encryption and decryption of messages. </p>
 * 
 * <p> Copyright: Tu35 © 2024 </p>
 * 
 * @version 1.00	2024-11-17 Added encryption and decryption methods
 * 
 */

public class EncryptionHelper {

	private static String BOUNCY_CASTLE_PROVIDER_IDENTIFIER = "BC";	
	private Cipher cipher;
	
	//defining the same Initialization Vector for encryption and decryptions
	private static final byte[] IV = EncryptionUtils.getInitializationVector("cse360project".toCharArray());
	
	byte[] keyBytes = new byte[] {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
	private SecretKey key = new SecretKeySpec(keyBytes, "AES");

	public EncryptionHelper() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", BOUNCY_CASTLE_PROVIDER_IDENTIFIER);		
	}
	
	public String encrypt(String plainText) throws Exception {

		if (plainText == null) {
			throw new IllegalArgumentException("Input cannot be null");
		}
	
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));
		
		//converting the plain text to bytes
		byte[] bytesText = plainText.getBytes();
		
		//encrypting the bytesText
		byte[] encrypted_ByteText = cipher.doFinal(bytesText);
		
		//return and encode the encrypted bytes to Base64 and return as a String 
		return Base64.getEncoder().encodeToString(encrypted_ByteText);
		
	}
	
	public String decrypt(String cipherText) throws Exception {

		if (cipherText == null) {
			throw new IllegalArgumentException("Encrypted input cannot be null");
		}
		
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));
		
		//decoding the Base64 encoded cipherText to get the encrypted bytes
	    byte[] encryptedBytes = Base64.getDecoder().decode(cipherText);
	    
	 //decrypting the encrypted bytes to get plain text bytes
	    byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
				
	 //convert the decrypted bytes to a String and return it 
		return new String(decryptedBytes);
	}
	
}
