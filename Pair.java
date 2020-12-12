/**
 * Pair to get and set offset of free block to be used in
 * MemoryManager
 *
 *  @author Hannah Nguyen <hanguyen>
 *  @author Ryan Maxey <ryanmaxey6>
 *  @version December 11, 2020
 */
public class Pair {

    private int fileOffset;
    private int length;

    /**
     * The Pair object is the record-stored object for Memory Manager's free
     * block linked list.
     *
     * @param offset : The location of the corresponding sequence or sequenceID
     * @param len : The length of the corresponding sequence or sequenceID
     */
    public Pair(int offset, int len)
    {
        fileOffset = offset;
        length = len;
    }

    /**
     * This is a getter function for the fileOffset attribute.
     * @return fileOffset : The offset/location of the sequence/sequenceID
     */
    public int getFileOffset()
    {
        return fileOffset;
    }

    /**
     * This is a setter function for the fileOffset attribute.
     * @param offset : The location of the sequence/sequenceID
     */
    public void setFileOffset(int offset)
    {
        fileOffset = offset;
    }

    /**
     * This is a getter function for the length attribute.
     * @return length : The length of the sequence/sequenceID
     */
    public int getLength()
    {
        return length;
    }

    /**
     * This is a setter function for the length attribute.
     * @param len : The length of the sequence/sequenceID
     */
    public void setLength(int len)
    {
        length = len;
    }

}