package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cipher.Cipher;
import cipher.CipherFactory;
import cipher.Main;
import org.junit.jupiter.api.Test;

import java.io.*;

public class BasicCipherTests {

    private final CipherFactory cipherFactory = new CipherFactory();

    @Test
    void testBasicCaesar() throws IOException {
        Cipher caesar = cipherFactory.getCaesarCipher(5);
        assertEquals("wmnst", caesar.encrypt("rhino"));
        assertEquals("btrgfy", caesar.encrypt("wombat"));
        assertEquals("", caesar.encrypt(""));
        assertEquals("fghi", caesar.encrypt("ABCD"));
        //Main.main({""});
    }



    @Test
    void testBasicVigenere() throws IOException {
        Cipher vigenere = cipherFactory.getVigenereCipher("abc");
        assertEquals("agesc", vigenere.encrypt("zebra"));
        assertEquals("uqpptupy", vigenere.encrypt("tomorrow"));


    }

    @Test
    void testBasicRandom() throws IOException {
        Cipher random = cipherFactory.getRandomSubstitutionCipher();
        String s = "albatross";
        assertEquals(s, random.decrypt(random.encrypt(s)));
        assertEquals(s, random.encrypt(random.decrypt(s)));
    }

    @Test
    void testBasicRSA() throws Exception {
        Cipher rsa = cipherFactory.getRSACipher();
        String s = "dog";
        File f = new File("C:\\Users\\flau1\\IdeaProjects\\A2\\src\\tests\\Input.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(f);
        f.createNewFile();
        rsa.encrypt(new ByteArrayInputStream(s.getBytes()), fileOutputStream);
        fileOutputStream.flush();
        FileInputStream fileInputStream = new FileInputStream(f);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(3);
        rsa.decrypt(fileInputStream, arrayOutputStream);
        arrayOutputStream.flush();
        assertEquals(s, arrayOutputStream.toString().trim());
        f.delete();
    }
}
