
package TTOSOM;

import java.util.ArrayList;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This is the test class of the TTOSOM class
 * @author Gonzalo Maldonado
 */
public class TTOSOMTest {
    
    String filePath;
    Instances dataSet;
    String dataSetPath;
    Instance item1;
    Random random;
    
    public TTOSOMTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     * First set up the dataset and the items the different test cases
     */
    @Before
    public void setUp() {
        filePath = "topology"; //file should be in the root path of the project
        dataSetPath = "iris.arff"; //file should be in the root path of the project
        dataSet = Main.readArff(dataSetPath);   
        item1 = dataSet.instance(0);
        random = new Random();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of describeTopology method, of class TTOSOM. In this case 
     * we test the root of the tree, which should have an id of 1.
     */
    @Test
    public void testDescribeTopology1() {
        ArrayList<NodeValue> elements = Main.readTopology(filePath);
        TTOSOM tree = new TTOSOM(dataSet, 0.0, 0.0, 0.0, 0.0, 0,null,true,random);
        tree.describeTopology(elements);
        assertEquals(tree.getRoot().getId(),1);
    }
    
    /**
     * Test of describeTopology method, of class TTOSOM. In this case 
     * we test that the node with id 3 has 3 children. 
     */
    @Test
    public void testDescribeTopology2(){
        ArrayList<NodeValue> elements = Main.readTopology(filePath);
        TTOSOM tree = new TTOSOM(dataSet, 0.0, 0.0, 0.0, 0.0, 0,null,true,random);
        tree.describeTopology(elements);
        Neuron neuron3 = tree.getRoot().getChildren().get(0).getChildren().get(0);
        assertEquals(neuron3.getChildren().size(), 3);
    }
    
    /**
     * Test of describeTopology method, of class TTOSOM. In this case 
     * we test that the node with id 11 is son of the node with id 10
     */
    @Test
    public void testDescribeTopology3(){
        ArrayList<NodeValue> elements = Main.readTopology(filePath);
        TTOSOM tree = new TTOSOM(dataSet, 0.0, 0.0, 0.0, 0.0, 0,null,true,random);
        tree.describeTopology(elements);
        Neuron neuron10 = tree.getRoot().getChildren().get(1);
        assertEquals(neuron10.getChildren().get(0).getId(),11);
    }
    
    /**
     * Test of describeTopology method, of class TTOSOM. In this case 
     * we test that the node with id 10 is father of the node with id 11
     */
    @Test
    public void testDescribeTopology4(){
        ArrayList<NodeValue> elements = Main.readTopology(filePath);
        TTOSOM tree = new TTOSOM(dataSet, 0.0, 0.0, 0.0, 0.0, 0,null,true,random);
        tree.describeTopology(elements);
        Neuron neuron11 = tree.getRoot().getChildren().get(1).getChildren().get(0);
        assertEquals(neuron11.getParent().getId(),10);
    }
    
    /**
     * Test of describeTopology method, of class TTOSOM. In this case 
     * we test that the node with id 6 is a leaf of the tree
     */
    @Test
    public void testDescribeTopology5(){
        ArrayList<NodeValue> elements = Main.readTopology(filePath);
        TTOSOM tree = new TTOSOM(dataSet, 0.0, 0.0, 0.0, 0.0, 0,null,true,random);
        tree.describeTopology(elements);
        Neuron neuron6 = tree.getRoot().getChildren().get(0).getChildren().get(0).getChildren().get(2);
        assertEquals(neuron6.getChildren().size(),0);
    }
    
    /**
     * Test of describeTopology method, of class TTOSOM. In this case 
     * we test that the node with id 9 is grandson of the node with id 2
     */
    @Test
    public void testDescribeTopology6(){
        ArrayList<NodeValue> elements = Main.readTopology(filePath);
        TTOSOM tree = new TTOSOM(dataSet, 0.0, 0.0, 0.0, 0.0, 0,null,true,random);
        tree.describeTopology(elements);
        Neuron neuron2 = tree.getRoot().getChildren().get(0);
        Neuron neuron9 = neuron2.getChildren().get(1).getChildren().get(1);
        assertEquals(neuron9.getId(),9);
    }
    
    private void modifyWeights(Neuron node){  
       if ( node != null ){
           for(int i=0;i<node.getWeight().numAttributes();i++){
               node.getWeight().setValue(node.getWeight().attribute(i),node.getId());
           }
           
         for(Neuron child : node.getChildren()){
             modifyWeights(child);
         }
      }
   }

    /**
     * Test of findBMU method, of class TTOSOM. This method uses
     * the Euclidean Distance (normalized version)
     */
    @Test
    public void testFindBMU1() {
        ArrayList<NodeValue> elements = Main.readTopology(filePath);
        Distance distance = new NormalizedEuclideanDistance();
        TTOSOM tree = new TTOSOM(dataSet, 0.0, 0.0, 0.0, 0.0, 0,distance,true,random);
        tree.describeTopology(elements);
        
        //to delete randomness
        modifyWeights(tree.getRoot());             
        //Let's pick a vector to calculate the BMU
        item1 = dataSet.firstInstance();        
        int expectedId = 2;
        Neuron result = tree.findBMU(tree.getRoot(), item1, tree.getRoot());
        assertEquals(expectedId, result.getId());
    }

    /**
     * Test of findBMU method, of class TTOSOM. This method uses
     * the Manhattan Distance (normalized version)
     */
    @Test
    public void testFindBMU2() {
        ArrayList<NodeValue> elements = Main.readTopology(filePath);
        Distance distance = new NormalizedManhattanDistance();
        TTOSOM tree = new TTOSOM(dataSet, 0.0, 0.0, 0.0, 0.0, 0,distance,true,random);
        tree.describeTopology(elements);
        //to delete randomness
        modifyWeights(tree.getRoot());
        
        //Let's pick a vector to calculate the BMU
        item1 = dataSet.firstInstance();                     
        
        int expectedId = 3;
        Neuron result = tree.findBMU(tree.getRoot(), item1, tree.getRoot());
        assertEquals(expectedId, result.getId());
    }
    
    
    /**
     * Test of calculateNeighborhood method, of class TTOSOM. It checks 
     * the neighbors by it's id. It calculates the the neighborhood 
     * of the root node having a radius of 2.
     */
    @Test
    public void testCalculateNeighborhood1() {
        ArrayList<Neuron> bubbleOfActivity = new ArrayList<Neuron>();
        ArrayList<NodeValue> elements = Main.readTopology(filePath);
        TTOSOM tree = new TTOSOM(dataSet, 0.0, 0.0, 0.0, 0.0, 0,null,true,random);
        tree.describeTopology(elements);
        Neuron current = tree.getRoot();
        long radius = 2;
        Neuron origin = null;
        TTOSOM instance = new TTOSOM(dataSet, 0.0, 0.0, 0.0, 0.0, 0,null,true,random);
        instance.calculateNeighborhood(bubbleOfActivity, current, radius, origin);
        
        int neuronsId[] = {2,3,7,10,11,12,13,14};
        
        for(int i=0;i<bubbleOfActivity.size();i++){
            assertEquals(neuronsId[i],bubbleOfActivity.get(i).getId());
        }
        
    }
    
    /**
     * Test of calculateNeighborhood method, of class TTOSOM. It checks 
     * the neighbors by it's id. It calculates the the neighborhood 
     * of the node with id 4 having a radius of 3.
     */
    @Test
    public void testCalculateNeighborhood2() {
        ArrayList<Neuron> bubbleOfActivity = new ArrayList<Neuron>();
        ArrayList<NodeValue> elements = Main.readTopology(filePath);
        TTOSOM tree = new TTOSOM(dataSet, 0.0, 0.0, 0.0, 0.0, 0,null,true,random);
        tree.describeTopology(elements);
        Neuron current = tree.getRoot().getChildren().get(0).getChildren().get(0).getChildren().get(0);
        long radius = 3;
        Neuron origin = null;
        TTOSOM instance = new TTOSOM(dataSet, 0.0, 0.0, 0.0, 0.0, 0,null,true,random);
        instance.calculateNeighborhood(bubbleOfActivity, current, radius, origin);
        assertEquals(6,bubbleOfActivity.size());
        
    }


    /**
     * Test of calculateValue method, of class TTOSOM. 
     */
    @Test
    public void testCalculateValue() {
        double currentValue = 9.0;
        double initialValue = 9.0;
        double finalValue = 0.0;
        int iterations = 100;
        TTOSOM instance = new TTOSOM(dataSet, 0.0, 0.0, 0.0, 0.0, 0,null,true,random);
        double expResult = 8.91;
        double result = instance.calculateValue(currentValue, initialValue, finalValue, iterations);
        assertEquals(expResult, result, 0.001);
    }
    
    /**
     * Test of updateRule method, of class TTOSOM.
     */
    @Test
    public void testUpdateRule(){
        double learningRate = 0.8;
        ArrayList<NodeValue> elements = Main.readTopology(filePath);
        TTOSOM tree = new TTOSOM(dataSet, 0.0, 0.0, 0.0, 0.0, 0,null,true,random);
        tree.describeTopology(elements);
        //to delete randomness
        modifyWeights(tree.getRoot());  
        Neuron root = tree.getRoot();
        Instance weight = (Instance) root.getWeight().copy();
        Instance inputSample = dataSet.firstInstance();
        tree.updateRule(weight,learningRate,inputSample);            
        assertEquals(4.28, weight.value(0),0.001);
        assertEquals(3, weight.value(1),0.001);
        assertEquals(1.32, weight.value(2),0.001);
        assertEquals(0.36, weight.value(3),0.001);
        
    }
}