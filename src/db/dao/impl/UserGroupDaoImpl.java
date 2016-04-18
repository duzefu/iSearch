package db.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import server.info.entites.transactionlevel.UserGroupEntity;
import db.dao.CategoryDao;
import db.dao.UserDao;
import db.dao.UserGroupDao;
import db.dbhelpler.UserHelper;
import db.entityswithers.UserGroupSwitcher;
import db.hibernate.tables.isearch.Category;
import db.hibernate.tables.isearch.GroupInfo;
import db.hibernate.tables.isearch.GroupToCategory;
import db.hibernate.tables.isearch.User;
import db.hibernate.tables.isearch.UserGroups;

public class UserGroupDaoImpl implements UserGroupDao {

	private SessionFactory sessionFactory;
	private UserDao userDao;
	private CategoryDao categoryDao;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public CategoryDao getCategoryDao() {
		return categoryDao;
	}

	public void setCategoryDao(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}

	@Override
	public int add(UserGroupEntity entity) {

		if (null == entity)
			return -1;

		List<Integer> uidlist = entity.getUidlist();
		if (null == uidlist || uidlist.isEmpty())
			return -1;

		List<UserGroups> u2glist = UserGroupSwitcher
				.usergroupEntityToPojo(entity);
		if (null == u2glist || u2glist.isEmpty())
			return -1;

		GroupInfo groupInfo = u2glist.get(0).getGroupInfo();
		Session session = this.sessionFactory.getCurrentSession();
		session.save(groupInfo);
		for (UserGroups group : u2glist) {
			if (null == group)
				continue;
			session.save(group);
		}

		return groupInfo.getId();
	}

	@Override
	public boolean delete(int id) {

		if (id <= 0)
			return false;

		GroupInfo target = null;
		List<GroupInfo> groupInfoList = this.sessionFactory.getCurrentSession()
				.createQuery("from GroupInfo ginfo where ginfo.id = :infoid")
				.list();
		if (null != groupInfoList && !groupInfoList.isEmpty())
			target = groupInfoList.get(0);
		Iterator<UserGroups> iterU2G = target.getUserGroupses().iterator();
		Session session = this.sessionFactory.getCurrentSession();
		while (iterU2G.hasNext()) {
			UserGroups curU2GRecord = iterU2G.next();
			if (null != curU2GRecord)
				session.delete(curU2GRecord);
		}
		session.delete(target);
		return true;
	}

	@Override
	public int updateGroupRemark(int id, String newContent) {

		if (id <= 0)
			return -1;

		GroupInfo target = this.getGroupPojoByID(id);
		target.setRemark(newContent);
		if (null == target)
			return -1;
		this.sessionFactory.getCurrentSession().update(target);

		return target.getId();
	}

	@Override
	public boolean updateGroupUsers(int id, List<Integer> uidlist) {

		if (id <= 0 || null == uidlist)
			return false;
		boolean ret = false;

		GroupInfo target = this.getGroupPojoByID(id);
		if (null == target)
			return false;
		Set<Integer> uidset = new HashSet<Integer>(uidlist);
		Iterator<UserGroups> iterU2G = target.getUserGroupses().iterator();
		Session session = this.sessionFactory.getCurrentSession();
		while (iterU2G.hasNext()) {
			UserGroups curU2G = iterU2G.next();
			Integer curUid = curU2G.getUser().getUserid();
			if (!uidset.contains(curUid)) {
				session.delete(curU2G);
			}
			uidset.remove(curU2G);
		}

		ret = this.addUsersToGroup(id, uidset);

		return ret;
	}

	protected GroupInfo getGroupPojoByID(int id) {

		if (id <= 0)
			return null;

		GroupInfo ret = null;
		List<GroupInfo> grouplist = this.sessionFactory.getCurrentSession()
				.createQuery("from GroupInfo group where group.id = :groupid")
				.setParameter("groupid", id).list();
		if (null != grouplist && !grouplist.isEmpty())
			ret = grouplist.get(0);

		return ret;
	}

	protected UserGroups getGroupToUserPojoByGroupIDAndUserID(int groupid,
			int uid) {

		if (groupid <= 0 || uid <= 0)
			return null;

		UserGroups ret = null;
		List<UserGroups> grouplist = this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from UserGroups g2u where g2u.groupInfo.id = :groupid and g2u.user.id = :uid")
				.setParameter("groupid", groupid).setParameter("uid", uid)
				.list();
		if (null != grouplist && !grouplist.isEmpty())
			ret = grouplist.get(0);

		return ret;
	}

	@Override
	public int addUsersToGroup(int groupid, Collection<Integer> uidset) {

		if (groupid <= 0 || null == uidset)
			return -1;
		if (uidset.isEmpty())
			return groupid;

		Iterator<Integer> iterUserID = uidset.iterator();
		GroupInfo groupInfo = new GroupInfo();
		groupInfo.setId(groupid);
		Session session = this.sessionFactory.getCurrentSession();
		while (iterUserID.hasNext()) {
			Integer curUid = iterUserID.next();
			UserGroups target = this.getGroupToUserPojoByGroupIDAndUserID(
					groupid, curUid);
			if (null != target)
				continue;
			target = new UserGroups();
			User user = new User();
			user.setUserid(curUid);
			target.setUser(user);
			target.setGroupInfo(groupInfo);
			session.save(target);
		}

		return groupid;
	}
	
	@Override
	public Set<Integer> addUserGroupInfo(Set<String> categorySet) {

		if (null == categorySet)
			return null;
		Set<Integer> ret = new HashSet<Integer>();
		if (categorySet.isEmpty())
			return ret;
		//拿到categorySet主题中所有不在group-to-category表格中的主题
		Set<String> absentGinfo =getAbsentGroupInfo(categorySet);
		if (!absentGinfo.isEmpty())
			addAbsentGroupInfo(absentGinfo);//存在某此类别没有群组信息，在数据库中添加相关的记录
		List<Integer> groupId = getGroupIDByCategoryName(categorySet);//再次查询就可以得到所有类别对应的群组ID
		if (null != groupId)
			ret.addAll(groupId);
		return ret;
	}

	/**
	 * 查找数据库的group_to_category表，检查参数中指定的主题类别是否都有对应的群组ID
	 * @param categorySet 主题类别（用户兴趣主题），英文名
	 * @return 如果categorySet中某一个主题还没有对应的群组编号，该主题名称就在返回的集合中
	 */
	private Set<String> getAbsentGroupInfo(Set<String> categorySet){
		
		Set<String> ret=new HashSet<String>(categorySet);//初始化返回集合，所有的类型都认为是没有对应的群组ID的
		Iterator<GroupToCategory> iter = getGTCPojoByCategoryName(categorySet)
				.iterator();
		while (iter.hasNext()) {
			GroupToCategory gtc = iter.next();
			//查到的GroupToCategory对象中，
			//Category成员变量一定与参数中指定的某一个类别名字相同，
			//表示它已经有对应的群组了，因此从返回集合中移除
			ret.remove(gtc.getCategory().getCategoryName());
		}
		return ret;
	}
	
	private List<Integer> getGroupIDByCategoryName(Set<String> cnameSet) {

		if (null == cnameSet)
			return null;
		List<Integer> ret = new ArrayList<Integer>();
		if (cnameSet.isEmpty())
			return ret;
		Iterator<GroupToCategory> iterGinfo = this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from GroupToCategory gtc where gtc.category.categoryName in (:cnameSet)")
				.setParameterList("cnameSet", cnameSet).list().iterator();
		while (iterGinfo.hasNext()) {
			ret.add(iterGinfo.next().getGroupInfo().getId());
		}

		return ret;
	}

	private List<GroupToCategory> getGTCPojoByCategoryName(Set<String> cnameSet) {

		return this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from GroupToCategory gtc where gtc.category.categoryName in (:cNameSet)")
				.setParameterList("cNameSet", cnameSet).list();
	}

	private void addAbsentGroupInfo(Set<String> cnameSet) {

		if (null == cnameSet)
			return;
		List<Integer> cidList = this.categoryDao.getCategoryIDByName(cnameSet);
		if (null == cidList || cidList.isEmpty())
			return;
		Iterator<Integer> iterCidList = cidList.iterator();
		Session session = this.sessionFactory.getCurrentSession();
		while (iterCidList.hasNext()) {
			int cid = iterCidList.next();
			Category c = new Category();
			c.setId(cid);
			GroupInfo ginfo = new GroupInfo();
			GroupToCategory gtc = new GroupToCategory();
			gtc.setCategory(c);
			gtc.setGroupInfo(ginfo);
			session.save(ginfo);
			session.save(gtc);
		}
		return;
	}
	/**
	 * 更新用户的群组关系表，其中每个用户是属于三个群组
	 * Set<Integer> gidSet 该用户新生成需要更新的三个群组
	 */
	@Override
	public boolean updateUserGroup(int uid, Set<Integer> gidSet) {

		if (uid <= 0 || null == gidSet)
			return false;
		if (gidSet.isEmpty())
			return true;

		Session session = this.sessionFactory.getCurrentSession();
		Iterator<UserGroups> iterUtg = session
				.createQuery("from UserGroups g where g.user.userid = :uid")
				.setParameter("uid", uid).list().iterator();
		Set<Integer> newGid = new HashSet<Integer>(gidSet);
		//更新用户的群组关系，删除之前的，加入新的三个，但是总共还是只有三个群组关系
		while (iterUtg.hasNext()) {
			UserGroups ug = iterUtg.next();
			GroupInfo ginfo=ug.getGroupInfo();
			Integer gid=ginfo.getId();
			if (!gidSet.contains(gid))
				session.delete(ug);
			newGid.remove(gid);
		}
		addUsersToGroup(uid, newGid);
		return true;
	}

	private boolean addUsersToGroup(int uid, Set<Integer> gidSet) {

		Iterator<Integer> iterGid = gidSet.iterator();
		Session session = this.sessionFactory.getCurrentSession();
		User user = new User();
		user.setUserid(uid);
		UserGroups ug=null;
		GroupInfo ginfo=null;
		while (iterGid.hasNext()) {
			Integer gid = iterGid.next();
			ginfo= new GroupInfo();
			ginfo.setId(gid);
			ug = new UserGroups();
			ug.setGroupInfo(ginfo);
			ug.setUser(user);
			session.save(ug);
		}

		return true;
	}

	@Override
	public boolean getGroupUserID(int userid, Set<Integer> ret) {

		if (!UserHelper.isLegalUserID(userid))
			return false;
		if(null==ret) return true;
		boolean retval=false;
		
		Set<Integer> gid = new HashSet<Integer>();
		retval=getUserGroupID(userid, gid);
		if(!retval) return retval;
		
		if (null != gid && !gid.isEmpty())
		retval=getUserIDInGroup(gid, ret);
		
		return retval;
	}

	private boolean getUserGroupID(int uid, Set<Integer> ret) {

		if(!UserHelper.isLegalUserID(uid)) return false;
		if(null==ret) return true;
		
		Iterator<UserGroups> iterUg = sessionFactory.getCurrentSession()
				.createQuery("from UserGroups ug where ug.user.id = :uid")
				.setParameter("uid", uid).list().iterator();
		while (iterUg.hasNext()) {
			ret.add(iterUg.next().getGroupInfo().getId());
		}
		return true;
	}

	private boolean getUserIDInGroup(Set<Integer> gidSet, Set<Integer> ret) {

		if(null==gidSet||gidSet.isEmpty()) return false;
		if(null==ret) return true;
		
		Iterator<UserGroups> iterUg = sessionFactory.getCurrentSession()
				.createQuery(	"from UserGroups ug where ug.groupInfo.id in (:gidSet)")
				.setParameterList("gidSet", gidSet).list().iterator();
		Set<Integer> res=new HashSet<Integer>();
		while(iterUg.hasNext()){
			ret.add(iterUg.next().getUser().getUserid());
		}
		return true;
	}
}
