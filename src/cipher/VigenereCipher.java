package cipher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class VigenereCipher extends AbstractCipher {

    private char[] key;

    /**
     * Index of the next character in the key that will be used for encrypting the next input character.
     * Example: If key = ['a','b','c'] and pos = 2, the next char used for encryption will be 'c'
     */
    private int pos = 0;


    /**
     *
     * @param key length less than 128, not empty, and only contain alphabetic characters
     *
     */
    public VigenereCipher(String key) {
        key = key.toLowerCase();
        if(!(key.length() < 128 || !(key.equals("")) || (key.matches("^[a-zA-Z]*$")))){
            throw new IllegalArgumentException("Illegal key for Vigenere");
        }
        this.key = key.toLowerCase().toCharArray();
    }

    /**
     * Encrypts the input
     * @param input     The char to be encrypted/decrypted. Must be a lowercase alphabetic character or whitespace.
     * @param isEncrypt Determines if the char is to be encrypted or decrypted. Encrypts the char when true.
     * @return Returns the encrypted input
     */
    @Override
    protected char translateChar(char input, boolean isEncrypt) {
        if (!Character.isLetter(input)) return input; //Don't encrypt whitespace
        int keyChar = isEncrypt ? key[pos] +1 : -key[pos]-1;
        char encryptedChar;
        if(isEncrypt) {encryptedChar = (char) (Math.floorMod((input + keyChar - 2 * 'a'), 26) + 'a'); }
        else {encryptedChar = (char) (Math.floorMod((input + keyChar), 26) + 'a'); }
        pos = (pos + 1) % key.length;
        return encryptedChar;
    }

    /**
     * Encrypt or decrypts file
     * @param plaintext The String to be encrypted/decrypted. If being decrypted, the String must only contain characters {@link #translateChar translateChar} can decrypt or whitespace.
     * @param isEncrypt Determines if the String is to be encrypted or decrypted. Encrypts the String when true.
     * @return
     */
    @Override
    protected String translateString(String plaintext, boolean isEncrypt) {
        pos = 0; //Reset pos if a new thing is being encrypted
        return super.translateString(plaintext, isEncrypt);
    }

    /**
     * Encrypts or decrypts message
     * @param in If being decrypted, the InputStream {@code in} must only contain characters {@link #translateString translateString} can decrypt or whitespace.
     * @param out
     * @param isEncrypt Boolean whether it's encrypting or not to put into translateStream
     * @throws IOException
     */
    @Override
    protected void translateStream(InputStream in, OutputStream out, boolean isEncrypt) throws IOException {
        pos = 0; //Reset pos if a new thing is being encrypted
        super.translateStream(in, out, isEncrypt);
    }

    /**
     * Output key to the OutputStream
     * @param out The OutputStream to write the cipher key to
     * @throws IOException
     */
    @Override
    public void save(OutputStream out) throws IOException {
        out.write("VIGENERE".getBytes(StandardCharsets.UTF_8));
        out.write("\n".getBytes(StandardCharsets.UTF_8));
        out.write((new String(key)).getBytes(StandardCharsets.UTF_8));
        out.write("\n".getBytes(StandardCharsets.UTF_8));
    }
}
