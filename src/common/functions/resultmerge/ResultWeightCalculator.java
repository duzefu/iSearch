package common.functions.resultmerge;

import java.util.List;

import common.entities.searchresult.Result;
import common.textprocess.textsegmentation.IK;

public class ResultWeightCalculator {

	/**
	 * 根据位置计算结果的初始分值
	 * 
	 * @param result
	 * @param position
	 */
	public static void calculateWeight(Result result, int position) {
		
		if (null == result) return;
		
		result.setValue(getFactorByPosition(position));

//		List<String> xtitle = null, xabstr = null;
//		xtitle = IK.fenci(title);// 对标题分词
//		xabstr = IK.fenci(abstr);// 对摘要分词
//
//		double weight1 = getWeightByAlth1(queryFenci, xtitle, xabstr);
//		double weight2 = getWeightByAlth2(queryFenci, xabstr);
//		double lastWeight = 0.48 * (pageFac + posFac) + 0.02
//				* (weight1 + weight2);// 最终权值

	}

	private static double getFactorByPosition(int pos) {

		if (pos <= 0||pos>100)
			return 0;
		double weight = 1+(double)(pos-1) * 9 / 99;
		weight = weight < 0 ? 0 : weight;
		return 1-Math.log10(weight);
	}

	private static double getWeightByAlth1(List<String> queryFenci,
			List<String> tFenci, List<String> aFenci) {

		if (null == queryFenci || null == tFenci || null == aFenci)
			return 0;

		int xcountT = 0, xcountA = 0;
		// xcountT为标题分词中和用户查询分词中相同的个数，小countA为摘要分词与查询分词中相同的个数
		for (int i = 0; i < queryFenci.size(); i++) {
			for (int j = 0; j < tFenci.size(); j++) {
				if (tFenci.get(j).equalsIgnoreCase(queryFenci.get(i)))
					xcountT++;
				if (xcountT >= queryFenci.size()) {
					xcountT = queryFenci.size();
					break;
				}
			}
		}
		for (int i = 0; i < queryFenci.size(); i++) {
			for (int j = 0; j < aFenci.size(); j++) {
				if (aFenci.get(j).equalsIgnoreCase(queryFenci.get(i)))
					xcountA++;
				if (xcountA >= queryFenci.size()) {
					xcountA = queryFenci.size();
					break;
				}
			}
		}
		double ret = 0;
		// 设置标题和摘要与用户查询的匹配度值p
		if (queryFenci.size() != 0)
			ret = 0.7 * xcountT / queryFenci.size() + 0.3 * xcountA
					/ queryFenci.size();

		return ret;
	}

	private static double getWeightByAlth2(List<String> queryFenci,
			List<String> aFenci) {

		double s = 0, sa = 0;// s为相似度值
		int countXG = 0;// 计数用
		if (aFenci != null) {// 计算用户查询分词与摘要分词的相关度值
			for (int i = 0; i < queryFenci.size(); i++) {
				for (int j = 0; j < aFenci.size(); j++) {
					if (aFenci.get(j).equalsIgnoreCase(queryFenci.get(i))) {
						sa += (j+1);
						countXG++;
					}
				}
				if (countXG != 0 && aFenci.size() != 0){
					double curVal=1 - sa / (countXG * aFenci.size());
					if(0==i) s=curVal;
					else s=(s+curVal)/2;
				}
				countXG = 0;
				sa = 0;
			}
		}

		return s;
	}

}
