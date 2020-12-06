import java.io.*;

public class MemoryManager
{
    //~ Fields ................................................................
    private RandomAccessFile memory;

    //~ Constructors ..........................................................
    public MemoryManager(String filename) throws FileNotFoundException
    {
        memory = new RandomAccessFile(filename, "rw");
    }

    //~Public  Methods ........................................................
    /**
     * The function to return a sequence, given its file descriptor and length.
     * @param loc : The location of the sequence in a file (not accounting for
     *              padding)
     * @param len : The length of a specific sequence, which is used by the
     *              function to determine how many bytes should be returned
     * @throws IOException
     */
    public byte[] getSeq(int loc, int len) throws IOException {
        memory.seek(loc);
        String builder = "";
        for (int l = 0; l < len; l++) {
            builder += memory.readChar();
        }
        return ASCIIConverter.ACGTtoBin(builder);
    }

    public int insertString(String sequence, int loc)
        throws IOException {

        memory.seek(loc);
        // RandomAccessFile will replace bytes instead of appending or
        // inserting when it's written to.
        byte[] seq = ASCIIConverter.ACGTtoBin(sequence);
        memory.write(seq);

        return loc + sequence.length();
    }
}
