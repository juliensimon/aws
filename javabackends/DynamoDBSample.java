package com.amazonaws.samples;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.amazonaws.services.dynamodbv2.util.TableUtils.TableNeverTransitionedToStateException;

public class DynamoDBSample {

	private final static String ratingIndexName = "ratingIndex";
	private final static String tableName = "moviesTable";

	public static void main(String[] args) throws Exception {

		/*
		 * Running Local DynamoDB:
		 * https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/
		 * DynamoDBLocal.html
		 * 
		 * $ java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar
		 * -sharedDb
		 */

		try {
			// Connection to local DynamoDB
			AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
					.withEndpointConfiguration(new EndpointConfiguration("http://localhost:8000", "us-east-1")).build();

			// Connection to remote DynamoDB
			// AmazonDynamoDB client =
			// AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

			// Needed for batch get item
			DynamoDB dynamoDB = new DynamoDB(client);

			Table table = createMovieTable(client);
			printTableDescription(client, table);
			// putItem API
			addMoviesToTable(table);
			// getItem API, table hash key
			printMovie(table, "The Return of the Jedi");
			// batchGetItem API, table hash key
			printMoviesbyTitles(dynamoDB, table, "A New Hope", "The Return of the Jedi");
			// updateItem API
			addCharacterToMovie(table, "Jabba", "The Return of the Jedi");
			printMovie(table, "The Return of the Jedi");
			// query API, GSI with index hash key
			printMoviesByRating(table, "*");
			// query API, GSI with index hash & range keys
			printMoviesByRatingAndDateRange(table, "*****", 1975, 1982);
			// scan API, String attribute
			printMoviesBySeries(table, "Star Wars");
			// scan API, String Set attribute
			printMoviesWithCharacter(table, "Yoda");
			// deleteItem API
			deleteBadMovies(table);
			printMoviesByRating(table, "*");
		} catch (AmazonServiceException ase) {
			System.out.println("Amazon Message:   " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Client Message: " + ace.getMessage());
		}
	}

	private static void addCharacterToMovie(Table table, String character, String title) {
		System.out.println("*** Adding character " + character + " to movie " + title);
		Item movie = table.getItem("title", title);
		try {
			Set<String> movieCharacters = movie.getStringSet("characters");
			movieCharacters.add(character);
			UpdateItemSpec updateSpec = new UpdateItemSpec().withUpdateExpression("SET #characters = :characters")
					.withNameMap(new NameMap().with("#characters", "characters"))
					.withValueMap(new ValueMap().with(":characters", movieCharacters))
					.withPrimaryKey("title", movie.getString("title"));
			UpdateItemOutcome result = table.updateItem(updateSpec);
			System.out.println(result);
		} catch (NullPointerException e) {
			System.out.println("ERROR: movie " + title + " not found");
		} finally {
			System.out.println("*** Done");
		}
	}

	private static void addMoviesToTable(Table table) {
		System.out.println("*** Adding movies");
		putMovie(table, "Star Wars", "A New Hope", 1977, "*****", "Luke", "Leia", "Obiwan", "C-3PO", "R2-D2", "Han",
				"Chewie");
		putMovie(table, "Star Wars", "The Empire Strikes Back", 1981, "*****", "Luke", "Leia", "Darth Vader", "C-3PO",
				"R2-D2", "Han", "Chewie");
		putMovie(table, "Star Wars", "The Return of the Jedi", 1983, "*****", "Yoda", "Darth Vader", "Luke", "Leia",
				"C-3PO", "R2-D2", "Han", "Chewie");
		putMovie(table, "Star Wars", "The Phantom Menace", 1999, "*", "Anakin", "Padme", "Yoda", "Jar Jar Binks");
		System.out.println("*** Done");
	}

	private static Table createMovieTable(AmazonDynamoDB client) {
		System.out.println("*** Creating table");
		// Define attributes used as table & index keys
		ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
		attributeDefinitions.add(new AttributeDefinition("title", ScalarAttributeType.S));
		attributeDefinitions.add(new AttributeDefinition("rating", ScalarAttributeType.S));
		attributeDefinitions.add(new AttributeDefinition("releaseDate", ScalarAttributeType.N));
		// Define secondary index
		GlobalSecondaryIndex ratingIndex = new GlobalSecondaryIndex().withIndexName(ratingIndexName)
				.withKeySchema(new KeySchemaElement("rating", KeyType.HASH),
						new KeySchemaElement("releaseDate", KeyType.RANGE))
				.withProjection(new Projection().withProjectionType(ProjectionType.ALL))
				.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
		// Create table and wait for it to be active
		CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
				.withKeySchema(new KeySchemaElement("title", KeyType.HASH))
				.withAttributeDefinitions(attributeDefinitions).withGlobalSecondaryIndexes(ratingIndex)
				.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
		TableUtils.createTableIfNotExists(client, createTableRequest);

		try {
			TableUtils.waitUntilActive(client, tableName);
		} catch (TableNeverTransitionedToStateException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("*** Done");
		return new DynamoDB(client).getTable(tableName);
	}

	private static void deleteBadMovies(Table table) {
		System.out.println("*** Deleting bad movies");
		ItemCollection<QueryOutcome> movies = findMoviesByRating(table, "*");
		Iterator<Item> iter = movies.iterator();
		while (iter.hasNext()) {
			Item i = iter.next();
			DeleteItemOutcome result = table.deleteItem("title", i.getString("title"));
			System.out.println(result);
		}
		System.out.println("*** Done");
	}

	private static ItemCollection<QueryOutcome> findMoviesByRating(Table table, String rating) {
		Index titleIndex = table.getIndex(ratingIndexName);
		QuerySpec spec = new QuerySpec().withKeyConditionExpression("rating = :v_rating")
				.withValueMap(new ValueMap().withString(":v_rating", rating));
		ItemCollection<QueryOutcome> movies = titleIndex.query(spec);
		return movies;
	}

	private static void prettyPrintItems(ItemCollection<?> items) {
		Iterator<Item> iter = items.iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next().toJSONPretty());
		}
	}

	private static void prettyPrintItems(List<Item> items) {
		for (Item i : items) {
			System.out.println(i.toJSONPretty());
		}
	}

	private static void printMovie(Table table, String title) {
		System.out.println("*** Printing movie " + title);
		Item movie = table.getItem("title", title);
		try {
			System.out.println("Item: " + movie.toJSONPretty());
		} catch (NullPointerException e) {
			System.out.println("ERROR: movie " + title + " not found");
		}
		System.out.println("*** Done");
	}

	private static void printMoviesbyTitles(DynamoDB client, Table table, String... titles) {
		System.out.println("*** Printing movies");
		/*
		 * batchGetItem() can get items from different tables in a single
		 * operation. For each table, we need to provide a
		 * TableKeysAndAttributes object holding the key(s) defined on the
		 * table, as well as the values we're looking for.
		 */
		TableKeysAndAttributes movieTitles = new TableKeysAndAttributes(table.getTableName()).withHashOnlyKeys("title",
				(Object[]) titles);
		BatchGetItemOutcome res = client.batchGetItem(movieTitles);
		/*
		 * The return value of batchGetItem() holds for each table a list of
		 * items corresponding to the key values we provided. We can also find
		 * out about key values that were not found.
		 */
		prettyPrintItems(res.getTableItems().get(table.getTableName()));
		if (res.getUnprocessedKeys().size() != 0) {
			System.out.println("At least one key was not found");
		}
		System.out.println("*** Done");
	}

	private static void printMoviesByRating(Table table, String rating) {
		System.out.println("*** Printing movies with rating " + rating);
		ItemCollection<QueryOutcome> movies = findMoviesByRating(table, rating);
		prettyPrintItems(movies);
		System.out.println("*** Done");
	}

	private static void printMoviesByRatingAndDateRange(Table table, String rating, int startYear, int endYear) {
		System.out.println("*** Printing movies between " + startYear + " and " + endYear + " with rating " + rating);
		Index titleIndex = table.getIndex(ratingIndexName);
		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("rating = :v_rating and releaseDate between :v_start and :v_end")
				.withValueMap(new ValueMap().withString(":v_rating", rating).withInt(":v_start", startYear)
						.withInt(":v_end", endYear));
		ItemCollection<QueryOutcome> movies = titleIndex.query(spec);
		prettyPrintItems(movies);
		System.out.println("*** Done");
	}

	private static void printMoviesBySeries(Table table, String series) {
		System.out.println("*** Printing movies from series " + series);
		// 'series' is neither a key nor an index: we need to scan the table
		ScanSpec scanSpec = new ScanSpec().withFilterExpression("series = :v_series")
				.withValueMap(new ValueMap().withString(":v_series", series));
		ItemCollection<ScanOutcome> movies = table.scan(scanSpec);
		prettyPrintItems(movies);
		System.out.println("*** Done");
	}

	private static void printMoviesWithCharacter(Table table, String character) {
		System.out.println("*** Printing movies with character " + character);
		// 'characters' is neither a key nor an index: we need to scan the table
		ScanSpec scanSpec = new ScanSpec().withFilterExpression("contains(characters, :v_character)")
				.withValueMap(new ValueMap().withString(":v_character", character));
		ItemCollection<ScanOutcome> movies = table.scan(scanSpec);
		prettyPrintItems(movies);
		System.out.println("*** Done");
	}

	private static void printTableDescription(AmazonDynamoDB client, Table table) {
		System.out.println("*** Describing table");
		DescribeTableRequest describeTableRequest = new DescribeTableRequest(tableName);
		TableDescription tableDescription = client.describeTable(describeTableRequest).getTable();
		System.out.println("Table Description: " + tableDescription);
		System.out.println("*** Done");
	}

	private static void putMovie(Table table, String series, String title, int releaseDate, String rating,
			String... characters) {
		Item movie = new Item().withPrimaryKey("title", title).withString("series", series)
				.withInt("releaseDate", releaseDate).withString("rating", rating)
				.withStringSet("characters", characters);
		System.out.println("Added movie " + title + " " + table.putItem(movie));
	}
}
