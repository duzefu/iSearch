package common.functions.webpagediagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import server.info.config.CategoryInfo.Category;
import server.info.entites.transactionlevel.UserInterestEntity;
import common.entities.blackboard.GenerateUserGroup;
import db.dbhelpler.UserHelper;
import db.dbhelpler.UserInterestHelper;

public class CoolDynamicBar {
	
	public static List<Entry<String, Double>> GetDataset(int userid, String username)
	{
		if(null == username || username.equals("")) return null; //判断userID非法
		Map<String, String> userMap = GenerateUserGroup.getClassifyWeight(Integer.toString(userid));
		Set<String> keySet = userMap.keySet();
		Map<String, Double> interestMap = new HashMap<String, Double>();
		double sum = 0.0;
		String dstr=null;
		for(String key : keySet){
			dstr=userMap.get(key);
			if(null==dstr||dstr.isEmpty()) userMap.put(key, "0");
			sum+=null==dstr?0:Double.parseDouble(dstr);
		}
		
		if(Math.abs(sum)<0.000001) sum=1;
		//给interestMap赋值
		interestMap.put("教育", Double.parseDouble(userMap.get("education")) / sum); 
		interestMap.put("IT", Double.parseDouble(userMap.get("it")) / sum); 
		interestMap.put("就业", Double.parseDouble(userMap.get("employment")) / sum); 
		interestMap.put("金融", Double.parseDouble(userMap.get("financial")) / sum); 
		interestMap.put("健康", Double.parseDouble(userMap.get("health")) / sum); 
		interestMap.put("文学", Double.parseDouble(userMap.get("literature")) / sum); 
		interestMap.put("军事", Double.parseDouble(userMap.get("military")) / sum); 
		interestMap.put("旅游", Double.parseDouble(userMap.get("tourism")) / sum); 
		interestMap.put("体育", Double.parseDouble(userMap.get("sports")) / sum); 
		

		//排序
		List<Map.Entry<String, Double>> infoIds = new ArrayList<Map.Entry<String, Double>>(
				interestMap.entrySet()); 
		
		for (int i = 0; i < infoIds.size(); i++) 
		    System.out.println(infoIds.get(i).toString());
		
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
			Map.Entry<String, Double> o2) {
				if(o2.getValue() - o1.getValue() > 0.0001)
					return 1;
				else if(o2.getValue() - o1.getValue() < -0.0001)
					return -1;
				return 0;
			}});
		
		//仅仅添加排名靠前的元素valCount控制
		int valCount = 4;
		List<Entry<String, Double>> returnList = new ArrayList<Entry<String, Double>>();
		for (int i = 0; i < valCount; i++)
			if(infoIds.get(i).getValue()>0)
			   returnList.add(infoIds.get(i));
	    return returnList;
	}
}
