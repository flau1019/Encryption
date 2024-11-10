package cipher;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class Chunker implements ChunkReader {
    private final int CHUNK_SIZE = 127;
    private InputStream inStream;
    private BufferedReader inReader;

    /**
     * Creates a new Chunker method. Chunker represents the implemetation of ChunkReader.
     */
    public Chunker(InputStream inStream) {
        this.inStream = inStream;
        this.inReader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
    }

    /**
     * Output chunksize.
     */
    @Override
    public int chunkSize() {
        return CHUNK_SIZE;
    }

    /**
     * Returns true if and only if there is at least one more byte of data to be
     * read in the current stream.
     */
    @Override
    public boolean hasNext() {
        int size = 0;
        try {
            size = inStream.available();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return (size > 0);
    }


    /**
     * Returns the next chunk of up to {@code chunkSize()} bytes from the current
     * input stream. The returned bytes are placed in the array {@code data},
     * starting from index 0. The number of bytes returned is always
     * {@code chunkSize()}, unless the end of the input stream has been reached and
     * there are fewer than {@code chunkSize()} bytes available, in which case all
     * remaining bytes are returned. The values in {@code data} after the region in
     * which bytes were written are unspecified.
     *
     * @param data An array of length at least {@code chunkSize()}.
     * @return The number of bytes returned, which is always between 1 and the chunk
     * size.
     * @throws EOFException if there are no more bytes available.
     * @throws IOException  if any IO exception occurs.
     */
    @Override
    public int nextChunk(byte[] data) throws EOFException, IOException {
        byte[] readData = inStream.readNBytes(126);
        int len = readData.length;
        data[0] = (byte) len;
        System.arraycopy(readData, 0, data, 1, len);
        return len;
    }

    /**
     * Edits the data parameter into the next chunk for decryption
     */
    @Override
    public int nextChunkDecrypt(byte[] data) throws EOFException, IOException {
        byte[] readData = inStream.readNBytes(128);
        System.arraycopy(readData, 0, data, 0, 128);
        return readData.length;
    }

    /**
     * Read the next len amount of cahracters from the inputstream and returns the string
     */
    @Override
    public String nextStringChunk(int len) throws IOException {
        StringBuilder chunk = new StringBuilder();
        while(chunk.length() < len){
            int c = inReader.read();
            if(c == -1) break; //End of stream reached;
            chunk.append((char)c);
        }
        return chunk.toString();
    }
}
