package server.info.entites.transactionlevel;

import java.util.Date;

public class UserFavorWordsEntity {

	protected int id;
	protected int uid;
	protected String word;
	
	//作参数传入到Dao实现接口时，以下两个至少有一个；作为返回值时，根据情况返回；
	protected CategoryEntity category;
	
	protected Date date;
	protected double weight;
	
	public int getId() {
		return id;
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
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public CategoryEntity getCategory() {
		if(null==this.category) this.category=new CategoryEntity();
		return category;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
}
