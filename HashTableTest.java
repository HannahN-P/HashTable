import student.TestCase;

import java.io.IOException;

/**
 * Tests the methods of HashTable
 *
 * @author Ryan Maxey <ryanmaxey6>
 * @author Hannah Nguyen <hanguyen>
 * @version December 11, 2020
 *
 * @param <K> The key class of a HashTable should always be a string.
 * @param <V> The value class of a HashTable should always be a SequenceBundle.
 */
public class HashTableTest<K, V extends SequenceBundle> extends TestCase {

    private HashTable<String, SequenceBundle> hashTable;

    /**
     * The setup of this test will instantiate the hash table, for testing, and
     * its corresponding memory manager.
     */
    public void setUp() throws IOException
    {
        int size = 32;
        MemoryManager mem = new MemoryManager("memoryfile.bin", size);
        hashTable = new HashTable<String, SequenceBundle>(SequenceBundle.class,
            size, mem);

    }

    /**
     * tests remove
     * @throws IOException
     */
    public void testRemove() throws IOException {
        String key = "";
        String seqId = "AAA";
        SequenceBundle removeVal = hashTable.remove(key);
        assertEquals(removeVal, null);
        removeVal = hashTable.remove(seqId);
        assertEquals(removeVal, null);

        assertEquals(hashTable.size(), 0);
    }
}
