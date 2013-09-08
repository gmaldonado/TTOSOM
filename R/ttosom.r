#   Tree-based Topology-Oriented SOM: Java implementation and R binding
#   Copyright (C) 2013  Gonzalo Maldonado, Cesar A. Astudillo
#
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

# Implementation of the TTOSOM algorithm as  described in
# César A. Astudillo and B. John Oommen. Imposing Tree-based Topologies onto Self Organizing Maps. Information Sciences, 2011, 181, pp. 3798-3815. doi:10.1016/j.ins.2011.04.038. 
# http://dx.doi.org/10.1016/j.ins.2011.04.038
# 
# All parameters must be strings, including learning rate, etc. this is because the integration with java.
# + dataset the training arff file 
# + topology The topology is specified in a text file in one line specifying the number of children per node following the guidelines of the describe topopology method described in
# + iterations the total number of training iterations
# + initRadius initial value for the radius of the bubble of activity
# + finalRadius final value for the radius of the bubble of activity
# + initLearning initial value for the learning rate.
# + finalLearning final value for the learning rate.
# + distance the type of distance, 0: Euclidean 1: Manhattan
# + input -i for input file
# + inputFile the source file where a TTOSOM (including topology and values for the weights) will be loaded.
# + output -o for output file
# + outputFile the source file where a TTOSOM (including topology and values for the weights) will be saved.
# + stats -x for cross validation, -t for test set ,-c for clustering
# + option_stats number of folds if -x in stats, path test set if -t in stats, nothing if -c in stats
# + seed -s for seed value
# + seed_value the new value of the seed

require(rJava)  ## check fot he existence of the rJava package an load it if available

ttosom <- function(dataset,topology="topology.txt",iterations="10",initRadius="8",finalRadius="0",initLearning="0.9",finalLearning="0.0",distance="0",inputFile="",outputFile="",stats="",option_stats="",seed_value=""){
	input=""
	output=""
	seed=""
	if(inputFile!=""){
		input="-i"
	}
	if(outputFile!=""){
		output="-o"
	}

	if(seed_value!=""){
		seed="-s"
	}

	.jinit(getwd(), parameters="-Xmx2048m")  ## current path and memory (2GB)
	object = .jnew("Main")  ## java object to be loaded

	args <- .jarray(c(dataset,topology,iterations,initRadius,finalRadius,initLearning,finalLearning,distance,input,inputFile,output,outputFile,stats,option_stats,seed,seed_value))  ## load parameteres of the class
	.jcall(object,"V","main",args) ## call the object 

	#If it's trying to classify. That's the traditional use of TTOSOM. In other case it will just show the statistics and no object will be returned.

	if(stats==""){
		result = .jcall(object,"[[D","getVectors")  ## get output for object, a matrix of weights

		matrix=sapply(result,.jevalArray) ##sapply - When you want to apply a function to each element of a list in turn, but you want a vector back, rather than a list.
	    
		matrix = t(matrix)  

		matrix  ## return the matrix with the prototype weigths
	}

}

