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

	private static final String SEED_OPTION = "s";
	private static final String CLUSTERING_OPTION = "c";
	private static final String DISTANCE_OPTION = "d";
	private static final String FINAL_LEARNING_RATE_OPTION = "L";
	private static final String INITIAL_LEARNING_RATE_OPTION = "l";
	private static final String FINAL_RADIUS_OPTION = "R";
	private static final String INITIAL_RADIUS_OPTION = "r";
	private static final String ITERATIONS_OPTION = "i";
	private static final String TREE_OPTION = "g";
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
	private boolean inClusteringMode;
	private final Random random;
	private int seed = 1;
	private List<Pair<Integer, Integer>> topology;
	private static  TTOSOM instance = null;

	public static TTOSOM getInstance(Instances trainingSet){
		if(instance == null){
			return new TTOSOM(trainingSet);
		}
		return instance;
	}

	private TTOSOM(Instances trainingSet){
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
		topology = readTopology(getOption(TREE_OPTION, options));
		iterations = toInt(getOption(ITERATIONS_OPTION, options));
		initialRadius = toDouble(getOption(INITIAL_RADIUS_OPTION, options));
		finalRadius = toDouble(getOption(FINAL_RADIUS_OPTION, options));
		initialLearning = toDouble(getOption(INITIAL_LEARNING_RATE_OPTION, options));
		finalLearning = toDouble(getOption(FINAL_LEARNING_RATE_OPTION, options));
		distance = selectDistance(getOption(DISTANCE_OPTION, options));
		inClusteringMode = createClusteringOption(getOption(CLUSTERING_OPTION, options));
		seed = toInt(getOption(SEED_OPTION, options));
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

		double currentRadius = initialRadius;
		double currentLearningRate = initialLearning;

		for(int i=0;i<iterations;i++){
			final int j = random.nextInt(data.numInstances());
			train(data.instance(j), root,round(currentRadius), currentLearningRate);
			currentRadius = calculateValue(currentRadius, initialRadius, finalRadius, iterations);
			currentLearningRate = calculateValue(currentLearningRate, initialLearning, finalLearning, iterations);
		}
	}


	private int clusterInstance(Instance instance){
		final Neuron bmu = findBMU(root, instance, root);
		return bmu.getId();
	}


	private void computeLabels() {
		if(!inClusteringMode){
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
		double currentDistance;
		Neuron candidateNeuron=null;

		for (final Neuron neuron : neuronList) {
			candidateNeuron = neuron;
			currentDistance=distance.calculate(inputSample, candidateNeuron.getWeight());
			if(currentDistance < distanceResult){
				bestSoFar = candidateNeuron;
				distanceResult=currentDistance;
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


	private Distance selectDistance(String distanceParameter){

		Distance distance = null;

		switch(distanceParameter){
		case "0":
			distance = new EuclideanDistance();
			break;
		case "1":
			distance = new ManhattanDistance();
			break;
		default:
			throw new IllegalArgumentException("Invalid type of distance");
		}
		return distance;
	}

	private boolean createClusteringOption(String clusteringOption){

		return "true".equals(clusteringOption) ? true : false;
	}

	public Instances getTrainingSet(){
		return this.trainingSet;
	}


	public Random getRandom(){
		return this.random;
	}

	public int getSeed(){
		return this.seed;
	}

	public boolean isInClusteringMode(){
		return inClusteringMode;
	}

}