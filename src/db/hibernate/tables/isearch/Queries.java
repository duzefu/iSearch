package db.hibernate.tables.isearch;

// Generated 2015-10-26 17:52:10 by Hibernate Tools 4.0.0

import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Queries generated by hbm2java
 */
@Entity
@Table(name = "queries", catalog = "isearch")
public class Queries implements java.io.Serializable {

	private Integer id;
	private String query;
	private Date date;
	private Set<QfgFeatures> qfgFeaturesesForQuerySecond = new HashSet<QfgFeatures>(
			0);
	private Set<QfgFeatures> qfgFeaturesesForQueryFirst = new HashSet<QfgFeatures>(
			0);

	public Queries() {
	}

	public Queries(String query, Date date,
			Set<QfgFeatures> qfgFeaturesesForQuerySecond,
			Set<QfgFeatures> qfgFeaturesesForQueryFirst) {
		this.query = query;
		this.date = date;
		this.qfgFeaturesesForQuerySecond = qfgFeaturesesForQuerySecond;
		this.qfgFeaturesesForQueryFirst = qfgFeaturesesForQueryFirst;
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

	@Column(name = "query")
	public String getQuery() {
		return this.query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date", length = 0)
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "queriesByQuerySecond")
	public Set<QfgFeatures> getQfgFeaturesesForQuerySecond() {
		return this.qfgFeaturesesForQuerySecond;
	}

	public void setQfgFeaturesesForQuerySecond(
			Set<QfgFeatures> qfgFeaturesesForQuerySecond) {
		this.qfgFeaturesesForQuerySecond = qfgFeaturesesForQuerySecond;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "queriesByQueryFirst")
	public Set<QfgFeatures> getQfgFeaturesesForQueryFirst() {
		return this.qfgFeaturesesForQueryFirst;
	}

	public void setQfgFeaturesesForQueryFirst(
			Set<QfgFeatures> qfgFeaturesesForQueryFirst) {
		this.qfgFeaturesesForQueryFirst = qfgFeaturesesForQueryFirst;
	}

}