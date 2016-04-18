package db.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseOperate {
	
	/**
	 * get database connection
	 * @return
	 */
	public static Connection getConnection()
	{
		Connection conn = null;
		try{
			Class.forName(Set.getDriver()).newInstance();
			conn = DriverManager.getConnection(Set.getUrl(), Set.getUsername(), Set.getPassword());
//			System.out.println("数据库连接成功！");
		}catch(Exception e){
//			System.out.println("数据库连接失败！");
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * close database connction
	 * @param conn
	 */
	public static void closeConn(Connection conn)
	{
		try{
			if(conn!=null)
			{
				conn.close();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * close database resultset
	 * @param rs
	 */
	public static void closeResult(ResultSet rs)
	{
		try{
			if(rs!=null)
			{
				rs.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * close database statement
	 * @param state
	 */
	public static void closeState(Statement state)
	{
		try{
			if(state!=null)
			{
				state.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}