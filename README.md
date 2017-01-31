TTOSOM
================================

TTOSOM: Java implementation

Technical matters of this classifier can be found [here](http://www.sciencedirect.com/science/article/pii/S0169743917300102).
###Requirements

* Java 8
* Apache Maven 3.3.x

###How to build 

This is very simple to build, once maven is installed, you just need to write:

`mvn clean install`

And this will generate a **fat jar**, which is a jar with all the neccessary dependencies, ready to use.


###How to execute

####Mandatory parameters
* -t training set file path.
* -g tree file path. 
* -i number of iterations. 
* -r initial radius.
* -R final radius.
* -l initial learning rate.
* -L final learning rate. 
* -d distance function (0 = Euclidean distance, 1 = Manhattan distance).

####Optional parameters

* -x number of folds for n-fold cross validation.
* -s seed value.
* -c clustering option (true or false)
* -T testing set file path.

####Meaning of each parameter

* **Training set file path:** Must be an ARFF file, which specifies the classes of the training set (at least one class). For every instance with an unknown class value, "?" should be used. 

* **Tree file path:** File which contains one line with the topology of the tree as an array. Every element in the line MUST be separated by an space and it indicates the number of children of that node in the tree

* **Number of iterations:** Number of iterations (integer).

* **Initial and final radius:** Initial and final radius (double).

* **Initial and final learning rate:** Initial and final learning rate (double).

* **Distance function:** Distance function to be used (0 = Euclidean distance, 1 = Manhattan distance).

* **Number of folds:** Number of folds to use n-fold cross validation.

* **Seed value:** Seed of the randomizer.

* **Clustering option:** If you want to operate only in cluster mode and get a cluster vector.

* **Testing set file path:** true if you want to get the cluster vector.

If you don't select any option, TTOSOM will work classifying each unlabeled instance in the training set. If the data has only unlabeled instances, it is going to operate in clustering mode.

###Execution example


This executes 10-cold cross validation with 100.000 iterations, initial radius =4, final radius=0, initial learning rate = 0.9, final learning rate = 0 and Euclidean distance

`java -jar TTOSOM.jar -t training-set.arff -g tree.file -i 100000 -r 4 -R 0 -l 0.9 -L 0 -d 0 -x 10`

