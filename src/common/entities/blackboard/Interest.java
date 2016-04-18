package common.entities.blackboard;

import java.util.Date;

public class Interest {

	private String wordname;
	private String owerClassifi;
	private Double value;
	private Date date;
	private int classificationID;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getOwerClassifi() {
		return owerClassifi;
	}

	public void setOwerClassifi(String owerClassifi) {
		this.owerClassifi = owerClassifi;
	}

	public String getWordname() {
		return wordname;
	}

	public void setWordname(String wordname) {
		this.wordname = wordname;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public int getClassificationID() {
		return classificationID;
	}

	public void setClassificationID(int classificationID) {
		this.classificationID = classificationID;
	}

	@Override
	public String toString() {
		return "Interest [wordname=" + wordname + ", owerClassifi="
				+ owerClassifi + ", value=" + value + ", date=" + date
				+ ", classificationID=" + classificationID + "]";
	}

}
