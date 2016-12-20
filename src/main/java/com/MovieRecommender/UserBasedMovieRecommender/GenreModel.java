package com.MovieRecommender.UserBasedMovieRecommender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

public class GenreModel 
{
	private String path;
	private Map<Long, Set<String>> genreData;
	
	public GenreModel(String path) throws IOException
	{
		File file = new File(path);
		if(!file.exists() || file.isDirectory())
		{
		    throw new FileNotFoundException(file.toString());
		}
		else
		{
			this.path = path;
		}
		buildModel();
	}
	
	private void buildModel() throws IOException
	{
		genreData = new HashMap<Long, Set<String>>();
		try(CSVReader reader = new CSVReader(new FileReader(path), '\t'))
		{
		String[] line;
			while ((line = reader.readNext()) != null) {
				if (line.length >= 2) {
					long id = Long.parseLong(line[0]);
					if(genreData.containsKey(id))
					{
						genreData.get(id).add(line[1]);
					}
					else{
						Set<String> genreSet = new HashSet<String>();
						genreSet.add(line[1]);
						genreData.put(id, genreSet);
					}
				}
			}
		}
	}
	
	public Set<String> getGenresByItemId(long id)
	{
		if(genreData.containsKey(id))
		{
			return genreData.get(id);
		}
		return null;
	}
}
