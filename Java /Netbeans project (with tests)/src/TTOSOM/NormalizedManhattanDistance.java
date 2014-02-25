
package TTOSOM;

import java.io.Serializable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;

/**
 * This class represents the Manhattan Distance function, but the normalized
 * version.
 * @author Gonzalo Maldonado
 */

public class NormalizedManhattanDistance implements Distance, Serializable{
    
    @Override
    public double calculateDistance(Instance item1, Instance item2,Instances trainingSet) {
        ManhattanDistance calculus = new ManhattanDistance(trainingSet);
        calculus.setDontNormalize(false);        
        return calculus.distance(item1, item2);    
    }
    
}
