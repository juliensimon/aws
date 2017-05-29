package com.amazonaws.samples;

import java.util.List;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;

public class DynamoDBSampleMapper {

	private static void putMovie(DynamoDBMapper mapper, String series, String title, int releaseDate, String rating,
			String... characters) {
		System.out.println("*** Adding movie " + title);
		mapper.save(new DynamoDBMovie(title, series, releaseDate, rating, characters));
		System.out.println("*** Done");
	}
	
	private static void printMovie(DynamoDBMapper mapper, String title) {
		System.out.println("*** Printing movie " + title);
		DynamoDBMovie movie = mapper.load(DynamoDBMovie.class, title);
		System.out.println(movie);
		System.out.println("*** Done");
	}
	
	private static void printMoviesByRating(DynamoDBMapper mapper, String rating) {
		System.out.println("*** Printing movies with rating " + rating);
		DynamoDBMovie movie = new DynamoDBMovie();
		movie.setRating(rating);

		DynamoDBQueryExpression<DynamoDBMovie> queryExpression = new DynamoDBQueryExpression<DynamoDBMovie>()
				.withHashKeyValues(movie)
				.withConsistentRead(false); // No strong consistency when querying a GSI
		List<DynamoDBMovie> movieList = mapper.query(DynamoDBMovie.class, queryExpression);
		
		for (DynamoDBMovie m : movieList) {
			System.out.println(m);
		}
		System.out.println("*** Done");
	}

	public static void main(String[] args) {
		// Connection to local DynamoDB
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				.withEndpointConfiguration(new EndpointConfiguration("http://localhost:8000", "us-east-1")).build();

		DynamoDBMapper mapper = new DynamoDBMapper(client);

		putMovie(mapper, "Star Wars", "The Last Jedi", 2017, "*****", "Luke", "Rey", "BB8");
		printMovie(mapper, "The Last Jedi");
		printMoviesByRating(mapper, "*****");
	}
}