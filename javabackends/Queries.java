package com.amazonaws.samples;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Queries {

	static int findByLastnameAndState(Connection conn, String lastname, String state) {
		int count = 0;
		String sql = "SELECT lastname,firstname,age FROM sales WHERE lastname=? AND state=?";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, lastname);
			preparedStatement.setString(2, state);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				count++;
				String last = rs.getString("lastname");
				String first = rs.getString("firstname");
				String age = rs.getString("age");
				System.out.println(String.join(" ", last, first, age));
			}
			preparedStatement.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return count;
	}
}
