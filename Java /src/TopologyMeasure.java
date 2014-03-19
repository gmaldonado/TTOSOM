 
import weka.core.Instance;
import weka.core.Instances;
 
public abstract class TopologyMeasure {
 
    /**
     * The input dataset
     */
    protected Instances x;
    /**
     * The root of the tree.
     */
    protected Neuron root;
    protected Distance distance;
 
    public TopologyMeasure() {
        super();
    }
 
    /**
     * Finds the BMU to a given data sample of the data set
     * @param currentNode current node being examined
     * @param inputSample input sample to calculate the BMU
     * @param bestSoFar Current best neuron so far.
     * @return best matching unit corresponding to the input sample
     */
    public Neuron findBMU(Neuron currentNode, Instance inputSignal, Neuron bestSoFar) {
     
        if(currentNode == null){
            return bestSoFar;
        }
        else{
     
            if(distance.calculateDistance(inputSignal, bestSoFar.getWeight(), x) >
            distance.calculateDistance(inputSignal, currentNode.getWeight(), x)){
                bestSoFar = currentNode;
            }
            for(Neuron child : currentNode.getChildren()){
                bestSoFar = findBMU(child, inputSignal, bestSoFar);
            }
        }
        return bestSoFar;
    }
 
    /**
     * Finds the second BMU to a given data sample of the data set.
     * @param currentNode current node being examined.
     * @param inputSample input sample to calculate the BMU.
     * @param bestSoFar Current best neuron so far.
     * @param firstBMU the first BMU, i.e., the neuron which is closest to the input signal.
     * @return second best matching unit corresponding to the input sample.
     */
    public Neuron findSecondBMU(Neuron currentNode, Instance inputSignal, Neuron bestSoFar,
            Neuron firstBMU) {
             
                if(currentNode == null){
                    return firstBMU;//if there is a tree with one neuron, that neuron is considered the first and second BMU.
                }
                else{
                    if (currentNode!=firstBMU){
                        if(distance.calculateDistance(inputSignal, bestSoFar.getWeight(), x) >
                        distance.calculateDistance(inputSignal, currentNode.getWeight(), x)){
                            bestSoFar = currentNode;
                        }
                        for(Neuron child : currentNode.getChildren()){
                            bestSoFar = findSecondBMU(child, inputSignal, bestSoFar,firstBMU);
                        }
                    }
                }
                return bestSoFar;
            }
 
}