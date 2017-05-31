package com.amazonaws.samples;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "moviesTable")
public class DynamoDBMovie {

	static void printMovieList(List<DynamoDBMovie> movieList) {
		for (DynamoDBMovie m : movieList) {
			System.out.println(m);
		}
	}
	
	private Set<String> characters;
	@DynamoDBIndexHashKey(globalSecondaryIndexName="ratingIndex")
	private String rating;
	@DynamoDBIndexRangeKey(globalSecondaryIndexName="ratingIndex")
	private int releaseDate;
	private String series;

	@DynamoDBHashKey()
	private String title;

	public DynamoDBMovie() {
	}

	public DynamoDBMovie(String title, String series, int releaseDate, String rating, String... characters) {
		this.title = title;
		this.series = series;
		this.releaseDate = releaseDate;
		this.rating = rating;		
		this.characters = new HashSet<String>();
		for (String character : characters) {
			this.characters.add(character);
		}
	}

	public Set<String> getCharacters() {
		return characters;
	}
	
	public String getRating() {
		return rating;
	}

	public int getReleaseDate() {
		return releaseDate;
	}

	public String getSeries() {
		return series;
	}

	public String getTitle() {
		return title;
	}

	public void setCharacters(Set<String> characters) {
		this.characters = characters;
	}
	
	public void setRating(String rating) {
		this.rating = rating;
	}

	public void setReleaseDate(int releaseDate) {
		this.releaseDate = releaseDate;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "DynamoDBMovie [title=" + title + ", series=" + series + ", releaseDate=" + releaseDate + ", rating="
				+ rating + ", characters=" + characters + "]";
	}

	public DynamoDBMovie withRating(String rating) {
		this.rating = rating;
		return this;
	}

}