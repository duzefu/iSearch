package db.dao;

import java.util.List;
import java.util.Set;

import server.info.entites.transactionlevel.CategoryEntity;

public interface CategoryDao {
	
	public int add(String name);
	
	public boolean delete(int id);
	
	public boolean delete(String name);
	
	public int update(int id, String name);
	
	public CategoryEntity get(int id);
	
	public CategoryEntity get(String name);
	
	public String getCategoryNameByID(int id);
	
	public int getCategoryIDByName(String name);
	
	public List<Integer> getCategoryIDByName(Set<String> cnameSet);
	
	public List<CategoryEntity> getAllCategoryName();
	
}
