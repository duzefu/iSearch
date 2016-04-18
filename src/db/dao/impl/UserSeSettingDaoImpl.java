/*
 * Copyright (c) 2012, Intelligent retrieval project team, ASE lab, Xidian university
 * All rights reserved.
 *
 * Filename: UserDaoImpl.java
 * Summary: It's the relization of interface for operations to database table User_Se_Setting.
 *
 * Current Version: 1.0
 * Author: Bryan Zou
 * Completion Date: 2012.9.1
 *
 * Superseded versions: 0.9
 * Original Author: Bryan Zou
 * Completion Date: 2012.7.30
 */

package db.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import db.dao.UserSeSettingDao;
import db.hibernate.tables.isearch.UserSeSetting;

public class UserSeSettingDaoImpl implements UserSeSettingDao {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * add a user'sesetting to databse
	 */
	@Override
	public void add(UserSeSetting usersesetting) {
		if (usersesetting == null) {
			return;
		}
		this.sessionFactory.getCurrentSession().save(usersesetting);
	}

	/**
	 * delete a user'sesetting from databse
	 */
	@Override
	public void delete(UserSeSetting usersesetting) {
		if (usersesetting == null) {
			return;
		}
		this.sessionFactory.getCurrentSession().delete(usersesetting);
	}

	/**
	 * update a particular user'sesetting from databse
	 */
	@Override
	public void update(UserSeSetting usersesetting) {
		if (usersesetting == null) {
			return;
		}
		this.sessionFactory.getCurrentSession().update(usersesetting);
	}

	/**
	 * find a particular user'sesetting list by username
	 * 
	 * @param username
	 * @return List<UserSeSetting>
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<UserSeSetting> findUserSeSettingByUsername(String username) {
		// 修改了hql语句中的：将UserSetting改为UserSeSetting（by许静）
		return (List<UserSeSetting>) this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from UserSeSetting userSeSetting where userSeSetting.username is'"
								+ username + "'").list();
	}

}
