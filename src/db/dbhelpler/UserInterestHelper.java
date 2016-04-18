package db.dbhelpler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.ui.context.Theme;

import com.ibm.icu.text.SimpleDateFormat;

import common.entities.blackboard.GenerateUserGroup;
import common.entities.blackboard.Interest;
import common.textprocess.userXMLfilehelpler.GetDBData;
import db.dao.UserInterestValueDao;
import db.entityswithers.CategorySwitcher;
import db.entityswithers.UserInterestValueSwitcher;
import db.hibernate.tables.isearch.User;
import db.hibernate.tables.isearch.UserInterestValue;
import server.commonutils.MyStringChecker;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.CategoryInfo;
import server.info.config.SpringBeanNames;
import server.info.config.CategoryInfo.Category;
import server.info.entites.transactionlevel.CategoryEntity;
import server.info.entites.transactionlevel.UserInterestEntity;
import server.info.entites.transactionlevel.UserInterestValueEntity;

public class UserInterestHelper {
	private UserInterestValueDao interestdao;
	private Comparator<Entry<Category, Double>> m_cmpDesc;
	private Comparator<Entry<Category, Double>> m_cmpAsc;

	// 单例模式相关
	private static UserInterestHelper instance;

	private static UserInterestHelper getInstance() {
		if (null == instance) {
			synchronized (UserInterestHelper.class) {
				if (null == instance) {
					instance = new UserInterestHelper();
				}
			}
		}
		return instance;
	}

	private UserInterestHelper() {
		interestdao = (UserInterestValueDao) SpringBeanFactoryUtil
				.getBean(SpringBeanNames.USER_INTEREST_VALUE_DAO_BEAN_NAME);

		m_cmpDesc = new Comparator<Map.Entry<Category, Double>>() {
			@Override
			public int compare(Entry<Category, Double> o1,
					Entry<Category, Double> o2) {
				double diff = o1.getValue() - o2.getValue();
				if (diff < 0)
					return 1;
				if (diff > 0)
					return -1;
				return 0;
			}
		};

		m_cmpAsc = new Comparator<Map.Entry<Category, Double>>() {
			@Override
			public int compare(Entry<Category, Double> o1,
					Entry<Category, Double> o2) {
				double diff = o1.getValue() - o2.getValue();
				if (diff < 0)
					return -1;
				if (diff > 0)
					return 1;
				return 0;
			}
		};
	}

	private static UserInterestValueDao getInterestDao() {
		return getInstance().interestdao;
	}

	/**
	 * 获取用户兴趣信息，降序排序返回，这里UserInterestEntity的内容是相应类别兴趣值占整体兴趣值的百分比
	 * 
	 * @param ret
	 *            用于返回数据
	 * @param username
	 *            用户名
	 * @return 返回ret中新增元素个数
	 */
	public static int getDescSortedInterestPercent(
			List<UserInterestEntity> ret, String username) {

		return getDescSortedInterestPercent(ret, username, -1);
	}

	/**
	 * 获取用户兴趣信息，降序排序返回，这里UserInterestEntity的内容是相应类别兴趣值占整体兴趣值的百分比
	 * 
	 * @param ret
	 *            用于返回数据
	 * @param username
	 *            用户名
	 * @param limit
	 *            限制返回结果的数量，传入不大于0的数将不限制
	 * @return 返回ret中新增元素个数
	 */
	public static int getDescSortedInterestPercent(
			List<UserInterestEntity> ret, String username, int limit) {

		int userid = UserHelper.getUserIDByUsername(username);
		return getDescSortedInterestPercent(ret, userid, limit);
	}

	/**
	 * 获取用户兴趣信息，降序排序返回，这里UserInterestEntity的内容是相应类别兴趣值占整体兴趣值的百分比
	 * 
	 * @param ret
	 *            用于返回数据
	 * @param userid
	 *            用户ID
	 * @return 返回ret中新增元素个数
	 */
	public static int getDescSortedInterestPercent(
			List<UserInterestEntity> ret, int userid) {

		return getDescSortedInterestPercent(ret, userid, -1);
	}

	/**
	 * 获取用户兴趣信息，降序排序返回，这里UserInterestEntity的内容是相应类别兴趣值占整体兴趣值的百分比
	 * 
	 * @param ret
	 *            用于返回数据
	 * @param userid
	 *            用户ID
	 * @param limit
	 *            限制返回结果的数量，传入不大于0的数将不限制
	 * @return 返回ret中新增元素个数
	 */
	public static int getDescSortedInterestPercent(
			List<UserInterestEntity> ret, int userid, int limit) {

		return getSortedInterestPercent(ret, userid, limit, true);
	}

	/**
	 * 获取用户兴趣信息，排序后返回，这里UserInterestEntity的内容是相应类别兴趣值占整体兴趣值的百分比
	 * 
	 * @param ret
	 *            用于返回数据
	 * @param userid
	 *            用户ID
	 * @param limit
	 *            限制返回结果的数量，传入不大于0的数将不限制
	 * @param isDesc
	 *            为true时表示返回前的排序方式是降序，false为升序
	 * @return 返回ret中新增元素个数
	 */
	public static int getSortedInterestPercent(List<UserInterestEntity> ret,
			int userid, int limit, boolean isDesc) {

		return getInstance().getSortedInterestPercentIns(ret, userid, limit,
				isDesc);
	}

	/**
	 * 获得用户的兴趣信息（所有的）
	 * 
	 * @param ret
	 *            用于返回数据，这里的double应该是原始数据值， 这个函数是getUserInterestValIns的入口
	 * @param username
	 *            用户名
	 * @return 获得的用户信息数量，ret中新增元素个数
	 */
	public static int getUserInterestVal(Map<Category, Double> ret,
			String username) {

		if (MyStringChecker.isBlank(username) || null == ret)
			return 0;
		int userid = UserHelper.getUserIDByUsername(username);
		return getUserInterestVal(ret, userid);
	}

	/**
	 * 获得用户的兴趣信息（所有的）
	 * 
	 * @param ret
	 *            用于返回数据，这里的double应该是原始数据值， 这个函数是getUserInterestValIns的入口
	 * @param userid
	 *            用户ID
	 * @return 获得的用户信息数量，ret中新增元素个数
	 */
	public static int getUserInterestVal(Map<Category, Double> ret, int userid) {

		return getInstance().getUserInterestValIns(ret, userid);
	}

	/************* 以下是实例函数 **************/

	/**
	 * 获取用户兴趣信息，排序后返回，这里UserInterestEntity的内容是相应类别兴趣值占整体兴趣值的百分比
	 * 
	 * @param ret
	 *            用于返回数据
	 * @param userid
	 *            用户ID
	 * @param limit
	 *            限制返回结果的数量，传入不大于0的数将不限制
	 * @param isDesc
	 *            为true时表示返回前的排序方式是降序，false为升序
	 * @return 返回ret中新增元素个数
	 */
	private int getSortedInterestPercentIns(List<UserInterestEntity> ret,
			int userid, int limit, final boolean isDesc) {

		if (null == ret || !UserHelper.isLoginUser(userid))
			return 0;
//		List<UserInterestValueEntity> temp = new ArrayList<UserInterestValueEntity>();;
//		temp = getEntitys(userid);
		List<InterestVo> tempI = getUserInterestValIns(userid, 1);
		
		Collections.sort(tempI, new Comparator<InterestVo>(){

			@Override
			public int compare(InterestVo o1, InterestVo o2) {
				double diff = o1.getValue() - o2.getValue();
				if (diff<0) {
					return 1;
				}else if(diff>0){
					return -1;
				}else{
					return 0;
				}
			}});
		
		changeToPercentCXL(tempI);
		
		if (limit <= 0)
			limit = Integer.MAX_VALUE;
		int amount = 0;
		for (int i = 0; i < tempI.size() && amount < limit; i++) {
			InterestVo vo = tempI.get(i);
			int cid = CategoryInfo.parseStringI(vo.getName());
			double value = vo.getValue();
			if (value<0.000001) {
				continue;
			}
//			System.out.println("percent interestvo id is "+cid +" name :"+vo.getName());
			ret.add(new UserInterestEntity(CategoryInfo.parseString(vo.getName()),vo.getValue()));
			++amount;
		}
		
		return amount;
	}

	private void changeToPercentCXL(List<InterestVo> temp) {
		if (null == temp)
			return;
		double sum = 0.0;
		for (InterestVo interest : temp) {
			sum = sum + interest.getValue();
		}
		if (Math.abs(sum) < 0.000001)
			return;// 所有兴趣值都是0；除非兴趣值有负数，那样百分比就不能这样计算了
		for (InterestVo interest : temp) {
			interest.setValue(interest.getValue()/sum);
		}
	}

	/**
	 * 把兴趣值转为百分比（0-1）值
	 * 
	 * @param data
	 */
	private void changeToPercent(Map<Category, Double> data) {

		if (null == data)
			return;
		double sum = 0.0;
		for (Iterator<Entry<Category, Double>> iter = data.entrySet()
				.iterator(); iter.hasNext();) {
			Double dval = iter.next().getValue().doubleValue();
			sum += null == dval ? 0.0 : dval.doubleValue();
		}
		if (Math.abs(sum) < 0.000001)
			return;// 所有兴趣值都是0；除非兴趣值有负数，那样百分比就不能这样计算了
		for (Iterator<Entry<Category, Double>> iter = data.entrySet()
				.iterator(); iter.hasNext();) {
			Entry<Category, Double> entry = iter.next();
			entry.setValue(entry.getValue() / sum);
		}
	}

	/**
	 * 获得用户的兴趣信息（所有的）
	 * 
	 * @param ret
	 *            用于返回数据，这里的double应该是原始数据值，
	 *            暂时不必转为百分比，利于重用，这个函数应该直接面对数据库层，被其他函数调用
	 * @param userid
	 *            用户ID
	 * @return 获得的用户信息数量，ret中新增元素个数
	 */
	private int getUserInterestValIns(Map<Category, Double> ret, int userid) {

		// TODO 这是临时做法，以后兴趣由数据库来记录时，应该修改写法
		if (!UserHelper.isLoginUser(userid) || null == ret)
			return 0;
		Map<String, String> userMap = GenerateUserGroup
				.getClassifyWeight(Integer.toString(userid));
		if (null == userMap || userMap.isEmpty())
			return 0;
		int amount = 0;
		for (Iterator<Entry<String, String>> it = userMap.entrySet().iterator(); it
				.hasNext();) {
			Entry<String, String> entry = it.next();
			ret.put(CategoryInfo.parseString(entry.getKey()),
					Double.valueOf(entry.getValue()));
			++amount;
		}
		return amount;
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
		List<UserInterestValueEntity> temp = null;
		List<InterestVo> userMap = new ArrayList<InterestVo>();
		
		if(!UserHelper.isLoginUser(userid)) return userMap;
		
		temp = getEntitys(userid);
		if (temp != null) {
			amount = temp.size();
		}else{
			return userMap;
		}
		
		Collections.sort(temp, new Comparator<UserInterestValueEntity>() {
			@Override
			public int compare(UserInterestValueEntity arg0,
					UserInterestValueEntity arg1) {
				double diff = arg0.getDate().getTime()
						- arg1.getDate().getTime();
				if (diff < 0)
					return 1;
				if (diff > 0)
					return -1;
				return 0;
			}
		});
		if (amount > 0) {
			getUserMapData(dayNumber, temp, userMap);
			Collections.sort(userMap, new Comparator<InterestVo>() {
				public int compare(InterestVo arg0, InterestVo arg1) {
					return -(arg0.getValue().compareTo(arg1.getValue()));
				}
			});
		}
//		for (InterestVo interest : userMap) {
//			System.out.println("get userinterest last day " + dayNumber + "  "
//					+ interest.toString());
//		}
		return userMap;
	}

	private static boolean compareTwoDate(Date d1, Date d2) {
		boolean answer = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String s1, s2;
		s1 = sdf.format(d1);
		s2 = sdf.format(d2);
		if (s1.equals(s2)) {
			answer = true;
		}
		return answer;
	}

	private static void getUserMapData(int dayNumber,
			List<UserInterestValueEntity> temp, List<InterestVo> ret) {
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
				;
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
