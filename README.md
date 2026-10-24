# Fraud-Detection
An adaptive boost machine learning algorithm to detect fraudulent transactions in a given geography.

This was my final project for the course COS226: Data Structures & Algorithms at Princeton University, written in collaboration with Remzi Abaci of Princeton University using packages provided by the Princeton Computer Science Department. Information about the assignment can be found on the Princeton Computer Science website here: https://www.cs.princeton.edu/courses/archive/spring25/cos226/assignments/fraud/specification.php.

The crux of this assignment is to create a machine learning algorithm that can detect fraudulent transactions across a finite number of transaction spots around Princeton. 

# Dimension reduction
The first part of the project is to reduce the dimension of a transaction summary by clustering transactions that occur at establishments that are close in proximity to one another. This is accomplished using connected components of a minimum spanning tree of an edge weighted graph. 

# Weak learners and adaptive boosting
The second part of the project is to implement and train a set of weak learners. An adaptive boosting algorithm is then used to create a functional fraud prediction algorithm.

# Results
Using 64 clusters and 250 iterations of the boosting algorithm, the model achieves 98.25% accuracy in predicting fraud across the test set. 
