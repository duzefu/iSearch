package db.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import server.info.entites.transactionlevel.UserGroupEntity;

public interface UserGroupDao {
	
	public int add(UserGroupEntity entity);
	public boolean delete(int id);
	public int updateGroupRemark(int id, String newContent);
	public boolean updateGroupUsers(int id, List<Integer> uidlist);
	public int addUsersToGroup(int groupid, Collection<Integer> uidset);
	/**
	 * 拿到主题对应的群组的id
	 * @param categorySet
	 * @return
	 */
	public Set<Integer> addUserGroupInfo(Set<String> categorySet);
	/** 更新用户的群组关系表，其中每个用户是属于三个群组
	 * @param uid
	 * @param gidSet 该用户新生成需要更新的三个群组的id
	 * @return
	 */
	public boolean updateUserGroup(int uid, Set<Integer> gidSet);
	public boolean getGroupUserID(int userid, Set<Integer> ret);
}
