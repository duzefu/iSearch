package db.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ClassifierOperate {
	private Connection conn;
	public Connection connection()
	{
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		try{
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/classifier?useUnicode=true&characterEncoding=UTF-8", Set.getUsername(), Set.getPassword());
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return conn;
	}
	
	public void close()
	{
		try{
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
