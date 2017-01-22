package com.classifier.ttosom;


import static com.classifier.ttosom.utils.Utils.readArffFile;
import static weka.core.Utils.getOption;

import weka.core.Instances;


public class Main {

	public static void main(String[] args) throws Exception{
		final Instances trainingSet = readArffFile(getOption("t",args)); //training
		final TTOSOM ttosom = new TTOSOM(trainingSet);
		ttosom.setOptions(args);
		ttosom.buildClassifier(trainingSet);

		classifyUnlabeledData(ttosom);

	}

	private static void classifyUnlabeledData(final TTOSOM ttosom) {
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
				//If we just have unlabeled instances, then do clustering
				System.out.println("Clustering (you have only unlabed data)");
				final int[] clusterVector =ttosom.generateClusterVector(dataSet);
				for (final int element : clusterVector) {
					System.out.print(element+" ");
				}
				System.out.println("");
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