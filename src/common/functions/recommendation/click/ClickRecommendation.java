package common.functions.recommendation.click;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import server.commonutils.MyStringChecker;
import server.info.entites.transactionlevel.ClickRecordEntity;
import common.entities.searchresult.Result;
import common.functions.resultmerge.MergeRecom;
import common.textprocess.userXMLfilehelpler.GetDBData;
import db.dbhelpler.ClickLogHelper;
import db.dbhelpler.UserGroupHelper;
import db.dbhelpler.UserHelper;

public class ClickRecommendation {

	private static ClickRecommendation instance;

	private static ClickRecommendation getInstance(){
		
		if(null==instance){
			synchronized (ClickRecommendation.class) {
				if(null==instance) instance=new ClickRecommendation();
			}
		}
		return instance;
	}
	
	private ClickRecommendation(){
		
	}

	/**
	 * 获取结果推荐内容
	 * @param ret 用于追加推荐结果（追加方式，如果已有内容，也不清空）
	 * @param query 查询词
	 * @param userid 用户ID
	 */
	public static void getClickRecommendation(List<Result> ret, String query,
			int userid) {

		if (!UserHelper.isLegalUserID(userid))
			return;
		Set<Integer> groupUserId = new HashSet<Integer>();
		UserGroupHelper.getGroupUserID(userid, groupUserId);
		getClickRecommendation(ret, query, groupUserId);
	}
	
	/**
	 * 获取结果推荐内容
	 * @param ret 用于追加推荐结果（追加方式，如果已有内容，也不清空）
	 * @param query 查询词
	 * @param groupUserid 群组用户的ID
	 */
	public static void getClickRecommendation(List<Result> ret, String query, Set<Integer> groupUserid){
		
		//参数检查
		if(MyStringChecker.isBlank(query)||null==groupUserid||groupUserid.isEmpty()||null==ret) return;
		
		//查找日志数据
		List<ClickRecordEntity> logs=new ArrayList<ClickRecordEntity>();
		ClickLogHelper.getClickedResults(logs,groupUserid,query);

		//格式化推荐结果
		Iterator<ClickRecordEntity> it=logs.iterator();
		while(it.hasNext()){
			ClickRecordEntity log=it.next();
			if(null==log) continue;
			Result r=new Result(log.getTitle(), log.getAbstr(), log.getUrl(), null);
			r.formatClickRecommResult(log);
			if(r.isUsable()) ret.add(r);
		}
	}

}
