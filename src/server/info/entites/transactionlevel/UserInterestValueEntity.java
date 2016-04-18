package server.info.entites.transactionlevel;

import java.util.Date;

public class UserInterestValueEntity {
	private int id;
	private int uid;
	private int category_id;
	private double value;
	private Date date;
	private String category_name;

	public UserInterestValueEntity() {
		this.id = -1;
		this.category_id = -1;
	}

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

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	@Override
	public String toString() {
		return "UserInterestValueEntity [id=" + id + ", uid=" + uid
				+ ", category_name=" + category_name + ", value=" + value
				+ ", date=" + date + "]";
	}

}
