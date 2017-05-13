package com.amazonaws.samples;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.DBInstance;

public class AuroraSample {

	private final static String driver = "org.mariadb.jdbc.Driver";
	private final static String endpoint = "jdbc:mysql://INSTANCE.REGION.rds.amazonaws.com/sales";

	private static Connection conn;

	public static void main(String[] args) {
		// Get Aurora credentials and connect
		String user = Connect.getCredential("backend-user");
		String password = Connect.getCredential("backend-password");
		conn = Connect.connect(driver, endpoint, user, password);

		// Describe RDS instances running in us-east-1
		AmazonRDS rds = AmazonRDSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		List<DBInstance> instancesList = rds.describeDBInstances().getDBInstances();
		for (DBInstance instance : instancesList) {
			System.out.println(
					"Instance id: " + instance.getDBInstanceIdentifier() + " *** " + instance.getDBInstanceClass());
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
