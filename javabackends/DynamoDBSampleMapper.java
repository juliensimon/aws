package com.amazonaws.samples;

import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class DynamoDBSampleMapper {
	
	public static void main(String[] args) {
		// Connection to local DynamoDB
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				.withEndpointConfiguration(new EndpointConfiguration("http://localhost:8000", "us-east-1")).build();

		// Connection to remote DynamoDB
		// AmazonDynamoDB client =
		// AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

		DynamoDBMapper mapper = new DynamoDBMapper(client);

		// generateCreateTableRequest API
		printCreateTable(mapper);
		// save API
		putMovie(mapper, "Star Wars", "The Last Jedi", 2017, "*****", "Luke", "Rey", "BB8");
		// load API
		printMovie(mapper, "The Last Jedi");
		// query API
		printMoviesByRating(mapper, "*****");
		// scan API
		printMoviesBySeries(mapper, "Star Wars");
	}

	private static void printCreateTable(DynamoDBMapper mapper) {
		// This API builds a CreateTableRequest object based on our POJO.
		// You can then use it to create the table with TableUtils.createTableIfNotExists().
		System.out.println("*** Printing CreateTableRequest");
		CreateTableRequest req = mapper.generateCreateTableRequest(DynamoDBMovie.class);
		System.out.println(req);
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
		List<DynamoDBMovie> movieList = mapper.query(DynamoDBMovie.class, queryWithRatingEqualTo(rating));
		DynamoDBMovie.printMovieList(movieList);
	}

	private static void printMoviesBySeries(DynamoDBMapper mapper, String series) {
		System.out.println("*** Printing movies from series " + series);
		List<DynamoDBMovie> movieList = mapper.scan(DynamoDBMovie.class, scanWithSeriesEqualTo(series));
		DynamoDBMovie.printMovieList(movieList);
		System.out.println("*** Done");
	}

	private static void putMovie(DynamoDBMapper mapper, String series, String title, int releaseDate, String rating,
			String... characters) {
		System.out.println("*** Adding movie " + title);
		mapper.save(new DynamoDBMovie(title, series, releaseDate, rating, characters));
		System.out.println("*** Done");
	}

	private static DynamoDBQueryExpression<DynamoDBMovie> queryWithRatingEqualTo(String rating) {
		DynamoDBQueryExpression<DynamoDBMovie> queryExpression = new DynamoDBQueryExpression<DynamoDBMovie>()
				.withHashKeyValues(new DynamoDBMovie().withRating(rating))
				.withConsistentRead(false);
				// No strong consistency when querying a GSI
		return queryExpression;
	}

	private static DynamoDBScanExpression scanWithSeriesEqualTo(String series) {
		HashMap<String, AttributeValue> values = new HashMap<String, AttributeValue>();
		values.put(":v_series", new AttributeValue(series));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
				.withFilterExpression("series = :v_series")
				.withExpressionAttributeValues(values);
		return scanExpression;
	}
}