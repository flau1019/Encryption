package cipher;

import java.util.Random;

public class RandomSubstitutionCipher extends MonoSubstitutionCipher {

    /**
     * If user passes no alphabetMap, then it creates one
     */
    public RandomSubstitutionCipher() {
        super(createAlphabetMap());
    }

    /**
     * If user passes a alphabetMap, then it passes to MonoSubstitution Cipher
     */
    public RandomSubstitutionCipher(String alphabetMap) {
        super(alphabetMap);
    }

    /**
     * Creates a new randomly organized array of ALPH_CHARS
     */
    private static char[] createAlphabetMap() {
        Random random = new Random();
        char[] shuffledAlph = ALPH_CHARS.clone();
        for (int i = 0; i < shuffledAlph.length - 1; i++) {
            swapChars(shuffledAlph, i, random.nextInt(i, shuffledAlph.length));
        }
        return shuffledAlph;
    }

    /**
     * Swaps the characters
     */
    private static void swapChars(char[] chars, int index1, int index2) {
        char temp = chars[index1];
        chars[index1] = chars[index2];
        chars[index2] = temp;
    }


}
