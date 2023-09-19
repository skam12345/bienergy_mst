package bienergy_mst.bienergy_mst.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlConnection {
	private Connection conn;
	
	public MysqlConnection() {
		
	}
	
	public Connection OpenConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			String url = "jdbc:mysql://129.154.213.191:3306/BIE_ECHARGING";
			conn = DriverManager.getConnection(url, "autogram", "autogram0918");
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return conn;
	}
}
