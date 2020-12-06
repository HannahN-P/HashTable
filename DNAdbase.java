import java.io.*;
import java.util.Hashtable;

// OVERWRITTEN/NEW DATA STRUCTURES
class Node {
    int id;
    int length;
    Node prev;
    Node next;

    Node(int i, int l, Node p, Node n) {
        id = i;
        length = l;
        prev = p;
        next = n;
    }
}

class DLL {
    Node head;
    int length = 1;

    DLL(Node h) {
        head = h;
    }

    public int bestFit(int min) {
        Node node = this.head;
        int best = 0;
        int i = 1;

        while (node != null) {
            if (node.length < best && node.length >= min) {
                best = i;
            }
            node = node.next;
            i++;
        }

        return best;
    }

    public Node get(int index) {
        Node node = this.head;
        int i = 0;
        while (node != null && i != index) {
            node = node.next;
            i++;
        }
        return node;
    }

    public void insert(Node ins) {
        Node node = head;
        while (node.next != null && node.id < ins.id) {
            node = node.next;
        }
        node.prev.next = ins;
        ins.next = node;
        ins.prev = node;
        node.prev = ins;
    }

    public void delete(Node del) {
        if (this.head == null || del == null) {
            return;
        }
        if (this.head == del) {
            head = del.next;
        }
        if (del.next != null) {
            del.next.prev = del.prev;
        }
        if (del.prev != null) {
            del.prev.next = del.next;
        }
        return;
    }

    public boolean replace(int index, Node rep) {
        int i = 0;
        Node node = head;
        while (i != index) {
            node = node.next;
        }
        node.next.prev = rep;
        node.prev.next = rep;
        return true;
    }
}

class Pair {
    int fileLocation;
    int length;

    // Getters
    public int getLoc() {return fileLocation;}
    public int getLen() {return length;}
    // Setters
    public void setLoc(int l) {fileLocation = l;}
    public void setLen(int l) {length = l;}
}

// MAIN CLASS
public class DNAdbase
{
    //~ Fields ................................................................
    static HashTable<String, SequenceBundle> table;
    static DLL list;
    static File file;
    static File output;
    static MemoryManager memory;

    //~ Public  Methods .......................................................
    public static void main(String[] args) throws IOException {
        file = new File(args[0]);
        output = new File(args[1]);
        int size = Integer.parseInt(args[2]);
        memory = new MemoryManager(args[3]);
        list = new DLL(new Node(0, size, null, null));
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

        if (!table.canInsert(sequence)) {
            return;
        }

        if (length != sequence.length()) {
            System.out.printf("Warning: Actual sequence length (%d) "
                + "does not match given length (%d)\n", sequence.length(),
                length);
            length = sequence.length();
        }
        int best = list.bestFit(length);
        if (best == -1) {
            Node last = new Node(
                table.get(sequenceId).getIDHandle().getFileLocation(), length,
                null, null);
            Node node = list.head;
            if (node == null) {
                list.head = last;
            }
            while (node.next != null) {
                node = node.next;
            }
            last.prev = node;
            node.next = last;
        }
        else {
            int free = length - list.get(best).length;
            if (free == 0) {
                list.delete(list.get(best));
            }
            else {
                // If an inserted block is smaller than the free block it's
                // being placed in, then the block is inserted before the
                // remaining free space.
                list.replace(best, new Node(list.get(best).id + length, free,
                    list.get(best).prev, list.get(best).next));
            }
        }

        // Both the sequenceID and sequence are written into the memory file
        // and the locations of where those strings, as bytes, in the file are
        // returned.
        int idLoc = memory.insertString(sequenceId, best);
        int seqLoc = memory.insertString(sequence,
            best + ASCIIConverter.ACGTtoBin(sequence).length);

        SequenceBundle val = new SequenceBundle(false, new Handle(idLoc,
            sequenceId.length()), new Handle(seqLoc, length));
        table.insert(sequence, val);
    }

    private static void remove(String sequenceID) throws IOException {
        int pos = table.get(sequenceID).getIDHandle().getFileLocation();
        if (pos < 0) {
            System.out.printf("SequenceID %s not found\n", sequenceID);
        }
        else {
            Node empty = new Node(pos,
                table.get(sequenceID).getIDHandle().getSequenceLength(), null,
                null);
            list.insert(empty);
            list.length++;
            Node find = list.head;
            while (find.next != null && find.id != pos) {
                find = find.next;
            }
            // The following while loops will merge any neighboring free blocks.
            // The merged free blocks will take on the sequenceID of the
            // earliest of the blocks.
            while (find.next != null &&
                find.id + find.length == find.next.id) {

                Node node = list.head;
                int index = 0;
                while (node.next != null && node.id != find.id) {
                    node = node.next;
                    index++;
                }
                list.replace(index, new Node(node.id,
                    node.length + node.next.length, node.next, node.prev));
                list.delete(node.next);
                find = node;
                list.length--;
            }
            while (find.prev != null &&
                find.prev.id + find.prev.length == find.id) {

                Node node = list.head;
                int index = 0;
                while (node.next != null && node.next.id != find.id) {
                    node = node.next;
                    index++;
                }
                list.replace(index, new Node(node.prev.id,
                    node.length + node.prev.length, node.next, node.prev));
                // DLL's delete function will only remove the first node it
                // encounters, traversing from the head of the list, with the
                // same ID.
                list.delete(node.prev);
                find = node;
                list.length--;
            }

            // The sequence ID and sequence are promptly removed from the
            // memory file; their bytes are replaced with padding (00) bytes.
            int idLen = ASCIIConverter.ACGTtoBin(sequenceID).length;
            int seqLen = memory.getSeq(
                table.get(sequenceID).getSequenceHandle().getFileLocation(),
                table.get(
                    sequenceID).getSequenceHandle().getSequenceLength()).length;
            int idLoc = table.get(sequenceID).getIDHandle().getFileLocation();
            // The individual length variables are based off the byte arrays.
            int tLen = (int)(Math.ceil((idLen + seqLen) / 4) * 4);
            int count = 0;
            while (count < tLen) {
                memory.insertString("", idLoc);
            }

            // TODO: THIS IS A CALL TO HASHTABLE'S REMOVE FUNCTION; THIS CHECK
            //       IS TO ENSURE PROPER IMPLEMENTATION.
            Handle take = table.remove(sequenceID).getSequenceHandle();
            System.out.printf("Sequence Removed %s\n%s\n", sequenceID,
                memory.getSeq(take.getFileLocation(), take.getSequenceLength()));
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
        System.out.print("Free Block List:");
        if (list.length == 0) {
            System.out.print(" none");
        }
        System.out.println();
        for (int l = 0; l < list.length; l++) {
            System.out.printf("[Block %d] Starting Byte Location: %d, "
                + "Size %d bytes\n", l, list.get(l).id, list.get(l).length);
        }
    }

    public static void search(String sequenceID) throws IOException {
        SequenceBundle find = table.get(sequenceID);
        if (find == null || find.getTombStone()) {
            System.out.printf("SequenceID %s not found\n", sequenceID);
        }
        else {
            Handle seq =
                table.get(sequenceID).getSequenceHandle();
            System.out.printf("Sequence Found: %s\n",
                memory.getSeq(seq.getFileLocation(), seq.getSequenceLength()));
        }
    }
}
