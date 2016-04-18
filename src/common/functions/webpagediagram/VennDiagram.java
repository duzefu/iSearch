package common.functions.webpagediagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import common.entities.searchresult.Result;

public class VennDiagram {
	private static double sumAllResult = 0.0;
	private static Map<String, Double> resultMap = new HashMap<String, Double>();
	private static List<String> legend = new ArrayList<String>();
	private static Map<Set<String>, Double> proportion = new HashMap<>();
	
	public static void GetSumAllResult(List<Result> result)
	{
	    //从后台获取不同结果的搜索比例
		Map<String, Double> searchEngine = new HashMap<String, Double>();   	
		searchEngine.put("搜狗", new Double(0.0));
		searchEngine.put("百度", new Double(0.0));
		searchEngine.put("必应", new Double(0.0));
		searchEngine.put("搜搜", new Double(0.0));
		searchEngine.put("有道", new Double(0.0));
		searchEngine.put("即刻", new Double(0.0));
		searchEngine.put("雅虎", new Double(0.0));
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
			if (r.getSource().contains("雅虎")) {
				double value = searchEngine.get("雅虎");
				value++;
				searchEngine.put("雅虎", value);
			}
		}
		
		//统计所有值
		Set<String> keySet = searchEngine.keySet();
		double sum = 0.0;
		for(String key : keySet)
			sum += searchEngine.get(key);
		
		sumAllResult = sum; 
	}
	
	private static void GetDataSet(List<Result> result) 
	{
		// 从后台获取不同结果的搜索比例
		Map<Set<String>, Double> searchEngine = new HashMap<Set<String>, Double>();
		Map<String, Double> retSearchEngine = new HashMap<>();

		for (Result r: result) {
			String key = r.getSource().replaceAll("\\(\\d+\\)", "");
			String[] engineKeys = key.split(" ");
			Set<Set<String>> allEnginesList = new HashSet<Set<String>>();
			if (engineKeys.length == 1) {
				Set<String> tmpSet = new HashSet<>();
				tmpSet.add(engineKeys[0]);
				allEnginesList.add(tmpSet);
			}else{
				for (int i = 1; i <= engineKeys.length; i++) {
					List<Set<String>> enginesList = combine(engineKeys, i);
					allEnginesList.addAll(enginesList);
				}
			}
			
			Iterator<Set<String>> it = allEnginesList.iterator();
			while (it.hasNext()) {
				Set<String> engines = it.next();
				if (searchEngine.containsKey(engines)) {
					double value = searchEngine.get(engines);
					value ++;
					searchEngine.put(engines, value);
				}else {
					searchEngine.put(engines, 1.0);
				}
			}
		}
		String[] temp = new String[1];
		Iterator<Map.Entry<Set<String>, Double>> it = searchEngine.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Set<String>, Double> entry = it.next();
			String[] engineName = entry.getKey().toArray(temp);
			String engName = StringUtils.join(engineName, " ");
			Double value = entry.getValue();
			retSearchEngine.put(engName, value);
		}
		resultMap = retSearchEngine;
			
//			if (searchEngine.containsKey(key)) {
//				double value = searchEngine.get(key);
//				value++;
//				searchEngine.put(key, value);
//			} else
//				searchEngine.put(key, 1.0);
//		}
//		resultMap =  searchEngine;
	}

	public static void InitData(List<Result> result)
	{
		sumAllResult = 0.0;
		resultMap.clear();
		legend.clear();
		GetSumAllResult(result);
		GetDataSet(result);
	}
	
	public static List<String> GetLegend() 
	{
		// 图例
		Set<String> keySet = resultMap.keySet();
		for (String key : keySet)
		{
			if(key.contains("搜狗")  && !legend.contains("搜狗"))
				legend.add("搜狗");
			if(key.contains("搜搜")  && !legend.contains("搜搜"))
				legend.add("搜搜");
			if(key.contains("百度")  && !legend.contains("百度"))
				legend.add("百度");
			if(key.contains("有道")  && !legend.contains("有道"))
				legend.add("有道");
			if(key.contains("必应")  && !legend.contains("必应"))
				legend.add("必应");
			if (key.contains("雅虎") && !legend.contains("雅虎")) {
				legend.add("雅虎");
			}
		}

		return legend;
	}

	public static Map<String, Double> GetValue() 
	{
		/*if (null == legend)
			return null;

		// 更换为ABC
		Set<String> keySet = resultMap.keySet();
		Map<String, String> pair = new HashMap<String, String>();//把搜索引擎对应到字母
		if(legend.size() >= 1)
			pair.put("A",  legend.get(0));
		if(legend.size() >= 2)
			pair.put("B",  legend.get(1));
		if(legend.size() >= 3)
			pair.put("C",  legend.get(2));


		// 把Map键替换为A,B,C
		Map<String, Double> returnValue = new HashMap<String, Double>();
		returnValue.put("A", new Double(0.0));
		returnValue.put("B", new Double(0.0));
		returnValue.put("C", new Double(0.0));
		returnValue.put("AB", new Double(0.0));
		returnValue.put("AC", new Double(0.0));
		returnValue.put("BC", new Double(0.0));
		returnValue.put("ABC", new Double(0.0));

		for (String key : keySet) {
			Double value = resultMap.get(key);
			if (pair.containsKey("A") && key.equals(pair.get("A"))) {//纯A
				returnValue.put("A", value);
			}
			else if (pair.containsKey("B") && key.equals(pair.get("B"))) {//纯B
				returnValue.put("B", value);
			} 
			else if (pair.containsKey("C") && key.equals(pair.get("C"))) {//纯C
				returnValue.put("C", value);
			} 
			
			if (pair.containsKey("A") && key.contains(pair.get("A"))
					&& pair.containsKey("B") && key.contains(pair.get("B"))
					&& pair.containsKey("C") && key.contains(pair.get("C")))
				returnValue.put("ABC", value);
			else if (pair.containsKey("A") && key.contains(pair.get("A"))
					&& pair.containsKey("B") && key.contains(pair.get("B")))
				returnValue.put("AB", value);
			else if (pair.containsKey("A") && key.contains(pair.get("A"))
					&& pair.containsKey("C") && key.contains(pair.get("C")))
				returnValue.put("AC", value);
			else if (pair.containsKey("B") && key.contains(pair.get("B"))
					&& pair.containsKey("C") && key.contains(pair.get("C")))
				returnValue.put("BC", value);
			
		}

		//对应相加
		returnValue.put("A", returnValue.get("A") + returnValue.get("AB") + returnValue.get("AC") + returnValue.get("ABC"));
		returnValue.put("B", returnValue.get("B") + returnValue.get("AB") + returnValue.get("BC") + returnValue.get("ABC"));
		returnValue.put("C", returnValue.get("C") + returnValue.get("AC") + returnValue.get("BC") + returnValue.get("ABC"));
		returnValue.put("AB", returnValue.get("AB") + returnValue.get("ABC"));
		returnValue.put("AC", returnValue.get("AC") + returnValue.get("ABC"));
		returnValue.put("BC", returnValue.get("BC") + returnValue.get("ABC"));
		
		returnValue.put("A", returnValue.get("A") / sumAllResult);
		BigDecimal roundDouble = new BigDecimal(returnValue.get("A"));
		returnValue.put("A", roundDouble.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue());

		returnValue.put("B", returnValue.get("B") / sumAllResult);
		roundDouble = new BigDecimal(returnValue.get("B"));
		returnValue.put("B", roundDouble.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue());

		returnValue.put("C", returnValue.get("C") / sumAllResult);
		roundDouble = new BigDecimal(returnValue.get("C"));
		returnValue.put("C", roundDouble.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue());

		returnValue.put("AB", returnValue.get("AB") / sumAllResult);
		roundDouble = new BigDecimal(returnValue.get("AB"));
		returnValue.put("AB", roundDouble.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue());

		returnValue.put("AC", returnValue.get("AC") / sumAllResult);
		roundDouble = new BigDecimal(returnValue.get("AC"));
		returnValue.put("AC", roundDouble.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue());

		returnValue.put("BC", returnValue.get("BC") / sumAllResult);
		roundDouble = new BigDecimal(returnValue.get("BC"));
		returnValue.put("BC", roundDouble.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue());

		returnValue.put("ABC", returnValue.get("ABC") / sumAllResult);
		if(Math.abs(returnValue.get("AB")-returnValue.get("ABC"))<0.01 ||Math.abs(returnValue.get("AC") - returnValue.get("ABC"))<0.01 || Math.abs(returnValue.get("BC") - returnValue.get("ABC"))<0.01)
		{
			double minValue = Math.min(Math.min(returnValue.get("AB") , returnValue.get("AC")), returnValue.get("BC"));
			if(minValue > 0 && minValue <= 0.01)
				returnValue.put("ABC", 0.005);
			else
				returnValue.put("ABC", minValue / 2);
		}
		
		roundDouble = new BigDecimal(returnValue.get("ABC"));
		returnValue.put("ABC", roundDouble
				.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());*/

		return resultMap;
	}
	
	public static List<Set<String>> combine(String[] engines, int k)
	{
		List<Set<String>> ret = new ArrayList<>();
		
		int n = engines.length;
		
		if (k>n) {
			return null;
		}
		
		int[] c = new int[n];
		//Initial
		for (int i = 0; i < n; i++) {
			c[i] = 0;
		}
		for (int i = 0; i < k; i++) {
			c[i] = 1;
		}
		
		boolean flag = true;
		boolean tempflag =false;
		int sum = 0;
		int pos = 0;
		
		while (flag) {
			sum=0;
			pos=0;
			tempflag = true;
			
			ret.add(getResult(c, engines, k));	
			
			for (int i = 0; i < c.length -1; i++) {
				if(c[i] == 1 && c[i+1] == 0){
					c[i] = 0;
					c[i+1] = 1;
					pos = i;
					break;
				}
			}
			for (int i = 0; i < pos; i++) {
				if(c[i] == 1){
					sum++;
				}
			}
			for (int i = 0; i < pos; i++) {
				if (i < sum) {
					c[i] = 1;
				}else{
					c[i] = 0;
				}
			}
			for (int i = n-k; i < c.length; i++) {
				if (c[i] == 0) {
					tempflag = false;
					break;
				}
			}
			if (tempflag == false) {
				flag = true;
			}else{
				flag = false;
			}
		}
		ret.add(getResult(c, engines, k));
		
		
		return ret;
	}

	private static Set<String> getResult(int[] c, String[] engines, int k)
	{
		Set<String> ret = new HashSet<>();
		
		for (int i = 0; i < c.length; i++) {
			if(c[i] == 1){
				ret.add(engines[i]);
			}
		}
		
		
		return ret;
	}
}
