package db.dao;

import java.util.Date;
import java.util.List;

public interface QueriesDao {
	
	public int add(String query, Date date);
	public int getQueryID(String query);
	public List<String> getSimiliarWords(String query);
}
