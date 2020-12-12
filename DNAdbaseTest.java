import student.TestCase;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Tests the methods of DNAdbase.
 *
 * @author Ryan Maxey <ryanmaxey6>
 * @author Hannah Nguyen <hanguyen>
 * @version December 11, 2020
 */
public class DNAdbaseTest extends TestCase
{

    private DNAdbase dbase;
    private ByteArrayOutputStream outContent;

    /**
     * The setup is necessary for all test cases in this file.  It does so by
     * initiating the DNAdbase and output stream global instances.
     */
    public void setUp()
    {
        dbase = new DNAdbase();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    /**
     * The invalid text file only includes commands that are considered invalid.
     * Therefore, the free block list and hash table should be consistently
     * empty.
     * @throws IOException
     */
    public void testInvalid() throws IOException
    {
        assertNotNull(dbase);

        String[] args = {"invalid.txt", "invalid_out.txt", "10",
            "invalid_mem.bin"};
        dbase.main(args);

        // Rather than an error message, a warning message should be returned
        // while attempting to insert a sequence.  The warning message is not
        // reached if the insert command is invalid.
        assertTrue(outContent.toString().contains("Warning: Actual sequence "
            + "length (1) does not match given length (0)"));
        // A sequence ID is "not found" when an invalid remove or search
        // commands are called.
        assertTrue(outContent.toString().contains("SequenceID CCG not found"));
        assertTrue(outContent.toString().contains("SequenceID GG not found"));
        assertTrue(outContent.toString().contains("SequenceID AACG not found"));
        // The following assertions check the error messages printed from
        // HashTable's canInsert().
        assertTrue(outContent.toString().contains("hashtable size must be a"
            + " multiple of 32"));
        assertTrue(outContent.toString().contains("SequenceID C exists"));

        // The memory and hash files are cleaned up.
        File memory = new File("invalid_mem.bin");
        File hash = new File("invalid_out.txt");
        memory.delete();
        hash.delete();
    }

    /**
     * testInsert will go through a text files that simply calls insert and
     * search commands.
     * @throws IOException
     */
    public void testInsert() throws IOException {
        // All calls to the insert command should be valid with no error or
        // warning messages.  The search command is called on to check that
        // the sequences were added successfully.
        String[] args = {"insert.txt", "insert_out.txt", "32",
            "insert_mem.bin"};
        dbase.main(args);

        assertTrue(outContent.toString().contains(
            "Sequence Found: ACCAGGATTA"));
        assertTrue(outContent.toString().contains("Sequence Found: CGTGC"));
        assertFalse(outContent.toString().contains("not found"));

        // The memory and hash files are cleaned up.
        File memory = new File("insert_mem.bin");
        File hash = new File("insert_out.txt");
        memory.delete();
        hash.delete();
    }

    /**
     * testRemove calls on the insert, remove, and search commands in order to
     * affirm that DNA sequences are successfully removed from the hash table
     * and memory file.
     * @throws IOException
     */
    public void testRemove() throws IOException {
        // All calls to the remove command should be valid, so certain keywords
        // should not be in the outContent stream.  In order to ensure that
        // calls to the remove command work correctly, three sequences are
        // inserted first.
        String[] args = {"remove.txt", "remove_out.txt", "32",
            "remove_mem.bin"};
        dbase.main(args);

        assertTrue(outContent.toString().contains("Sequence Removed AA:\n"
            + "GGAC"));
        assertTrue(outContent.toString().contains("Sequence Removed A:\n"
            + "GG"));
        // The output of search commands will also be checked to make sure
        // the sequence IDs and their sequences have been removed from the
        // hash table.
        assertTrue(outContent.toString().contains("SequenceID AA not found"));
        assertTrue(outContent.toString().contains("SequenceID A not found"));

        // The memory and hash files are cleaned up.
        File memory = new File("remove_mem.bin");
        File hash = new File("remove_out.txt");
        memory.delete();
        hash.delete();
    }

    /**
     * In addition to checking the validity of the insert and delete commands,
     * testPrint is also meant to make sure that free and filled blocks are
     * correctly printed in the right format.  It also handles edge cases for
     * the print command.
     * @throws IOException
     */
    public void testPrint() throws IOException {
        String[] args = {"print.txt", "print_out.txt", "32",
            "print_mem.bin"};
        dbase.main(args);

        assertFalse(outContent.toString().contains("Sequence IDs: none"));
        assertTrue(outContent.toString().contains("A: hash slot"));
        assertTrue(outContent.toString().contains("C: hash slot"));
        assertTrue(outContent.toString().contains("Free Block List: none"));
        assertTrue(outContent.toString().contains("Sequence Removed T:\nCA"));
        assertTrue(outContent.toString().contains(
            "[Block 1] Starting Byte Location: 0, Size 2 bytes"));

        // The memory and hash files are cleaned up.
        File memory = new File("remove_mem.bin");
        File hash = new File("remove_out.txt");
        memory.delete();
        hash.delete();
    }

}
