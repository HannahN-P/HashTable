import student.TestCase;

public class HashTableTest extends TestCase {

    private HashTable<String, SequenceBundle> hashTable;
    private MemoryManager manager;

    public void setUp()
    {
        // manager = new MemoryManager();
        hashTable = new HashTable<String, SequenceBundle>(SequenceBundle.class, 
            5, manager);
    }

}
