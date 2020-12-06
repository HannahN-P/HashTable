import student.TestCase;

/**
 * Tests the methods of Handle.
 * @author ryanm
 *
 */
public class HandleTest extends TestCase {

    private Handle handle;
    
    /**
     * Creates a new handle for testing.
     */
    public void setUp()
    {
        handle = new Handle(100, 50);
    }
    
    /**
     * tests getFileLocation and setFileLocation
     */
    public void testSetFileLocation()
    {
        assertTrue(handle.getFileLocation() == 100);
        handle.setFileLocation(125);
        assertTrue(handle.getFileLocation() == 125);
    }
    
    /**
     * tests getSequenceLength and setSequenceLength
     */
    public void testSetSequenceLength()
    {
        assertTrue(handle.getSequenceLength() == 50);
        handle.setSequenceLength(40);
        assertTrue(handle.getSequenceLength() == 40);
    }
    
}
