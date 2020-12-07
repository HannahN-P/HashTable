import java.io.IOException;

/**
 * A hash table with keys and values
 * @author ryanm
 *
 * @param <K> The key
 * @param <V> The value
 */
public class HashTable<K, V extends SequenceBundle> {

    /**
     * The size of a bucket is 32 slots
     */
    static final int BUCKET_SIZE = 32;
    private V[] table;
    private int size;
    private int capacity;
    private MemoryManager manager;

    /**
     * Creates a new HashTable with the given capacity and a MemoryManager for
     * accessing keys in memory.
     * @param capacity The capacity of the HashTable
     * @param m The MemoryManager to use
     */
    @SuppressWarnings("unchecked")
    public HashTable(int capacity, MemoryManager m)
    {
        size = 0;
        this.capacity = capacity;
        Object[] temp = new Object[capacity];
        table = (V[]) temp;
        manager = m;
    }

    /**
     * Hash the given string for the given table capacity.
     * @param s The string to hash
     * @param M The capacity of the hash table
     * @return A long representing the index to place the object with the given
     * string in the hash table
     */
    private long sfold(String s, int M)
    {
        int intLength = s.length() / 4;
        long sum = 0;
        for (int j = 0; j < intLength; j++) {
          char c[] = s.substring(j * 4, (j * 4) + 4).toCharArray();
          long mult = 1;
          for (int k = 0; k < c.length; k++) {
            sum += c[k] * mult;
            mult *= 256;
          }
        }

        char c[] = s.substring(intLength * 4).toCharArray();
        long mult = 1;
        for (int k = 0; k < c.length; k++) {
          sum += c[k] * mult;
          mult *= 256;
        }

        sum = (sum * sum) >> 8;
        return(Math.abs(sum) % M);
    }

    /**
     * Gets the next position in the probe sequence. Keep in mind that the
     * probe sequence must remain in the same bucket.
     * @param home The home position of the key
     * @param count The count of where we wish to be in the probe sequence
     * @return The next position in the probe sequence
     */
    private int probe(int home, int count)
    {
        int lowerBound = (home / BUCKET_SIZE) * BUCKET_SIZE;
        int probePosition = ((home + count) % BUCKET_SIZE) + lowerBound;
        return probePosition;
    }

    /**
     * Removes the entry associated with the given key from the hash table
     * @param key The key of the entry to remove
     * @return The value of the removed entry if found; null otherwise
     * @throws IOException
     */
    public V remove(K key) throws IOException
    {
        int home = (int)sfold(key.toString(), capacity);
        int pos = home;

        for (int i = 1; i <= BUCKET_SIZE; i++)
        {
            if (table[pos] == null)
            {
                // found an empty slot which means the entry is not in the
                // hash table
                return null;
            }
            else if (!table[pos].getTombStone())
            {
                // if an entry is not a tomb stone, then we need to check if it
                // has the same sequenceID as the one we are trying to remove
                // if it does, then we should tomb-stone it
                Handle idHandle = table[pos].getIDHandle();
                byte[] seqId = manager.getSeq(idHandle);
                String seqIdString = ASCIIConverter.BinToACGT(seqId,
                    idHandle.getSequenceLength());
                if (seqIdString.equals(key.toString()))
                {
                    V seq = table[pos];
                    table[pos].setTombStone(true);
                    size--;
                    return seq;
                }
            }
            pos = probe(home, i);
        }

        return null;
    }

    /**
     * Searches the hash table for the entries corresponding with the given key
     * @param key The key to search for
     * @return The value associated with the key if there is one; null
     * otherwise
     * @throws IOException
     */
    public V get(K key) throws IOException
    {
        int home = (int)sfold(key.toString(), capacity);
        int pos = home;

        for (int i = 1; i <= BUCKET_SIZE; i++)
        {
            if (table[pos] == null)
            {
                return null;
            }
            else if (!table[pos].getTombStone())
            {
                // if an entry is not a tomb stone, then we need to check if it
                // has the same sequenceID as the one we are looking for
                // if it does, then we have found the entry
                Handle idHandle = table[pos].getIDHandle();
                byte[] seqId = manager.getSeq(idHandle);
                String seqIdString = ASCIIConverter.BinToACGT(seqId,
                    idHandle.getSequenceLength());
                if (seqIdString.equals(key.toString()))
                {
                    // found the matching entry; return it
                    return table[pos];
                }
            }
            pos = probe(home, i);
        }

        return null;
    }

    /**
     * Determines if the given key can be inserted into the hash table.
     * @param key The key to insert
     * @return True if there is an empty space or a tomb stone to overwrite and
     * an entry with the same key is not already in the hash table; false
     * otherwise
     * @throws IOException
     */
    public boolean canInsert(K key) throws IOException
    {
        int home = (int)sfold(key.toString(), capacity);
        int pos = home;

        boolean foundTombStone = false;

        for (int i = 1; i <= BUCKET_SIZE; i++)
        {
            if (table[pos] == null)
            {
                // found an empty slot
                return true;
            }
            else if (table[pos].getTombStone() && !foundTombStone)
            {
                // found the first tomb stone
                foundTombStone = true;
            }
            else if (!table[pos].getTombStone())
            {
                // if an entry is not a tomb stone, then we need to check if it
                // has the same sequenceID as the one we are trying to insert
                // if it does, then we should reject the insertion
                Handle idHandle = table[pos].getIDHandle();
                byte[] seqId = manager.getSeq(idHandle);
                String seqIdString = ASCIIConverter.BinToACGT(seqId,
                    idHandle.getSequenceLength());
                if (seqIdString.equals(key.toString()))
                {
                    System.out.printf("SequenceID %s exists\n", key);
                    return false;
                }
            }
            pos = probe(home, i);
        }

        if (!foundTombStone) {
            System.out.printf("Bucket full. "
                + "Sequence %s could not be inserted\n", key);
        }
        return foundTombStone;
    }

    /**
     * Inserts the value associated with the key into the hash table.
     * @param key The key to determine where to place the value
     * @param value The value to place in the hash table
     * @throws IOException
     */
    public void insert(K key, V value) throws IOException
    {
        int home = (int)sfold(key.toString(), capacity);
        int pos = home;

        boolean foundTombStone = false;
        int tombStonePos = -1;

        for (int i = 1; i <= BUCKET_SIZE; i++)
        {
            if (table[pos] == null)
            {
                // found an empty slot
                table[pos] = value;
                size++;
                return;
            }
            else if (table[pos].getTombStone() && !foundTombStone)
            {
                // found the first tomb stone
                foundTombStone = true;
                tombStonePos = pos;
            }
            else if (!table[pos].getTombStone())
            {
                // if an entry is not a tomb stone, then we need to check if it
                // has the same sequenceID as the one we are trying to insert
                // if it does, then we should reject the insertion
                Handle idHandle = table[pos].getIDHandle();
                byte[] seqId = manager.getSeq(idHandle);
                String seqIdString = ASCIIConverter.BinToACGT(seqId,
                    idHandle.getSequenceLength());
                if (seqIdString.equals(key.toString()))
                {
                    return;
                }
            }
            pos = probe(home, i);
        }

        if (foundTombStone)
        {
            table[tombStonePos] = value;
            size++;
        }

    }

    /**
     * Output array interpretation of HashTable in console
     * @throws IOException
     */
    public void printTable() throws IOException
    {
        for (int h = 0; h < size; h++) {
            if (table[h] != null && !table[h].getTombStone())
            {
                System.out.printf("%s: hash slot [%d]\n",
                    manager.getSeq(table[h].getIDHandle()), h);
            }
        }
        return;
    }

    /**
     * Gets the size of the hash table
     * @return The size of the hash table
     */
    public int size()
    {
        return size;
    }
}