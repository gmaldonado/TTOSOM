package com.classifier.ttosom.distance;

import weka.core.Instance;

public interface Distance{

	public double calculate(Instance item1, Instance item2);

}
