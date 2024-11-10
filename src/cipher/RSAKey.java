package cipher;

import java.math.BigInteger;

/**
 * @param d decryption key
 * @param e encryption key
 * @param n modulus
 */
record RSAKey(BigInteger d, BigInteger e, BigInteger n) {
}