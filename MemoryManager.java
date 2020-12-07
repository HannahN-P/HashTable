import java.io.*;

//OVERWRITTEN/NEW DATA STRUCTURES
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



public class MemoryManager
{
    //~ Fields ................................................................
    private RandomAccessFile memory;
    private DLL list;

    //~ Constructors ..........................................................
    public MemoryManager(String filename) throws FileNotFoundException
    {
        memory = new RandomAccessFile(filename, "rw");
        list = new DLL(new Node(0, size, null, null));
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
    public byte[] getSeq(Handle seqHandle) throws IOException {
        memory.seek(seqHandle.getFileLocation());
        int bitsNeeded = seqHandle.getSequenceLength() * 2;
        int numBytes = (bitsNeeded / 8);
        if (bitsNeeded % 8 != 0)
        {
            numBytes += 1;
        }
        byte[] result = new byte[numBytes];
        
        memory.read(result);
        
        return result;
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
    
    public Handle insertSeq(String sequence)
    {
        /**
         * 1. Use free block list to determine where to insert the sequence
         * 2. Insert the sequence
         * 3. Create a handle with the file offset of the sequence and the 
         * length in characters. Should be in the form: (beginning of sequence 
         * in memory file, sequence.length())
         * 4. Update the free block list
         * 5. Return the handle
         */
        
        
        // code from DNAdbase
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
    }
    
    public byte[] removeSeq(Handle seqHandle)
    {
        /**
         * 1. Use the Handle to locate the sequence in the memory file. 
         * 2. Store sequence in variable so it can be returned
         * 3. Overwrite the sequence in memory file with 0s
         * 4. Update free block list
         * 5. Return the variable containing the sequence
         * 
         * The below code should cover steps 1 and 2
         */
        
        memory.seek(seqHandle.getFileLocation());
        int bitsNeeded = seqHandle.getSequenceLength() * 2;
        int numBytes = (bitsNeeded / 8);
        if (bitsNeeded % 8 != 0)
        {
            numBytes += 1;
        }
        byte[] result = new byte[numBytes];
        
        memory.read(result);
        
        
        
        // code from DNAdbase
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
    }
    
    public void printFreeBlocks()
    {
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

}
