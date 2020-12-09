import java.io.*;
import java.util.Arrays;

public class DNAdbase
{
    //~ Fields ................................................................
    //private static HashTable<String, SequenceBundle> table;
    //private static File file;
    //private static File output;
    //private static MemoryManager memory;
    //private static int size;

    //~ Public  Methods .......................................................
    public static void main(String[] args) throws IOException {
        // The expeted argument order for the DNAdbase is:
        // <command file> <hash file> <max size of hash table> <memory file>
        File file = new File(args[0]);
        File output = new File(args[1]);
        int size = Integer.parseInt(args[2]);
        MemoryManager memory = new MemoryManager(args[3], size);
        HashTable<String, SequenceBundle> table = new HashTable<String,
            SequenceBundle>(SequenceBundle.class, size, memory);

        // The maximum size of the code's hash table is expected to be a
        // multiple of 32.  An issue will also occur if the command file
        // doesn't exist in the same directory.
        if (size % 32 != 0) {
            System.out.println(
                "Error: hashtable size must be a multiple of 32");
        }
        if (!file.exists()) {
            return;
        }

        // The following code will scan the command file, line by line, and
        // split each line by whitespaces.
        String line;
        FileReader reader = new FileReader(file);
        BufferedReader buffered = new BufferedReader(reader);
        while ((line = buffered.readLine()) != null) {
            String[] command = line.split("\\s+");
            // This while loop accounts for leading whitespaces.
            while (command.length > 1 && command[0].length() < 1)
            {
                command = Arrays.copyOfRange(command, 1, command.length);
            }

            if (command[0].equals("insert")) {
                line = buffered.readLine();
                String sequenceId = command[1];
                int length = Integer.parseInt(command[2]);
                String sequence = line;
                // A = 00 | T = 11 | C = 01 | G = 10 | padding = 00 | free = 11

                if (length != sequence.length()) {
                    System.out.printf("Warning: Actual sequence length (%d) "
                        + "does not match given length (%d)\n", sequence.length(),
                        length);
                    length = sequence.length();
                }

                // This portion of code checks if the sequenceID can be inserted
                // into the hash table before the sequence ID is inserted into
                // the memory file.
                if (!table.canInsert(sequenceId)) {
                    return;
                }
                Handle seqIdHandle = memory.insertSeq(sequenceId, size);

                // This portion of code checks if the sequence can be inserted
                // into the hash table before the sequence is inserted into the
                // memory file.
                if (!table.canInsert(sequence)) {
                    return;
                }
                Handle seqHandle = memory.insertSeq(sequence, size);

                // A SequenceBundle object, containing the two handles, is
                // created and inserted into the hash table.
                SequenceBundle val = new SequenceBundle(false, seqIdHandle,
                    seqHandle);
                table.insert(sequenceId, val);
            }
            else if (command[0].equals("remove")) {
                String sequenceID = command[1];
                // The entry with the corresponding sequenceID is removed from
                // the hash table.
                SequenceBundle removeVal = table.remove(sequenceID);

                if (removeVal == null)
                {
                    System.out.printf("SequenceID %s not found\n", sequenceID);
                }
                else
                {
                    // Both the sequence and sequence ID are subsequently
                    // removed from the memory file.
                    memory.removeSeq(removeVal.getIDHandle());
                    byte[] seqBytes = memory.removeSeq(
                        removeVal.getSequenceHandle());
                    String theSeq = ASCIIConverter.BinToACGT(seqBytes,
                        removeVal.getSequenceHandle().getSequenceLength());

                    System.out.printf("Sequence Removed %s\n%s\n", sequenceID, theSeq);
                }
            }
            else if (command[0].equals("print")) {
                // All stored sequences are printed out.
                System.out.print("Sequence IDs:");
                if (table.size() == 0) {
                    System.out.print(" none");
                }
                System.out.println();
                table.printTable();

                // All free blocks are printed out.
                memory.printFreeBlocks();
            }
            else if (command[0].equals("search")) {
                String sequenceID = command[1];
                SequenceBundle find = table.get(sequenceID);
                if (find == null) {
                    System.out.printf("SequenceID %s not found\n", sequenceID);
                }
                else {
                    Handle seqHandle = find.getSequenceHandle();
                    System.out.printf("Sequence Found: %s\n",
                        memory.getSeq(seqHandle));
                }
            }
            else if (command[0].length() > 0) {
                System.out.printf("%s is not a command\n", command[0]);
            }
        }

        buffered.close();
        reader.close();
    }

}
