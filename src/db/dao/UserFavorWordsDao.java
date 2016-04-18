package db.dao;

import java.util.List;

import server.info.entites.transactionlevel.UserFavorWordsEntity;

public interface UserFavorWordsDao {
	
	public int add(UserFavorWordsEntity entity);
	public List<Integer> getIDByWordAndUser(String word, int uid);
	public List<String> getAllWordsByUser(int uid);
	public int update(UserFavorWordsEntity entity);
	public List<UserFavorWordsEntity> getWordsOfUserOrderByWeightDesc(int uid);
	public List<UserFavorWordsEntity> getWordsOfUser(int uid);
	public boolean delete(int id);
	public int findByWordAndCategory(String word, String categoryName);
	public int findByWordAndCategory(String word, int catetoryID);
	
}
