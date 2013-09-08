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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This class represents the implementation of the TTOSOM
 */

public class TTOSOM extends Classifier implements Serializable{
   private Neuron root; 
   private ArrayList<Instance> neurons = new ArrayList<Instance>();
   private double initialLearning;
   private double initialRadius;
   private double finalLearning;
   private double finalRadius;
   private int iterations;
   private Distance distance;
   private Instances dataSet;
   private boolean clustering;
   private Random random;
   
   public TTOSOM(Instances dataSet, double initialLearning, double initialRadius, double finalLearning,
                 double finalRadius,int iterations, Distance distance,
                 boolean clustering, Random random){
       this.initialLearning = initialLearning;
       this.initialRadius = initialRadius;
       this.finalLearning = finalLearning;
       this.finalRadius = finalRadius;
       this.iterations = iterations;
       this.distance = distance;
       this.dataSet = dataSet;
       this.clustering=clustering;
       this.random = random;
   }
   
   /**
    * Creates a new tree from a given ArrayList 
    * @param elements description of every Neuron in the tree whit its id and children
    * @param dataSet given data set
    */
   public void describeTopology(ArrayList<NodeValue> elements){
       root = new Neuron(null,elements,dataSet);
   }
   
   public Neuron getRoot(){
       return root;
   }
   
   /**
    * Prints the vectors of the tree and adds the weight vectors of the 
    * neurons to the weights list.
    * @param node root tree
    * @param indent intentation level to print every branch in the tree
    */
   public void preOrderWeights(Neuron node, int indent){  
       if ( node != null )
      {  if ( indent > 0 )
            for ( int k = 0; k < indent; k++ )
               System.out.print (" ");
          System.out.println(node.getWeightWithoutClass());
          neurons.add(node.getWeight());
         for(Neuron child : node.getChilds()){
             preOrderWeights(child, indent+2);
         }
      }
   }

   /**
    * Gets all the vectors of every neuron
    * @return list with al weight vectors of every neuron
    */
    public ArrayList<Instance> getNeurons() {
        return neurons;
    }
   
   
   /**
    * Finds the BMU to a given data sample of the data set
    * @param actualNode current node in which is traversing
    * @param inputSample input sample to calculate the BMU
    * @param bmu actual BMU
    * @param distance distance to calculate the BMU
    * @param dataSet data set which contains the input sample
    * @return best matching unit corresponding to the input sample
    */
   public Neuron findBMU(Neuron actualNode,Instance inputSample, Neuron bmu){
       if(actualNode == null){
           return bmu;
       }
       else{
           if(distance.calculateDistance(inputSample, bmu.getWeight(), dataSet) >
              distance.calculateDistance(inputSample, actualNode.getWeight(), dataSet)){
               bmu = actualNode;
           }
           for(Neuron child : actualNode.getChilds()){
               bmu = findBMU(child, inputSample, bmu);
           }
       }
       return bmu;
   }
   
   /**
    * Calculates the bubble of activity from a given Neuron and a given radius.
    * @param bubbleOfActivity current bubble of activity 
    * @param current current node in which is traversing
    * @param radius radius to calculate the BoA
    * @param origin origin node 
    */
   public void calculateNeighborhood(ArrayList<Neuron> bubbleOfActivity,
                                                Neuron current, long radius, Neuron origin){  
       if(radius<=0){
           return;
       }
       else{
           for(Neuron child : current.getChilds()){
               if(child != origin ){
                   bubbleOfActivity.add(child);
                   calculateNeighborhood(bubbleOfActivity,child,radius-1, current);
               }
           }
           Neuron parent = current.getParent();
           if(parent!=null && parent!=origin){
               bubbleOfActivity.add(parent);
               calculateNeighborhood(bubbleOfActivity,parent,radius-1, current);
           }
       }
       return;
       
   }
   
   /**
    * Training process to a given sample 
    * @param inputSample input sample to train 
    * @param root root of the tree
    * @param distance distance which is being used to the training process
    * @param radius radius which is being used to the training process
    * @param learningRate learning rate which is being used to the training process
    * @param dataSet dataset containing the input sample
    */
   public void train(Instance inputSample, Neuron root, 
                     long radius,double learningRate){
       Neuron bmu = findBMU(root, inputSample, root);
       ArrayList<Neuron> bubbleOfActivity = new ArrayList<Neuron>();
       bubbleOfActivity.add(bmu);
       calculateNeighborhood(bubbleOfActivity, root, radius, null);
       for(Neuron neuron : bubbleOfActivity){
           updateRule(neuron.getWeight(), learningRate, inputSample);
       }

   }
   
   /**
    * Update rule to the weight vector
    * @param weight weight vector to be updated 
    * @param learningRate learning rate to use in the update rule 
    * @param inputSample input sample to use in the update rule
    */
   public void updateRule(Instance weight, double learningRate,Instance inputSample){
       for(int i=0;i<weight.numAttributes();i++){
           double value = weight.value(i)+learningRate*(inputSample.value(i) - weight.value(i));
           weight.setValue(i,value);        
       }
       
   }

   /**
    * This method it is used to calculate the value of a paremeter (like 
    * learning rate or radius)
    * @param currentValue current value of the parameter
    * @param initialValue initial value of the parameter
    * @param finalValue final value of the parameter
    * @param iterations iterations of the process
    * @return the new value of the parameter 
    */
   public double calculateValue(double currentValue, double initialValue, 
                                double finalValue,
                                int iterations){
       
       double delta = (initialValue - finalValue)/iterations;
       currentValue -= delta;
       return currentValue;
       
   }

    
   /**
    * This method gets the id of the BMU of a given instance.
    * @param instance Instance from we will find the BMU.
    * @return id of the BMU.
    */
   private int clusterInstance(Instance instance){
       Neuron bmu = findBMU(root, instance, root);
       return bmu.getId();
   }
   
   /**
    * This method generates a cluster vector. This vector represents 
    * the ID of the BMU of a given instance in position i. 
    * @param data data to generate the cluster vector.
    * @param distance distance which will be used to find the BMU.
    * @return cluster vector.
    */
   public int[] generateClusterVector(Instances data,Distance distance){
       int[] clusterVector = new int[data.numInstances()];
       for(int i=0;i<data.numInstances();i++){
           clusterVector[i] = clusterInstance(data.instance(i));
       }
       return clusterVector;
   }
   
   /**
    * Method which creates the model, from we will classify the instances
    * later.
    * @param data data set from we will train the classifier.
    * @throws Exception 
    */
    @Override
    public void buildClassifier(Instances data) throws Exception {
        int numInstances = data.numInstances();
        double radius = initialRadius;
        double learningRate = initialLearning;
        
        //Let's create the clustering
        for(int i=0;i<iterations;i++){
            int j = random.nextInt(numInstances);
            train(data.instance(j), root,Math.round(radius), learningRate); 
            radius = calculateValue(radius, initialRadius, finalRadius, iterations);
            learningRate = calculateValue(learningRate, initialLearning, finalLearning, iterations);  
           
        }
        
        if(!clustering){
            //Let's get the labeled data to generate the classifier 
            Instances labeledData = new Instances(dataSet,0);
            Instance aux;

            for(int i=0;i<dataSet.numInstances();i++){
                aux = dataSet.instance(i);
                if(!aux.classIsMissing()){
                    labeledData.add((Instance) aux.copy());
                }
            }

            //Let's assign a class to each neuron in the tree.
            int[] clusterVector = generateClusterVector(labeledData, distance);
            assignClassToNeurons(root, labeledData, clusterVector);
        }
        

    }
    
    /**
     * Assign classes to every neuron in the tree. 
     * @param node neuron to which assign the class
     * @param labeledData labeled data in the data set.
     * @param clusterVector cluster vector.
     */
    private void assignClassToNeurons(Neuron node,Instances labeledData,
                                      int[] clusterVector){
        if ( node != null ){
            int [] classes = new int[labeledData.numClasses()];
            
            for(int i=0;i<classes.length;i++){
                classes[i] = 0;
            }
            
            int classIndex =-1;
            int numAttributes = labeledData.numAttributes();
            for(int i=0;i<clusterVector.length;i++){
                if(clusterVector[i]==node.getId()){
                    classIndex = (int) labeledData.instance(i).value(numAttributes-1);
                    classes[classIndex]++;
                }
            }
            
            int counter=0;
            for(int i=0;i<classes.length;i++){
                counter+=classes[i];
            }
            if(counter!=0){
                int maxValue = Integer.MIN_VALUE;
                int popularClassIndex=-1;
                for(int i=0;i<classes.length;i++){
                    if(classes[i]>maxValue){
                        maxValue=classes[i];
                        popularClassIndex = i;
                    }
                }

                node.getWeight().setClassValue(popularClassIndex);
            }
            else{
                node.getWeight().setClassMissing();
            }
            for(Neuron child : node.getChilds()){
                assignClassToNeurons(child,labeledData,clusterVector);
             }
        }
    }
   
    
    /**
     * Method which classify an instance
     * @param instance instance to classify
     * @return the class which we will asign to the instance.
     */
    @Override
    public double classifyInstance(Instance instance){
        Neuron bmu = findBMU(root,instance, root);
        return bmu.getWeight().classValue();
    }

}


