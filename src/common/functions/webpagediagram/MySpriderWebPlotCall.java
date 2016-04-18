package common.functions.webpagediagram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;

import server.info.config.CategoryInfo;
import server.info.config.LangEnvironment.LangEnv;
import db.dbhelpler.InterestVo;
import db.dbhelpler.UserInterestValueHelper;

/**兴趣网状图相关
 * ֩��ͼ
 * */
public class MySpriderWebPlotCall {
	/*public static void main(String args[]) {
		// ��SWING����ʾ
		JFrame jf = new JFrame();
		jf.add(erstelleSpinnenDiagramm(1));
		jf.pack();
		jf.setVisible(true);
		// ��JFreeChart����ΪͼƬ�����ļ�·����
		for (int i = 1; i < 4; i++) {
			saveAsFile("d:/JfreeChart/MySpiderWebPlot"+i+".png", 500, 400,i);
		}
	}*/

	public static JPanel erstelleSpinnenDiagramm(int userid,int dayNumber) {
		JFreeChart jfreechart = createChart(userid,dayNumber,LangEnv.en);
		ChartPanel chartpanel = new ChartPanel(jfreechart);
		return chartpanel;
	}

	public static void saveAsFile(String outputPath, int weight, int height,int userid,int dayNumber) {
		FileOutputStream out = null;
		try {
			File outFile = new File(outputPath);
			if (!outFile.getParentFile().exists()) {
				outFile.getParentFile().mkdirs();
			}
			out = new FileOutputStream(outputPath);

			// ����ΪPNG
			ChartUtilities.writeChartAsPNG(out, createChart(userid,dayNumber,LangEnv.en), weight, height);
			// ����ΪJPEG
			// ChartUtilities.writeChartAsJPEG(out, chart, 500, 400);
			out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

	public static JFreeChart createChart(int userid,int dayNumber,LangEnv lang) {
//		SpiderWebPlot spiderwebplot = new SpiderWebPlot(createDataset()); //SpiderWebPlot��Jfreechart�Դ���
		MySpiderWebPlot spiderwebplot = new MySpiderWebPlot(createDataset(userid,dayNumber,lang)); //����Jfreechart�Դ���SpiderWebPlot���̳�SpiderWebPlot��дMySpiderWebPlot
		JFreeChart jfreechart;
		if (lang==LangEnv.cn) {
			jfreechart = new JFreeChart("第"+dayNumber+"天的兴趣图",
					TextTitle.DEFAULT_FONT, spiderwebplot, false);
		}else{
			jfreechart = new JFreeChart("The Interest of Day "+dayNumber,
					TextTitle.DEFAULT_FONT, spiderwebplot, false);
		}
		LegendTitle legendtitle = new LegendTitle(spiderwebplot);
		legendtitle.setPosition(RectangleEdge.BOTTOM);
		jfreechart.addSubtitle(legendtitle);
		return jfreechart;
	}

	public static DefaultCategoryDataset createDataset(int userid,int dayNumber,LangEnv lang) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//		System.out.println("createdataset userid is :"+userid);
		String group;
		if (lang==LangEnv.cn) {
			group = "兴趣";
		}else{
			group = "Interests";
		}
		List<InterestVo> interests = UserInterestValueHelper.getUserInterestValIns(userid, dayNumber);
		double sum = 0;
		for (InterestVo interestVo : interests) {
			sum = sum + interestVo.getValue();
		}
		for (Iterator<InterestVo> it = interests.iterator(); it.hasNext();) {
			InterestVo interest = it.next();
			int index = CategoryInfo.parseStringI(interest.getName());
			String name = "";
			if (lang==LangEnv.cn) {
				name = CategoryInfo.getCNString(index);
			}else{
				name = CategoryInfo.getENString(index);
			}
			dataset.addValue(interest.getValue()/sum, group, name);
		}
		
		return dataset;
	}
}
