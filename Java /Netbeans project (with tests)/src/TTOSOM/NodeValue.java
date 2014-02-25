
package TTOSOM;

/**
 * This class represents a value into an ArrayList. It is used to 
 * create a Neuron with "key" id and "value" childrens.
 * @author Gonzalo Maldonado
 */
public class NodeValue {
    
    private int key;
    private int value;
    
    public NodeValue(int key, int value){
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

}
