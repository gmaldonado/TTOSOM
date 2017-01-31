package com.classifier.ttosom.distance;

import static java.lang.Math.abs;

import weka.core.Instance;

public class ManhattanDistance implements Distance{

	@Override
	public double calculate(Instance item1, Instance item2) {

		double result = 0.0;

		for(int i=0;i<item1.numAttributes();i++){
			if(!item1.isMissing(i) && !item2.isMissing(i)){
				result+=abs(item1.value(i)-item2.value(i));
			}
		}
		return result;
	}

}
