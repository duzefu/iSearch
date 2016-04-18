package common.functions.resultmerge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import server.commonutils.MyStringChecker;
import server.info.entites.transactionlevel.ClickRecordEntity;

import common.entities.searchresult.*;
import common.functions.userinterest.UserInterestModel;
import common.textprocess.textclassifier.ClassifyResult;
import db.dbhelpler.ClickLogHelper;
import db.dbhelpler.UserHelper;

public class Merge {

	private final static int MAX_ABSTRACT_LENTH = 100;// 摘要最大字数

	public static void resultMerge(List<Result> tarlist, List<Result> newRes,
			int userid, String query, boolean isLogin) {

		if (null == tarlist || null == newRes || newRes.isEmpty()
				|| !UserHelper.isLegalUserID(userid)
				|| MyStringChecker.isBlank(query))
			return;

		// 添加搜索结果分类
		for (Iterator<Result> it = newRes.iterator(); it.hasNext();) {
			Result result = it.next();
			ClassifyResult.addResultClassification(query, result);
		}
		calculateInitWeight(newRes);
		// 用户兴趣修正结果的分值
		if (isLogin)
			Merge.processWithUserInterest(newRes, userid, query);
		// 用户历史点击信息修正结果分值
		resultProcessWithUserClickHistory(newRes, userid, query);
		limitAbstrLenght(newRes);
		// 注意下面这一步应该在上面的步骤完成之后才做
		// 而且上面的步骤只对新结果执行，不要再对旧结果列表执行，避免有的结果被反复加权
		/*
		 * 原因：w=(w_org+w_new)*fac=w_org*fac+w_new*fac
		 * 即：对于重复的结果，应该是原始权重累加，再乘以加权因子fac（兴趣因子，历史记录因子）
		 */
		Merge.mergeListDistinct(tarlist, newRes);
		Collections.sort(tarlist, new MergeSort());
	}

	/**
	 * 根据位置信息初始化结果分值
	 * 
	 * @param ls
	 */
	private static void calculateInitWeight(List<Result> ls) {

		for (Iterator<Result> it = ls.iterator(); it.hasNext();) {
			Result curRes = it.next();
			if (null == curRes)
				continue;
			int position = curRes.getPosition();
			ResultWeightCalculator.calculateWeight(curRes, position);
		}
	}

	/**
	 * 把两个列表中的结果合并为一个，同时把相同的结果去除，权重累加
	 * 
	 * @param orglist
	 *            目标列表
	 * @param newRes
	 *            待合成结果列表
	 */
	private static void mergeListDistinct(List<Result> orglist,
			List<Result> newRes) {

		if (null == newRes || newRes.isEmpty() || null == orglist)
			return;

		Map<String, Result> url2res = new HashMap<String, Result>(), title2res = new HashMap<String, Result>();
		getUrlTitleToResultMap(orglist, url2res, title2res);
		for (Iterator<Result> itnew = newRes.iterator(); itnew.hasNext();) {
			Result curRes = itnew.next();
			if (null == curRes)
				continue;

			// 根据URL确定原来的结果中是否存在相同的结果；
			// URL无法确定时，标题相同的也被认为是相同的结果（不太合理，但对于微博结果有效）
			Result orgRes = url2res.get(curRes.getLink());
			if (null == orgRes)
				orgRes = title2res.get(curRes.getTitle());

			// 当前结果已经出现过了
			if (null != orgRes) {
				orgRes.setValue(orgRes.getValue() + curRes.getValue());
				orgRes.setSource(Merge.mergeSource(orgRes, curRes));
				orgRes.setArray(orgRes.getSource());
				String orgAbstr = orgRes.getAbstr();
				if (null == orgAbstr || orgAbstr.isEmpty()) {
					String newAbstr = curRes.getAbstr();
					if (null != newAbstr && !newAbstr.isEmpty())
						orgRes.setAbstr(newAbstr);
				}
			} else {
				url2res.put(curRes.getLink(), curRes);
				title2res.put(curRes.getTitle(), curRes);
				orglist.add(curRes);
				// 需要把结果从新列表中删除，否则下一次迭代的it.next()可能抛异常（ConcurrentModificationException）
				// 原因是在上面的if语句中，可能对结果进行修改
				// 如果修改的那条结果指向了newRes中的元素（orglist与newRes共享），就会导致这个异常（没有通过迭代器修改了newRes的元素）
				itnew.remove();
			}
		}

	}

	private static String mergeSource(Result orgres, Result newres) {

		if (null == orgres || null == newres)
			return null;

		String ret = orgres.getSource();
		Set<String> orgEnames = orgres.getSourceEngineCnName(), newEnames = newres
				.getSourceEngineCnName();
		Map<String, String> enametoEstr = Merge.findEngineStr(newres);
		Iterator<String> iternn = newEnames.iterator();
		while (iternn.hasNext()) {
			String newName = iternn.next();
			if (!orgEnames.contains(newName))
				ret += " " + enametoEstr.get(newName);
		}

		return ret;
	}

	private static Map<String, String> findEngineStr(Result result) {

		Map<String, String> ret = new HashMap<String, String>();

		if (null == result)
			return ret;
		String source = result.getSource();
		if (null == source || source.isEmpty())
			return ret;
		String[] srcStr = source.split(" ");
		if (null == srcStr || srcStr.length == 0)
			return ret;
		Set<String> srcEngName = result.getSourceEngineCnName();
		if (null == srcEngName)
			return ret;
		Iterator<String> iterEname = srcEngName.iterator();
		while (iterEname.hasNext()) {
			String curename = iterEname.next();
			for (int i = 0; i < srcStr.length; ++i) {
				if (srcStr[i].contains(curename))
					ret.put(curename, srcStr[i]);
			}
		}

		return ret;
	}

	/**
	 * 生成合成时用的两张hash表，分别记录了URL-搜索结果对象、标题-搜索结果对象； 用于判断搜索结果是否已经存在（去重）以及将相同的结果合并。
	 * 
	 * @param rlist
	 *            搜索结果列表
	 * @param url2res
	 *            URL-搜索结果对象哈希表（相当于返回值）
	 * @param title2res
	 *            标题-搜索结果对象哈希表（相当于返回值）
	 */
	private static void getUrlTitleToResultMap(List<Result> rlist,
			Map<String, Result> url2res, Map<String, Result> title2res) {

		if (null == rlist || rlist.isEmpty() || url2res == null
				|| null == title2res)
			return;

		Iterator<Result> iterRes = rlist.iterator();
		while (iterRes.hasNext()) {
			Result curRes = iterRes.next();
			String url = curRes.getLink(), title = curRes.getTitle();
			if (null != url && !url2res.containsKey(url))
				url2res.put(url, curRes);
			if (null != title && !title2res.containsKey(title))
				title2res.put(title, curRes);
		}

		return;
	}

	/**
	 * 对于登录用户，按其兴趣调整结果的权重
	 * 
	 * @param list
	 * @param userid
	 * @param query
	 */
	private static void processWithUserInterest(List<Result> list, int userid,
			String query) {

		if (null == list || list.isEmpty() || userid <= 0)
			return;

		for (Iterator<Result> it = list.iterator(); it.hasNext();) {
			Result result = it.next();
			ClassifyResult.addResultClassification(query, result);
		}

		double averageWeight = Merge.getAverageWeightOfResult(list);
		// notation by cxl 2015-10-27
		// Map<String, Double> userInterest = UserInterestModel
		// .getUserInterest(userid);
		Map<String, Double> userInterest = UserInterestModel.getUserInterestCXL(userid);
		Merge.normalizeInterestValue(userInterest);

		for (Iterator<Result> it = list.iterator(); it.hasNext();) {
			Result curRes = it.next();
			if (null == curRes)
				continue;
			Double interestFactor = userInterest
					.get(curRes.getClassification());
			interestFactor = null == interestFactor ? 0 : interestFactor;
			curRes.setValue(curRes.getValue() + averageWeight * interestFactor);
		}

	}

	/**
	 * 用户兴趣分值归一化
	 * 
	 * @param interest
	 */
	private static void normalizeInterestValue(Map<String, Double> interest) {

		if (null == interest)
			return;

		double sum = 0;
		Iterator<String> iterClassKey = interest.keySet().iterator();
		while (iterClassKey.hasNext()) {
			String key = iterClassKey.next();
			Double value = interest.get(key);
			sum += null == value ? 0 : value;
		}

		iterClassKey = interest.keySet().iterator();
		while (iterClassKey.hasNext()) {
			String key = iterClassKey.next();
			Double value = interest.get(key), newVal = null == value ? 0
					: value / sum;
			interest.put(key, newVal);
		}
	}

	/**
	 * 获取搜索结果列表的各结果权重的平均值
	 * 
	 * @param list
	 * @return
	 */
	private static double getAverageWeightOfResult(List<Result> list) {

		if (null == list || list.isEmpty())
			return 0;

		double ret = 0;
		int count = 0;
		for (Iterator<Result> it = list.iterator(); it.hasNext();) {
			Result r = it.next();
			if (null == r)
				continue;
			ret += r.getValue();
			++count;
		}

		return ret / (0 == count ? 1 : count);
	}

	/**
	 * 获取搜索结果列表的各结果权重的平均值
	 * 
	 * @param list
	 * @return
	 */
	private static double getAverageWeightOfHistoryResult(
			List<ClickRecordEntity> list) {

		if (null == list || list.isEmpty())
			return 0;

		double ret = 0;
		int count = 0;
		for (Iterator<ClickRecordEntity> it = list.iterator(); it.hasNext();) {
			ClickRecordEntity r = it.next();
			if (null == r)
				continue;
			ret += r.getWeight();
			++count;
		}

		return ret / (0 == count ? 1 : count);
	}

	/**
	 * 根据用户点击记录，调整搜索结果权重
	 * 
	 * @param list
	 * @param userid
	 * @param query
	 */
	private static void resultProcessWithUserClickHistory(List<Result> list,
			int userid, String query) {

		List<ClickRecordEntity> clickHistory = new LinkedList<ClickRecordEntity>();
		ClickLogHelper.getClickedResults(clickHistory, userid, query);
		resultWeightOfClickHistory(list, clickHistory);
	}

	/**
	 * add by cxl 2015-07-02 根据点击历史，添加用户点击的结果权重，使得排序更加合理
	 * 
	 * @param tarLs
	 *            要加权重的结果列表
	 * @param hisLs
	 *            历史的结果列表
	 */
	private static void resultWeightOfClickHistory(List<Result> tarLs,
			List<ClickRecordEntity> hisLs) {

		if (null == tarLs || tarLs.isEmpty() || null == hisLs
				|| hisLs.isEmpty())
			return;

		double aveWtTar = getAverageWeightOfResult(tarLs), aveWtHis = getAverageWeightOfHistoryResult(hisLs);
		Map<String, Double> url2Wt = new HashMap<String, Double>(), title2Wt = new HashMap<String, Double>();
		getUrlTitleToWeightMap(hisLs, url2Wt, title2Wt);
		for (Iterator<Result> it = tarLs.iterator(); it.hasNext();) {
			Result r = it.next();
			if (null == r)
				continue;
			Double hwt = url2Wt.get(r.getSource());
			if (null == hwt)
				hwt = title2Wt.get(r.getTitle());
			if (null != hwt && hwt > 0.000001) {
				r.setValue(r.getValue() + aveWtTar * hwt / aveWtHis);
			}
		}
	}

	private static void getUrlTitleToWeightMap(List<ClickRecordEntity> logLs,
			Map<String, Double> url2Wt, Map<String, Double> title2Wt) {

		if (null == logLs || logLs.isEmpty() || null == url2Wt
				|| null == title2Wt)
			return;

		for (Iterator<ClickRecordEntity> it = logLs.iterator(); it.hasNext();) {
			ClickRecordEntity obj = it.next();
			if (null == obj)
				continue;
			String url = obj.getUrl(), title = obj.getTitle();
			double newWt = obj.getWeight();

			Double orgWt = url2Wt.get(url);
			url2Wt.put(url, newWt + (null == orgWt ? 0 : orgWt.doubleValue()));

			orgWt = title2Wt.get(title);
			title2Wt.put(title,
					newWt + (null == orgWt ? 0 : orgWt.doubleValue()));
		}
	}

	private static void limitAbstrLenght(List<Result> ls) {

		Iterator<Result> it = ls.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			if (null == r)
				continue;
			String abstr = r.getAbstr();
			int len = null == abstr ? 0 : abstr.length();
			if (0 == len || len <= MAX_ABSTRACT_LENTH)
				continue;
			r.setAbstr(abstr.substring(0, MAX_ABSTRACT_LENTH - 3) + "...");
		}
	}
}
