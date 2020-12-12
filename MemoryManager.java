import java.io.*;
import java.util.*;

// -------------------------------------------------------------------------
/**
 *  MemoryManager is the main class of this file. It manages the memory file
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
    private LinkedList<Pair> list;

    //~ Constructors ..........................................................
    /**
     * The constructor sets up an instance of the MemoryManager by setting up
     * the memory file and doubly linked list of free blocks.
     * 
     * @param filename 	name of the file used by the memory manager to
     * 					store strings
     * @param size 		size of the hash table
     * @throws IOException
     */
    public MemoryManager(String filename, int size) throws IOException
    {
        memory = new RandomAccessFile(filename, "rw");
        new FileWriter(filename, false).close();
        list = new LinkedList<Pair>();
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
        int numBytes = byteNeeded(seqHandle.getSequenceLength());
        byte[] result = new byte[numBytes];

        memory.read(result);

        return result;
    }

    /**
     * This is a helper method to the helper methods insertSeq and removeSeq.
     * The function essentially seeks a specified location in the
     * RandomAccessFile and then appends or overwrites to the file.
     *
     * @param sequence : The sequence (as a string) to be inserted into the 
     * file
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

        int memLoc = 0;
        if (list.isEmpty())
        {
            // The sequence is added to the end of the file in this case,
            // because there are no free blocks available in the linked list.
            memLoc = insertString(str, (int)memory.length());
        }
        else {
            Pair bestFit = null;
            int bytes = byteNeeded(str.length());

            // The method searches for a free block that best fits the sequence
            // to be inserted.
            for (Pair block : list) {
                if (block.getLength() >= bytes && (bestFit == null ||
                    block.getLength() < bestFit.getLength())) {
                    bestFit = block;
                    if (block.getLength() == bytes) {
                    	break;
                    }
                }
            }

            // The following if statement is executed if a best fit block has
            // been found; otherwise, the sequence is appended to the end of
            // the memory file.
            if (bestFit != null) {
                // The best fit free block is then updated.
                memLoc = insertString(str, bestFit.getFileOffset());

                bestFit.setFileOffset(bestFit.getFileOffset() + bytes);
                bestFit.setLength(bestFit.getLength() - bytes);
            }
            else {
                memLoc = insertString(str, (int)memory.length());
            }
        }

        // The free block linked list is updated to remove any filled
        // blocks.
        for (Pair block : list) {
            if (block.getLength() <= 0) {
                list.remove(block);
                break;
            }
        }

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
        int numBytes = byteNeeded(seqHandle.getSequenceLength());
        
        byte[] result = new byte[numBytes];
        memory.read(result);

        // A node to insert as a free block in the doubly linked list is
        // created.  The node is inserted into a list that should be ordered by
        // file descriptors.
        byte[] empty = new byte[numBytes];
        // The sequence ID and sequence are promptly removed from the
        // memory file; their bytes are replaced with padding (00) bytes.
        memory.seek(memory.getFilePointer() - numBytes);
        memory.write(empty);
        // freeBlock is the free block that was just inserted.  It will be used
        // to access neighboring nodes in the doubly linked list.
        Pair freeBlock = new Pair(seqHandle.getFileLocation(), numBytes);

        int pos = -1;
        for (int i = 0; i < list.size(); i++) {
            if (freeBlock.getFileOffset() < list.get(i).getFileOffset()) {
                pos = i;
                break;
            }
        }
        if (pos != -1) {
            list.add(pos, freeBlock);
        }
        else {
            list.add(freeBlock);
        }

        // The following loops will merge any neighboring free blocks.
        // The merged free blocks will take on the sequenceID of the
        // earliest of the blocks.
        for (int i = 0; i < list.size() - 1; i++) {
            Pair currBlock = list.get(i);
            Pair nextBlock = list.get(i + 1);
            if (currBlock.getFileOffset() + currBlock.getLength() >=
                nextBlock.getFileOffset()) {

                currBlock.setLength(nextBlock.getFileOffset() -
                    currBlock.getFileOffset() + nextBlock.getLength());
                list.remove(nextBlock);
                i--;
            }
        }

        Pair lastFree = list.get(list.size() - 1);
        if ((lastFree.getFileOffset() + 
        		lastFree.getLength()) == memory.length()) {
            list.remove(lastFree);
            memory.setLength(memory.length() - lastFree.getLength());
        }

        return result;
    }

    /**
     * This is for the print function in the DNAdbase.  It prints all of the
     * free blocks from the doubly linked list (DLL).
     * @throws IOException
     */
    public void printFreeBlocks() throws IOException
    {
        System.out.print("Free Block List:");
        if (list.size() == 0) {
            System.out.print(" none");
        }
        System.out.println();
        for (int l = 0; l < list.size(); l++) {
            System.out.printf("[Block %d] Starting Byte Location: %d, "
                + "Size %d bytes\n", l + 1, list.get(l).getFileOffset(),
                list.get(l).getLength());
        }
    }

    private int byteNeeded(int len) {
    	int bitsNeeded = len * 2;
        int numBytes = (bitsNeeded / 8);
        if (bitsNeeded % 8 != 0)
        {
            numBytes += 1;
        }
        return numBytes;
    }

}
