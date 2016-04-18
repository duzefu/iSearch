package common.functions.webpagediagram;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.entities.searchresult.Result;
import common.functions.schedule.EngineScore;

public class CoolDynamicEngineScore {
	public static EngineScore[] engineScore = new EngineScore[3];
	
	public static List<String> GetDataset(List<Result> result) 
	{   
		if(null == engineScore || null == result || result.isEmpty()) return null;
	    List<String> returnList = new ArrayList<String>();
		//从后台获取不同结果的搜索比例
		Map<String, Double> searchEngine = new HashMap<String, Double>();   	
		for(EngineScore es : engineScore)
			searchEngine.put(es.getName(), es.getScore());
		
		Set<String> keySet = searchEngine.keySet();
		
		//设置条状图要显示的数据
		List<String> engineNames = GetSearchEngine(result);
		
		for(String key : keySet)
			if(Math.abs(searchEngine.get(key) - 0.0) > 0.0001)
			{
				BigDecimal roundDouble = new BigDecimal(searchEngine.get(key));
				double newDouble = roundDouble.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				if(key.equals("baidu") && engineNames.contains(new String("百度")))
					returnList.add(new String("百度" + "\n" + newDouble));
				else if(key.equals("soso") && engineNames.contains(new String("搜搜")))
					returnList.add(new String("搜搜" + "\n" + newDouble));
				else if(key.equals("bing") && engineNames.contains(new String("必应")))
					returnList.add(new String("必应" + "\n" + newDouble));
				else if(key.equals("sogou") && engineNames.contains(new String("搜狗")))
					returnList.add(new String("搜狗" + "\n" + newDouble));
				else if(key.equals("youdao") && engineNames.contains(new String("有道")))
					returnList.add(new String("有道" + "\n" + newDouble));
			}
		Collections.sort(returnList, new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) 
			{
				// TODO Auto-generated method stub
				if(Double.parseDouble(o2.substring(3))-Double.parseDouble(o1.substring(3)) > 0.0001)
					return 1;
				else if(Double.parseDouble(o2.substring(3))-Double.parseDouble(o1.substring(3)) < -0.0001)
					return -1;
				else
					return 0;
			}});
			
	    return returnList;
	}   
	    
	private static List<String> GetSearchEngine(List<Result> result)
	{
		List<String> engineNames = new ArrayList<String>();
		Map<String, Double> searchEngine = new HashMap<String, Double>();   	
		searchEngine.put("搜狗", new Double(0.0));
		searchEngine.put("百度", new Double(0.0));
		searchEngine.put("必应", new Double(0.0));
		searchEngine.put("搜搜", new Double(0.0));
		searchEngine.put("有道", new Double(0.0));
		searchEngine.put("即刻", new Double(0.0));
		for(Result r : result)
		{
			if(r.getSource().contains("搜狗"))
			{
				double value = searchEngine.get("搜狗");
				value++;
				searchEngine.put("搜狗", value);
			}
			if(r.getSource().contains("百度"))
			{
				double value = searchEngine.get("百度");
				value++;
				searchEngine.put("百度", value);
			}
			if(r.getSource().contains("必应"))
			{
				double value = searchEngine.get("必应");
				value++;
				searchEngine.put("必应", value);
			}
			if(r.getSource().contains("搜搜"))
			{
				double value = searchEngine.get("搜搜");
				value++;
				searchEngine.put("搜搜", value);
			}
			if(r.getSource().contains("有道"))
			{
				double value = searchEngine.get("有道");
				value++;
				searchEngine.put("有道", value);
			}
			if(r.getSource().contains("即刻"))
			{
				double value = searchEngine.get("即刻");
				value++;
				searchEngine.put("即刻", value);
			}
		}
		
		Set<String> keySet = searchEngine.keySet();
		for(String key : keySet)
			if(Math.abs(searchEngine.get(key) - 0.0) > 0.001)
				engineNames.add(key);
		return engineNames;
	}
}
