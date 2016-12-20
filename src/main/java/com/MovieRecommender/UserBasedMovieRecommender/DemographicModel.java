package com.MovieRecommender.UserBasedMovieRecommender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

public class DemographicModel {
	
	private String path;
	private Map<Long, YearAndSex> modelData;
	
	public DemographicModel(String path) throws IOException
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
		modelData = new HashMap<Long, YearAndSex>();
		buildModel();
	}
	
	private void buildModel() throws IOException
	{
		try(CSVReader reader = new CSVReader(new FileReader(path), '\t'))
		{
		String[] line;
			while ((line = reader.readNext()) != null) {
				if (line.length >= 3) {
					if (!line[0].contains("undef")) {
						long id = Integer.parseInt(line[0]);
						int year = -1;
						String sex = "undef";
						if (!line[1].contains("undef")) {
							year = Integer.parseInt(line[1]);
						}
						if (!line[2].contains("undef")) {
							if (line[2].equalsIgnoreCase("m")
									|| line[2].equalsIgnoreCase("f")) {
								sex = line[2];
							} else {
								sex = "undef";
							}
						} else {
							sex = line[2];
						}
						modelData.put(id, new YearAndSex(year, sex));
					}
				}
			}
		}
	}
	
	public int getBirthYearByUserID(long id)
	{
		if(modelData.containsKey(id))
		{
			return modelData.get(id).getYear();
		}
		return -1;
	}
	
	public String getSexTypeByUserID(long id)
	{
		if(modelData.containsKey(id))
		{
			return modelData.get(id).getSex();
		}
		return "undef";
	}
	
	private class YearAndSex
	{
		private int year;
		private String sex;
		public YearAndSex(int year, String sex)
		{
			this.year = year;
			this.sex = sex;
		}
		
		public int getYear() {
			return year;
		}
		public void setYear(int year) {
			this.year = year;
		}
		public String getSex() {
			return sex;
		}
		public void setSex(String sex) {
			this.sex = sex;
		}
	}
	
}
