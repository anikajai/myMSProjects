Data description
==================
https://www.kaggle.com/bittlingmayer/amazonreviews
This dataset is an extract from the Amazon Reviews Kaggle competition. The goal is to perform
sentiment analysis to determine whether a review is positive or negative. We have provided a
CSV file on D2L which contains the binary label (positive/negative) and the corresponding text
for the 400,000 reviews.

Problem statement
=================
Use Neural Networks, Decision trees and a third algorithm with Accuracy, ROC curve, precision/recall and Confusion matrix.

Solution
========
Using stratified sampling selected 10K records among 0.4 million records. From 5000 of positive labelled records, kept random 4000
as part of training set and 1000 as part of test set. Similarly for 5000 negative class labelled records. Thus, a total of 8000 records
as training set and 2000 as test set.
On these records, performed numerous preprocessing steps:
a. removal of stop words, 
b. removal of characters like comma, full-stop,etc.
c. Converted all text to lower case

Extracted features based on TF-IDF per review record. But, the resultant feature set was too high. Thus, called dimensioanlity reduction
on this. After experimenting with numerous values for selection set among these dimensions, settled for 100 dimensions.
Other than this, appended probabilities returned from Mathematica's Classify method with "Sentiment" attribute.
These probabilities were denoting the probability of the review to be positive, negative and neutral.
Total set of 103 attributes, the training set was trained for Random Forest, Neural Networks and Support Vector Machine. I chose third
algorithm as SVM because I observed that datase was performing bad on probability based classifier like Naive Bayes.