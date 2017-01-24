package com.classifier.ttosom.utils;

import com.classifier.ttosom.TTOSOM;

import weka.classifiers.Evaluation;
import weka.core.Instances;

public class PerformanceMetricsUtils {

	public static void crossValidation(final TTOSOM ttosom, Instances trainingSet,final int folds){
		try{
			System.out.println("Cross Validation");
			final Evaluation eval = new Evaluation(trainingSet);
			eval.crossValidateModel(ttosom, trainingSet,folds, ttosom.getRandom());
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			System.out.println(eval.toMatrixString());
		}
		catch(final Exception e){
			//System.out.println(e.printStackTrace());
			e.printStackTrace();
		}
	}

	public static void trainTestSet(TTOSOM ttosom, Instances dataset, Instances testingSet){
		try {
			System.out.println("Training and test sets");
			ttosom.buildClassifier(dataset);
			final Evaluation eval = new Evaluation(dataset);
			eval.evaluateModel(ttosom, testingSet);
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			System.out.println(eval.toMatrixString());
		}
		catch (final Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

}
