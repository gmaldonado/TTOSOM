package com.classifier.ttosom;


import static com.classifier.ttosom.utils.Utils.readTopology;
import static java.lang.Math.round;
import static org.apache.commons.lang3.math.NumberUtils.toDouble;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static weka.core.Utils.getOption;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.classifier.ttosom.distance.Distance;
import com.classifier.ttosom.distance.EuclideanDistance;
import com.classifier.ttosom.distance.ManhattanDistance;
import com.classifier.ttosom.domain.Neuron;

import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;


public class TTOSOM extends AbstractClassifier implements Serializable{

	private static final long serialVersionUID = -8464084336600641535L;
	private Neuron root;
	private Map<Neuron, Map<Integer, List<Neuron>>> map;
	private final List<Neuron> neuronList = new ArrayList<Neuron>();
	private double initialLearning;
	private double initialRadius;
	private double finalLearning;
	private double finalRadius;
	private int iterations;
	private Distance distance;
	private final Instances trainingSet;
	private boolean isClusteringMode;
	private final Random random;
	private int seed = 1;
	private List<Pair<Integer, Integer>> topology;

	public TTOSOM(Instances trainingSet){
		this.random = new Random(seed);
		this.trainingSet = trainingSet;
	}

	@Override
	public void buildClassifier(Instances data) throws Exception {
		describeTopology();
		data = trainingSet;
		cluster(data);
		computeLabels();
	}

	@Override
	public double classifyInstance(Instance instance){
		final Neuron bmu = findBMU(root,instance, root);
		return bmu.getWeight().classValue();
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		super.setOptions(options);

		topology = readTopology(getOption("tree", options)); //topology tree
		iterations = toInt(getOption("it", options));
		initialRadius = toDouble(getOption("ir", options));
		finalRadius = toDouble(getOption("fr", options));
		initialLearning = toDouble(getOption("il", options));
		finalLearning = toDouble(getOption("fl", options));
		distance = selectDistance(getOption("d", options));
		isClusteringMode = createClusteringOption(getOption("c", options));
		seed = toInt(getOption("s", options));
	}

	private void assignClassToNeurons(Neuron node,Instances labeledData,
			int[] clusterVector){

		if ( node != null ){
			final int [] classes = new int[labeledData.numClasses()];

			int classIndex =-1;
			final int numAttributes = labeledData.numAttributes();
			for(int i=0;i<clusterVector.length;i++){
				if(clusterVector[i]==node.getId()){
					classIndex = (int) labeledData.instance(i).value(numAttributes-1);
					classes[classIndex]++;
				}
			}

			int counter=0;

			for (final int clazz : classes) {
				counter+=clazz;
			}
			if(counter!=0){
				int maxValue = Integer.MIN_VALUE;
				int popularClassIndex=-1;
				for(int i=0;i<classes.length;i++){
					if(classes[i]>maxValue){
						maxValue=classes[i];
						popularClassIndex = i;
					}
				}

				node.getWeight().setClassValue(popularClassIndex);
			}
			else{
				node.getWeight().setClassValue(random.nextInt(labeledData.numClasses()));

			}
			for(final Neuron child : node.getChildren()){
				assignClassToNeurons(child,labeledData,clusterVector);
			}
		}
	}


	private void calculateNeighborhood(List<Neuron> bubbleOfActivity,
			Neuron current, long radius, Neuron origin){
		if(radius<=0){
			return;
		}
		else{
			for(final Neuron child : current.getChildren()){
				if(child != origin ){
					bubbleOfActivity.add(child);
					calculateNeighborhood(bubbleOfActivity,child,radius-1, current);
				}
			}
			final Neuron parent = current.getParent();
			if(parent!=null && parent!=origin){
				bubbleOfActivity.add(parent);
				calculateNeighborhood(bubbleOfActivity,parent,radius-1, current);
			}
		}
		return;

	}

	private double calculateValue(double currentValue, double initialValue,
			double finalValue,
			int iterations){

		final double delta = (initialValue - finalValue)/iterations;
		currentValue -= delta;
		return currentValue;

	}

	private void cluster(Instances data) {

		double radius = initialRadius;
		double learningRate = initialLearning;

		for(int i=0;i<iterations;i++){
			final int j = random.nextInt(data.numInstances());
			train(data.instance(j), root,round(radius), learningRate);
			radius = calculateValue(radius, initialRadius, finalRadius, iterations);
			learningRate = calculateValue(learningRate, initialLearning, finalLearning, iterations);

		}
	}


	private int clusterInstance(Instance instance){
		final Neuron bmu = findBMU(root, instance, root);
		return bmu.getId();
	}


	private void computeLabels() {
		if(!isClusteringMode){
			final Instances labeledData = new Instances(trainingSet,0);
			Instance aux;

			for(int i=0;i<trainingSet.numInstances();i++){
				aux = trainingSet.instance(i);
				if(!aux.classIsMissing()){
					labeledData.add((Instance) aux.copy());
				}
			}

			final int[] clusterVector = generateClusterVector(labeledData);
			assignClassToNeurons(root, labeledData, clusterVector);
		}
	}


	private void describeTopology(){
		root = new Neuron(null,topology,trainingSet);
		map= new HashMap<Neuron, Map<Integer, List<Neuron>>> ();

		neuronList.clear();
		treeToArray(root);
		preComputeNeighbors();
	}


	private Neuron findBMU(Neuron currentNode,Instance inputSample, Neuron bestSoFar){

		double distanceResult =distance.calculate(inputSample, bestSoFar.getWeight());
		double c;
		Neuron candidateNeuron=null;

		for (final Neuron neuron : neuronList) {
			candidateNeuron = neuron;
			c=distance.calculate(inputSample, candidateNeuron.getWeight());
			if(c < distanceResult){
				bestSoFar = candidateNeuron;
				distanceResult=c; // remember the distance
			}
		}
		return bestSoFar;
	}

	public int[] generateClusterVector(Instances data){
		final int[] clusterVector = new int[data.numInstances()];
		for(int i=0;i<data.numInstances();i++){
			clusterVector[i] = clusterInstance(data.instance(i));
		}
		return clusterVector;
	}

	private List<Neuron> getPrecomputedBubble(long radius, Neuron bmu) {
		List<Neuron> bubbleOfActivity;
		final Map<Integer, List<Neuron>> list = map.get(bmu);

		if(radius+1>list.size()) {
			bubbleOfActivity=list.get(new Integer(list.size()-1));
		} else {
			bubbleOfActivity=list.get(new Integer((int)radius));
		}

		return bubbleOfActivity;
	}

	private void preComputeNeighbors() {
		map.clear();
		List<Neuron> bubbleOfActivity;
		Map<Integer, List<Neuron>> radiusToNeurons;
		for (final Neuron neuron : neuronList ){
			int radius=0;
			radiusToNeurons = new HashMap<Integer, List<Neuron>>() ;
			do{
				bubbleOfActivity = new ArrayList<Neuron>();
				bubbleOfActivity.add(neuron);
				calculateNeighborhood(bubbleOfActivity, neuron, radius, null);
				radiusToNeurons.put(radius, bubbleOfActivity);
				radius++;
			}
			while(bubbleOfActivity.size()<neuronList.size());
			map.put(neuron,radiusToNeurons);
		}
	}

	private void train(Instance inputSample, Neuron root,
			long radius,double learningRate){

		final Neuron bmu = findBMU(root, inputSample, root);
		final List<Neuron> bubbleOfActivity = getPrecomputedBubble(radius, bmu);

		for(final Neuron neuron : bubbleOfActivity){
			updateRule(neuron.getWeight(), learningRate, inputSample);
		}
	}


	private void treeToArray(Neuron node){
		if(node!=null){
			neuronList.add(node);
			for(final Neuron child : node.getChildren()){
				treeToArray(child);
			}
		}
	}

	public void updateRule(Instance weight, double learningRate,Instance inputSample){
		for(int i=0;i<weight.numAttributes();i++){
			final double value = weight.value(i)+learningRate*(inputSample.value(i) - weight.value(i));
			weight.setValue(i,value);
		}

	}


	private Distance selectDistance(String distance){
		if("0".equals(distance) || distance==null){
			return new EuclideanDistance();
		}
		return new ManhattanDistance();
	}

	private boolean createClusteringOption(String clusteringOption){

		return "true".equals(clusteringOption) ? true : false;
	}

	public Instances getTrainingSet(){
		return this.trainingSet;
	}



}