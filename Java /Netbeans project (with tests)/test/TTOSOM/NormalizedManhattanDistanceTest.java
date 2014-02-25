
package TTOSOM;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This is the test class of the Manhattan Distance calculus (normalized version)
 * @author Gonzalo Maldonado
 */
public class NormalizedManhattanDistanceTest {
    
    Instance item1;
    Instance item2;
    Instances trainingSet;
    NormalizedManhattanDistance distance;
    String dataSetPath;
    Instances dataSet;
    
    public NormalizedManhattanDistanceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     * First set up the dataset and the items to compare the distance
     */
    @Before
    public void setUp() {
        dataSetPath = "iris.arff"; //file should be in the root path of the project
        dataSet = Main.readArff(dataSetPath);
        item1 = dataSet.instance(0);
        item2 = dataSet.instance(1);
        distance = new NormalizedManhattanDistance();
    }
    
    @After
    public void tearDown() {
    }

    
    
    /**
     * Test of calculateDistance method, of class NormalizedManhattanDistance.
     * It tests the distance between item1 and item2
     */
    @Test
    public void testCalculateDistance1() {
        double expResult = 0.255;
        double result = distance.calculateDistance(item1,item2, dataSet);
        assertEquals(expResult, result, 0.01);
    }
    
    /**
     * Test of calculateDistance method, of class NormalizedManhattanDistance.
     * It tests the distance between item2 and item1
     */
    @Test
    public void testCalculateDistance2() {
        double expResult = 0.255;
        double result = distance.calculateDistance(item2, item1, dataSet);
        assertEquals(expResult, result, 0.01);
    }
    
}