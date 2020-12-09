import java.io.*;

//OVERWRITTEN/NEW DATA STRUCTURES
 // -------------------------------------------------------------------------
 /**
  *  The node class is a simple overwritten class with unique attributes.
  *
  *  It has a file location stored as ID, sequence length stored as length,
  *  a link to the previous node stored as prev, and a link to the next node
  *  stored as next.
  *
  *  @author Hannah Nguyen <hanguyen>
  *  @author Ryan Maxey <ryanmaxey6>
  *  @version Dec 8, 2020
  */
class Node {
    int id;
    int length;
    Node prev;
    Node next;

    /**
     * This is the single constructor for the overwritten Node class.
     *
     * @param i : The file descriptor (location) of the new node
     * @param l : The character length of the sequence or sequence ID
     * @param prev : The previous node
     * @param next : The next node
     */
    Node(int i, int l, Node p, Node n) {
        id = i;
        length = l;
        prev = p;
        next = n;
    }
}

 // -------------------------------------------------------------------------
 /**
  *  DLL stands for doubly linked list.  It's a list of Node objects where
  *  each node can access neighboring nodes.  It has an attribute for the
  *  head node and an attribute for the list's length.
  *
  *  @author Hannah Nguyen <hanguyen>
  *  @author Ryan Maxey <ryanmaxey6>
  *  @version Dec 8, 2020
  */
class DLL {
    Node head;
    int length = 1;

    /**
     * The constructor only sets the head node as the starting length of the DLL
     * will be based on whether or not the head node is null.
     *
     * @param h : The head (first) node of the new doubly linked list
     */
    DLL(Node h) {
        head = h;
        if (h == null) {
            length = 0;
        }
        // The following will reset the starting length in the case that the
        // head node already has next nodes.
        Node node = head;
        while (node.next != null) {
            length++;
            node = node.next;
        }
    }

    /**
     * bestFit will return the index of the free block slot (in the current
     * DLL) that is most suited for a sequence.  The "best fit" is chosen by
     * the free block with closest length that is greater or equal to the
     * sequence's length.
     *
     * @param min : This is the sequence length, which would be the minimum
     *              length for the "best fit" block
     * @param best : The index of the "best fit" block in the DLL is stored in
     *               the integer variable best
     */
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

    /**
     * The function will return a node from the current DLL.
     *
     * @param index : The index, starting at 0, of the node that should be retrieved
     * @return node : The node that corresponds to the index parameter
     */
     public Node get(int index) {
         Node node = this.head;
         int i = 0;
         while (node != null && i != index) {
             node = node.next;
             i++;
         }
         return node;
     }

     /**
      * The function inserts a Node object into the current DLL.  The inserted
      * node's position is based off of its file location.
      *
      * The insert order is decided by parsing the DLL until the function
      * reaches a node that has a ID that's greater or equal to the parameter
      * node's ID.  The node that the function will stop at is the node that
      * should go after the inserted node.
      *
      * @param ins : The node (with an ID and length) that should be inserted
      */
     public void insert(Node ins) {
         Node node = head;
         while (node.next != null && node.id < ins.id) {
             node = node.next;
         }
         if (node.prev != null) {
             node.prev.next = ins;
         }
         ins.next = node;
         ins.prev = node.prev;
         node.prev = ins;

         if (node == head) {
             head = node.prev;
         }
         length++;
     }

     /**
      * The function removes the specified Node object from the current DLL.
      * The node is deleted by setting the previous and next nodes of
      * neighboring nodes.
      *
      * @param del : This is the node that should be deleted from the DLL.
      */
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

         length--;
         return;
     }

     /**
      * The replace function will overwrite the attributes of a node at a
      * specified index.
      *
      * @param index : index is the location of the node to be replaced in the
      *                DLL; the index will count from 0 (the head of the DLL)
      * @param rep : The rep node is a container for the attributes that will
      *              be replaced in the original node
      * @return boolean : The boolean returned will indicate the success of the
      *                   replacement
      */
     public boolean replace(int index, Node rep) {
         if (index > length - 1 || rep == null) {
             return false;
         }
         int i = 0;
         Node node = head;
         while (i != index) {
             node = node.next;
             i++;
         }
         if (node.prev != null) {
             node.prev.next = rep;
         }
         if (node.next != null) {
             node.next.prev = rep;
         }
         node.id = rep.id;
         node.length = rep.length;

         return true;
     }
}

// -------------------------------------------------------------------------
/**
 *  MemoryManager is the main class of this file.  It manages the memory file
 *  by writing to it.  Most of its functions end up being called by the
 *  DNAdbase class.
 *
 *  @author Hannah Nguyen <hanguyen>
 *  @author Ryan Maxey <ryanmaxey6>
 *  @version Dec 8, 2020
 */
public class MemoryManager
{
    //~ Fields ................................................................
    private RandomAccessFile memory;
    private DLL list;

    //~ Constructors ..........................................................
    /**
     * The constructor sets up an instance of the MemoryManager by setting up
     * the memory file and doubly linked list of free blocks.
     */
    public MemoryManager(String filename, int size) throws FileNotFoundException
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
        // The function will jump to the beginning of a sequence and read in
        // the sequence bytes.
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

    /**
     * This is a helper method to the helper methods insertSeq and removeSeq.
     * The function essentially seeks a specified location in the
     * RandomAccessFile and then appends or overwrites to the file.
     *
     * @param sequence : The sequence (as a string) to be inserted into the file
     * @param loc : This integer should be the file location of the
     *              corresponding sequence parameter
     * @return loc + sequence : This is the current file location after writing
     *                          to the RandomAccessFile
     */
    public int insertString(String sequence, int loc)
        throws IOException {

        memory.seek(loc);
        // RandomAccessFile will replace bytes instead of appending or
        // inserting when it's written to.
        byte[] seq = ASCIIConverter.ACGTtoBin(sequence);
        // TODO: It's uncertain on whether or not RandomAccessFile will only
        //       overwrite X amount of bytes in the file, from loc.  X would be
        //       the length of the sequence in bytes.
        memory.write(seq);

        return loc;
    }

    /**
     * This helps the insert() function in DNAdbase.  It takes a string, either
     * the sequence or sequence ID, inserts it into the memory file and updates
     * the doubly linked list.
     *
     * @param str : The string to be inserted can be the sequence or sequenceID
     * @param size : The total length of the HashTable that's set from DNAdbase
     * @return ret : ret is a Handle object with the file location and length
     */
    public Handle insertSeq(String str, int size)
        throws IOException
    {
        /**
         * 1. Use free block list to determine where to insert the sequence
         * 2. Insert the sequence
         * 3. Create a handle with the file offset of the sequence and the
         * length in characters. Should be in the form: (beginning of sequence
         * in memory file, sequence.length())
         * 4. Update the free block list
         * 5. Return information about the inserted sequence and sequenceID
         */

        int best = list.bestFit(str.length());
        int printLoc = list.get(best).id;
        // In this case, the code cannot find the "best fit" inside of the free
        // block list and the sequence won't overflow from the list.  If there
        // is no empty slot for the sequence (or sequenceID), then it should be
        // added to the back of the hash table.
        if (best == -1 && best + str.length() > size) {
            int loc = list.get(list.length - 1).id;
            Node last = new Node(loc, str.length(), null, null);

            // The last free node is retrieved by parsing through the doubly
            // linked list.
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
        else if (best >= 0) {
            // In this case, the "best fit" free block has been found, and the
            // remaining free space for the block is calculated.
            int free = list.get(best).length - str.length();
            if (free == 0) {
                // If the free block is equal to the inserted block, in size,
                // then the free block is deleted from the linked list.
                list.delete(list.get(best));
            }
            else {
                // If an inserted block is smaller than the free block it's
                // being placed in, then the block is inserted before the
                // remaining free space.
                list.replace(best, new Node(list.get(best).id + str.length(),
                    free, list.get(best).prev, list.get(best).next));
                // Therefore, the node is replaced with a smaller size and a
                // file descriptor offset.
            }
        }
        else {
            // This indicates that there was an error in implementing the
            // bestFit function.
            return null;
        }

        // The string is written into the memory file and the locations of that
        // string, as bytes, and the inserted sequence is returned as a Handle
        // object.
        int memLoc = this.insertString(str, printLoc);
        return new Handle(memLoc, str.length());
    }

    /**
     * The removeSeq function is a helper method for the remove function in the
     * DNAdbase class.
     *
     * @param seqHandle : The sole parameter is a handle object with the ID and
     *                    length of the sequence to be removed
     * @return result : The returned result is a byte array that's read in from
     *                  the memory file (before removal)
     */
    public byte[] removeSeq(Handle seqHandle) throws IOException
    {
        /**
         * 1. Use the Handle to locate the sequence in the memory file.
         * 2. Store sequence in variable so it can be returned
         * 3. Overwrite the sequence in memory file with 0s
         * 4. Update free block list
         * 5. Return information about the removed sequence
         */

        // The following segment will retrieve the string to be removed.
        // The string will be converted to a byte array.
        memory.seek(seqHandle.getFileLocation());
        int bitsNeeded = seqHandle.getSequenceLength() * 2;
        int numBytes = (bitsNeeded / 8);
        if (bitsNeeded % 8 != 0)
        {
            numBytes += 1;
        }
        byte[] result = new byte[numBytes];
        memory.read(result);

        // A node to insert as a free block in the doubly linked list is
        // created.  The node is inserted into a list that should be ordered by
        // file descriptors.
        Node empty = new Node(seqHandle.getFileLocation(),
            seqHandle.getSequenceLength(), null, null);
        list.insert(empty);
        // find is the free block that was just inserted.  It will be used to
        // access neighboring nodes in the doubly linked list.
        Node find = list.head;
        while (find.next != null && find.id != seqHandle.getFileLocation()) {
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
            while (node.next != null && node.id != find.id) {
                node = node.next;
                index++;
            }
            list.replace(index, new Node(node.prev.id,
                node.length + node.prev.length, node.prev, node.next));
            // DLL's delete function will only remove the first node it
            // encounters, traversing from the head of the list, with the
            // same ID.
            list.delete(node.prev);
            find = node;
            list.length--;
        }

        // The sequence ID and sequence are promptly removed from the
        // memory file; their bytes are replaced with padding (00) bytes.
        int len = result.length;
        int loc = seqHandle.getFileLocation();
        // The tLen (total length) variable is based on the individual length
        // (of the byte array) and its padding.
        int tLen = len;
        if (tLen % 4 != 0) {
            tLen = (int)(Math.ceil((double)len / 4) * 4);
        }
        memory.seek(loc);
        byte[] filler = new byte[tLen];
        memory.write(filler);

        return result;
    }

    /**
     * This is for the print function in the DNAdbase.  It prints all of the
     * free blocks from the doubly linked list (DLL).
     */
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
