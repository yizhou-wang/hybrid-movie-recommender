# Hybrid Movie Recommender 
**Authors: Yizhou Wang, Jiechao Gao, Xing Yuan**

This is a README file for our big data analytics final project open source codings.

The software and platforms used for this project are varying because of different purposes of applications. 
The recommender construction used Java programming language under mahout dependency. The platform for this part is Eclipse Neon 1a, with built mahout dependency. Used mahout sources are listed below.

	org.apache.mahout.cf.taste.common.TasteException
	org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood
	org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender
	org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity
	org.apache.mahout.cf.taste.model.DataModel
	org.apache.mahout.cf.taste.model.Preference
	org.apache.mahout.cf.taste.model.PreferenceArray
	org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator
	org.apache.mahout.cf.taste.impl.model.file.FileDataModel
	org.apache.mahout.cf.taste.neighborhood.UserNeighborhood
	org.apache.mahout.cf.taste.recommender.RecommendedItem
	org.apache.mahout.cf.taste.recommender.UserBasedRecommender
	org.apache.mahout.cf.taste.similarity.UserSimilarity

There is a data folder including 3 data files. 

 1. data.csv 		<——> 	ydata-ymovies-user-movie-ratings-train-v1_0.txt
 2. demographics.txt 	<——> 	ydata-ymovies-user-demographics-v1_0.txt
 3. synopsis 		<——> 	ydata-ymovies-movie-content-descr-v1_0.txt

We run this coding and this is the main function RecommendationEngine. We can see there are three models, DemographicModel which means User-Based Model, SynopsisModel which means Content-Based Model, and FileDataModel which means Data Model. 

To make the demo simple, we can enter the test number of users here. I set this number to be 50, so it will run the first 50 users of the dataset. 

In each loop, we use the model above to find neighbors of the user, then write the recommendation results into a csv file. This file will be used for data visualization in System G, because System G need to load two csv files as nodes and edges.

Result visualization is shown on IBM System G. Use System G to load csv files and use gShell command to show different properties of the recommendation results. 
