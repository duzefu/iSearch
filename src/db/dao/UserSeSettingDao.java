/*
* Copyright (c) 2012, Intelligent retrieval project team, ASE lab, Xidian university
* All rights reserved.
*
* Filename: UserSeSettingDao.java
* Summary: It's the interface for operations to database table User_Se_Setting.
*
* Current Version: 1.0
* Author: Bryan Zou
* Completion Date: 2012.9.1
*
* Superseded versions: 0.9
* Original Author: Bryan Zou
* Completion Date: 2012.7.30
*/

package db.dao;

import java.util.List;

import db.hibernate.tables.isearch.UserSeSetting;

public interface UserSeSettingDao {
	
	public void add(UserSeSetting usersesetting);
	
	public void delete(UserSeSetting usersesetting);
	
	public void update(UserSeSetting usersesetting);
	
	public List<UserSeSetting> findUserSeSettingByUsername(String username);
	
}
