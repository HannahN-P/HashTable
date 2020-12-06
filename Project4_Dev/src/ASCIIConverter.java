/**
 * ASCIIConverter converts between a binary DNA sequence and a DNA sequence 
 * represented as characters.
 * @author ryanm
 *
 */
public class ASCIIConverter {
    
    /**
     * Convert a DNA sequence from a string to a byte[].
     * @param sequence The DNA sequence as a string
     * @return The DNA sequence as a byte[]
     */
    public static byte[] ACGTtoBin(String sequence)
    {
        int bitsNeeded = sequence.length() * 2;
        int numBytes = (bitsNeeded / 8);
        if (bitsNeeded % 8 != 0)
        {
            numBytes += 1;
        }
        byte[] result = new byte[numBytes];
        
        for (int i = 0; i < sequence.length(); i++)
        {
            int shiftAmount = (6 - ((i % 4) * 2));
            int bytePos = i / 4;
            
            if (sequence.charAt(i) == 'A')
            {
                // bits would be 00
                byte mask = 0b00000000;
                mask = (byte)(mask << shiftAmount);
                result[bytePos] = (byte)(result[bytePos] | mask);
            }
            else if (sequence.charAt(i) == 'C')
            {
                // bits would be 01
                byte mask = 0b00000001;
                mask = (byte)(mask << shiftAmount);
                result[bytePos] = (byte)(result[bytePos] | mask);
            }
            else if (sequence.charAt(i) == 'G')
            {
                // bits would be 10
                byte mask = 0b00000010;
                mask = (byte)(mask << shiftAmount);
                result[bytePos] = (byte)(result[bytePos] | mask);
            }
            else if (sequence.charAt(i) == 'T')
            {
                // bits would be 11
                byte mask = 0b00000011;
                mask = (byte)(mask << shiftAmount);
                result[bytePos] = (byte)(result[bytePos] | mask);
            }
        }
        return result;
    }
    
    /**
     * Converts a DNA sequence from a byte[] to a string.
     * @param sequence The DNA sequence as a byte[]
     * @param seqLength The length of the DNA sequence in characters
     * @return The DNA sequence as a string
     */
    public static String BinToACGT(byte[] sequence, int seqLength)
    {
        if ((seqLength * 2) > sequence.length * 8)
        {
            System.out.println("Given sequence length is longer than the number"
                + " of bytes");
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < seqLength; i++)
        {
            byte mask = 0b00000011;
            int shiftAmount = (6 - ((i % 4) * 2));
            int bytePos = i / 4;
            byte currBits = (byte)(sequence[bytePos] >> shiftAmount);
            currBits = (byte)(currBits & mask);
            
            if (currBits == 0b00000000)
            {
                result.append('A');
            }
            else if (currBits == 0b00000001)
            {
                result.append('C');
            }
            else if (currBits == 0b00000010)
            {
                result.append('G');
            }
            else if (currBits == 0b00000011)
            {
                result.append('T');
            }
        }
        return result.toString();
    }
    
}
