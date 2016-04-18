package common.textprocess.textclassifier;
//import java.sql.*;
public class PriorProbability {
	
	private static double it = 424270;
	private static double fi = 528090;
	private static double sp = 289823;
	private static double hl = 570620;
	private static double em = 576819;
	private static double mi = 619327;
	private static double ed = 754327;
	private static double li = 810254;
	private static double tr = 444944;
	
	public static double getprior(int index)
	{
		
		double all = (it+fi+sp+hl+em+mi+ed+li+tr);
		double p = 0;
		switch(index)
		{
		case 0:
			p = (it/all);break;
		case 1:
			p = (fi/all);break;
		case 2:
			p = (sp/all);break;
		case 3:
			p = (hl/all);break;
		case 4:
			p = (em/all);break;
		case 5:
			p = (mi/all);break;
		case 6:
			p = (ed/all);break;
		case 7:
			p = (li/all);break;
		case 8:
			p = (tr/all);break;
		}
		return p;
		
		/*
		int p = 0;
		Connection conn;
		Statement stmt;
		ResultSet rs;
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch(Exception e)
		{
		}
		
		try{
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/classify", "root", "root");
			//?useUnicode=true&characterEncoding=UTF-8 
			//conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8", "root", "root");
			
			stmt = conn.createStatement();
			String sql = "select * from "+databasename;
			rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				p = p+rs.getInt(2);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return p;
		*/
	}
	/*
	public static void main(String[] args)
	{
		String[] database = new String[]{"it","financial","sports","health","employment","military","education","literature","tourism"};
		PriorTest gp = new PriorTest();
		for(int i=0;i<database.length;i++)
		{
			int s = gp.GetPrior(database[i]);
		}		
	}*/

}
