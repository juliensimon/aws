package com.amazonaws.samples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersRequest;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;

public class Connect {

	static Connection connect(String driver, String endpoint) {
		Connection conn = null;
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			conn = DriverManager.getConnection(endpoint);

		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return conn;
	}

	static Connection connect(String driver, String endpoint, String user, String password) {
		Connection conn = null;
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			conn = DriverManager.getConnection(endpoint, user, password);

		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return conn;
	}

	static String getCredential(String credentialName) {
		// Retrieve credential from EC2 Systems Manager Parameter Store
		AWSSimpleSystemsManagement client = AWSSimpleSystemsManagementClientBuilder.defaultClient();
		GetParametersRequest request = new GetParametersRequest().withNames(credentialName);
		request.setWithDecryption(true);

		List<Parameter> paramList = client.getParameters(request).getParameters();
		return paramList.get(0).getValue();
	}

}
