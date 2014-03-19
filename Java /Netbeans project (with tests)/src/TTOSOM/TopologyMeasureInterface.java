package TTOSOM;
 
/**
 * Interface for a series of topology measures, that are able to measure errors of SOMs output. 
 * 
 * @author Cesar Astudillo, Jorge Poblete
 * @since 18 march 2014
 * @version 1.0.1
 * 
 */
public interface TopologyMeasureInterface {
 
    /**
     * 
     * @return the topology error
     */
     
    public double calculateError();
     
}