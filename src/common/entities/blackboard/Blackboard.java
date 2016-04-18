package common.entities.blackboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import common.entities.blackboard.Classification;
import server.commonutils.LogU;
import server.info.config.CategoryInfo;
import server.info.config.CategoryInfo.Category;

public class Blackboard {

	private static List<Classification> classification;
	// 将黑板按照单例模式设计
	private static Blackboard blackboard;

	private Blackboard() {
		setClassification();
	}

	public static Blackboard getInstance() {

		if (null == blackboard)
			blackboard = new Blackboard();
		return blackboard;
	}

	public static List<Classification> getClassification() {
		if (classification == null)
			classification = new ArrayList<Classification>();
		return classification;
	}

	public static void setClassification() {

		Set<String> allCategory=new HashSet<String>();
		CategoryInfo.getAllCategoryEnName(allCategory);
		int count=0;
		for (Iterator<String> it=allCategory.iterator();it.hasNext();) {
			Classification cf = new Classification();
			List<Classification> listCla = Blackboard.getClassification();
			listCla.add(count, cf);
			listCla.get(count).setClassifiName(it.next());
			++count;
		}
	}
}
