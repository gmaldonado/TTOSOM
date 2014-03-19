package TTOSOM;
 
import weka.core.Instances;
 
/**
 * 
 * Calculates the Quantization Error of the SOM.
 * 
 * References:
 * <ol>
 *<li>
 * César A. Astudillo and B. John Oommen. Topology-oriented self-organizing maps: a survey. March 2014, Pages 1-26, Pattern Analysis and Applications, Springer London. doi:10.1007/s10044-014-0367-9.
 * </li>
 * <li>
 * Arsuaga Uriarte, E. and Díaz Martín, F., Topology preservation in SOM. International Journal of Applied Mathematics and Computer Sciences. v1 i1. 19-22.
 * </li>
 * </ol>
 * @author Cesar Astudillo, Jorge Poblete
 * @since 18 march 2014
 * @version 1.0.1
 *
 */
public class QuantizationError extends TopologyMeasure implements TopologyMeasureInterface {
 
    /**
     * @param x The input dataset.
     * @param root The root of the tree.
     */
    public QuantizationError(Instances x, Neuron root) {
        super();
        this.x = x;
        this.root = root;
        distance = new NormalizedEuclideanDistance();
    }
 
 
    /* (non-Javadoc)
     * @see TTOSOM.TopologyMeasure#calculateError()
     */
    @Override
    public double calculateError() {
 
        //step 1
        // calculate s1(x), the BMU
        double total = 0.0;
        double dist;
        Neuron s;
 
        for (int i = 0; i < x.numInstances(); i++) {
            s=findBMU(root, x.instance(i), root);
            dist=distance.calculateDistance(s.getWeight(), x.instance(i), x); // calculate d(S1(xi),x1)
            total = total + dist;
        }
 
        //step 2
        //return average distance
        return total/x.numInstances();
    }
 
}