import student.TestCase;

/**
 * Tests the methods of Handle.
 *
 * @author Ryan Maxey <ryanmaxey6>
 * @author Hannah Nguyen <hanguyen>
 * @version December 11, 2020
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
        assertEquals(handle.getFileLocation(), 100);
        handle.setFileLocation(125);
        assertEquals(handle.getFileLocation(), 125);
    }

    /**
     * tests getSequenceLength and setSequenceLength
     */
    public void testSetSequenceLength()
    {
        assertEquals(handle.getSequenceLength(), 50);
        handle.setSequenceLength(40);
        assertEquals(handle.getSequenceLength(), 40);
    }

}
