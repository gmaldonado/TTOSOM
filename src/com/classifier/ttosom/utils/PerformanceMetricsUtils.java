package com.classifier.ttosom.utils;

import static com.classifier.ttosom.utils.Utils.readArffFile;

import java.util.Random;

import com.classifier.ttosom.TTOSOM;

import weka.classifiers.Evaluation;
import weka.core.Instances;

public class PerformanceMetricsUtils {

	public static void crossValidation(final TTOSOM ttosom, final Instances dataset, final int folds, final int seed){
		try{
			System.out.println("Cross Validation");
			final Evaluation eval = new Evaluation(dataset);
			eval.crossValidateModel(ttosom, dataset,folds, new Random(seed));
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			System.out.println(eval.toMatrixString());
		}
		catch(final Exception e){
			//System.out.println(e.printStackTrace());
			e.printStackTrace();
		}
	}

	public static void trainTestSet(TTOSOM ttosom, Instances dataset, String testingSetPath){
		try {
			System.out.println("Training and test sets");
			ttosom.buildClassifier(dataset);
			final Evaluation eval = new Evaluation(dataset);
			final Instances test = readArffFile(testingSetPath);
			eval.evaluateModel(ttosom, test);
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			System.out.println(eval.toMatrixString());
		}
		catch (final Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

}
