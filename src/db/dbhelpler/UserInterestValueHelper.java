package db.dbhelpler;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ibm.icu.text.SimpleDateFormat;

import common.entities.blackboard.GenerateUserGroup;
import common.entities.blackboard.Interest;
import common.textprocess.userXMLfilehelpler.GetDBData;
import server.commonutils.MyStringChecker;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.CategoryInfo;
import server.info.config.CategoryInfo.Category;
import server.info.config.SpringBeanNames;
import server.info.entites.transactionlevel.CategoryEntity;
import server.info.entites.transactionlevel.UserInterestEntity;
import server.info.entites.transactionlevel.UserInterestValueEntity;
import db.dao.UserInterestValueDao;
import db.entityswithers.CategorySwitcher;
import db.entityswithers.UserInterestValueSwitcher;
import db.hibernate.tables.isearch.User;
import db.hibernate.tables.isearch.UserInterestValue;

public class UserInterestValueHelper {
	private UserInterestValueDao interestdao;

	private static UserInterestValueHelper instance;

	private static UserInterestValueHelper getInstance() {
		if (null == instance) {
			synchronized (UserInterestValueHelper.class) {
				if (null == instance) {
					instance = new UserInterestValueHelper();
				}
			}
		}
		return instance;
	}

	private UserInterestValueHelper() {
		interestdao = (UserInterestValueDao) SpringBeanFactoryUtil
				.getBean(SpringBeanNames.USER_INTEREST_VALUE_DAO_BEAN_NAME);
	}

	private static UserInterestValueDao getInterestDao() {
		return getInstance().interestdao;
	}

	// check
	public static int add(UserInterestValueEntity interest) {
		return getInstance().interestdao.add(interest);

	}

	// check
	public static void delete(int uid, int cid) {
		getInstance().interestdao.delete(uid, cid);
	}

	// check
	public static int update(UserInterestValueEntity log) {
		return getInstance().interestdao.update(log);

	}

	// check
	public static UserInterestValueEntity get(int id) {
		return getInstance().interestdao.get(id);

	}

	// check
	public static UserInterestValueEntity getEntity(int uid, int cid, Date date) {
		return getInstance().interestdao.getEntity(uid, cid, date);

	}

	// check
	public static void getInterestThemsOfUser(
			List<UserInterestValueEntity> ret, int userid) {
		getInstance().interestdao.getInterestThemsOfUser(ret, userid);
	}

	public static List<UserInterestValueEntity> getEntitys(int uid) {
		return getInstance().interestdao.getEntitys(uid);
	}

	// 实例函数
	public static List<InterestVo> getUserInterestValIns(int userid,
			int dayNumber) {

		// TODO 这是临时做法，以后兴趣由数据库来记录时，应该修改写法
		int amount = 0;
		List<InterestVo> userMap = new ArrayList<InterestVo>();
		List<UserInterestValueEntity> temp = null;
		temp = getEntitys(userid);
		if (temp!=null) {
			amount = temp.size();
		}else{
			return userMap;
		}
		Collections.sort(temp, new Comparator<UserInterestValueEntity>() {
			@Override
			public int compare(UserInterestValueEntity arg0,
					UserInterestValueEntity arg1) {
				double diff=arg0.getDate().getTime()-arg1.getDate().getTime();
				if(diff<0) return 1;
				if(diff>0) return -1;
				return 0;
			}
		});
		if (amount>0) {
			getUserMapData(dayNumber, temp, userMap);
			 Collections.sort(userMap, new Comparator<InterestVo>() {
		            public int compare(InterestVo arg0, InterestVo arg1) {
		                return -(arg0.getValue().compareTo(arg1.getValue()));
		            }
		        });
		}
//		for (InterestVo interest : userMap) {
//			System.out.println("get userinterest last day "+dayNumber+"  "+interest.toString());
//		}
		return userMap;
	}
	private static boolean compareTwoDate(Date d1,Date d2){
		boolean answer = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String s1,s2;
		s1 = sdf.format(d1);
		s2 = sdf.format(d2);
		if (s1.equals(s2)) {
			answer = true;
		}
		return answer;
	}
	private static void getUserMapData(int dayNumber,List<UserInterestValueEntity> temp,List<InterestVo> ret){
		switch (dayNumber) {
		case 0:
			for (UserInterestValueEntity interest : temp) {
				int cid = interest.getCategory_id();
				InterestVo item = new InterestVo();
				item.setName(CategoryInfo.parseStringS(cid));
				item.setValue(interest.getValue());
				ret.add(item);
			}
			break;
		case 1:
			Date tempDate = new Date();
			tempDate = temp.get(0).getDate();
			for (UserInterestValueEntity interest : temp) {
				if (compareTwoDate(tempDate, interest.getDate())) {
					int cid = interest.getCategory_id();
					InterestVo item = new InterestVo();
					item.setName(CategoryInfo.parseStringS(cid));
					item.setValue(interest.getValue());
					ret.add(item);
				}
			}
			break;
		case 2:
			Date tempDate1 = new Date();
			int mark = 0;
			tempDate1 = temp.get(0).getDate();
			for (UserInterestValueEntity interest : temp) {
				if (!compareTwoDate(tempDate1, interest.getDate())) {
					mark = 1;
					tempDate1 = interest.getDate();
					break;
				}
			}
			if (mark == 1) {
				for (UserInterestValueEntity interest : temp) {
					if (compareTwoDate(tempDate1, interest.getDate())) {
						int cid = interest.getCategory_id();
						InterestVo item = new InterestVo();
						item.setName(CategoryInfo.parseStringS(cid));
						item.setValue(interest.getValue());
						ret.add(item);
					}
				}
			}
			break;
		case 3:
			Date tempDate2 = new Date();
			int mark1 = 0;
			tempDate2 = temp.get(0).getDate();
			for (UserInterestValueEntity interest : temp) {
				if (!compareTwoDate(tempDate2, interest.getDate())) {
					++mark1;
					tempDate2 = interest.getDate();
					if (mark1 == 2) {
						break;
					}
				}
			}
			if (mark1 == 2) {
				for (UserInterestValueEntity interest : temp) {
					if (compareTwoDate(tempDate2, interest.getDate())) {
						int cid = interest.getCategory_id();
						InterestVo item = new InterestVo();
						item.setName(CategoryInfo.parseStringS(cid));
						item.setValue(interest.getValue());
						ret.add(item);
					}
				}
			}
			break;
		default:
			break;
		}
	}
	
	public static int updateUserInterestByInterestWords(Integer userid) {
		List<Interest> interestList = new ArrayList<Interest>();
		int amount = 0;
		GetDBData gd = new GetDBData();
		try {
			interestList = gd.findInterestsCXL(userid);
			amount = interestList == null ? 0 : interestList.size();
			for (Interest element : interestList) {

				UserInterestValueEntity entity = getEntity(userid,
						element.getClassificationID(), element.getDate());

				UserInterestValue ret = new UserInterestValue();
				ret.setValue(element.getValue());

				ret.setDate(element.getDate());
				
				User user = new User();
				user.setUserid(userid);
				ret.setUser(user);

				CategoryEntity tarEntity = UserInterestValueSwitcher
						.getCategoryDao().get(element.getClassificationID());
				db.hibernate.tables.isearch.Category tarCate = null;
				if (null != tarEntity)
					tarCate = CategorySwitcher.categoryPojoToEntity(tarEntity);
				ret.setCategory(tarCate);

				UserInterestValueEntity pojoInterest = UserInterestValueSwitcher
						.userinterestPojoToEntity(ret);

				if (entity != null) {
					update(pojoInterest);

				} else {
					add(pojoInterest);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return amount;
	}

}
