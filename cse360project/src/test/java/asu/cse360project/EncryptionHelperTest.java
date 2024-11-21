package asu.cse360project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import asu.cse360project.EncryptionHelpers.EncryptionHelper;

//declaring the JUnit testing for encryption and decryption methods
public class EncryptionHelperTest {

    @Test
    public void EncryptionTest() throws Exception{
        EncryptionHelper helper = new EncryptionHelper();
        assertNotEquals("cse360 project!", helper.encrypt("hello"));
    }
    

    @Test
    void testEncryptionDecryptionWithSimpleText() throws Exception {
        EncryptionHelper encryptionHelper = new EncryptionHelper();

        String input = "HelloWorld";
        String encrypted = encryptionHelper.encrypt(input);
        String decrypted = encryptionHelper.decrypt(encrypted);
        assertEquals(input, decrypted, "Decrypted text doesnt match the original input");
    }

    @Test
    void testEncryptionDecryptionWithEmptyString() throws Exception {
        EncryptionHelper encryptionHelper = new EncryptionHelper();

        String input = "";
        String encrypted = encryptionHelper.encrypt(input);
        String decrypted = encryptionHelper.decrypt(encrypted);
        assertEquals(input, decrypted, "Decrypted text doesnt match the original input");
    }

    @Test
    void testEncryptionDecryptionWithRandomString() throws Exception {
        EncryptionHelper encryptionHelper = new EncryptionHelper();

        String input = "A1b@C#d3E   !Fg$5^H* Ij&    ^6724gyUGWK8_L-M+9N%O)P(Q=R:  S;T|    U`V~W<X,Y.Z/0\r\n";
        String encrypted = encryptionHelper.encrypt(input);
        String decrypted = encryptionHelper.decrypt(encrypted);
        assertEquals(input, decrypted, "Decrypted text doesnt match the original input");
    }

    @Test
    void testEncryptionDecryptionWithSpecialCharacters() throws Exception{
        EncryptionHelper encryptionHelper = new EncryptionHelper();

        String input = "!@#$%^&*(_56@)+|<=ohno>?:{}[];'\"";
        String encrypted = encryptionHelper.encrypt(input);
        String decrypted = encryptionHelper.decrypt(encrypted);
        assertEquals(input, decrypted, "Decrypted text doesnt handle special characters correctly");
    }

    @Test
    void testEncryptionDecryptionWithSpaces() throws Exception {
        EncryptionHelper encryptionHelper = new EncryptionHelper();

        String input = "   adding a higgggge      amount    of   spacessss     ";
        String encrypted = encryptionHelper.encrypt(input);
        String decrypted = encryptionHelper.decrypt(encrypted);
        assertEquals(input, decrypted, "Decrypted text doesnt preserve spaces");
    }

    @Test
    void testEncryptionDecryptionWithNumbers() throws Exception {
        EncryptionHelper encryptionHelper = new EncryptionHelper();

        String input = "1234567890";
        String encrypted = encryptionHelper.encrypt(input);
        String decrypted = encryptionHelper.decrypt(encrypted);
        assertEquals(input, decrypted, "Decrypted text doesnt preserve numbers");
    }

    @Test
    void testEncryptionDecryptionWithLongText() throws Exception {
        EncryptionHelper encryptionHelper = new EncryptionHelper();

        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longText.append("Encryption__Test__@#@#");
        }
        String input = longText.toString();
        String encrypted = encryptionHelper.encrypt(input);
        String decrypted = encryptionHelper.decrypt(encrypted);
        assertEquals(input, decrypted, "Decrypted text doesnt match the original input");
    }

    @Test
    void testEncryptionDecryptionWithEmptyStringMultipleTimes() throws Exception {
        EncryptionHelper encryptionHelper = new EncryptionHelper();

        String input = "";
        for (int i = 0; i < 10; i++) {
            String encrypted = encryptionHelper.encrypt(input);
            String decrypted = encryptionHelper.decrypt(encrypted);
            assertEquals(input, decrypted, "Decrypted text doesnt remain consistent with multiple encryptions and decryptions");
        }
    }

    @Test
    void testEncryptionWithNullInput() throws Exception {
        EncryptionHelper encryptionHelper = new EncryptionHelper();

        assertThrows(IllegalArgumentException.class, () -> {
            encryptionHelper.encrypt(null);
        }, "Encrypting null input should throw an exception");
    }

    @Test
    void testDecryptionWithNullInput() throws Exception {
        EncryptionHelper encryptionHelper = new EncryptionHelper();

        assertThrows(IllegalArgumentException.class, () -> {
            encryptionHelper.decrypt(null);
        }, "Decrypting null input should throw an exception");
    }

    
}
    