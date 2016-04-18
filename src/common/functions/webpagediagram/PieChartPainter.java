package common.functions.webpagediagram;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import common.entities.searchresult.Result;
import common.functions.resultanalysis.ResultProportionAnalysis;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartColor;

import server.engine.api.EngineFactory;
import server.engine.api.EngineFactory.EngineName;
import server.info.config.VisibleConstant;
import server.info.config.LangEnvironment.LangEnv;

public class PieChartPainter {
	
	private static DefaultPieDataset GetDataSet(List<Result> result, LangEnv lang)
	{
		DefaultPieDataset dpd = new DefaultPieDataset();
	    Map<EngineName, Double> data=new HashMap<EngineName, Double>();
	    ResultProportionAnalysis.getResultDistributionRate(data, result);
		Iterator<Entry<EngineName, Double>> it=data.entrySet().iterator();
		while(it.hasNext()){
			Entry<EngineName, Double> entry=it.next();
			double val=entry.getValue();
			if(Math.abs(val)>0.000001) dpd.setValue(VisibleConstant.getStrEngNameWeb(entry.getKey(),lang), val);
		}
		
	    return dpd;
	}
	
	public static JFreeChart GetPieChart(List<Result> result, LangEnv lang)
	{
		//可以查具体的API文档,第一个参数是标题，第二个参数是一个数据集，
		//第三个参数表示是否显示Legend，第四个参数表示是否显示提示，第五个参数表示图中是否存在URL
		DefaultPieDataset dataSet = GetDataSet(result, lang);
		JFreeChart chart = ChartFactory.createPieChart("", dataSet, false, false, false);
		
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setBackgroundPaint(ChartColor.WHITE);
		plot.setInteriorGap(0.000001);
		plot.setOutlinePaint(ChartColor.WHITE); // 设置绘图面板外边的填充颜色
		plot.setShadowPaint(null);
		//plot.setBackgroundPaint(ChartColor.WHITE);//标签边框颜色
		//{0}={1}({2})
		plot.setLabelGenerator(
				new StandardPieSectionLabelGenerator( "{0}:{2}", 
						NumberFormat.getNumberInstance(),
						new DecimalFormat("0.0%")));
		// 图例显示百分比:自定义方式， {0} 表示选项， {1} 表示数值， {2} 表示所占比例
		plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0}"));
		//设置字体，否则乱码
		
		//chart.getTitle().setFont(new Font("微软雅黑",Font.PLAIN,25));//设置标题字体
		//chart.getTitle().setHorizontalAlignment(HorizontalAlignment.LEFT);
		
		PiePlot piePlot= (PiePlot) chart.getPlot();//获取图表区域对象
		piePlot.setLabelFont(new Font("微软雅黑",Font.PLAIN,18));
		piePlot.setLabelBackgroundPaint(ChartColor.WHITE);
		//piePlot.setInteriorGap(0.00000000000001);
		//chart.getLegend().setItemFont(new Font("微软雅黑",Font.PLAIN,25));
		List<String> keys = dataSet.getKeys();  
		for (Iterator<String> it=keys.iterator();it.hasNext();) {
			String key=it.next();
			 Paint color = EngineFactory.getShowColorOfEngine(key);
			 plot.setSectionPaint(key, color);
		}
            
		return chart;
	}
}
