package common.utils.querypreprocess;


public class QueryParticiple {
	/**
	 * 将查询词中的空格转换成+号
	 * @param query
	 * @return
	 */
	public static String participle(String query)
	{
		String[] allwords = query.split(" ");
	
		String out = allwords[0];
		for(int i=1;i<allwords.length;i++)
		{
			if(!allwords[i].equalsIgnoreCase(""))
			{
				out += "+"+allwords[i];
			}
		}
		return out;
	}
}
