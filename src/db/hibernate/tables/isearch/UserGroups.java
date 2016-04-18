package db.hibernate.tables.isearch;

// Generated 2015-10-26 17:52:10 by Hibernate Tools 4.0.0

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * UserGroups generated by hbm2java
 */
@Entity
@Table(name = "user_groups", catalog = "isearch")
public class UserGroups implements java.io.Serializable {

	private Integer id;
	private User user;
	private GroupInfo groupInfo;

	public UserGroups() {
	}

	public UserGroups(User user, GroupInfo groupInfo) {
		this.user = user;
		this.groupInfo = groupInfo;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uid")
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gid")
	public GroupInfo getGroupInfo() {
		return this.groupInfo;
	}

	public void setGroupInfo(GroupInfo groupInfo) {
		this.groupInfo = groupInfo;
	}

}