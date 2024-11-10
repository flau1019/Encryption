package cipher;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link AbstractCipher} in which the every lowercase alphabetic char maps to a unique lowercase alphabetic char. This mapping represents the encryption of a char and decryption can be represented with a reversed version of this map.
 */
public abstract class MonoSubstitutionCipher extends AbstractCipher {
    /* Class invariants: Key contains the 26 unique lowercase alphabetic chars 'a' - 'z'.
                         For every char in AbstractCipher.ALPH_CHARS, encoding key maps that char to the char in the same index in key.
                         For every char in key, decoding key maps that char to the char in the same index in AbstractCipher.ALPH_CHARS.
     */

    /**
     * Key representing a specific MonoSubstitutionCipher. Contains the 26 unique lowercase alphabetic chars 'a' - 'z'.
     */
    protected final char[] key;
    /**
     * Mapping of an unencoded char in the range [a, z] to an encoded char in the range [a, z]. Reversed map of {@code decodingKey}.
     */
    private final Map<Character, Character> encodingKey = new HashMap<>();

    /**
     * Mapping of an encoded char in the range [a, z] to an unencoded char in the range [a, z]. Reversed map of {@code encodingKey}.
     */
    private final Map<Character, Character> decodingKey = new HashMap<>();

    /**
     * Creates a MonoSubstitutionCipher from an {@code alphabetMap}.
     * @param alphabetMap Specific ordering of the array of 26 lowercase characters 'a' - 'z' that the alphabetic ordering of characters 'a' - 'z' will be mapped to. Must have length = 26.
     */
    public MonoSubstitutionCipher(char[] alphabetMap) {
        //Initializing encoding and decoding maps
        assert (alphabetMap.length == ALPH_CHARS.length);
        for (int i = 0; i < alphabetMap.length; i++) {
            char oldChar = ALPH_CHARS[i];
            char newChar = alphabetMap[i];
            encodingKey.put(oldChar, newChar);
            decodingKey.put(newChar, oldChar);
        }
        key = alphabetMap;
    }

    /**
     * Calls the MonoSubstitutionCipher(char[] alphabetMap) constructor
     * @param alphabetMap
     */
    public MonoSubstitutionCipher(String alphabetMap) {
        this(alphabetMap.toCharArray());
    }

    /**
     * Translates a single character using the provided mapping. Translation will be an encoding if {@code isEncrypt} is true, otherwise it will be a decoding
     *
     * @param input Char to be translated.
     *              Requires {@code input} to be either a lowercase letter or whitespace.
     */
    @Override
    protected char translateChar(char input, boolean isEncrypt) {
        Map<Character, Character> key = isEncrypt ? encodingKey : decodingKey;
        if (Character.isAlphabetic(input)) return key.get(input); //Returns the translated character
        return " ".charAt(0);
    }

    /**
     * Save the key of MONO into an output stream
     * @param out The OutputStream to write the cipher key to
     * @throws IOException
     */
    @Override
    public void save(OutputStream out) throws IOException {
        out.write("MONO".getBytes());
        out.write("\n".getBytes(StandardCharsets.UTF_8));
        out.write(new String(key).getBytes());
        out.write("\n".getBytes(StandardCharsets.UTF_8));
    }
}