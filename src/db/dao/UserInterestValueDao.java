package db.dao;

import java.util.Date;
import java.util.List;

import server.info.entites.transactionlevel.UserInterestValueEntity;

public interface UserInterestValueDao {
	
	public int add(UserInterestValueEntity interest);

	public void delete(int uid,int cid);

	public int update(UserInterestValueEntity log);

	public UserInterestValueEntity get(int id);
	
	public UserInterestValueEntity getEntity(int uid,int cid,Date date);
	public List<UserInterestValueEntity> getEntitys(int uid);

	/**
	 * 从数据库获取用户感兴趣的主题及分数等信息
	 * 
	 * @param ret
	 *            获取到的信息追加存放到这里，不能为null
	 * @param userid
	 *            要查找的用户的ID
	 */
	public void getInterestThemsOfUser(List<UserInterestValueEntity> ret, int userid);

}
