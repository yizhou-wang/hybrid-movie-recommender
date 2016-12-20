package com.MovieRecommender.UserBasedMovieRecommender;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class RecommendationEngine {
	
	private int NUMBER_OF_ITEMS_TO_RECOMMEND = 20;
	private int NUMBER_OF_FINAL_ITEMS_TO_RECOMMEND = 5;
	
	private DemographicModel dModel = null;
	private GenreModel gModel = null;
	private SynopsisModel sModel = null;
	private DataModel dataModel;
	
	private String DemographicModelPath = "data/demographics.txt";
	private String GenreModelPath = "data/genres.txt";
	private String SynopsisModelPath = "data/synopsis2.txt";
	private String DataModelPath = "data/data.csv";
	
	public RecommendationEngine() throws TasteException
	{
		try {
			dModel = new DemographicModel(DemographicModelPath);
		} catch (IOException e) {
			System.out.println("Demographics Data not available. Skipping demographics data.");
		}
		try {
			sModel = new SynopsisModel(SynopsisModelPath);
		} catch (IOException e) {
			System.out.println("Synopsis Data not available. Skipping synopsis data.");
		}
		try {
			dataModel = new FileDataModel(new File(DataModelPath));
		} catch (IOException e) {
			System.out.println("Data is not available. Exiting...");
			System.exit(1);
		}
	}
		
	public void recommendForAllUsers() throws TasteException, FileNotFoundException
	{
		LongPrimitiveIterator iterator = dataModel.getUserIDs();
		
		PrintWriter pw = new PrintWriter(new File("movies_edges.csv"));
        StringBuilder sb = new StringBuilder();
        StringBuilder sb_out = new StringBuilder();

		for(int i=0; i<50; i++)  //test num of users
		{
			long id = iterator.nextLong();
			sb_out = recommendForAUser(id, sb);	        		
		}
		
		pw.write(sb_out.toString());
        pw.close();
	}
	
	public StringBuilder recommendForAUser(long userId, StringBuilder sb) throws TasteException, FileNotFoundException
	{
    	UserSimilarity similarity = new TanimotoCoefficientSimilarity(dataModel);
    	UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dataModel);
    	List<RecommendedItem> recommendedList = 
    			recommend(dataModel, similarity, neighborhood, userId, NUMBER_OF_ITEMS_TO_RECOMMEND);
    	Map<RecommendedItem, Double> itemWeights = new HashMap<RecommendedItem, Double>();
    	for(RecommendedItem r : recommendedList)
    	{
    		itemWeights.put(r, (double)r.getValue());
    	}
    	factorModels(userId, itemWeights);
    	Set<Entry<RecommendedItem, Double>> entrySet = itemWeights.entrySet();
    	List<Entry<RecommendedItem, Double>> entryList = new ArrayList<Entry<RecommendedItem, Double>>(entrySet);
    	Collections.sort(entryList, new Comparator<Entry<RecommendedItem, Double>>()
    			{
					@Override
					public int compare(Entry<RecommendedItem, Double> o1,
							Entry<RecommendedItem, Double> o2) 
					{
						return o2.getValue().compareTo(o1.getValue());
					}
    			});
    	Map<Long, Double> resultList = new HashMap<Long, Double>();
    	System.out.println("Recommendation for user " + userId);
    	
    	for(int i=0; i<NUMBER_OF_FINAL_ITEMS_TO_RECOMMEND; i++)
    	{
    		resultList.put(entryList.get(i).getKey().getItemID(), entryList.get(i).getValue());
    		System.out.println(entryList.get(i).getKey().getItemID());
            // save csv file
	        sb.append(userId);
	        sb.append(',');
	        sb.append(entryList.get(i).getKey().getItemID());
	        sb.append(',');
	        sb.append(entryList.get(i).getValue());
	        sb.append('\n');
    	}
        
    	return sb;
	}
	
	private List<RecommendedItem> recommend(DataModel model, UserSimilarity similarity,
			UserNeighborhood neighborhood, long userId, int numberOfItemsToRecommend) throws TasteException 
	{
		UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
		return recommender.recommend(userId, numberOfItemsToRecommend);
	}
	
	private void factorModels(long userId, Map<RecommendedItem, Double> weights) throws TasteException
	{
		for(RecommendedItem r : weights.keySet())
		{
			if(dModel != null)
			{
				PreferenceArray preferences = dataModel.getPreferencesForItem(r.getItemID());
				factorDemographicsModel(userId, weights, r, preferences);
			}
			PreferenceArray preferencesForItem = dataModel.getPreferencesFromUser(userId);
			if(gModel != null)
			{
				factorGenreModel(weights, r, preferencesForItem);
			}
			if(sModel != null)
			{	
				factorSynopsisModel(weights, r, preferencesForItem);
			}
		}
	}

	private void factorDemographicsModel(long userId,
			Map<RecommendedItem, Double> weights, RecommendedItem r,
			PreferenceArray preferences) {
		int gender = 0;
		int yeardiff = 0;
		int prefcount = 0;
		for(Preference p : preferences)
		{
			if(p.getValue() > 0)
			{
				if(dModel.getSexTypeByUserID(p.getUserID()).equalsIgnoreCase("m"))
				{
					gender++;
				}
				else if(dModel.getSexTypeByUserID(p.getUserID()).equalsIgnoreCase("f"))
				{
					gender--;
				}
				if(dModel.getBirthYearByUserID(p.getUserID()) != -1)
				{
					yeardiff += 
							Math.abs(dModel.getBirthYearByUserID(p.getUserID() 
									- dModel.getBirthYearByUserID(userId)));
				}
			}
			prefcount++;
		}
		if(gender < 0){
			if(dModel.getSexTypeByUserID(userId).equalsIgnoreCase("f"))
			{
				weights.put(r, weights.get(r) + 0.2);
			}
		}
		else if(gender > 0)
		{
			if(dModel.getSexTypeByUserID(userId).equalsIgnoreCase("m"))
			{
				weights.put(r, weights.get(r) + 0.2);
			}
		}
		int x = yeardiff/(prefcount*5);
		double birthyearweight = ((5 - x) >= 0)?x:0;
		weights.put(r, weights.get(r) + birthyearweight*0.1);
	}
	
	private void factorGenreModel(Map<RecommendedItem, Double> weights, RecommendedItem r,
			PreferenceArray preferences)
	{
		int commonGenre = 0;
		int items = 0;
		for(Preference p : preferences)
		{
			if(p.getValue() > 0)
			{
				if(gModel.getGenresByItemId(r.getItemID()) != null){
					Set<String> intersection 
						= new HashSet<String>(gModel.getGenresByItemId(r.getItemID()));
					intersection.retainAll(gModel.getGenresByItemId(p.getItemID()));
					commonGenre += intersection.size();
					items++;
				}
			}
		}
		if(items > 0)
		{
			int common = commonGenre/items;
			if(common > 4)
			{
				common = 4;
			}
			weights.put(r, weights.get(r) + common*0.2);
		}
	}
	
	private void factorSynopsisModel(Map<RecommendedItem, Double> weights, RecommendedItem r,
			PreferenceArray preferences)
	{
		int commonContent = 0;
		int items = 0;
		for(Preference p : preferences)
		{
			if(p.getValue() > 0)
			{
				if(sModel.getContentByItemId(r.getItemID()) != null){
					Set<String> intersection 
						= new HashSet<String>(sModel.getContentByItemId(r.getItemID()));
					intersection.retainAll(sModel.getContentByItemId(p.getItemID()));
					commonContent += intersection.size();
					items++;
				}
			}
		}
		if(items > 0)
		{
			int overlap = commonContent/items;
			if(overlap > 10)
			{
				overlap = 10;
			}
			weights.put(r, weights.get(r) + overlap*0.05);
		}
	}
}
