package asu.cse360project.EncryptionHelpers;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * <p> EncryptionUtils. </p>
 * 
 * <p> Description: A class to contain methods for interfacing Encryption functionality to the rest of the project </p>
 * 
 * <p> Copyright: Tu35 © 2024 </p>
 * 
 * @version 1.00	2024-11-17 Added helper methods for encryption and decryption
 * 
 */

public class EncryptionUtils {
	private static int IV_SIZE = 16;
	
	public static char[] toCharArray(byte[] bytes) {		
        CharBuffer charBuffer = Charset.defaultCharset().decode(ByteBuffer.wrap(bytes));
        return Arrays.copyOf(charBuffer.array(), charBuffer.limit());
	}
	
	public static byte[] toByteArray(char[] chars) {		
        ByteBuffer byteBuffer = Charset.defaultCharset().encode(CharBuffer.wrap(chars));
        return Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
	}
		
	public static byte[] getInitializationVector(char[] text) {
		char iv[] = new char[IV_SIZE];
		
		int textPointer = 0;
		int ivPointer = 0;
		while(ivPointer < IV_SIZE) {
			iv[ivPointer++] = text[textPointer++ % text.length];
		}
		
		return toByteArray(iv);
	}
	
	public static void printCharArray(char[] chars) {
		for(char c : chars) {
			System.out.print(c);
		}
	}
}
