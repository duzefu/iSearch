package db.entityswithers;

import java.util.ArrayList;
import java.util.List;

import server.info.entites.transactionlevel.UserGroupEntity;
import db.hibernate.tables.isearch.GroupInfo;
import db.hibernate.tables.isearch.User;
import db.hibernate.tables.isearch.UserGroups;

public class UserGroupSwitcher {

	/**
	 * 根据用户群组实体，生成插入数据库所需要的Hiberate POJO对象。
	 * 
	 * @param entity
	 *            一个群组的信息，包括该群组用户ID的List，以及群组备注信息
	 * @return 列表，其中每一个元素UserGroups的成员变量groupInfo相同，使用hibenrate
	 *         save以后对应数据库中的群组信息表；user成员变量对应该组中的某一个用户。
	 */
	public static List<UserGroups> usergroupEntityToPojo(UserGroupEntity entity) {

		if (null == entity)
			return null;
		List<Integer> uidlist = entity.getUidlist();
		if (null == uidlist || uidlist.isEmpty())
			return null;

		List<UserGroups> ret = new ArrayList<UserGroups>();

		GroupInfo groupInfo = new GroupInfo();
		groupInfo.setRemark(entity.getGroupRemark());

		for (Integer uid : uidlist) {
			if (uid <= 0)
				continue;
			User user = new User();
			user.setUserid(uid);

			UserGroups u2g = new UserGroups();
			u2g.setUser(user);
			u2g.setGroupInfo(groupInfo);
			ret.add(u2g);
		}
		
		return ret;
	}
	
	public static UserGroupEntity usergroupPojoToEntity(List<UserGroups> pojo){
		
		if(null==pojo) return null;
		
		UserGroupEntity ret=new UserGroupEntity();
		if(pojo.isEmpty()) return ret;
		GroupInfo group=pojo.get(0).getGroupInfo();
		if(null!=group) ret.setGroupRemark(group.getRemark());
		
		List<Integer> uidlist=ret.getUidlist();
		for(UserGroups groupInfo:pojo){
			if(null==pojo) continue;
			uidlist.add(groupInfo.getUser().getUserid());
		}
		return ret;
	}

}
