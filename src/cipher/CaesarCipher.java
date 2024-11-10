package cipher;


public class CaesarCipher extends MonoSubstitutionCipher {
    /**
     * Creates a Caesar Cipher with the given shift. Supports positive, negative, and 0 shifts.
     * Calls the MonoSubstutionCipher(String alphabetMap) in MonoSubStitutionCipher.java
     * @param shift
     */
    public CaesarCipher(int shift) {
        super(createAlphabetMap(shift));
    }

    /**
     * Creates an alphabet map based on the shift
     * @param shift
     * @return The alphabet map
     */
    private static String createAlphabetMap(int shift) {
        int shiftAdjusted = Math.floorMod(shift, 26);
        return (ALPH_STRING.substring(shiftAdjusted, ALPH_LENGTH) + ALPH_STRING.substring(0, shiftAdjusted));
    }
}