package db.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import server.info.entites.transactionlevel.CategoryEntity;
import db.dao.CategoryDao;
import db.entityswithers.CategorySwitcher;
import db.hibernate.tables.isearch.Category;

public class CategoryDaoImpl implements CategoryDao {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public int add(String name) {

		if (null == name || name.isEmpty())
			return -1;
		Category catePojo = this.getPojoByName(name);
		if (null == catePojo) {
			catePojo = new Category();
			catePojo.setCategoryName(name);
			this.sessionFactory.getCurrentSession().save(catePojo);
		}
		return catePojo.getId();
	}

	@Override
	public boolean delete(int id) {

		if (id <= 0)
			return false;
		Category target = this.getPojoById(id);
		if (null == target)
			return false;
		this.sessionFactory.getCurrentSession().delete(target);
		return true;
	}

	@Override
	public boolean delete(String name) {

		if (null == name || name.isEmpty())
			return false;
		Category target = this.getPojoByName(name);
		if (null == target)
			return false;
		this.sessionFactory.getCurrentSession().delete(target);
		return true;
	}

	@Override
	public int update(int id, String name) {

		if (id <= 0 || null == name || name.isEmpty())
			return -1;
		List<Category> tarList = this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from Category c where c.id = :cid or c.name = :cname")
				.list();
		Category target = null;
		int ret = -1;
		switch (tarList.size()) {
		case 1:
			target = tarList.get(0);
			target.setCategoryName(name);
			this.sessionFactory.getCurrentSession().update(target);
			ret = target.getId();
			break;
		case 0:
		case 2:
		default:
			break;
		}
		return ret;
	}

	@Override
	public CategoryEntity get(int id) {

		CategoryEntity ret = null;
		if (id <= 0)
			return ret;
		Category target = this.getPojoById(id);
		if (null != target)
			ret = CategorySwitcher.categoryPojoToEntity(target);
		return ret;
	}

	@Override
	public CategoryEntity get(String name) {

		CategoryEntity ret = null;
		if (null == name || name.isEmpty())
			return ret;
		Category target = this.getPojoByName(name);
		if (null != target)
			ret = CategorySwitcher.categoryPojoToEntity(target);
		return ret;
	}

	Category getPojoByName(String name) {

		if (null == name || name.isEmpty())
			return null;
		Category ret = null;
		Iterator<Category> itcpojo = this.sessionFactory.getCurrentSession()
				.createQuery("from Category c where c.categoryName = :cname")
				.setParameter("cname", name).list().iterator();
		if (itcpojo.hasNext())
			ret = itcpojo.next();
		return ret;
	}

	Category getPojoById(int id) {

		if (id <= 0)
			return null;
		Category ret = null;
		Session s=this.sessionFactory.getCurrentSession();
		Iterator<Category> itcpojo = s
				.createQuery("from Category c where c.id = :cid")
				.setParameter("cid", id).iterate();
		if(itcpojo.hasNext()){
			ret=itcpojo.next();
		}
		return ret;
	}

	@Override
	public int getCategoryIDByName(String name) {

		int ret = -1;
		if (null == name || name.isEmpty())
			return ret;

		Category target = this.getPojoByName(name);
		if (null != target)
			ret = target.getId();

		return ret;
	}

	@Override
	public String getCategoryNameByID(int id) {

		if (id <= 0)
			return null;

		String ret = null;
		Category target = this.getPojoById(id);
		if (null != target)
			ret = target.getCategoryName();

		return ret;
	}

	@Override
	public List<Integer> getCategoryIDByName(Set<String> cnameSet) {

		if (null == cnameSet)
			return null;
		List<Integer> ret = new ArrayList<Integer>();
		if (cnameSet.isEmpty())
			return ret;

		Iterator<Category> iterCate = this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from Category c where c.categoryName in (:cnameSet)")
				.setParameterList("cnameSet", cnameSet).list().iterator();
		while(iterCate.hasNext()){
			ret.add(iterCate.next().getId());
		}
		
		return ret;
	}

	@Override
	public List<CategoryEntity> getAllCategoryName() {
		
		Iterator<Category> all=this.sessionFactory.getCurrentSession().createQuery("from Category").list().iterator();
		List<CategoryEntity> ret=new ArrayList<CategoryEntity>();
		while(null!=all&&all.hasNext()){
			Category c=all.next();
			ret.add(CategorySwitcher.categoryPojoToEntity(c));
		}
		return ret;
	}

}
