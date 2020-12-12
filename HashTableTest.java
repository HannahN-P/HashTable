import student.TestCase;

import java.io.IOException;

/**
 * Tests the methods of HashTable
 *
 * @author Ryan Maxey <ryanmaxey6>
 * @author Hannah Nguyen <hanguyen>
 */
public class HashTableTest<K, V extends SequenceBundle>
	extends TestCase {

    private HashTable<String, SequenceBundle> hashTable;
    private MemoryManager manager;

    public void setUp() throws IOException
    {
        // manager = new MemoryManager();
    	int size = 32;
        MemoryManager manager = new MemoryManager("memoryfile.bin", size);
        hashTable = new HashTable<String, SequenceBundle>(SequenceBundle.class,
        		size, manager);

    }

    /**
     * tests insert
     * @throws IOException
     */
    public void testInsert() throws IOException {

//    	String seqId = "AAA";
//    	SequenceBundle val;
//    	val = new SequenceBundle(false, null,
//                null);
//        hashTable.insert(seqId, val);
        //assertTrue(hashTable.canInsert(seqId) == false);
    }

    /**
     * tests remove
     * @throws IOException
     */
    public void testRemove() throws IOException {
        String key = "";
        String seqId = "AAA";
        SequenceBundle removeVal = hashTable.remove(key);
        assertEquals(removeVal,null);
        removeVal = hashTable.remove(seqId);
        assertEquals(removeVal,null);

        assertTrue(hashTable.size() == 0);
    }
}
