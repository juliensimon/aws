package com.amazonaws.samples;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.redshift.AmazonRedshift;
import com.amazonaws.services.redshift.AmazonRedshiftClientBuilder;
import com.amazonaws.services.redshift.model.Cluster;

public class RedshiftSample {

	private final static String driver = "com.amazon.redshift.jdbc.Driver";
	private final static String endpoint = "jdbc:redshift://CLUSTER.REGION.redshift.amazonaws.com:5439/sales";

	private static Connection conn;

	public static void main(String[] args) {
		// Get Redshift credentials and connect
		String user = Connect.getCredential("backend-user");
		String password = Connect.getCredential("backend-password");
		conn = Connect.connect(driver, endpoint, user, password);

		// Describe Redshift clusters running in us-east-1
		AmazonRedshift redshift = AmazonRedshiftClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		List<Cluster> instancesList = redshift.describeClusters().getClusters();
		for (Cluster cluster : instancesList) {
			System.out.println("Cluster id: " + cluster.getClusterIdentifier() + " *** " + cluster.getNumberOfNodes()
					+ " nodes, " + cluster.getNodeType());
		}

		int count = Queries.findByLastnameAndState(conn, "JONES", "Florida");
		System.out.println(count + " rows");

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
