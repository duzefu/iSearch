package common.functions.webpagediagram;

import java.awt.Font;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import common.entities.blackboard.GenerateUserGroup;

public class BarChartPainter {
	
	public static int userID;
	
	private static CategoryDataset GetDataset() //创建柱状图数据集
	{
		Map<String, String> userMap = GenerateUserGroup.getClassifyWeight(Integer.toString(userID));
		Set<String> keySet = userMap.keySet();
		DefaultCategoryDataset dataset=new DefaultCategoryDataset();
		
		double sum = 0.0;
		for(String key : keySet)
			sum += Double.parseDouble(userMap.get(key));
		
		dataset.setValue(Double.parseDouble(userMap.get("education")) / sum, "教育", "教育");
		dataset.setValue(Double.parseDouble(userMap.get("it"))/ sum, "IT", "IT");
		dataset.setValue(Double.parseDouble(userMap.get("employment"))/ sum, "职业", "职业");
		dataset.setValue(Double.parseDouble(userMap.get("financial"))/ sum, "金融", "金融");
		dataset.setValue(Double.parseDouble(userMap.get("health"))/ sum, "健康", "健康");
		dataset.setValue(Double.parseDouble(userMap.get("literature"))/ sum, "文学", "文学");
		dataset.setValue(Double.parseDouble(userMap.get("military"))/ sum, "军事", "军事");
		dataset.setValue(Double.parseDouble(userMap.get("sports"))/ sum, "体育", "体育");
		dataset.setValue(Double.parseDouble(userMap.get("tourism"))/ sum, "旅游", "旅游");
		//dataset.setValue(Double.parseDouble(userMap.get("automobile"))/ sum, "汽车", "汽车");
		
		
	
	    return dataset;
	}
	
	public static JFreeChart GetBarChart() //用数据集创建一个图表
	{
	    JFreeChart chart = ChartFactory.createBarChart(
	    		"hi", "用户兴趣", "兴趣度", 
	    		BarChartPainter.GetDataset(), PlotOrientation.VERTICAL, 
	    		true, true, false); //创建一个JFreeChart
	    
	    chart.setTitle(new TextTitle("用户近期兴趣变迁", new Font("黑体",Font.BOLD + Font.ITALIC,20)));//可以重新设置标题，替换“hi”标题
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