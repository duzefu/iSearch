package common.functions.recommendation.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;
import db.dao.UserGroupDao;

public class UserGroupDivider {

	private static UserGroupDivider ugdivider;

	private UserGroupDivider() {
	}

	public static UserGroupDivider getInstance() {

		if (null == ugdivider) {
			synchronized (UserGroupDivider.class) {
				ugdivider = new UserGroupDivider();
			}
		}

		return ugdivider;
	}

	private UserGroupDao ugDao;
	private static final int MAX_GROUP_COUNT=4;
	
	private UserGroupDao getUgDao() {

		if (null == ugDao){
			synchronized (this) {
				ugDao = (UserGroupDao) SpringBeanFactoryUtil
						.getBean(SpringBeanNames.USER_GROUP_DAO_BEAN_NAME);
			}
		}
		return ugDao;
	}
	/**
	 * 根据用户的文件中拿到的类别数字列表，来拿到group_to_category的groupid
	 * @param cateToWeight 根据用户文件拿到的类别数字列表
	 * @param ret 存放群组的id
	 * @return 
	 */
	public boolean getUserGroupID(Map<String, Double> cateToWeight, Set<Integer> ret){
		
		if(null==cateToWeight||null==ret) return false;
		//对主题-权重Map按照权重从大到小排序
		List<Entry<String, Double>> cateToWeightList=new ArrayList<Entry<String,Double>>(cateToWeight.entrySet());
		sortUserInterestMap(cateToWeightList);
		
		//根据权重排序，获取用户兴趣最高的三个主题的英文名称
		Set<String> interestClassSet=new HashSet<String>();
		fillUserInterestClassNames(interestClassSet, cateToWeightList);
		
		//拿到主题英文名对应的群组id，如果对应的群组信息不存在，则在数据库中添加
		UserGroupDao ugDao=getUgDao();
		Set<Integer> gidSet=ugDao.addUserGroupInfo(interestClassSet);
		if(null!=gidSet) ret.addAll(gidSet);
		
		return true;
	}
	/**
	 * 对拿到的类别以及对应的权重，进行排序
	 * @param mentryList 从大到小的方式排序
	 */
	public static void sortUserInterestMap(List<Entry<String, Double>> mentryList){
		
		if(null==mentryList||mentryList.isEmpty()) return;
		Collections.sort(mentryList, new Comparator<Entry<String, Double>>(){

			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				double v1=o1.getValue(), v2=o2.getValue();
				if(v1<v2) return 1;
				if(v1>v2) return -1;
				return 0;
			}
			
		});
	}
	/**
	 * 将从大到小排好序的用户文件主题和对应的权重值，从前面选择三个不重复的返回
	 * @param ret 前三个主题
	 * @param categoryToWeightList 主题，权重map表（已排序的）
	 */
	private static void fillUserInterestClassNames(Set<String> ret, List<Entry<String, Double>> categoryToWeightList){
		
		if(null==ret||null==categoryToWeightList) return;
		
		int count=0;
		Iterator<Entry<String, Double>> iter=categoryToWeightList.iterator();
		while(iter.hasNext()&&count<MAX_GROUP_COUNT){
			Entry<String, Double> pair=iter.next();
			if(null==pair) continue;
			if(Math.abs(pair.getValue())<0.000001) break;
			String className=pair.getKey();
			if(!ret.contains(className)){
				ret.add(className);
				++count;
			}
		}
		
		return;
	}
	
}
