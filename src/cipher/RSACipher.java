package cipher;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class RSACipher implements Cipher {
    private BigInteger d;
    private BigInteger n;
    private BigInteger e;

    public RSACipher(RSAKey key) {
        d = key.d();
        n = key.n();
        e = key.e();
    }

    public RSACipher() {
        this(generateRandomKey());
    }

    private static RSAKey generateRandomKey() {
        Random rnd = new Random();
        BigInteger q = new BigInteger(510, 20, rnd);
        BigInteger p = new BigInteger(510, 20, rnd);
        BigInteger n = q.multiply(p);
        BigInteger phi = (q.subtract(BigInteger.ONE)).multiply(p.subtract(BigInteger.ONE));
        BigInteger e = new BigInteger(String.valueOf(65537));
        BigInteger d = multiplicativeInverse(phi, e);
        if (d.signum() == -1) d = d.add(n); //Force d to be positive
        return new RSAKey(d, e, n);
    }

    /**
     * Calulates the multiplacativeInverse
     * @param p Totient of n
     * @param e Must be in range 1 < e < phi(n) and relatively prime to {@code phi}
     * @return
     */
    private static BigInteger multiplicativeInverse(BigInteger p, BigInteger e) {
        return e.modInverse(p);
    }

    /**
     * Encrypts the inputed byte[] with the RSA encryption
     * @param input
     * @return
     */
    public byte[] encrypter(byte[] input) {
        BigInteger a = new BigInteger(input);
        BigInteger encrypted = a.modPow(e, n);
        byte[] output = encrypted.toByteArray();
        if(output.length < 128){
            byte[] paddedOutput = new byte[128];
            System.arraycopy(output, 0, paddedOutput, 128 - output.length, output[0]);
        }
        return output;
    }

    public byte[] decrypter(byte[] input) {
        return (new BigInteger(input).modPow(d, n)).toByteArray();
    }

    /**
     * Inputs a file, and the encrypts in chunks
     * @param in File Inputstream goes in
     * @param out OutputStream comes out
     * @throws IOException
     */
    public void translateStream(InputStream in, OutputStream out) throws IOException {
        ChunkReader cfr = new Chunker(in);
        int chunk = cfr.chunkSize();
        byte[] byteArray = new byte[chunk];
        while (cfr.hasNext()) {
            try {
                cfr.nextChunk(byteArray);
                out.write(encrypter(byteArray));

            } catch (IndexOutOfBoundsException ex) {
            }
        }
    }

    /**
     * Inputs a file, and the decrypts in chunks
     * @param in File Inputstream goes in
     * @param out OutputStream comes out
     * @throws IOException
     */
    public void decryptStream(InputStream in, OutputStream out) throws IOException {
        ChunkReader cfr = new Chunker(in);
        int chunk = 128;
        byte[] byteArray = new byte[chunk];
        while (cfr.hasNext()) {
            try {
                cfr.nextChunkDecrypt(byteArray);
                byte[] output = decrypter(byteArray);
                byte[] simplified = new byte[output[0]];
                System.arraycopy(output, 1, simplified, 0, output[0]);
                out.write(simplified);
            } catch (IndexOutOfBoundsException ex) {
            }
        }
    }


    @Override
    public void encrypt(InputStream in, OutputStream out) throws IOException {
        translateStream(in, out);
    }

    @Override
    public void decrypt(InputStream in, OutputStream out) throws IOException {
        decryptStream(in, out);
    }

    @Override
    public String encrypt(String plaintext) {
        throw new IllegalArgumentException("Not allowed to encrypt a string input from command line");
    }

    @Override
    public String decrypt(String ciphertext) {
        throw new IllegalArgumentException("Not allowed to decrypt a string input from command line");
    }

    /**
     * Output key to the OutputStream
     * @param out The OutputStream to write the cipher key to
     * @throws IOException
     */
    @Override
    public void save(OutputStream out) throws IOException {
        out.write("RSA".getBytes(StandardCharsets.UTF_8));
        out.write("\n".getBytes(StandardCharsets.UTF_8));
        out.write(d.toString().getBytes());
        out.write("\n".getBytes(StandardCharsets.UTF_8));
        out.write(e.toString().getBytes());
        out.write("\n".getBytes(StandardCharsets.UTF_8));
        out.write(n.toString().getBytes());
        out.write("\n".getBytes(StandardCharsets.UTF_8));
    }
}
