import student.TestCase;

public class HashTableTest extends TestCase {

    private HashTable<String, SequenceBundle> hashTable;
    private DNAdbase manager;

    public void setUp()
    {
        // manager = new MemoryManager();
        hashTable = new HashTable<String, SequenceBundle>(5, manager);
    }

}