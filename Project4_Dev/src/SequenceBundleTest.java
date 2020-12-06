import student.TestCase;

/**
 * Tests the methods of SequenceBundle.
 * @author ryanm
 *
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
        assertTrue(bundle.getIDHandle().getFileLocation() == 50);
        assertTrue(bundle.getIDHandle().getSequenceLength() == 5);
        bundle.setIDHandle(new Handle(100, 10));
        assertTrue(bundle.getIDHandle().getFileLocation() == 100);
        assertTrue(bundle.getIDHandle().getSequenceLength() == 10);
    }
    
    /**
     * tests getSequenceHandle and setSequenceHandle
     */
    public void testSetSequenceHandle()
    {
        assertTrue(bundle.getSequenceHandle().getFileLocation() == 140);
        assertTrue(bundle.getSequenceHandle().getSequenceLength() == 70);
        bundle.setSequenceHandle(new Handle(160, 50));
        assertTrue(bundle.getSequenceHandle().getFileLocation() == 160);
        assertTrue(bundle.getSequenceHandle().getSequenceLength() == 50);
    }
    
}
