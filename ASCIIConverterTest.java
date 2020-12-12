import student.TestCase;

/**
 * Tests the methods of ASCIIConverter.
 *
 * @author Ryan Maxey <ryanmaxey6>
 * @author Hannah Nguyen <hanguyen>
 * @version December 11, 2020
 */
public class ASCIIConverterTest extends TestCase {

    /**
     * tests ACGTtoBin
     */
    public void testACGTtoBin()
    {
        String sequence = "CCCCATGGACGT";
        byte[] result = ASCIIConverter.acgtToBin(sequence);
        for (int i = 0; i < result.length; i++)
        {
            System.out.printf("0x%02X\n", result[i]);
        }
        assertEquals(0b01010101, result[0]);
        assertEquals(0b00111010, result[1]);
        assertEquals(0b00011011, result[2]);
    }

    /**
     * tests BinToACGT
     */
    public void testBinToACGT()
    {
        byte b1 = 0b00011011;
        byte b2 = 0b00110110;
        byte[] bytes = new byte[2];
        bytes[0] = b1;
        bytes[1] = b2;
        String result = ASCIIConverter.binToACGT(bytes, 8);
        System.out.println(result);
        assertEquals("ACGTATCG", result);

        byte b3 = 0b00110000;
        bytes = new byte[3];
        bytes[0] = b1;
        bytes[1] = b2;
        bytes[2] = b3;
        result = ASCIIConverter.binToACGT(bytes, 10);
        System.out.println(result);
        assertEquals("ACGTATCGAT", result);
    }

}
