package common.functions.recommendation.click;

/**
 * 这个类用于点击推荐，从数据库获取点击历史中的查询词、点击次数，并计算相似度
 * @author zhou
 *
 */
public class QueryClickCountAndSim {

	
	private String m_strQuery;
	private double m_dCount;
	private double m_dSimilarity;
	
	public QueryClickCountAndSim(){
		
	}
	
	public QueryClickCountAndSim(String query){
		this(query,0,0);
	}
	
	public QueryClickCountAndSim(String query, double count, double similarity){
		m_strQuery=query;
		m_dCount=count;
		m_dSimilarity=similarity;
	}
	
	public void setQuery(String query){
		m_strQuery=query;
	}
	public String getQuery(){
		return m_strQuery;
	}
	
	public double getCount() {
		return m_dCount;
	}
	public void setCount(double count) {
		m_dCount = count;
	}
	public double getSimilarity() {
		return m_dSimilarity;
	}
	public void setSimilarity(double weight) {
		m_dSimilarity = weight;
	}
	
	
}
