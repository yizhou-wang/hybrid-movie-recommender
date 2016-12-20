package com.MovieRecommender.UserBasedMovieRecommender;

import java.io.FileNotFoundException;

import org.apache.mahout.cf.taste.common.TasteException;

/**
 * User Based Recommender
 * 
 * Recommend Movies based on Hybrid Recommender Model. 
 * 
 * Used Models:
 * 	1.	Users-based Model
 * 	2.	Content-based Model
 * 
 * Used Data:
 * 	Yahoo! Movie Dataset, including
 * 	1.	ydata-ymovies-user-movie-ratings-train-v1_0.txt
 * 	2.	ydata-ymovies-user-demographics-v1_0.txt
 * 	3.	ydata-ymovies-movie-content-descr-v1_0.txt
 * 
 */

public class MovieRecommender 
{
    public static void main( String[] args ) throws TasteException, FileNotFoundException
    {
    	// Recommendation Engine for Movie Recommendation
    	RecommendationEngine engine = new RecommendationEngine();
    	engine.recommendForAllUsers();
    }
}
