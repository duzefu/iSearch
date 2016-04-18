package server.info.entites.transactionlevel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClickRecordEntity {

	private int id;
	private int uid;
	private String userName;
	Date datetime;
	private String query;
	private String title;
	private String abstr;
	private String url;
	private int rank;
	private int clickRank;
	private List<String> wordsDivision;
	private int categoryId;
	private double weight;
	
	public ClickRecordEntity(){
		this.uid=-1;
		this.rank=-1;
		this.clickRank=-1;
	}
	
	public List<String> getWordsDivision(){
		if (null==this.wordsDivision) {
			this.wordsDivision=new ArrayList<String>();
		}
		return this.wordsDivision;
	}
	
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getClickRank() {
		return clickRank;
	}
	public void setClickRank(int clickRank) {
		this.clickRank = clickRank;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAbstr() {
		return abstr;
	}

	public void setAbstr(String abstr) {
		this.abstr = abstr;
	}

	public void setWordsDivision(List<String> wordsDivision) {
		this.wordsDivision = wordsDivision;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
}
