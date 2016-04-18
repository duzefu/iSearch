/*
 * by zcl 2015.10
 * 先注释掉调度函数，由于分数文件、成员搜索引擎名称管理方式变化
 * 以后实现调度时再重新考虑实现方式
 */

//package common.functions.schedule;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import server.commonutils.CharUtil;
//import server.info.config.ConstantValue;
//import common.functions.webpagediagram.CoolDynamicEngineScore;
//import common.functions.webpagediagram.SearchEngineScore;
//import common.textprocess.textsegmentation.IK;
//import db.jdbc.ClassifierOperate;
//import db.jdbc.DatabaseOperate;
//
//public class ScheduleStrategy {
//
//	private EngineScore[] engScores;
//	private static Set<String> EngineNameForEnglish = new HashSet<String>();
//	
//	static{
//		EngineNameForEnglish.add("bing");
//		EngineNameForEnglish.add("必应");
//		EngineNameForEnglish.add("雅虎");
//		EngineNameForEnglish.add("Yahoo");
//	}
//	/**
//	 * 获取本次搜索的调度，每个成员搜索引擎以&号分开，最后多出一个&
//	 * @param query
//	 * @param userid
//	 * @return
//	 */
//	public String schedule(String query, int userid){
//		
//		String ret="";
//		Map<String, Double> scheRes=this.scheduleForAndroid(query, userid);
//		Iterator<String> iterKey=scheRes.keySet().iterator();
//		while(iterKey.hasNext()){
//			String engName=iterKey.next();
//			ret+=engName+"&";
//		}
//		
//		return ret;
//		
//	}
//	
//	/**
//	 * 根据查询词和用户id确定调度策略
//	 * 
//	 * @param query
//	 * @param userid
//	 * @return引擎名称和权重
//	 */
//	public Map<String, Double> scheduleForAndroid(String query, int userid) {
//		
//		if(null==query||query.isEmpty()||userid<=0) return new HashMap<String, Double>();
//		
//		List<String> words = new ArrayList<String>();
//		try {
//			words = IK.fenci(query);
//		} catch (Exception e) {
//			words.add(query);
//		}
//		int engine_count = ConstantValue.getEngineCount();
//		EngineScore[] scores = new EngineScore[engine_count];
//		for (int i = 0; i < engine_count; i++) {
//			EngineScore t = new EngineScore();
//			t.setName(ConstantValue.engineNames[i]);
//			scores[i] = t;
//		}
//		boolean sure = false;
//		for (String word : words) {
//			// 通过用户兴趣模型得到查询词类别
//			String classes = classifier_user(word, userid);
//			// 没有根据用户兴趣模型得到查询词类别
//			if (null == classes) {
//				// 通过训练库得到查询词类别
//				classes = classifier_data(word);
//			}
//			if (null == classes)// 没有根据训练库得到查询词类别
//			{
//				continue;
//			} else {
//				EngineScore[] scores_sub = calculate(classes);
//				for (int j = 0; j < engine_count; j++) {
//					scores[j].setScore(scores_sub[j].getScore()
//							+ scores[j].getScore());
//				}
//				sure = true;
//			}
//
//		}
//
//		if (!sure)
//			scores = calculate_all_subject(query);
//		
//		if(CharUtil.isEnglishPattern(query)){
//			this.scoreChangeForEnglishQuery(scores);
//		}
//		Arrays.sort(scores);
//		Map<String, Double> ret=new HashMap<String, Double>();
//		int count = 0;
//		for (int i = 0; i < scores.length; i++) {
//			String engName=scores[i].getName();
//			Double weight=scores[i].getScore();
//			ret.put(engName, weight);
//			if (++count == 3)
//				break;
//		}
//		
//		return ret;
//
//	}
//
//	private void scoreChangeForEnglishQuery(EngineScore[] scores){
//		
//		if(null==scores||scores.length==0) return;
//		for(int i=0;i<scores.length;++i){
//			EngineScore es=scores[i];
//			if(EngineNameForEnglish.contains(es.getName())) es.setScore(es.getScore()*1.5);
//		}
//	}
//	
//	public static Map<String, Double> getEngineScore(String query, int userid,
//			String schedule) {
//
//		if (null == schedule)
//			return null;
//
//		Map<String, Double> ret = new HashMap<String, Double>();
//		ScheduleStrategy getter = new ScheduleStrategy();
//		getter.schedule(query, userid);
//		String[] schArr = schedule.split("&");
//		if (null == schArr)
//			return ret;
//		Set<String> engineNames = new HashSet<String>();
//		for (int i = 0; i < schArr.length; ++i) {
//			String curEngName = schArr[i];
//			engineNames.add(schArr[i]);
//			engineNames.add(ConstantValue.switchEngineName(curEngName));
//		}
//		EngineScore[] engScores = getter.engScores;
//		for (int i = 0; i < engScores.length; ++i) {
//			EngineScore score = engScores[i];
//			if (engineNames.contains(score.getName())) {
//				ret.put(score.getName(), score.getScore());
//			}
//		}
//
//		return ret;
//	}
//
//	/**
//	 * 根据用户兴趣模型确定查询词类别
//	 * 
//	 * @param query
//	 * @param userid
//	 * @return
//	 */
//	private String classifier_user(String query, int userid) {
//		String classification = null;
//
//		Connection conn = null;
//		Statement stmt = null;
//		ResultSet rs = null;
//		try {
//			conn = DatabaseOperate.getConnection();
//			conn.setAutoCommit(false);
//			stmt = conn.createStatement();
//			String select_sql = "select classification from user_favor_words where userid = "
//					+ userid + " and word = '" + query + "'";
//			rs = stmt.executeQuery(select_sql);
//			conn.commit();
//			if (rs.next())
//				classification = rs.getString("classification");
//		} catch (SQLException e) {
//			e.printStackTrace();
//			try {
//				conn.rollback();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//		} finally {
//			DatabaseOperate.closeResult(rs);
//			DatabaseOperate.closeState(stmt);
//			DatabaseOperate.closeConn(conn);
//		}
//		return classification;
//	}
//
//	/**
//	 * 在训练库中确定查询词类别
//	 * 
//	 * @param query
//	 * @return
//	 */
//	private String classifier_data(String query) {
//
//		int count = ConstantValue.classNames.length;
//		ClassScore[] scores = new ClassScore[count];
//
//		// String score3 = "10";
//		// String score1 = "100";
//		// List<String> easyScore = new ArrayList<String>();
//		// if(Math.abs(Double.parseDouble(score1) - Double.parseDouble(score3))
//		// < 3)
//		// easyScore.set(0, ((Double) (Double.parseDouble(score1) + 3)).
//		// toString());
//
//		Connection conn = null;
//		Statement stmt = null;
//		ResultSet rs = null;
//
//		ClassifierOperate co = new ClassifierOperate();
//		try {
//			conn = co.connection();
//			;
//			conn.setAutoCommit(false);
//			stmt = conn.createStatement();
//			for (int i = 0; i < count; i++) {
//				ClassScore es = new ClassScore();
//				es.setName(ConstantValue.classNames[i]);
//
//				String select_sql = "select * from "
//						+ ConstantValue.classNames[i] + " where word = '"
//						+ query + "'";
//				rs = stmt.executeQuery(select_sql);
//				conn.commit();
//				if (rs.next())
//					es.setScore(rs.getInt("weight"));
//				else
//					es.setScore(0);
//				scores[i] = es;
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			try {
//				conn.rollback();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//		} finally {
//			try {
//				if (null != rs)
//					rs.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//
//			try {
//				if (null != stmt)
//					stmt.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			co.close();
//		}
//
//		Arrays.sort(scores);
//		for (int i = 0; i < scores.length; i++) {
//			if (0 != scores[i].getScore())
//				return scores[0].getName();
//		}
//		return null;
//	}
//
//	/**
//	 * 根据确定的查询种类选择最优的三个成员搜索引擎（未使用） 选出给定类别擅长度最高的三个搜索引擎
//	 * 
//	 * @param classification
//	 *            该参数是确定的查询词类别
//	 * @return
//	 */
//	public String calculate_subject_three(String classification) {
//		EngineScore[] scores = calculate(classification);
//		Arrays.sort(scores);
//		String re = "";
//		for (int i = 0; i < 3; i++) {
//			re += scores[i].getName() + "&";
//		}
//		return re;
//	}
//
//	/**
//	 * 计算针对某主题，所有搜索引擎的擅长度
//	 * 
//	 * @param c
//	 *            该参数是确定的查询词类别
//	 * @return
//	 */
//	private EngineScore[] calculate(String classification) {
//
//		int c = -1;
//		for (int i = 0; i < ConstantValue.classNames.length; i++) {
//			if (ConstantValue.classNames[i].equalsIgnoreCase(classification))
//				c = i;
//		}
//		return calculate(c);
//	}
//
//	/**
//	 * ----lhx--20141204 计算针对某主题，所有搜索引擎的擅长度
//	 * 
//	 * @param c
//	 *            该参数是确定的查询词类别的序号
//	 * @return
//	 */
//	private EngineScore[] calculate(int c) {
//		double concept = 0;
//		double log = 0;
//		int count = ConstantValue.engineNames.length;
//		for (int i = 0; i < count; i++) {
//			concept += ConstantValue.concept_score[i][c];
//			log += ConstantValue.log_score[i][c];
//		}
//		EngineScore[] scores = new EngineScore[count];
//		for (int i = 0; i < count; i++) {
//			EngineScore es = new EngineScore();
//			es.setName(ConstantValue.engineNames[i]);
//			es.setScore((ConstantValue.log_score[i][c] * concept / log)
//					+ ConstantValue.concept_score[i][c]);
//			scores[i] = es;
//		}
//
//		// 2014.12.5添加，为画SearchEngineScore添加
//		SearchEngineScore.engineScore = scores;
//		CoolDynamicEngineScore.engineScore = scores;
//		this.engScores = scores;
//		return scores;
//	}
//
//	/**
//	 * 计算得到全部分类中效果最优的三个成员搜索引擎
//	 * 
//	 * @return
//	 */
//	private EngineScore[] calculate_all_subject(String query) {
//		int engine_count = ConstantValue.engineNames.length;
//		int classes_count = ConstantValue.classNames.length;
//		EngineScore[] scores = new EngineScore[engine_count];
//		for (int i = 0; i < engine_count; i++) {
//			EngineScore t = new EngineScore();
//			t.setName(ConstantValue.engineNames[i]);
//			scores[i] = t;
//		}
//
//		for (int i = 0; i < classes_count; i++) {
//			EngineScore[] scores_subject = calculate(i);
//
//			for (int j = 0; j < scores_subject.length; j++) {
//				scores[j].setScore(scores_subject[j].getScore()
//						+ scores[j].getScore());
//			}
//
//		}
//
//		return scores;
//	}
//}
