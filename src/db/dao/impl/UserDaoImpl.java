package db.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import server.commonutils.LogU;
import server.commonutils.Md5;
import server.commonutils.MyStringChecker;
import server.info.config.MyEnums.UserLoginResult;
import server.info.entites.transactionlevel.UserEntity;
import db.dao.UserDao;
import db.entityswithers.UserSwitcher;
import db.hibernate.tables.isearch.User;

public class UserDaoImpl implements UserDao {

	private SessionFactory sessionFactory;
	private static String userNamePre = "inu";
	private static int userNameCount = -1;
	private static final String DEFAULT_USER_PASSWORD = "000000";

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	private int getUserNameCount() {
		if (UserDaoImpl.userNameCount <= 0) {
			userNameCount = 0;
			synchronized (UserDaoImpl.class) {
				List<User> ulist = this
						.findUserByNamePrefix(UserDaoImpl.userNamePre);
				if (null != ulist)
					this.userNameCount = ulist.size();
			}
		}
		return ++userNameCount;
	}

	@Override
	public int add(UserEntity user) {

		if (user == null)
			return -1;

		User tarUser = UserSwitcher.userEntityToPojo(user);
		return this.add(tarUser);
		
	}

	@Override
	public void delete(int uid) {
		if (uid <= 0)
			return;

		this.sessionFactory.getCurrentSession()
				.createQuery("delete from User u where u.userid = :uid")
				.setParameter("uid", uid).executeUpdate();
		return;
	}

	@Override
	public int update(UserEntity entity) {

		int uid = null != entity ? entity.getUid() : -1;
		if (uid <= 0)
			return -1;

		User tarUser = this.getUserPojoByID(uid);
		if (null != tarUser)
			this.changeUser(tarUser, entity);
		this.sessionFactory.getCurrentSession().update(tarUser);
		return tarUser.getUserid();

	}

	@Override
	@SuppressWarnings("unchecked")
	public List<UserEntity> findUserByUsername(String username) {

		if (null == username || username.isEmpty())
			return null;
		List<User> ulist = this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from User user where user.username is '" + username
								+ "' order by user.password desc").list();
		List<UserEntity> ret = null;
		if (null == ulist)
			ret = new ArrayList<UserEntity>();
		else
			ret = this.changeListPojoToEntity(ulist);

		return ret;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<UserEntity> findUserByCookieid(String cookieid) {

		if (null == cookieid || cookieid.isEmpty())
			return null;

		List<User> ulist = (List<User>) this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from User user where user.cookieid is '" + cookieid
								+ "' order by user.password desc").list();
		List<UserEntity> ret = null;
		if (null == ulist)
			ret = new ArrayList<UserEntity>();
		else
			ret = this.changeListPojoToEntity(ulist);

		return ret;
	}

	/**
	 * find a particular user list by IMEI
	 * 
	 * @param imei
	 * @return List<User>
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<UserEntity> findUserByImei(String imei) {

		if (null == imei||imei.isEmpty())
			return null;

		List<User> ulist = (List<User>) this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from User user where user.imei is '" + imei
								+ "'order by user.password desc").list();
		List<UserEntity> ret = null;
		if(null==ulist) ulist=new ArrayList<User>();
		if (ulist.isEmpty())
		{
			User user=new User();
			user.setImei(imei);
			this.add(user);
			ulist.add(user);
		}
		ret = this.changeListPojoToEntity(ulist);

		return ret;
	}

	@Override
	public UserEntity get(int id) {

		if (id <= 0)
			return null;

		List<User> ulist=this.getUserPojoByUserID(id);
		if(null==ulist) return null;
		UserEntity ret=null;
		Iterator<User> itUser=ulist.iterator();
		if(itUser.hasNext()) ret=UserSwitcher.userPojoToEntity(itUser.next());
		return ret;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<UserEntity> findUserByEmailAdress(String emailaddress) {

		if (null == emailaddress || emailaddress.isEmpty())
			return null;

		List<User> ulist = (List<User>) this.sessionFactory.getCurrentSession()
				.createQuery("from User user where user.emailadress = :email")
				.setParameter("email", emailaddress).list();
		List<UserEntity> ret = null;
		if (null == ulist)
			ret = new ArrayList<UserEntity>();
		else
			ret = this.changeListPojoToEntity(ulist);

		return ret;
	}

	@Override
	public int getUserIdByCookieid(String cookie) {

		if (null == cookie || cookie.isEmpty())
			return -1;

		List<UserEntity> ulist = this.findUserByCookieid(cookie);
		UserEntity curUser = null;
		int ret=-1;
		if (null == ulist || ulist.isEmpty()) {
			curUser = new UserEntity();
			curUser.setUsername(this.userNamePre + this.getUserNameCount());
			curUser.setPassword(DEFAULT_USER_PASSWORD);
			curUser.setCookie(cookie);
			ret=this.add(curUser);
		} else {
			ret = ulist.get(0).getUid();
		}

		return ret;
	}

	protected List<User> findUserByNamePrefix(String name) {

		if (null == name || name.isEmpty())
			return null;

		return this.sessionFactory.getCurrentSession()
				.createQuery("from User u where u.username like :prefix")
				.setParameter("prefix", name + "%").list();
	}

	protected User getUserPojoByID(int uid) {

		if (uid <= 0)
			return null;

		User ret = null;
		List<User> ulist = this.getUserPojoByUserID(uid);
		if (null == ulist)
			return null;
		Iterator<User> itUser = ulist.iterator();
		if (itUser.hasNext())
			ret = itUser.next();
		return ret;
	}

	protected void changeUser(User target, UserEntity entity) {

		if (null == target || null == entity)
			return;

		target.setCookieid(entity.getCookie());
		target.setEmailadress(entity.getEmail());
		target.setImei(entity.getImei());

		target.setPassword(Md5.encrypt(entity.getPassword()));
		target.setUsername(entity.getUsername());

		return;
	}

	protected List<UserEntity> changeListPojoToEntity(List<User> ulist) {

		if (null == ulist)
			return null;

		List<UserEntity> ret = new ArrayList<UserEntity>();
		for (User u : ulist) {
			UserEntity entity = UserSwitcher.userPojoToEntity(u);
			if (null == entity)
				continue;
			ret.add(entity);
		}

		return ret;
	}

	@Override
	public int isLegalUserPassword(String username, String pwd) {

		int ret=-1;
		if (null == pwd || null == username || pwd.isEmpty()
				|| username.isEmpty())
			return ret;

		List<User> ulist = this.getUserPojoByUserName(username);
		if(null==ulist) return ret;
		pwd=Md5.encrypt(pwd);
		for(User u:ulist){
			if(pwd.equals(u.getPassword())){
				ret=u.getUserid();
				break;
			}
		}
		
		return ret;
	}

	protected List<User> getUserPojoByUserName(String username) {

		if (null == username || username.isEmpty())
			return null;
		return this.sessionFactory.getCurrentSession()
				.createQuery("from User u where u.username = :uname")
				.setParameter("uname", username).list();
	}

	protected List<User> getUserPojoByUserID(int uid) {

		if (uid <= 0)
			return null;
		return this.sessionFactory.getCurrentSession()
				.createQuery("from User u where u.id = :uid")
				.setParameter("uid", uid).list();
	}
	
	/**
	 * 添加用户记录，该函数只负责添加，只要传递的user参数非空，就会插入数据库；
	 * user信息中用户名或其他信息的唯一性由主调函数保证；
	 * 参数user中的id不要设置，否则该函数会抛异常
	 * @param user
	 * @return
	 */
	protected int add(User user)
	{
		int ret=-1;

		if (null != user)
		{
			this.sessionFactory.getCurrentSession().save(user);
			ret=user.getUserid();
		}
		
		return ret;
	}

	@Override
	public List<Integer> getAllUserID() {
		
		List<Integer> ret=null;
		
		Iterator<Object[]> res=this.sessionFactory.getCurrentSession().createQuery("select u.userid,u.username from User u").list().iterator();
		ret=new ArrayList<Integer>();
		if(null==res) return ret;
		while(res.hasNext()){
			Object[] curRes=(Object[])res.next();
			Integer uid=(Integer)(curRes[0]);
			ret.add(uid);
		}
		
		return ret;
	}

	@Override
	public boolean setPasswd(int uid, String passwd) {
		
		if(uid<=0||null==passwd||passwd.isEmpty()) return false;
		User u=getUserPojoByID(uid);
		if(null!=u){
			u.setPassword(Md5.encrypt(passwd));
			sessionFactory.getCurrentSession().update(u);
		}
		return true;
	}

	@Override
	public int getUserIDByUserName(String username) {
		
		int ret=0;
		if(MyStringChecker.isBlank(username)) return ret;
		
		List<User> ulist=getUserPojoByUserName(username);
		if(null!=ulist&&!ulist.isEmpty()) ret=ulist.get(0).getUserid();
		
		return ret;
	}

	@Override
	public boolean isExistUnameOrEmail(String username, String email) {
		
		String hqlPrefix="from User u where", forUname=" u.username = :uname", forEmail=" u.emailadress = :email", hql;
		boolean nameReq=!MyStringChecker.isBlank(username), emailReq=!MyStringChecker.isBlank(email), ret=false;
		
		if(!nameReq&&!emailReq) return ret;
		if(nameReq&&emailReq) hql=hqlPrefix+forUname+" or"+forEmail;
		else hql=hqlPrefix+(nameReq?forUname:forEmail);
		
		Query query=sessionFactory.getCurrentSession().createQuery(hql);
		if(nameReq) query.setParameter("uname", username);
		if(emailReq) query.setParameter("email", email);
		Iterator<User> it=query.iterate();
		if(it.hasNext()) ret=true;
		
		return ret;
	}

	@Override
	public UserLoginResult checkUserInfo(String username, String passwd, int ret[]) {
		
		ret[0]=-1;
		if(MyStringChecker.isBlank(username)){
			return UserLoginResult.no_exist_user;
		}
		if(MyStringChecker.isBlank(passwd)){
			return UserLoginResult.error_passwd;
		}
		List<User> ls=sessionFactory.getCurrentSession().createQuery("from User u where u.username = :name").setParameter("name", username).list();
		if(null==ls||ls.isEmpty()){
			return UserLoginResult.no_exist_user;
		}
		UserLoginResult result=UserLoginResult.error_passwd;
		for(Iterator<User> iter=ls.iterator();iter.hasNext();){
			User u=iter.next();
			String dbpasswd=u.getPassword();
			if(dbpasswd==null||dbpasswd.equals(Md5.encrypt(passwd))){
				result=UserLoginResult.success;
				ret[0]=u.getUserid();
				break;
			}
		}
		return result;
	}
}
