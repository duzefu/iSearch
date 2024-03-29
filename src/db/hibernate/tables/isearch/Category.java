package db.hibernate.tables.isearch;

// Generated 2015-10-26 17:52:10 by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Category generated by hbm2java
 */
@Entity
@Table(name = "category", catalog = "isearch")
public class Category implements java.io.Serializable {

	private Integer id;
	private String categoryName;
	private Set<UserFavorWords> userFavorWordses = new HashSet<UserFavorWords>(
			0);
	private Set<UserInterestValue> userInterestValues = new HashSet<UserInterestValue>(
			0);
	private Set<GroupToCategory> groupToCategories = new HashSet<GroupToCategory>(
			0);
	private Set<ClickLog> clickLogs = new HashSet<ClickLog>(0);

	public Category() {
	}

	public Category(String categoryName, Set<UserFavorWords> userFavorWordses,
			Set<UserInterestValue> userInterestValues,
			Set<GroupToCategory> groupToCategories, Set<ClickLog> clickLogs) {
		this.categoryName = categoryName;
		this.userFavorWordses = userFavorWordses;
		this.userInterestValues = userInterestValues;
		this.groupToCategories = groupToCategories;
		this.clickLogs = clickLogs;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "category_name", length = 45)
	public String getCategoryName() {
		return this.categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
	public Set<UserFavorWords> getUserFavorWordses() {
		return this.userFavorWordses;
	}

	public void setUserFavorWordses(Set<UserFavorWords> userFavorWordses) {
		this.userFavorWordses = userFavorWordses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
	public Set<UserInterestValue> getUserInterestValues() {
		return this.userInterestValues;
	}

	public void setUserInterestValues(Set<UserInterestValue> userInterestValues) {
		this.userInterestValues = userInterestValues;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
	public Set<GroupToCategory> getGroupToCategories() {
		return this.groupToCategories;
	}

	public void setGroupToCategories(Set<GroupToCategory> groupToCategories) {
		this.groupToCategories = groupToCategories;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
	public Set<ClickLog> getClickLogs() {
		return this.clickLogs;
	}

	public void setClickLogs(Set<ClickLog> clickLogs) {
		this.clickLogs = clickLogs;
	}

}
