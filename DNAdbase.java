import java.io.*;

public class DNAdbase
{
    //~ Fields ................................................................
    static HashTable<String, SequenceBundle> table;
    static File file;
    static File output;
    static MemoryManager memory;

    //~ Public  Methods .......................................................
    public static void main(String[] args) throws IOException {
        file = new File(args[0]);
        output = new File(args[1]);
        int size = Integer.parseInt(args[2]);
        memory = new MemoryManager(args[3]);
        table = new HashTable<String, SequenceBundle>(size, memory);

        if (size % 32 != 0) {
            System.out.println(
                "Error: hashtable size must be a multiple of 32");
        }
        if (!file.exists()) {
            return;
        }

        String line;
        FileReader reader = new FileReader(file);
        BufferedReader buffered = new BufferedReader(reader);
        while ((line = buffered.readLine()) != null) {
            // Call on helper methods
            String[] command = line.split("\\s+");
            if (command[0].equals("insert")) {
                line = buffered.readLine();
                insert(command[1], Integer.parseInt(command[2]), line);
            }
            else if (command[0].equals("remove")) {
                remove(command[1]);
            }
            else if (command[0].equals("print")) {
                print();
            }
            else if (command[0].equals("search")) {
                search(command[1]);
            }
            else {
                System.out.printf("%s is not a command\n", command[0]);
            }
        }
        buffered.close();
        reader.close();
    }


    //~ Private  Methods ......................................................
    private static void insert(String sequenceId, int length, String sequence)
        throws IOException {
        // A = 00 | T = 11 | C = 01 | G = 10 | padding = 00 | free = 11

        // check if the sequenceId can be inserted into the hash table
        if (!table.canInsert(sequenceId)) {
            return;
        }

        // check if the given sequence length matches the actual sequence 
        // length
        if (length != sequence.length()) {
            System.out.printf("Warning: Actual sequence length (%d) "
                + "does not match given length (%d)\n", sequence.length(),
                length);
            length = sequence.length();
        }
        
        // insert the sequenceId into the memory file
        Handle seqIdHandle = memory.insertSeq(sequenceId);
        
        // insert the sequence into the memory file
        Handle seqHandle = memory.insertSeq(sequence);
        
        // create a SequenceBundle object containing the two handles and insert
        // it into the hash table
        SequenceBundle val = new SequenceBundle(false, seqIdHandle, seqHandle);
        table.insert(sequenceId, val);
    }

    private static void remove(String sequenceID) throws IOException {
        
        // remove the entry with the sequenceID from the hash table
        SequenceBundle removeVal = table.remove(sequenceID);
        
        if (removeVal == null)
        {
            System.out.printf("SequenceID %s not found\n", sequenceID);
        }
        else
        {
            // remove the sequenceId and sequence from the memory file
            memory.removeSeq(removeVal.getIDHandle());
            byte[] seqBytes = memory.removeSeq(removeVal.getSequenceHandle());
            String theSeq = ASCIIConverter.BinToACGT(seqBytes, 
                removeVal.getSequenceHandle().getSequenceLength());
            
            System.out.printf("Sequence Removed %s\n%s\n", sequenceID, theSeq);
        }        
    }

    private static void print() throws IOException {
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

    public static void search(String sequenceID) throws IOException {
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
}
