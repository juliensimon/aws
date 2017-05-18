package com.amazonaws.samples;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

public class DynamoDBSample {

	private final static String tableName = "my-favorite-movies-table";
	
	private static Item newItem(String name, int year, String rating, String... characters) {
		return new Item().withPrimaryKey("name", name)
				.withInt("year", year)
				.withString("rating", rating)
				.withStringSet("characters", characters);
	}

	public static void main(String[] args) throws Exception {

		/*
		 * Running Local DynamoDB:
		 * https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/
		 * DynamoDBLocal.html
		 * 
		 * $ java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar
		 * -sharedDb
		 * 
		 * AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard()
		 * .withEndpointConfiguration( new
		 * AwsClientBuilder.EndpointConfiguration("http://localhost:8000",
		 * "us-east-1")) .build();
		 */

		// No login/password for DynamoDB, we use AWS credentials
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
		DynamoDB dynamoDB = new DynamoDB(client);

		try {

			// Create a table with a primary hash key named 'name', which holds
			// a string
			CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
					.withKeySchema(new KeySchemaElement().withAttributeName("name").withKeyType(KeyType.HASH))
					.withAttributeDefinitions(new AttributeDefinition().withAttributeName("name")
							.withAttributeType(ScalarAttributeType.S))
					.withProvisionedThroughput(
							new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));

			// Create table if it does not exist yet
			TableUtils.createTableIfNotExists(client, createTableRequest);
			// wait for the table to move into ACTIVE state
			TableUtils.waitUntilActive(client, tableName);
			Table table = dynamoDB.getTable(tableName);

			// Describe our new table
			DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
			TableDescription tableDescription = client.describeTable(describeTableRequest).getTable();
			System.out.println("Table Description: " + tableDescription);

			// Add a few items
			Item item = newItem("Star Wars", 1977, "*****", "Luke", "Leia", "Obiwan", "C-3PO", "R2-D2", "Han", "Chewie");
			PutItemOutcome putResult = table.putItem(item);
			System.out.println("Result: " + putResult);
			
			item = newItem("Star Trek", 1979, "****", "Kirk", "Spock", "Scottie");
			putResult = table.putItem(item);
			System.out.println("Result: " + putResult);

			item = newItem("The Phantom Menace", 1999, "*", "Anakin", "Padme", "Jar Jar Binks");
			putResult = table.putItem(item);
			System.out.println("Result: " + putResult);
			
			item = newItem("The Lord of the Rings", 2001, "*****", "Frodo", "Gandalf", "Aragorn", "Legolas", "Gimli");
			putResult = table.putItem(item);
			System.out.println("Result: " + putResult);

			// Get an item
			item = table.getItem("name", "Star Wars");
			System.out.println("Item: " + item.toJSONPretty());

			// Find all 5-star movies
			Map<String, AttributeValue> expressionAttributeValues = 
				    new HashMap<String, AttributeValue>();
				expressionAttributeValues.put(":v_rating", new AttributeValue().withS("*****"));
				
	        ScanRequest scanRequest = new ScanRequest()
	        	    .withTableName(tableName)
	        	    .withFilterExpression("rating = :v_rating")
	        	    .withExpressionAttributeValues(expressionAttributeValues);
	        
			ScanResult items = client.scan(scanRequest);
			System.out.println("Found "+items.getCount()+ " 5-star movies");
			for (Map<String, AttributeValue> i : items.getItems()) {
			    System.out.println(i);
			}
			
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
}
