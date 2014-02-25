package TTOSOM;

import weka.core.Instance;
import weka.core.Instances;

/**
 * This interface represents a new Strategy to calculate the distance. We 
 * are using the Strategy Pattern.
 * @author Gonzalo Maldonado
 */
public interface Distance {

    public double calculateDistance(Instance item1, Instance item2, Instances instances);
    
}
