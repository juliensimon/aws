package com.amazonaws.samples;

import java.sql.Connection;
import java.sql.SQLException;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
import com.amazonaws.services.elasticmapreduce.model.DescribeClusterRequest;
import com.amazonaws.services.elasticmapreduce.model.DescribeClusterResult;

public class HiveSample {

	private final static String clusterId = "CLUSTER_ID";
	private final static String driver    = "com.amazon.hive.jdbc41.HS2Driver";
	private final static String endpoint  = "jdbc:hive2://localhost:10000/default";

	private static Connection conn;

	public static void main(String[] args) {
		/*
		 * Connect
		 *
		 * No login/password for Hive, we use an SSH tunnel to the EMR cluster
		 * ssh -o ServerAliveInterval=10 -i ssh-key-path -N -L
		 * 10000:localhost:10000 hadoop@master-node-dns-name
		 */
		conn = Connect.connect(driver, endpoint);

		// Describe our EMR cluster
		AmazonElasticMapReduce emr = AmazonElasticMapReduceClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.build();
		DescribeClusterRequest req = new DescribeClusterRequest().withClusterId(clusterId);
		DescribeClusterResult res = emr.describeCluster(req);
		System.out.println(res);

		int count = Queries.findByLastnameAndState(conn, "JONES", "Florida");
		System.out.println(count + " rows");

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
