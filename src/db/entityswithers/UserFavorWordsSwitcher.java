package db.entityswithers;

import java.util.ArrayList;
import java.util.List;

import server.info.entites.transactionlevel.UserFavorWordsEntity;
import db.hibernate.tables.isearch.Category;
import db.hibernate.tables.isearch.User;
import db.hibernate.tables.isearch.UserFavorWords;

public class UserFavorWordsSwitcher {

	public static UserFavorWordsEntity favorwordsPojoToEntity(UserFavorWords pojo){
		
		if(null==pojo) return null;
		
		UserFavorWordsEntity ret=new UserFavorWordsEntity();
		Category category=pojo.getCategory();
		ret.getCategory().setId(category.getId());
		ret.getCategory().setName(category.getCategoryName());
		ret.setDate(pojo.getDate());
		ret.setUid(pojo.getUser().getUserid());
		ret.setWeight(pojo.getValue());
		ret.setWord(pojo.getWord());
		ret.setId(pojo.getWordid());
		
		return ret;
	}
	
	public static UserFavorWords favorwordsPojoToEntity(UserFavorWordsEntity entity){
		
		if(null==entity) return null;
		
		UserFavorWords ret=new UserFavorWords();
		Category category=new Category();
		category.setId(entity.getId());
		category.setCategoryName(entity.getCategory().getName());
		ret.setCategory(category);
		ret.setDate(entity.getDate());
		ret.setValue(entity.getWeight());
		ret.setWord(entity.getWord());
		ret.setWordid(entity.getId());
		User owner=new User();
		owner.setUserid(entity.getUid());
		ret.setUser(owner);
		
		return ret;
	}
	
	public static List<UserFavorWordsEntity> favorwordsListPojoToEntity(List<UserFavorWords> pojolist){
		
		if(null==pojolist) return null;
		
		List<UserFavorWordsEntity> ret=new ArrayList<UserFavorWordsEntity>();
		for(UserFavorWords pojo: pojolist){
			if(null==pojo) continue;
			ret.add(UserFavorWordsSwitcher.favorwordsPojoToEntity(pojo));
		}
		
		return ret;
	}
}
