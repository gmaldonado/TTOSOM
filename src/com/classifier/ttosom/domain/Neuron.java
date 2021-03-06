package com.classifier.ttosom.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import weka.core.Instance;
import weka.core.Instances;

public class Neuron implements Serializable{


	private static final long serialVersionUID = 1L;
	private int id;
	private List<Neuron> children;
	private Neuron parent;
	private Instance weight;

	public Neuron(Neuron parent,List<Pair<Integer,Integer>> topology,Instances dataSet){
		final int childrenCount = topology.get(0).getValue();
		final Random random = new Random();
		this.weight = (Instance)dataSet.instance(random.nextInt(dataSet.numInstances())).copy();
		this.children = new ArrayList<Neuron>();
		this.parent = parent;
		this.id = topology.get(0).getKey();
		topology.remove(0);
		for(int i=0;i<childrenCount;i++){
			this.children.add(new Neuron(this,topology,dataSet));
		}
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

	public List<Neuron> getChildren() {
		return children;
	}

	public void setChildren(List<Neuron> children) {
		this.children = children;
	}

	public Neuron getParent() {
		return parent;
	}

	public void setParent(Neuron parent) {
		this.parent = parent;
	}


}