package com.MovieRecommender.UserBasedMovieRecommender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.HashMap;

import au.com.bytecode.opencsv.CSVReader;

public class SynopsisModel 
{
	private final int MAX_TERMS = 20;
	private String path;
	private Map<Long, Set<String>> contentData;
	
	public SynopsisModel(String path) throws IOException
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
	
	private void processLine(long id, String content)
	{
		content.replaceAll("[-+.^:,]","");
		Set<String> wordSet = new HashSet<String>();
		try(Scanner scanner = new Scanner(content))
		{
			while(scanner.hasNext())
			{
				String word = scanner.next();
				if(word.length() > 2 && wordSet.size() <= MAX_TERMS)
				{
					wordSet.add(word);
				}
			}
		}
		contentData.put(id, wordSet);
	}
	
	private void buildModel() throws IOException
	{
		contentData = new HashMap<Long, Set<String>>();
		try(CSVReader reader = new CSVReader(new FileReader(path), '\t'))
		{
		String[] line;
			while ((line = reader.readNext()) != null) {
				if (line.length >= 2) {
					processLine(Long.parseLong(line[0]), line[1]);
				}
			}
		}
	}
	
	public Set<String> getContentByItemId(long id)
	{
		if(contentData.containsKey(id))
		{
			return contentData.get(id);
		}
		return null;
	}
}
