
package TTOSOM;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This class represents a neuron in the Tree of the TTOSOM
 * @author Gonzalo Maldonado
 */
public class Neuron implements Serializable{
    
    private int id; 
    private ArrayList<Neuron> children;
    private Neuron parent;
    private Instance weight;
    
    public Neuron(Neuron parent,ArrayList<NodeValue> elements,Instances dataSet){
        int childrensCount = elements.get(0).getValue();
        Random random = new Random();
        
        /*You should do a copy instead of reference the instance, because if 
          you reference the instance, then when you modify the vector in the
          update rule, you will be modifying the original data set and 
          this could cause chaos, so it's VERY IMPORTANT to copy the object,
          that it's the same as the method clone in Java*/
        this.weight = (Instance)dataSet.instance(random.nextInt(dataSet.numInstances())).copy();
        this.children = new ArrayList<Neuron>();
        this.parent = parent;
        this.id = elements.get(0).getKey();
        
        elements.remove(0);
                       
        for(int i=0;i<childrensCount;i++){
            this.children.add(new Neuron(this,elements,dataSet));
        }
        
    }
    
    public String getWeightWithoutClass(){
        String weightWithoutClass="";
        for(int i=0;i<this.weight.numAttributes()-1;i++){
            weightWithoutClass+=this.weight.value(i);
            if(i!=this.weight.numAttributes()-2){
                weightWithoutClass+=",";
            }
        }
        return weightWithoutClass;
    }
    
    public void setWeight(Instance weight){
        this.weight = weight;
    }
    
    public Instance getWeight(){
        return this.weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Neuron> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Neuron> children) {
        this.children = children;
    }

    public Neuron getParent() {
        return parent;
    }

    public void setParent(Neuron parent) {
        this.parent = parent;
    }

    
}