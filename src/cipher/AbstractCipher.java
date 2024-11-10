package cipher;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * An alphabetic {@link Cipher} for encrypting and decrypting Strings and Streams.
 */
public abstract class AbstractCipher implements Cipher {
    public static final String ALPH_STRING = "abcdefghijklmnopqrstuvwxyz";
    public static final char[] ALPH_CHARS = ALPH_STRING.toCharArray();
    public static final int ALPH_LENGTH = ALPH_CHARS.length;

    /**
     * Makes all capital letters lowercase and removes all non-alphabetic or whitespace characters.
     *
     * @param s The string to be cleaned.
     * @return The cleaned string containing only lowercase alphabetic or whitespace characters.
     */
    public static String cleanInput(String s) {
        return s.toLowerCase().replaceAll("[^a-z\\s]", "");
    }

    /**
     * Encrypts or decrypts the given char {@code input}.
     *
     * @param input     The char to be encrypted/decrypted. Must be a lowercase alphabetic character or whitespace.
     * @param isEncrypt Determines if the char is to be encrypted or decrypted. Encrypts the char when true.
     * @return
     */
    abstract protected char translateChar(char input, boolean isEncrypt);

    /**
     * Encrypts or decrypts the given string {@code plaintext} using {@link #translateChar translateChar} after cleaning the string with {@link #cleanInput cleanInput}.
     *
     * @param plaintext The String to be encrypted/decrypted. If being decrypted, the String must only contain characters {@link #translateChar translateChar} can decrypt or whitespace.
     * @param isEncrypt Determines if the String is to be encrypted or decrypted. Encrypts the String when true.
     */
    protected String translateString(String plaintext, boolean isEncrypt) {
        StringBuilder translated = new StringBuilder();
        String cleanPlainText = cleanInput(plaintext);
        for (int i = 0; i < cleanPlainText.length(); i++) {
            translated.append(translateChar(cleanPlainText.charAt(i), isEncrypt));
        }
        return translated.toString();
    }

    /**
     * Encrypts or decrypts the given input stream {@code in} using {@link #translateString translateString} and writes the output to {@code out} using UTF_8.
     *
     * @param in If being decrypted, the InputStream {@code in} must only contain characters {@link #translateString translateString} can decrypt or whitespace.
     * @throws IOException if an I/O error occurs.
     */
    protected void translateStream(InputStream in, OutputStream out, boolean isEncrypt) throws IOException {
        ChunkReader cfr = new Chunker(in);
        while (cfr.hasNext()) {
            String input = cfr.nextStringChunk(128); //Reads in chunks of 128
            String ciphertext = translateString(input, isEncrypt);
            out.write(ciphertext.getBytes(StandardCharsets.UTF_8));
        }
    }


    @Override
    public String encrypt(String plaintext) {
        return translateString(plaintext, true);
    }

    @Override
    public String decrypt(String plaintext) {
        return translateString(plaintext, false);
    }

    @Override
    public void encrypt(InputStream in, OutputStream out) throws IOException {
        translateStream(in, out, true);
    }

    @Override
    public void decrypt(InputStream in, OutputStream out) throws IOException {
        translateStream(in, out, false);
    }
}
