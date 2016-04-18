package server.info.entites.transactionlevel;

import java.util.Map.Entry;

import server.info.config.CategoryInfo.Category;

public class UserInterestEntity {

	private Category m_enuCategory;
	private double m_dVal;

	public UserInterestEntity() {
	};

	public UserInterestEntity(Entry<Category, Double> entry) {
		setCategory(entry.getKey());
		setValue(entry.getValue());
	}

	public UserInterestEntity(Category c, Double value) {
		setCategory(c);
		setValue(value);
	}

	public Category getCategory() {
		return m_enuCategory;
	}

	public double getValue() {
		return m_dVal;
	}

	public void setCategory(Category cat) {
		m_enuCategory = cat;
	}

	public void setValue(double val) {
		m_dVal = val;
	}

	public void setValue(Double dval) {
		m_dVal = null == dval ? 0.0 : dval.doubleValue();
	}

	@Override
	public String toString() {
		return "UserInterestEntity [m_enuCategory=" + m_enuCategory
				+ ", m_dVal=" + m_dVal + "]";
	}
}
