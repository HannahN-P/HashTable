/**
 * A Handle contains the file location for the start of a sequence and the
 * length of that sequence in characters.
 *
 * @author Ryan Maxey <ryanmaxey6>
 * @author Hannah Nguyen <hanguyen>
 */
public class Handle {

    private int fileLocation;
    private int sequenceLength;

    /**
     * Creates a Handle with the given location in a file and sequence length
     * in characters.
     * @param location The location of the sequence in a file in bytes
     * @param length The length of the sequence in characters
     */
    public Handle(int location, int length)
    {
        fileLocation = location;
        sequenceLength = length;
    }

    /**
     * Gets the location of the sequence in the file in bytes.
     * @return The file location of the sequence
     */
    public int getFileLocation()
    {
        return fileLocation;
    }

    /**
     * Gets the length of the sequence in characters.
     * @return The sequence length in characters
     */
    public int getSequenceLength()
    {
        return sequenceLength;
    }

    /**
     * Sets the file location for the start of the sequence.
     * @param location The starting location of the sequence in the file
     */
    public void setFileLocation(int location)
    {
        fileLocation = location;
    }

    /**
     * Sets the length of the sequence in characters.
     * @param length The sequence length in characters
     */
    public void setSequenceLength(int length)
    {
        sequenceLength = length;
    }

}
