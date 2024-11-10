package cipher;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * Command line interface to allow users to interact with your ciphers.
 *
 * <p>We have provided some infrastructure to parse most of the arguments. It is your responsibility
 * to implement the appropriate actions according to the assignment specifications. You may choose
 * to "fill in the blanks" or rewrite this class.
 *
 * <p>Regardless of which option you choose, remember to minimize repetitive code. You are welcome
 * to add additional methods or alter the provided code to achieve this.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        int pos = 3;
        try {
            pos = parseCipherType(args, pos);
            int temp = pos;
            pos = parseCipherFunction(args, pos);
            if (temp == pos) { throw new IllegalArgumentException("No Output Function Selected");}
            temp = pos;
            pos = parseOutputOptions(args, pos);
            if (temp == pos) { throw new IllegalArgumentException("No Output Option Selected");}
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static final CipherFactory cipherFactory = new CipherFactory();
    private static Cipher cipher;
    private static String output;

    /**
     * Pulls a single key from the save file for use by a Mono or Vigenere Cipher
     *
     * @param file
     * @param type
     * @return key
     * @throws IOException
     */
    private static String load(String file, String type) throws IOException {
        File encrypted = new File(file);
        FileInputStream encryptInputStream = new FileInputStream(encrypted);
        BufferedReader reader = new BufferedReader(new InputStreamReader(encryptInputStream));
        String line = reader.readLine();
        if (line.equals(type)) {
            return reader.readLine();
        }
        return "ERROR";
    }

    /**
     * Loads a BufferedReader from a file
     *
     * @param file
     * @return
     * @throws FileNotFoundException If the file does not exist or cannot be opened
     */
    private static BufferedReader loadReader(String file) throws FileNotFoundException {
        File encrypted = new File(file);
        FileInputStream encryptInputStream = new FileInputStream(encrypted);
        return new BufferedReader(new InputStreamReader(encryptInputStream));
    }

    /**
     * Reads the keys from an RSA save file
     * @throws IllegalArgumentException Thrown if the file is not an RSA key
     */
    public static RSAKey RSALoad(String file) throws IOException, IllegalArgumentException {
        BufferedReader reader = loadReader(file);
        if ("RSA".equals(reader.readLine())) {
            BigInteger d = new BigInteger(reader.readLine());
            BigInteger e = new BigInteger(reader.readLine());
            BigInteger n = new BigInteger(reader.readLine());
            return new RSAKey(d, e, n);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Set up the cipher type based on the options found in args starting at position pos, and
     * return the index into args just past any cipher type options.
     */
    private static int parseCipherType(String[] args, int pos) throws IllegalArgumentException, IOException {
        // check if arguments are exhausted
        if (pos == args.length) return pos;

        String cmdFlag = args[pos++];
        switch (cmdFlag) {
            case "--caesar":
                try {
                    cipher = cipherFactory.getCaesarCipher(Integer.valueOf(args[pos++]));
                } catch(NumberFormatException e){
                    throw new IllegalArgumentException("Incorrect Caesar shift at pos " + pos);
                }
                break;
            case "--random":
                cipher = cipherFactory.getRandomSubstitutionCipher();
                break;
            case "--monoLoad":
                String key = load(args[pos++], "MONO");
                if (key.equals("ERROR")) {
                    throw new FileNotFoundException("Incorrect File at argument " + pos);
                } else {
                    cipher = cipherFactory.getMonoCipher(key);
                }
                break;
            case "--vigenere":
                try {
                    cipher = cipherFactory.getVigenereCipher(args[pos++]);
                } catch(Exception e){
                    throw new IllegalArgumentException(e.getMessage());
                }
                break;
            case "--vigenereLoad":
                key = load(args[pos++], "VIGENERE");
                if (key.equals("ERROR")) {
                    throw new FileNotFoundException("Incorrect File at argument " + pos);
                } else {
                    cipher = cipherFactory.getVigenereCipher(key);
                }
                break;
            case "--rsa":
                cipher = cipherFactory.getRSACipher();
                break;
            case "--rsaLoad":
                cipher = cipherFactory.getRSACipher(RSALoad(args[pos++]));
                break;
            default:
                throw new IllegalArgumentException("Incorrect Cipher Type at argument " + pos);
        }
        return pos;
    }

    /**
     * Global arrayOutputStream for trying to encrypt a file and puts it into arrayOutputStream
     */
    private static ByteArrayOutputStream arrayOutputStreamEncrypt = null;

    /**
     * Encrypts/Decrypts file through fileinput
     * @param filename Name of input file
     * @param pos position of the argument
     * @param isencrypt Encryption mode or decryption mode
     * @return The next position of the argument
     * @throws IOException
     */
    private static int fileinput(String filename, int pos, boolean isencrypt) throws IOException {
        File encrypted = new File(filename);
        FileInputStream encryptInputStream = new FileInputStream(encrypted);
        arrayOutputStreamEncrypt = new ByteArrayOutputStream();
        if (isencrypt) {
            cipher.encrypt(encryptInputStream, arrayOutputStreamEncrypt);
        } else {
            cipher.decrypt(encryptInputStream, arrayOutputStreamEncrypt);
        }
        arrayOutputStreamEncrypt.flush();
        pos++;
        return pos;
    }

    /**
     * Parse the operations to be performed by the program from the command-line arguments in args
     * starting at position pos. Return the index into args just past the parsed arguments.
     */
    private static int parseCipherFunction(String[] args, int pos) throws IllegalArgumentException, IOException {
        // check if arguments are exhausted
        if (pos == args.length) return pos;
        switch (args[pos++]) {
            case "--em":
                output = cipher.encrypt(args[pos]);
                pos++;
                break;
            case "--ef":
                String filename = args[pos];
                pos = fileinput(filename, pos, true);
                break;
            case "--dm":
                output = cipher.decrypt(args[pos]);
                break;
            case "--df":
                String filename2 = args[pos];
                pos = fileinput(filename2, pos, false);
                break;
            default:
                if (args[--pos].equals("--save")) {
                    parseOutputOptions(args, pos);
                } else {
                    throw new IllegalArgumentException("Illegal Cipher Function at argument " + pos);
                }
        }
        return pos;
    }

    /**
     * Parse options for output, starting within {@code args} at index {@code argPos}. Return the
     * index in args just past such options.
     */
    private static int parseOutputOptions(String[] args, int pos) throws IllegalArgumentException, IOException {
        // check if arguments are exhausted
        if (pos == args.length) return pos;
        int test = pos;
        String cmdFlag;
        while (pos < args.length) {
            switch (cmdFlag = args[pos++]) {
                case "--print":
                    if (arrayOutputStreamEncrypt == null) {
                        System.out.println(output);
                    } else {
                        System.out.println(arrayOutputStreamEncrypt.toString());
                    }
                    break;
                case "--out":
                    cmdFlag = args[pos++];
                    FileOutputStream writing = new FileOutputStream(cmdFlag);
                    if (arrayOutputStreamEncrypt == null) {
                        writing.write(output.getBytes(StandardCharsets.UTF_8));
                    } else {
                        byte[] asdf = arrayOutputStreamEncrypt.toByteArray();
                        writing.write(asdf);
                    }
                    writing.flush();
                    writing.close();
                    break;
                case "--save":
                    cmdFlag = args[pos++];
                    writing = new FileOutputStream(cmdFlag);
                    ByteArrayOutputStream arrayOutputStreamDecrypt = new ByteArrayOutputStream();
                    cipher.save(arrayOutputStreamDecrypt);
                    writing.write(arrayOutputStreamDecrypt.toByteArray());
                    writing.flush();
                    writing.close();
                    break;
                default:
                    if (test == pos) {
                        throw new IllegalArgumentException("Illegal output option at argument " + pos);
                    }
            }
        }
        return pos;
    }
}