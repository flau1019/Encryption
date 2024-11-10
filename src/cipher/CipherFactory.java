package cipher;

/**
 * Factory class for creating cipher objects.
 */
public class CipherFactory {

    /**
     * Returns: a monoalphabetic substitution cipher with the English alphabet mapped to the
     * provided alphabet.<br>
     * Requires: {@code encrAlph} contains exactly one occurrence of each English letter and nothing
     * more. No requirement is made on case.
     *
     * @param encrAlph the encrypted alphabet where its length < 26 and each character appears once
     */
    public Cipher getMonoCipher(String encrAlph) {
        encrAlph.toLowerCase();
        String temp = "abcdefghijklmnopqrstuvwxyz";
        if(encrAlph.length() != 26){
            throw new IllegalArgumentException("Incorrect key load for Mono ciphers");
        }
        for(int i = 0; i < encrAlph.length(); i++) {
            char c =  temp.charAt(i);
            if(encrAlph.indexOf(c)==-1|| encrAlph.lastIndexOf(c) != encrAlph.indexOf(c)){
                throw new IllegalArgumentException("Incorrect key load for Mono ciphers");
            }
        }
        return new RandomSubstitutionCipher(encrAlph);
    }

    /**
     * Returns a new Caesar cipher with the given shift parameter.
     *
     * @param shift the cipher's shift parameter
     */
    public Cipher getCaesarCipher(int shift) {
        return new CaesarCipher(shift);
    }

    /**
     * Returns a Vigenere cipher (with multiple shifts).
     *
     * @param key the cipher's shift parameters. Note that a is a shift of 1.
     */
    public Cipher getVigenereCipher(String key) {
        return new VigenereCipher(key);
    }

    /**
     * Returns a new monoalphabetic substitution cipher with a randomly generated mapping.
     */
    public Cipher getRandomSubstitutionCipher() {
        return new RandomSubstitutionCipher();
    }

    /**
     * Returns a new RSA cipher with a randomly generated keys.
     */
    public Cipher getRSACipher() {
        return new RSACipher();
    }

    /**
     * Returns a new RSA cipher with given key.
     */
    public Cipher getRSACipher(RSAKey k) {
        return new RSACipher(k);
    }
}
