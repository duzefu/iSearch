package common.utils.querypreprocess;

public class DealingWithQuery {
	//对查询词中的特殊字符作统一处理
	public static String CorrectQuery(String query)
	{
		query = query.replace("'", "''");
		query = query.replace("[", "[[]");
		query = query.replace("%", "[%]");
		query = query.replace("_", "[_]");
		query = query.replace("^", "[^]");
		query = query.replace("!", "[!]");
		query = query.replace("~", "[~]");
		query = query.replace("@", "[@]");
		query = query.replace("#", "[#]");
		query = query.replace("$", "[$]");
		query = query.replace("%", "[^]");
		query = query.replace("&", "[&]");
		query = query.replace("*", "[*]");
		query = query.replace("(", "[(]");
		query = query.replace(")", "[)]");
		//something else should be added
		return query;
	}
}
