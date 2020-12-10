public class Pair {

    private int fileOffset;
    private int length;

    public Pair (int offset, int len)
    {
        fileOffset = offset;
        length = len;
    }

    public int getFileOffset()
    {
        return fileOffset;
    }

    public void setFileOffset(int offset)
    {
        fileOffset = offset;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int len)
    {
        length = len;
    }

}