/**
 * A SequenceBundle has Handles for the sequence ID and the actual sequence.
 * It also contains a tomb stone flag to indicate if the bundle has been
 * tomb-stoned.
 *
 * @author Ryan Maxey <ryanmaxey6>
 * @author Hannah Nguyen <hanguyen>
 * @version December 11, 2020
 */
public class SequenceBundle {

    private boolean tombStone;
    private Handle idHandle;
    private Handle sequenceHandle;

    /**
     * Creates a SequenceBundle with Handles for the sequence ID and sequence
     * @param t True if this bundle has been tomb-stoned; false otherwise
     * @param id The Handle for the sequence ID
     * @param sequence The Handle for the sequence
     */
    SequenceBundle(boolean t, Handle id, Handle sequence)
    {
        tombStone = t;
        idHandle = id;
        sequenceHandle = sequence;
    }

    /**
     * Gets the flag indicating if this bundle has been tomb-stoned
     * @return True if the bundle is tomb-stoned; false otherwise
     */
    public boolean getTombStone()
    {
        return tombStone;
    }

    /**
     * Sets the flag indicating if this bundle is tomb-stoned
     * @param t The boolean representing the tomb stone
     */
    public void setTombStone(boolean t)
    {
        tombStone = t;
    }

    /**
     * Gets the Handle for the sequence ID
     * @return The Handle for the sequence ID
     */
    public Handle getIDHandle()
    {
        return idHandle;
    }

    /**
     * Sets the Handle for the sequence ID
     * @param id The new Handle for the sequence ID
     */
    public void setIDHandle(Handle id)
    {
        idHandle = id;
    }

    /**
     * Gets the Handle for the actual sequence
     * @return The Handle for the actual sequence
     */
    public Handle getSequenceHandle()
    {
        return sequenceHandle;
    }

    /**
     * Sets the Handle for the sequence
     * @param sequence The new Handle for the sequence
     */
    public void setSequenceHandle(Handle sequence)
    {
        sequenceHandle = sequence;
    }

}
