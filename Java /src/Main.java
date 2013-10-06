/**
 *   Tree-based Topology-Oriented SOM: Java implementation and R binding
 *   Copyright (C) 2013  Gonzalo Maldonado, Cesar A. Astudillo
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Main class to use the TTOSOM
 */
public class Main {
    
    private static Instances dataSet;
    private static ArrayList<NodeValue> nodes;
    private static TTOSOM ttosom;
    private static Distance distance;
    private static double initialRadius;
    private static double finalRadius;
    private static double initialLearning;
    private static double finalLearning;
    private static int iterations;
    private static double radius;
    private static double learningRate;
    private static String dataSetPath;
    private static String treeFilePath;
    private static int distanceType;
    private static String readSerializePath;
    private static String writeSerializePath;
    private static final String INPUT = "-i";
    private static final String OUTPUT = "-o";
    private static final String TESTING = "-t";
    private static final String CROSSVALIDATION = "-x";
    private static final String CLUSTERING = "-c";
    private static final String SEED = "-s";
    private static boolean clusteringOption;
    private static int seedValue;
    private static String testingSetPath;
    private static int folds;
    private static Random random;

    public static void main(String[] args){
        
        testingSetPath = null;
        folds = -1;
        clusteringOption = false;
        seedValue=1;
        
        //Reading the parameters of the program.
        try{
            dataSetPath = args[0];
            treeFilePath = args[1];
            iterations = Integer.parseInt(args[2]);
            initialRadius = Double.parseDouble(args[3]);
            finalRadius = Double.parseDouble(args[4]);
            initialLearning = Double.parseDouble(args[5]);
            finalLearning = Double.parseDouble(args[6]);
            distanceType = Integer.parseInt(args[7]); 
            
        }
        catch(Exception exception){
            System.err.println(  "How to use: dataset_file_path tree_file_path number_of_iterations(integer) "
                               + "initial_radius(real) final_radius(real) initial_learning_rate(real) "
                               + "final_learning_rate(real) distance_type(integer) "
                               + "[-i serialife_from](optional) [-o serialize_to](optional) "
                               + "[-t testing_file](optional)] || [-x number_of_folds](optional)] "
                               + "[-s seed_value](optional)");
            System.exit(0);
        }
        
        
        
        String firstOption="";
        String pathFirstOption="";
        String secondOption="";
        String pathSecondOption="";
        String thirdOption="";
        String pathThirdOption="";
        String fourthOption="";
        String pathFourthOption="";
        
        try{
            firstOption = args[8];
            if(!firstOption.equals(CLUSTERING)){
                try{
                    pathFirstOption = args[9];
                }
                catch(Exception exception){
                    System.out.println("Error, wrong path");
                    System.exit(0);
                }
                if(firstOption.equals(INPUT)){
                    readSerializePath = pathFirstOption;
                }
                else if(firstOption.equals(OUTPUT)){
                    writeSerializePath = pathFirstOption;
                }
                else if(firstOption.equals(TESTING)){
                    testingSetPath = pathFirstOption;
                }
                else if(firstOption.equals(CROSSVALIDATION)){
                    folds = Integer.parseInt(pathFirstOption);
                }
                else if(firstOption.equals(SEED)){
                    seedValue = Integer.parseInt(pathFirstOption);
                }
                
            }
            else{
                clusteringOption=true;
            }
            
            try{
               secondOption = args[10];
               if(!secondOption.equals(CLUSTERING)){
                try{
                    pathSecondOption = args[11];
                }
                catch(Exception exception){
                    System.out.println("Error, wrong path");
                    System.exit(0);
                }
                if(secondOption.equals(INPUT)){
                    readSerializePath = pathSecondOption;
                }
                else if(secondOption.equals(OUTPUT)){
                    writeSerializePath = pathSecondOption;
                }
                else if(secondOption.equals(TESTING)){
                    testingSetPath = pathSecondOption;
                }
                else if(secondOption.equals(CROSSVALIDATION)){
                 folds = Integer.parseInt(pathSecondOption);
                }
                else if(secondOption.equals(SEED)){
                    seedValue = Integer.parseInt(pathSecondOption);
                }
               }
               else{
                   clusteringOption=true;
               }
               
            try{
               thirdOption = args[12]; 
               if(!thirdOption.equals(CLUSTERING)){
                try{
                    pathThirdOption = args[13];
                }
                catch(Exception exception){
                    System.out.println("Error, wrong path");
                    System.exit(0);
                }
                if(thirdOption.equals(INPUT)){
                    readSerializePath = pathThirdOption;
                }
                else if(thirdOption.equals(OUTPUT)){
                    writeSerializePath = pathThirdOption;
                }
                else if(thirdOption.equals(TESTING)){
                    testingSetPath = pathThirdOption;
                }
                else if(thirdOption.equals(CROSSVALIDATION)){
                 folds = Integer.parseInt(pathThirdOption);
                }
                else if(thirdOption.equals(SEED)){
                    seedValue = Integer.parseInt(pathThirdOption);
                }
               }
               else{
                   clusteringOption=true;
               }
               try{
               fourthOption = args[14]; 
               if(!fourthOption.equals(CLUSTERING)){
                try{
                    pathFourthOption = args[15];
                }
                catch(Exception exception){
                    System.out.println("Error, wrong path");
                    System.exit(0);
                }
                if(fourthOption.equals(INPUT)){
                    readSerializePath = pathFourthOption;
                }
                else if(fourthOption.equals(OUTPUT)){
                    writeSerializePath = pathFourthOption;
                }
                else if(fourthOption.equals(TESTING)){
                    testingSetPath = pathFourthOption;
                }
                else if(fourthOption.equals(CROSSVALIDATION)){
                 folds = Integer.parseInt(pathFourthOption);
                }
                else if(fourthOption.equals(SEED)){
                    seedValue = Integer.parseInt(pathFourthOption);
                }
               }
               else{
                   clusteringOption=true;
               }
               
            }
            catch(Exception exception){
            }
               
            }
            catch(Exception exception){
            }
            }
            catch(Exception exception){
            }
            
        }
        catch(Exception exception){
        }
        random = new Random(seedValue);
        dataSet = readArff(dataSetPath);
        
        //Depending on the option selected by the user selects the distance to use
        switch(distanceType){
            case 0:
                distance = new NormalizedEuclideanDistance();
                break;
            default:
                distance = new NormalizedManhattanDistance();
                break;
        }

        nodes = readTopology(treeFilePath);

        //Read from a file which contains the TTOSOM as a serialized object
        if(readSerializePath != null){
            readSerializableObject(readSerializePath);
            ttosom.preOrderWeights(ttosom.getRoot(), -10);
        }
        else{
            ttosom = new TTOSOM(dataSet,initialLearning, initialRadius,
                                finalLearning, finalRadius, iterations,
                                distance,clusteringOption,random);
        }


        ttosom.describeTopology(nodes);
        
        
        if(testingSetPath==null && folds==-1 && !clusteringOption){
            try {
                ttosom.buildClassifier(dataSet);
            } 
            catch (Exception ex) {
                System.out.println("Unknown error");
            }

            //Prints the weight vectors.
            ttosom.preOrderWeights(ttosom.getRoot(),-10);

            
            
            //Let's separate the unlabeled data
            Instances unlabeled = new Instances(dataSet,0);
            for(int i=0;i<dataSet.numInstances();i++){
                    if(dataSet.instance(i).classIsMissing()){
                        unlabeled.add(dataSet.instance(i));
                    }
            }
            if(unlabeled.numInstances()>0){
                if(unlabeled.numInstances()==dataSet.numInstances()){
                    //If we just have unlabeled instances, then do clustering
                    System.out.println("Clustering (you have only unlabed data)");
                    int[] clusterVector =ttosom.generateClusterVector(dataSet, distance);
                    for(int i=0;i<clusterVector.length;i++){
                        System.out.print(clusterVector[i]+" ");
                    }
                    System.out.println("");     
                }
                else{
                    System.out.println("");
                    System.out.println("Classifying the unlabeled data");
                    System.out.println("");


                    Instances labeled = new Instances(unlabeled);

                    for(int i=0;i<unlabeled.numInstances();i++){
                        double classLabel = ttosom.classifyInstance(unlabeled.instance(i));
                        labeled.instance(i).setClassValue(classLabel);
                        System.out.println(labeled.instance(i));
                    }
                }
                
                
            }
           
          
            //If we want to write the result to a file, then do it.
            if(writeSerializePath != null){
                writeSerializableObject(writeSerializePath);
            }
        }
        else if(testingSetPath!=null && folds==-1 && !clusteringOption){
             trainTestSet();
        }
        else if(folds!=-1 && testingSetPath==null && !clusteringOption){
            crossValidation();
        }
        else if(clusteringOption && testingSetPath==null && folds==-1){
            System.out.println("Clustering");
            try {
                ttosom.buildClassifier(dataSet);
            } 
            catch (Exception ex) {
                System.out.println("Unknown error");
            }
            int[] clusterVector =ttosom.generateClusterVector(dataSet, distance);
            for(int i=0;i<clusterVector.length;i++){
                System.out.print(clusterVector[i]+" ");
            }
            System.out.println("");
        }
        else{
            System.out.println("Error, you should select just ONE option: "
                    + "to use a testing set or to use cross validation");
        }
        
        
    }
    
    private static void crossValidation(){
        try{
            System.out.println("Cross Validation");
            Evaluation eval = new Evaluation(dataSet);
            eval.crossValidateModel(ttosom, dataSet,folds, new Random(seedValue));
            System.out.println(eval.toSummaryString());
            System.out.println(eval.toClassDetailsString());
            System.out.println(eval.toMatrixString());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    private static void trainTestSet(){
        try {
            System.out.println("Training and test sets");
            ttosom.buildClassifier(dataSet);
            Evaluation eval = new Evaluation(dataSet);
            Instances test = readArff(testingSetPath);
            eval.evaluateModel(ttosom, test);
            System.out.println(eval.toSummaryString());
            System.out.println(eval.toClassDetailsString());
            System.out.println(eval.toMatrixString());
        } 
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * This method will be used if we want to get the weight vectors of 
     * every neuron in the tree and use it in weka
     * 
     * @return Matrix of weight vectors
     */
    public static double[][] getVectors(){
        ArrayList<Instance> neurons = ttosom.getNeurons();
        int rows = neurons.size();
        int columns = neurons.get(0).numAttributes()-1;
        double[][] result = new double[rows][columns];
        for(int i=0;i<rows;i++){
            for(int j=0;j<columns;j++){
                result[i][j] = neurons.get(i).value(j);
            }
        }
        return result;
    }
    
    /**
     * Writes a serializable object into a defined path
     * @param path path to write the file
     */
    private static void writeSerializableObject(String path){
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(new FileOutputStream(path));
            out.writeObject(ttosom);
            out.flush();
            out.close();
            System.out.println("Tree successfully saved");
        } 
        catch (IOException ex) {
            System.err.println("Path "+path+" not found");
        } 
    }
    /**
     * Reads an object from a serialized file given it's path
     * @param path path to the file where the object is contained.
     */
    private static void readSerializableObject(String path){
        ObjectInputStream in;
        try {
            in = new ObjectInputStream(new FileInputStream(path));
            ttosom = (TTOSOM) in.readObject();
            in.close();
        } 
        catch (IOException ex) {
           System.err.println("File "+path+" not found. "+"Initializing new tree");
           ttosom = new TTOSOM(dataSet,initialLearning, initialRadius, 
                   finalLearning, finalRadius, iterations, distance,
                   clusteringOption,random);
        } 
        catch (ClassNotFoundException ex) {
           System.err.println("Error, class not found");
        } 
    }
    
    /**
     * Loading data from a given ARFF file which contains the data set.
     * @param file path to the file which contains the data set 
     * @return data set into Instances
     */
    public static Instances readArff(String file){
        Instances data=null;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            data = new Instances(reader);
            reader.close();
            data.setClassIndex(data.numAttributes() - 1);
            return data;
        }
        catch(Exception e){
            System.out.println("Error reading ARFF");
            System.exit(0);
        }
        return data;

    }
    
    /**
     * Reading the topology from a given file and loading it into an ArrayList
     * @param filePath path which contains the topology 
     * @return the topology describing the Tree
     */
    public static ArrayList<NodeValue> readTopology(String filePath){
        File file = null;
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList<NodeValue> nodes = new ArrayList<NodeValue>();
        
        try{
            file = new File(filePath);
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            
            String line = br.readLine();
            String[] values = line.split(" ");
            for(int i=0;i<values.length;i++){
                NodeValue nodeValue = new NodeValue(i+1,Integer.parseInt(values[i]));
                nodes.add(nodeValue);
            }
            return nodes;
            
        }
        catch(Exception e){
            System.out.println("Error reading topology file");
            System.exit(0); //http://netbeans-org.1045718.n5.nabble.com/quot-Forked-Java-VM-exited-abnormally-quot-when-running-junit-tests-seperately-td4388780.html
        }
        finally {
            try{
                if( fr!=null ){   
                    fr.close();     
                }  
            }
            catch(Exception e2){
                System.out.println("Error closing topology file");
                System.exit(0); //http://netbeans-org.1045718.n5.nabble.com/quot-Forked-Java-VM-exited-abnormally-quot-when-running-junit-tests-seperately-td4388780.html
            }
        }
        
        return null;      
    }
    
}
