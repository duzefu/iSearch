package server.info.entities.communication;

import java.io.Serializable;

public class RecommQueryAndPercent implements Serializable,Comparable<RecommQueryAndPercent>{
	
	private static final long serialVersionUID = 6862514853927463724L;
	private String query;
	private double percent;
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	@Override
	public int compareTo(RecommQueryAndPercent o) {
		
		if(this.percent<o.percent) return -1;
		else if(this.percent>o.percent) return 1;
		return 0;
	}
	
	
	
}
