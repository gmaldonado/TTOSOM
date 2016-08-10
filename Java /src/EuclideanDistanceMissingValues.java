package TTOSOM;

import java.io.Serializable;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This class represents the Euclidean Distance function, but the normalized
 * version.
 * @author Cesar Astudillo
 */

public class EuclideanDistanceMissingValues implements Distance, Serializable{


    @Override
    public double calculateDistance(Instance item1, Instance item2,Instances trainingSet) {
        //must have same dimensionality
        assert(item1.numAttributes()==item2.numAttributes());
        
        double d=0.0;
        
        for (int i = 0; i < item1.numAttributes(); i++) {
            if ( !item1.isMissing(i) && !item2.isMissing(i))
            {
                d+=Math.pow(item1.value(i)-item2.value(i), 2);
            }
        }
        return Math.sqrt(d);
    }
  
}
