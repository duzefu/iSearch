package common.functions.webpagediagram;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import common.entities.searchresult.Result;
import common.functions.schedule.EngineScore;

public class SearchEngineScore {
	
	public static EngineScore[] engineScore;
	private static DefaultCategoryDataset GetDataset(List<Result> result) 
	{   
		DefaultCategoryDataset dpd = new DefaultCategoryDataset();
	    //从后台获取不同结果的搜索比例
		Map<String, Double> searchEngine = new HashMap<String, Double>();   	
		
		
		Arrays.sort(engineScore, new Comparator<EngineScore>(){

			@Override
			public int compare(EngineScore o1, EngineScore o2) {
				// TODO Auto-generated method stub
				if(o2.getScore()-o1.getScore()>0)
					return 1;
				else if(o2.getScore()-o1.getScore()<0)
					return -1;
				else
					return 0;
				//return o2.getScore()-o1.getScore()>0?1:-1;
			}
			
		});
		for(EngineScore es : engineScore)
			searchEngine.put(es.getName(), es.getScore());
		
		Set<String> keySet = searchEngine.keySet();
		//设置条状图要显示的数据
		List<String> engineNames = GetSearchEngein(result);
		for(String key : keySet)
			if(Math.abs(searchEngine.get(key) - 0.0) > 0.0001)
			{
				if(key.equals("baidu") && engineNames.contains(new String("百度")))
					dpd.setValue(searchEngine.get(key), "百度", "百度");
				else if(key.equals("soso") && engineNames.contains(new String("搜搜")))
					dpd.setValue(searchEngine.get(key), "搜搜", "搜搜");
				else if(key.equals("bing") && engineNames.contains(new String("必应")))
					dpd.setValue(searchEngine.get(key), "必应", "必应");
				else if(key.equals("sogou") && engineNames.contains(new String("搜狗")))
					dpd.setValue(searchEngine.get(key), "搜狗", "搜狗");
				else if(key.equals("youdao") && engineNames.contains(new String("有道")))
					dpd.setValue(searchEngine.get(key), "有道", "有道");
			}
		
	    return dpd;
	}   
	    
	private static List<String> GetSearchEngein(List<Result> result)
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
	
	private static boolean IsContained(List<String> engineNames, String str)
	{
		for(String tmp : engineNames)
			if(tmp.equals(str))
				return true;
		return false;
	}
	public static JFreeChart GetChartScore(List<Result> result) 
	{   
		 JFreeChart chart = ChartFactory.createBarChart(
		    		"搜索引擎能力评价", "搜索引擎", "能力评价", 
		    		SearchEngineScore.GetDataset(result), PlotOrientation.HORIZONTAL, 
		    		true, true, false); //创建一个JFreeChart
		    
		    chart.setTitle(new TextTitle("搜索引擎能力评价", new Font("黑体",Font.BOLD + Font.ITALIC,20)));//可以重新设置标题，替换“hi”标题
		    CategoryPlot plot=(CategoryPlot)chart.getPlot();//获得图标中间部分，即plot
		    
		    CategoryAxis categoryXAxis=plot.getDomainAxis();//获得横坐标
		    categoryXAxis.setLabelFont(new Font("微软雅黑",Font.BOLD,25));//设置横坐标字体
		    categoryXAxis.setTickLabelFont(new Font("微软雅黑",Font.BOLD,25));	    //水平底部标题
		    ValueAxis valueYAxis = plot.getRangeAxis(); //获得纵坐标
		    valueYAxis.setLabelFont(new Font("微软雅黑",Font.BOLD,25));
		    chart.getLegend().setItemFont(new Font("微软雅黑", Font.PLAIN, 25));
		    return chart;
	}
}
