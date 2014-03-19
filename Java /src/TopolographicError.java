 
import java.util.ArrayList;
 
import weka.core.Instances;
 
/**
 * @author castudillo
 *
 */
public class TopolographicError extends TopologyMeasure implements
        TopologyMeasureInterface {
 
    /**
     * @param x The input dataset.
     * @param root The root of the tree.
     */
    public TopolographicError(Instances x, Neuron root) {
        super();
        this.x = x;
        this.root = root;
        distance = new NormalizedEuclideanDistance();
    }   
     
     
    /* (non-Javadoc)
     * @see TTOSOM.TopologyMeasureInterface#calculateError()
     */
    @Override
    public double calculateError() {
        Neuron bmu,bmu2;
        ArrayList<Neuron> bubble=new ArrayList<Neuron>();
        double total = 0;
        for (int i = 0; i < x.numInstances(); i++) {
            bmu=findBMU(root, x.instance(i), root);
            bmu2 = findSecondBMU(root, x.instance(i), root, bmu);
 
            //compute the bubble of radius=1
            if (bmu.getParent()!=null)//add the parent
                bubble.add(bmu.getParent());
            for (Neuron neuron : bmu.getChildren()) { //and the kids 
                bubble.add(neuron); 
            }
             
            if(bubble.contains(bmu2)!=false){ // verify that bmu2 exist in the bubble of radius=1
                total = total+1; //count of the total of hits
            }
        }
         
         
        return total/x.numInstances(); //average of total hits
    }
 
}