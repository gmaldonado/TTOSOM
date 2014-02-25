package TTOSOM;

import java.io.Serializable;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This class represents the Euclidean Distance function, but the normalized
 * version.
 * @author Gonzalo Maldonado
 */

public class NormalizedEuclideanDistance implements Distance, Serializable{


    @Override
    public double calculateDistance(Instance item1, Instance item2,Instances trainingSet) {
        EuclideanDistance calculus = new EuclideanDistance(trainingSet);
        calculus.setDontNormalize(false);      
        return calculus.distance(item1, item2);    
    }
  
}
