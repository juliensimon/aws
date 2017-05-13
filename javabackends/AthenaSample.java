package com.amazonaws.samples;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AthenaSample {

	private final static String driver = "com.amazonaws.athena.jdbc.AthenaDriver";
	// We're using locally stored credentials to connect to Athena
	private final static String endpoint = "jdbc:awsathena://CLUSTER.REGION.amazonaws.com:443?"
			+ "aws_credentials_provider_class=com.amazonaws.auth.PropertiesFileCredentialsProvider&"
			+ "aws_credentials_provider_arguments=PATH_TO_CREDENTIALS&"
			+ "s3_staging_dir=s3://BUCKET_NAME";

	private static Connection conn;

	private static int findByLastnameAndState(String lastname, String state) {
		int count = 0;

		// PreparedStatement is unsupported for now
		// https://github.com/prestodb/presto/issues/1195
		String sql = "SELECT lastname,firstname,age FROM athenatest.salesparquet" + " WHERE lastname=" + lastname
				+ " AND state=" + state;
		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				count++;
				String last = rs.getString("lastname");
				String first = rs.getString("firstname");
				String age = rs.getString("age");
				System.out.println(String.join(" ", last, first, age));
			}
			st.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return count;
	}

	public static void main(String[] args) {
		// Connect: no login/password for Athena, we use AWS credentials
		conn = Connect.connect(driver, endpoint);

		// Extra quotes needed
		int count = findByLastnameAndState("'JONES'", "'Florida'");
		System.out.println(count + " rows");

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
