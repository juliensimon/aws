package com.amazonaws.samples;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.athena.AmazonAthena;
import com.amazonaws.services.athena.AmazonAthenaClientBuilder;
import com.amazonaws.services.athena.model.GetQueryExecutionRequest;
import com.amazonaws.services.athena.model.GetQueryExecutionResult;
import com.amazonaws.services.athena.model.ListQueryExecutionsRequest;
import com.amazonaws.services.athena.model.ListQueryExecutionsResult;

public class AthenaSample {

	private final static String driver = "com.amazonaws.athena.jdbc.AthenaDriver";
	// We're using locally stored credentials to connect to Athena
	private final static String endpoint = "jdbc:awsathena://athena.REGION.amazonaws.com:443?"
			+ "aws_credentials_provider_class=com.amazonaws.auth.PropertiesFileCredentialsProvider&"
			+ "aws_credentials_provider_arguments=PATH_TO_CREDENTIALS&"
			+ "s3_staging_dir=s3://BUCKET_NAME";

	public static void main(String[] args) {
		AmazonAthena client = AmazonAthenaClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		ListQueryExecutionsResult listQueryRes = listPastQueries(client, 5);
		printPastQueries(client, listQueryRes);
		client.shutdown();

		// Connect: no login/password for Athena, we use AWS credentials
		Connection conn = Connect.connect(driver, endpoint);
		// Extra quotes needed
		int count = findByLastnameAndState(conn, "'JONES'", "'Florida'");
		System.out.println(count + " rows");

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static int findByLastnameAndState(Connection conn, String lastname, String state) {
		int count = 0;

		// PreparedStatement is unsupported for now
		// https://github.com/prestodb/presto/issues/1195
		String sql = "SELECT lastname,firstname,age FROM athenatest.salesparquet"
				+ " WHERE lastname=" + lastname
				+ " AND state=" + state;

		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				count++;
				String last  = rs.getString("lastname");
				String first = rs.getString("firstname");
				String age   = rs.getString("age");
				System.out.println(String.join(" ", last, first, age));
			}
			st.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return count;
	}

	private static ListQueryExecutionsResult listPastQueries(AmazonAthena client, int count) {
		ListQueryExecutionsRequest listQueryReq = new ListQueryExecutionsRequest().withMaxResults(count);
		ListQueryExecutionsResult listQueryRes  = client.listQueryExecutions(listQueryReq);
		return listQueryRes;
	}

	private static void printPastQueries(AmazonAthena client, ListQueryExecutionsResult listQueryRes) {
		for (String queryId : listQueryRes.getQueryExecutionIds()) {
			GetQueryExecutionRequest getReq = new GetQueryExecutionRequest().withQueryExecutionId(queryId);
			GetQueryExecutionResult getRes  = client.getQueryExecution(getReq);
			System.out.println(getRes);
		}
	}
}
