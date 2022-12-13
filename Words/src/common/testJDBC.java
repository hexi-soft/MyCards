package common;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class testJDBC {

	private static final String DB_DRIVER = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
	private static final String DB_CONNECTION = "jdbc:microsoft:sqlserver://localhost:1433; DatabaseName=TXTKBASE";
	private static final String DB_USER = "sa";
	private static final String DB_PASSWORD = "hujia521";

	public static void main(String[] argv) {

		try {

			selectRecordsFromTable();

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		}

	}

	private static void selectRecordsFromTable() throws SQLException {

		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT * from words where word = ?";

		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			preparedStatement.setString(1, "a");

			// execute select SQL stetement
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {

				String word = rs.getString("word");
				String chinese = rs.getString("chinese");

				System.out.println("word : " + word);
				System.out.println("chinese : " + chinese);

			}

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		} finally {

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}

	}

	private static Connection getDBConnection() {

		Connection dbConnection = null;

		try {

			Class.forName(DB_DRIVER);

		} catch (ClassNotFoundException e) {

			System.out.println(e.getMessage());

		}

		try {

			dbConnection = DriverManager.getConnection(
                             DB_CONNECTION, DB_USER,DB_PASSWORD);
			return dbConnection;

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		}

		return dbConnection;

	}

}
