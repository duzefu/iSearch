package common.textprocess.textclassifier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import common.textprocess.textsegmentation.*;
import db.jdbc.ClassifierOperate;

public class ClassConditionalProbability {
	private static String[] classes = new String[]{"it","financial","sports","health","employment","military","education","literature","tourism"};
	private static double[] counts = new double[]{12845,10150,9405,15745,15940,14890,19530,28080,16660};
	
	public static double getClassConditionalProbability(WordList wl,int index)
	{
		
		ClassifierOperate co = new ClassifierOperate();
		Statement stmt = null;
		ResultSet rs = null;
		double c = 1.0;
		try{
			stmt = co.connection().createStatement();
			double[] x = new double[wl.totalwords()];
			double y = 0;
			for(int i=0;i<wl.totalwords();i++)
			{
				String sql = "select * from "+classes[index]+" where word = '"+wl.getWord(i).getword()+"'";
				rs = stmt.executeQuery(sql);
				if(rs.next())
				{
					x[i] = 1.0+rs.getFloat(2);
					y = y+rs.getFloat(2);
				}
				else
				{
					x[i] = 1.0;
				}
			}
			for(int i=0;i<wl.totalwords();i++)
			{
				c= c*(1000*x[i]*wl.getWord(i).getweight()/(counts[index]+y));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		try {
			rs.close();
			stmt.close();
			co.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
}
