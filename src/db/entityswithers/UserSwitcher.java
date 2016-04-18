package db.entityswithers;

import server.commonutils.Md5;
import server.info.entites.transactionlevel.UserEntity;
import db.hibernate.tables.isearch.User;

public class UserSwitcher {

	public static User userEntityToPojo(UserEntity entity) {

		if (null == entity) return null;

		User ret = new User();

		int uid = entity.getUid();
		if (uid > 0) ret.setUserid(uid);
		ret.setEmailadress(entity.getEmail());
		ret.setPassword(Md5.encrypt(entity.getPassword()));
		ret.setUsername(entity.getUsername());
		return ret;
	}

	public static UserEntity userPojoToEntity(User user) {

		if (null == user)
			return null;
		UserEntity ret = new UserEntity();
		ret.setCookie(user.getCookieid());
		ret.setEmail(user.getEmailadress());
		ret.setImei(user.getImei());
		ret.setPassword(user.getPassword());
		ret.setUid(user.getUserid());
		ret.setUsername(user.getUsername());
		return ret;
	}

}
