package com.classifier.ttosom.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import weka.core.Instances;

public  class Utils {


	public static Instances readArffFile(String file){
		Instances data=null;
		try{
			final BufferedReader reader = new BufferedReader(new FileReader(file));
			data = new Instances(reader);
			reader.close();
			data.setClassIndex(data.numAttributes() - 1);
			return data;
		}
		catch(final FileNotFoundException exception){
			System.out.println("Error when reading ARFF file");
			//System.exit(0);
		}
		catch(final IOException exception){
			System.out.println("Error when reading ARFF file");
			//System.exit(0);
		}

		return data;

	}

	public static List<Pair<Integer,Integer>> readTopology(String filePath){
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;
		final List<Pair<Integer,Integer>> nodes = new ArrayList<Pair<Integer,Integer>>();

		try{
			file = new File(filePath);
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			final String line = br.readLine();
			final String[] values = line.split(" ");
			for(int i=0;i<values.length;i++){
				//	final Pair nodeValue = new Pair(i+1,Integer.parseInt(values[i]));
				final Pair<Integer, Integer> nodeValue = Pair.of(i+1, Integer.parseInt(values[i]));
				nodes.add(nodeValue);
			}
			return nodes;

		}
		catch(final Exception e){
			System.out.println("Error reading topology file");
			System.exit(0); //http://netbeans-org.1045718.n5.nabble.com/quot-Forked-Java-VM-exited-abnormally-quot-when-running-junit-tests-seperately-td4388780.html
		}
		finally {
			try{
				if( fr!=null ){
					fr.close();
				}
			}
			catch(final Exception e2){
				System.out.println("Error closing topology file");
				System.exit(0); //http://netbeans-org.1045718.n5.nabble.com/quot-Forked-Java-VM-exited-abnormally-quot-when-running-junit-tests-seperately-td4388780.html
			}
		}

		return null;
	}


}
