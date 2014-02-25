package TTOSOM;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This is the test class of the Main class 
 * @author Gonzalo Maldonado
 */
public class MainTest {
    
    String filePath;
    ArrayList<NodeValue> list;
    ArrayList<NodeValue> result;
    
    public MainTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     * First set up all the data which will be used in the differents tests
     */
    @Before
    public void setUp() {
        filePath = "topology"; //file should be in the root path of the project
        int [] array = {2,2,3,0,0,0,2,0,0,4,0,0,0,0};
        list = new ArrayList<NodeValue>();
        for(int i=0;i<array.length;i++){
            list.add(new NodeValue(i+1,array[i]));
        }
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of readTopology method, of class Main. It compares all the 
     * resulting data
     */
    @Test
    public void testReadTopology() {
        result = Main.readTopology(filePath);
        assertEquals(result.size(),list.size());
        for(int i=0;i<result.size();i++){
           assertEquals(result.get(i).getKey(), list.get(i).getKey());
           assertEquals(result.get(i).getValue(), list.get(i).getValue());
        } 
        
    }
}