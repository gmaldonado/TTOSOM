package com.classifier.ttosom.distance;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.io.Serializable;

import weka.core.Instance;

public class EuclideanDistance implements Distance,Serializable{


	private static final long serialVersionUID = 1L;

	@Override
	public double calculate(Instance item1, Instance item2) {

		double result = 0.0;

		for (int i=0; i< item1.numAttributes(); i++) {
			if (!item1.isMissing(i) && !item2.isMissing(i)){
				result+=pow(item1.value(i)-item2.value(i), 2);
			}
		}
		return sqrt(result);
	}

}
