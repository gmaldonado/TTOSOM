package com.classifier.ttosom;


import static com.classifier.ttosom.utils.PerformanceMetricsUtils.crossValidation;
import static com.classifier.ttosom.utils.PerformanceMetricsUtils.trainTestSet;
import static com.classifier.ttosom.utils.Utils.readArffFile;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static weka.core.Utils.getOption;

import java.util.stream.IntStream;

import weka.core.Instances;


public class Main {


	private static final String TESTING_SET_OPTION = "T";
	private static final String CROSS_VALIDATION_OPTION = "x";
	private static final String TRAINING_SET_OPTION = "t";

	public static void main(String[] args) throws Exception{
		final Instances trainingSet = readArffFile(getOption(TRAINING_SET_OPTION,args));
		final String foldsOption = getOption(CROSS_VALIDATION_OPTION,args);
		final String testingSetOption = getOption(TESTING_SET_OPTION,args);
		final TTOSOM ttosom = TTOSOM.getInstance(trainingSet);
		ttosom.setOptions(args);

		if(isNotBlank(foldsOption)){
			final Integer folds = toInt(foldsOption);
			crossValidation(ttosom,trainingSet,folds);
		}

		else if(ttosom.isInClusteringMode()) {
			printClusterVector(ttosom);
		}

		else if(isNotBlank(testingSetOption)){
			final Instances testingSet = readArffFile(testingSetOption);
			trainTestSet(ttosom, trainingSet, testingSet);
		}

		else {
			classifyUnlabeledData(ttosom);
		}


	}


	private static void printClusterVector(TTOSOM ttosom) throws Exception{
		ttosom.buildClassifier(ttosom.getTrainingSet());
		final int[] clusterVector = ttosom.generateClusterVector(ttosom.getTrainingSet());
		IntStream.of(clusterVector).forEach(element -> System.out.print(" "+element));
	}

	private static void classifyUnlabeledData(final TTOSOM ttosom) throws Exception {
		final Instances dataSet = ttosom.getTrainingSet();
		//Let's separate the unlabeled data
		final Instances unlabeled = new Instances(dataSet,0);
		for(int i=0;i<dataSet.numInstances();i++){
			if(dataSet.instance(i).classIsMissing()){
				unlabeled.add(dataSet.instance(i));
			}
		}
		if(unlabeled.numInstances()>0){
			if(unlabeled.numInstances()==dataSet.numInstances()){
				printClusterVector(ttosom);
			}
			else{
				System.out.println("");
				System.out.println("Classifying the unlabeled data");
				System.out.println("");


				final Instances labeled = new Instances(unlabeled);

				for(int i=0;i<unlabeled.numInstances();i++){
					final double classLabel = ttosom.classifyInstance(unlabeled.instance(i));
					labeled.instance(i).setClassValue(classLabel);
					System.out.println(labeled.instance(i));
				}
			}


		}
	}


}