package server.info.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import db.dao.CategoryDao;
import server.commonutils.MyStringChecker;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.entites.transactionlevel.CategoryEntity;

/**
 * 类型信息管理
 * 
 * @author zhou
 *
 */
public class CategoryInfo {

	/*
	 * 类别信息以后可能的变化比较大，这个类也可能有很大变化 甚至可能出现动态类别（聚类）、类层次关系（类别细化），
	 * 而成员搜索引擎调度以及用户兴趣也可能与类别有关 为了兼容以前的代码，这里的枚举变量用英文，并且与以前一样（包括数据库的category表）
	 */
	public enum Category {
		/* 1 */financial,
		/* 2 */it,
		/* 3 */employment,
		/* 4 */sports,
		/* 5 */education,
		/* 6 */health,
		/* 7 */military,
		/* 8 */tourism,
		/* 9 */literature
	};

	private final static String visibleCnForm[] = {
	/* 1 */"经济",
	/* 2 */"IT",
	/* 3 */"就业",
	/* 4 */"体育",
	/* 5 */"教育",
	/* 6 */"健康",
	/* 7 */"军事",
	/* 8 */"旅游",
	/* 9 */"文学" };
	private final static String visibleEnForm[] = {
	/* 1 */"Financial",
	/* 2 */"IT",
	/* 3 */"Employment",
	/* 4 */"Sports",
	/* 5 */"Education",
	/* 6 */"Health",
	/* 7 */"Military",
	/* 8 */"Tourism",
	/* 9 */"Literature" };

	private final static Map<String, Category> strCnToPos;
	private final static Map<String, Category> strEnToPos;
	private final static Map<Integer, Category> dbIdToCat;
	private final static Map<Integer, Integer> catToDbId;
	
	private final static Category values[];
	
	static {
		values=Category.values();
		strCnToPos = new HashMap<String, Category>();
		strEnToPos = new HashMap<String, Category>();
		dbIdToCat=new HashMap<Integer, Category>();
		catToDbId=new HashMap<Integer, Integer>();
		for (int i = 0; i < visibleCnForm.length; ++i) {
			strCnToPos.put(visibleCnForm[i], values[i]);
			strEnToPos.put(visibleEnForm[i], values[i]);
		}
		
		/*
		 * 注：这种写法有一个限制，即必须在spring容器启动结束后（ web.xml中配置了一个listener执行完）才能调这个类的函数，
		 * 否则类加载器执行这段代码将抛异常，因此如果以后需要在服务器启动时使用listener读入一些与类别有关的信息，例如搜索引擎评分之类的，
		 * 这个listener必须放在spring的listener之后
		 */
		CategoryDao dao=(CategoryDao)SpringBeanFactoryUtil.getBean(SpringBeanNames.CATEGORY_DAO_BEAN_NAME);
		List<CategoryEntity> list=new LinkedList<CategoryEntity>();
		list = dao.getAllCategoryName();
		for(Iterator<CategoryEntity> iter=list.iterator();iter.hasNext();){
			CategoryEntity entity=iter.next();
			if(null==entity) continue;
			Category tar=null;
			//这里会抛异常，如果数据库中的类别名字与枚举变量不对应，
			//或者在数据库中新增了类别，这里没有相应修改
			//这里还是拦截了这种异常，防止数据库category表无意中增加了无效的行导致系统崩溃
			//但仍然输出异常信息，如果因为新增了类别导致这里出异常，就应该留意
			try{
				tar=Category.valueOf(entity.getName());
			}catch(Exception e){
				e.printStackTrace();
			}
			if(null==tar) continue;
			int id=entity.getId();
			dbIdToCat.put(id, tar);
			catToDbId.put(tar.ordinal(), id);
		}
		
	}

	/**
	 * 将字符串转为枚举类型（用于数据库-系统层）
	 * 
	 * @param str
	 *            字符串
	 * @return 对应的枚举类型，如果是错误的字符串，会返回null
	 */
	public final static Category parseString(String str) {

		Category ret = null;
		if (MyStringChecker.isBlank(str))
			return ret;
		Category values[] = Category.values();
		for (int i = 0; i < values.length; ++i) {
			Category tmp = values[i];
			if (tmp.toString().equals(str)) {
				ret = tmp;
				break;
			}
		}
		return ret;
	}
	public final static Category parseString(int index) {

		Category ret = null;
		if (index < 1 || index > Category.values().length)
			return ret;
		Category values[] = Category.values();
		for (int i = 0; i < values.length; ++i) {
			Category tmp = values[i];
			if (i==index) {
				ret = tmp;
				break;
			}
		}
		return ret;
	}
	public final static String parseStringS(int index) {

		switch (index) {
		case 1:
			return "financial";
		case 2:
			return "it";
		case 3:
			return "employment";
		case 4:
			return "sports";
		case 5:
			return "education";
		case 6:
			return "health";
		case 7:
			return "military";
		case 8:
			return "tourism";
		case 9:
			return "literature";
		}
		return "";
	}
	public final static int parseStringI(String them) {
		if (them.equalsIgnoreCase("education")) {
			return 5;
		}else if (them.equalsIgnoreCase("it")) {
			return 2;
		}else if (them.equalsIgnoreCase("financial")) {
			return 1;
		}else if (them.equalsIgnoreCase("employment")) {
			return 3;
		}else if (them.equalsIgnoreCase("sports")) {
			return 4;
		}else if (them.equalsIgnoreCase("health")) {
			return 6;
		}else if (them.equalsIgnoreCase("military")) {
			return 7;
		}else if (them.equalsIgnoreCase("tourism")) {
			return 8;
		}else if (them.equalsIgnoreCase("literature")) {
			return 9;
		}
		return 0;
	}
	/**
	 * 获取枚举类型对应的字符串形式（用于系统-数据库层）
	 * 
	 * @param cat
	 *            类别
	 * @return 与数据库category表中对应的字符串
	 */
	public final static String getStringType(Category cat) {

		if (null == cat)
			return null;
		return cat.toString();
	}

	/**
	 * 获得类别对应的“可视化”字符串（用于系统-客户端层）
	 * 
	 * @param cat
	 * @return
	 */
	public final static String getVisibleCnString(Category cat) {

		if (null == cat) return "";
		return visibleCnForm[cat.ordinal()];
	}

	/**
	 * 获得类别对应的“可视化”字符串（用于系统-客户端层）
	 * 
	 * @param cat
	 * @return
	 */
	public final static String getVisibleEnString(Category cat) {

		if (null == cat) return "";
		return visibleEnForm[cat.ordinal()];
	}

	/**
	 * 根据类型信息的字符串形式，转为枚举变量
	 * 
	 * @param str
	 *            类型信息的字符串，不能有错，不能为null，调用前应该做检查；否则这里会抛异常
	 * @return
	 */
	public final static Category parseVisibleString(String str) {

		int pos = getPosOfStringForm(str);
		return Category.values()[pos];
	}

	public final static int getAllCategoryEnName(Set<String> ret) {

		if (null == ret)
			return 0;
		Category cats[] = Category.values();
		for (int i = 0; i < cats.length; ++i) {
			ret.add(cats[i].toString());
		}
		return cats.length;
	}

	private final static int getPosOfStringForm(String category) {

		return parseStringI(category);
	}

	public final static String getENString(int index) {

		return visibleEnForm[index-1];
	}

	public final static String getCNString(int index) {

		return visibleCnForm[index-1];
	}
/*公共的互转函数*/
	
	/**
	 * 将各种类型的的名字信息转为枚举类型，
	 * 建议在从客户端拿到字符串（或数据库中）后，立刻调用这个函数转为枚举；
	 * 同时根据返回值是不是为null，就可以知道客户端的数据是不是有错，再针对错误及时做出处理，
	 * 避免让这种错误深入到系统下层，不利于调试bug
	 * @param str 类别的字符串信息
	 * @return
	 */
	public final static Category tryParseAllName(String str){
		
		Category ret=null;
		ret=strCnToPos.get(str);//中文名转换
		if(null==ret) ret=strEnToPos.get(str);//英文名转换
		if(null==ret){
			try{
				ret=Category.valueOf(str);//数据库名转换
			}catch(Exception e){
			}
		}
		return ret;
	}
	
	/*数据库-系统内部类型互转函数*/
	
	/**
	 * 将字符串转为枚举类型（用于数据库-系统层），这个函数不处理异常，效率高
	 * 如果在调用的时候确定字符串不会有错时可以使用；如果不确定，应该使用tryParseAllName()函数
	 * @param str 数据库中的字符串
	 * @return 对应的枚举类型，如果是错误的字符串，会返回null
	 */
	public final static Category parseDBName(String str) {
		return Category.valueOf(str);
	}
	
	/**
	 * 根据类别在数据库中的ID，得到对应的枚举变量，
	 * 如果这个函数返回null，是传入的ID不对，调用者应该考虑这种情况为什么会发生，怎么处理
	 * @param id 类别在数据库中的ID
	 * @return 
	 */
	public final static Category parseDBId(int id){
		return dbIdToCat.get(id);
	}
	
	
	/**
	 * 将内部枚举变量转为在数据库中使用的字符串名字，
	 * 如果需要操作数据库，又要使用类别名字，就用这个函数来转换
	 * @param cat 类别
	 * @return 与数据库category表中对应的字符串
	 */
	public final static String getDBName(Category cat) {
		if (null == cat) return null;
		return cat.toString();
	}
	
	/**
	 * 将内部枚举变量转为在数据库中的ID
	 * @param cat
	 * @return
	 */
	public final static int getDBId(Category cat){
		if(null==cat) return -1;
		return catToDbId.get(cat.ordinal());
	}

	/*客户端字符串-系统内部类型互转函数*/
	
	
}
