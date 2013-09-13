#TTOSOM How to 

###Parameters 

The parameters of the TTOSOM are (mandatory order):

* data set path 
* tree file path 
* number of iterations 
* initial radius
* final radius
* initial learning rate
* final learning rate 
* distance function (0 = Euclidean distance, 1 = Manhattan distance)

Additional parameters 

* -i input file which loads a tree as a serialized TTOSOM object
* -o output file which saves a tree as a serialized TTOSOM object
* -t test set path 
* -x cross validation folds 
* -s seed value 
* -c clustering option 

The meaning of the parameters are: 

* data set path
	Must be an ARFF file which must specify the classes of the data set (at least one, with any value). For every instance with not known label, you should use "?", as an 

* example:
	1.3, 2.5, 3.4,? 

* tree file path
	File which contains one line with the topology of the tree as an array. Every element in the line MUST be separated by an space and it indicates the number of childs of that * node in the tree

* number of iterations
	This is the number of iterations for the tree and must be an integer value

* initial radius
	This is the initial radius and must be a double value 

* final radius
	This is the final radius and must be a double value 

* initial learning rate
	This is the initial learning rate and must be a double value

* final learning rate 
	This is the final learning rate and must be a double value 

* distance function
	This is the distance function to be used. 0 for Euclidean distance and 1 for Manhattan distance

* -i input file 
	Loads a tree as a serialized TTOSOM object (-i is mandatory, to indicate the option)


* -o output file 
	Saves a tree as a serialized TTOSOM object (-o is mandatory, to indicate the option)

* -t test set path	
	If you want to use a test set to test the TTOSOM and get statistical information (-t is mandatory, to indicate the option)

* -x cross validation folds 
	If you want to use cross validation, you should select the number of folds (-x is mandatory to indicate the option)

* -s seed value 
	If you want to change the seed of the random value, by default is 1 (-s is mandatory, to indicate the option)

* -c
	The program operates in cluster mode and returns the cluster vector, which indicates which is the cluster (neuron) for that instance in the TTOSOM


Note: You can not use -t, -x and -c together, just once a time.

If you do not select any option, the TTOSOM will work as a classifier. Will classify the not labeled instances. If the data set just have unlabelled instances, it is going to operate in clustering mode.


Examples
----------------

NOTE: The only provided dataset is "iris.arff", the other ones are just examples.

This executes 10-cold cross validation with 100.000 iterations, initial radius =8, final radius=0, initial learning rate = 0.0, final learning rate = 0 and Euclidean distance

java -jar TTOSOM.jar iris.arff topology.txt 100000 8 0 0.9 0 0 -c 10

This executes an example using a test set with 100.000 iterations, initial radius =8, final radius=0, initial learning rate = 0.0, final learning rate = 0 and Euclidean distance

java -jar TTOSOM.jar 80.arff topology.txt 100000 8 0 0.9 0 0 -t 20.arff

This classifies the instances  with 100.000 iterations, initial radius =8, final radius=0, initial learning rate = 0.0, final learning rate = 0 and Euclidean distance

java -jar TTOSOM.jar dataset.arff topology.txt 100000 8 0 0.9 0 0