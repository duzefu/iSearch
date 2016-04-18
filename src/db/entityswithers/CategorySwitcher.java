package db.entityswithers;

import server.info.entites.transactionlevel.CategoryEntity;
import db.hibernate.tables.isearch.Category;

public class CategorySwitcher {

	public static CategoryEntity categoryPojoToEntity(Category cpojo){
		
		if(null==cpojo) return null;
		CategoryEntity ret=new CategoryEntity();
		ret.setId(cpojo.getId());
		ret.setName(cpojo.getCategoryName());
		return ret;
	}
	
	public static Category categoryPojoToEntity(CategoryEntity centity){
		
		if(null==centity) return null;
		Category ret=new Category();
		int cid=centity.getId();
		if(cid>0) ret.setId(cid);
		ret.setCategoryName(centity.getName());
		return ret;
	}
	
}
