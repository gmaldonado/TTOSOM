 
import weka.core.Instances;
 
/**
 * @author castudillo
 *
 */
public class MainTopologyMeasure {
 
     
    static TTOSOM ttosom;
    static Instances x; 
     
    /**
     * @param args
     */
    public static void main(String[] args) {
        Main.main(args);
        ttosom=Main.getTtosom();
        x=Main.getDataSet();
        ttosom.printWeightVectors(ttosom.getRoot(), 2);
         
         
                 
        QuantizationError qe=new QuantizationError(x, ttosom.getRoot());
        System.out.println("qe: "+qe.calculateError());
 
        TopolographicError te=new TopolographicError(x, ttosom.getRoot());
        System.out.println("te: "+te.calculateError());
 
    }
}
 
