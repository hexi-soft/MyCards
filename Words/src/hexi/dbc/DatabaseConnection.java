package hexi.dbc;

import java.sql.Connection;

public interface DatabaseConnection {
	public Connection getConnection();
	public void close();
}
