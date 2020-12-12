import student.TestCase;

/**
 * Tests the methods of SequenceBundle.
 *
 * @author Ryan Maxey <ryanmaxey6>
 * @author Hannah Nguyen <hanguyen>
 * @version December 11, 2020
 */
public class SequenceBundleTest extends TestCase {

    private SequenceBundle bundle;

    /**
     * creates a new SequenceBundle for testing
     */
    public void setUp()
    {
        Handle idHandle = new Handle(50, 5);
        Handle sequenceHandle = new Handle(140, 70);
        bundle = new SequenceBundle(false, idHandle, sequenceHandle);
    }

    /**
     * tests getTombStone and setTombStone
     */
    public void testSetTombStone()
    {
        assertFalse(bundle.getTombStone());
        bundle.setTombStone(true);
        assertTrue(bundle.getTombStone());
    }

    /**
     * tests getIDHandle and setIDHandle
     */
    public void testSetIDHandle()
    {
        assertEquals(bundle.getIDHandle().getFileLocation(), 50);
        assertEquals(bundle.getIDHandle().getSequenceLength(), 5);
        bundle.setIDHandle(new Handle(100, 10));
        assertEquals(bundle.getIDHandle().getFileLocation(), 100);
        assertEquals(bundle.getIDHandle().getSequenceLength(), 10);
    }

    /**
     * tests getSequenceHandle and setSequenceHandle
     */
    public void testSetSequenceHandle()
    {
        assertEquals(bundle.getSequenceHandle().getFileLocation(), 140);
        assertEquals(bundle.getSequenceHandle().getSequenceLength(), 70);
        bundle.setSequenceHandle(new Handle(160, 50));
        assertEquals(bundle.getSequenceHandle().getFileLocation(), 160);
        assertEquals(bundle.getSequenceHandle().getSequenceLength(), 50);
    }

}
