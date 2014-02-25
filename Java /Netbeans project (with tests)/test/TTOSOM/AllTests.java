
package TTOSOM;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
/**
 * This class it's used to test all the test cases 
 * @author Gonzalo Maldonado
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({
    NormalizedEuclideanDistanceTest.class, 
    NormalizedManhattanDistanceTest.class,
    TTOSOMTest.class,
    MainTest.class})

public class AllTests {
    
}