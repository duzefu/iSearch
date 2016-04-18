package common.functions.userinterest;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.sql.ResultSet;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import server.info.config.ConfigFilePath;
import server.info.entites.transactionlevel.UserInterestValueEntity;
import common.textprocess.textsegmentation.*;
import db.dbhelpler.InterestVo;
import db.dbhelpler.UserInterestValueHelper;
import db.jdbc.DatabaseOperate;

public class UserInterestModel {

	private static int count = 100;

	Connection conn;
	Statement stmt;
	ResultSet rs;

	public static Map<String, Double> getUserInterest(int userid) {

		if (userid <= 0)
			return new HashMap<String, Double>();

		String fileStart = ConfigFilePath.getUserXMLFileRoot();
		String fileEnd = ".xml";
		Map<String, Double> ret = new HashMap<String, Double>();
		File file = new File(fileStart + userid + fileEnd);
		if (file.exists()) {
			Document document = null;
			SAXReader reader = new SAXReader();
			try {
				document = reader.read(file);
			} catch (DocumentException e) {
				e.printStackTrace();
			}

			if (null == document)
				return ret;

			List classificationList = document
					.selectNodes("/user/classification");
			// 遍历用户xml文件中每一个classification
			Iterator<Element> ite = classificationList.iterator();
			while (ite.hasNext()) {
				Element classify = ite.next();
				ret.put(classify.attributeValue("classiName"),
						Double.parseDouble(classify.attributeValue("weight")));
			}
		}

		return ret;
	}

	public static Map<String, Double> getUserInterestCXL(int userid) {
		if (userid <= 0)
			return new HashMap<String, Double>();
		List<InterestVo> tempList = UserInterestValueHelper.getUserInterestValIns(userid, 1);
		
		Map<String, Double> ret = new HashMap<String, Double>();
		if (tempList!=null) {
			for (Iterator<InterestVo> iterator=tempList.iterator(); iterator.hasNext();) {
				InterestVo vo = iterator.next();
				ret.put(vo.getName(), vo.getValue());
			}
		}
//		for (Map.Entry<String, Double>map:ret.entrySet()) {
//			System.out.println("result interest map key:"+map.getKey()+" value:"+map.getValue());
//		}
		return ret;
	}

	/**
	 * 更新用户兴趣模型
	 * 
	 * @param userid
	 *            用户ID
	 * @param wl
	 *            用户点击的结果网页分词结果
	 * @param classification
	 *            网页类别
	 */
	public static void udpateUserFavorWord(int userid, WordList wl,
			String classification) {

		/**
		 * 分三个步骤完成操作： 步骤一： 把数据库表isearch/user_favor_words中userid的所有记录按遗忘因子，降低其权重
		 * 步骤二： 对于wl中的新词，只取10个；同时每一个用户userid对应最多只能有100条记录；超出则把原记录中权重最低的删除 步骤三：
		 * 逐一检查wl的前10个，将其存入数据库中；若相应的词已经存在，权重累加。
		 */
		Connection conn = null;
		Statement stmtSelect = null;
		ResultSet rsSelect = null;
		Statement stmtUpdate = null;

		try {
			// 数据库相关变量初始化
			conn = DatabaseOperate.getConnection();
			stmtSelect = conn.createStatement();
			stmtUpdate = conn.createStatement();

			// 日期相关变量初始化
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			Date datenow = cal.getTime();
			String date = df.format(datenow);

			/**************** 步骤一 *********************/
			// 提取该用户在user_favor_words的所有记录
			String selectallwords = "select * from user_favor_words where userid = "
					+ userid;
			rsSelect = stmtSelect.executeQuery(selectallwords);

			// 对每一条记录执行遗忘操作
			double x = 0;
			String updatedate = "";
			int wordCount = 0;

			// 对用户兴趣执行遗忘操作
			while (rsSelect.next()) {
				// 计算当前记录的遗忘因子
				x = UserInterestModel.coefficient(UserInterestModel
						.getIntervalDays(rsSelect.getDate(6), datenow));
				// 当前词在数据库中的ID
				int wordid = rsSelect.getInt(1);
				updatedate = "update user_favor_words set value = "
						+ (x * rsSelect.getDouble(7)) + ", date = '" + date
						+ "' where wordid = " + wordid;
				// 遗忘操作
				stmtUpdate.executeUpdate(updatedate);
				wordCount++;
			}

			// 如果选择结果集不空，关闭该结果集
			DatabaseOperate.closeResult(rsSelect);

			/******************** 步骤二 *******************/
			// 只取网页中的10个词
			int length = wl.totalwords();
			if (wl.totalwords() > 10) {
				length = 10;
			}

			// 词数限制，新的必定要添加，把原来的词语中权重最低的删除
			if (wordCount + length > count) {
				String selectdeleteword = "select * from user_favor_words where userid = "
						+ userid + " order by value";
				rsSelect = stmtSelect.executeQuery(selectdeleteword);
				for (int i = 0; i < (wordCount + length - count)
						&& rsSelect.next(); ++i) {
					int wordid = rsSelect.getInt(1);
					String delete = "delete from user_favor_words where wordid = "
							+ wordid;
					stmtUpdate.executeUpdate(delete);
				}
			}

			// 如果选择结果集不空，关闭该结果集
			DatabaseOperate.closeResult(rsSelect);

			/********************* 步骤三 *********************/
			// 将新词添加到用户兴趣模型中
			String word = null;
			double value;
			for (int i = 0; i < length; ++i) {

				word = wl.getWord(i).getword();
				// 根据词语以及网页的所有词，计算新权重
				value = compute(wl.getWord(i), wl);
				// 用户兴趣模型中，当前词语的所有项
				String select = "select * from user_favor_words where userid = "
						+ userid
						+ " and word = '"
						+ word
						+ "' and classification = '" + classification + "'";
				rsSelect = stmtSelect.executeQuery(select);

				// 用户兴趣模型中已经有这个词了
				if (rsSelect.next()) {
					int wordid = rsSelect.getInt(1);
					String update = "update user_favor_words set value = "
							+ (rsSelect.getDouble(7) + value)
							+ " where wordid = " + wordid;
					stmtUpdate.executeUpdate(update);
				} else {
					String insert = "insert into user_favor_words(userid,word,classification,date,value,category_id) select "
							+ userid
							+ ",'"
							+ word
							+ "','"
							+ classification
							+ "','"
							+ date
							+ "',"
							+ value
							+ ","
							+ "category.id from category where category.category_name = '"
							+ classification + "'";
					stmtUpdate.executeUpdate(insert);
					/*
					 * String selectdeleteword =
					 * "select * from user_favor_words where userid = "
					 * +userid+" order by value"; rs =
					 * stmt.executeQuery(selectdeleteword); if(rs.next()) {
					 * System.out.println("若数据库中没有该词，开始插入操作"); String insert =
					 * "insert into user_favor_words(userid,word,classification,date,value) values("
					 * +
					 * userid+",'"+word+"','"+classification+"','"+date+"',"+value
					 * +")"; stmt.executeUpdate(insert); if(count>30) { int
					 * wordid = rs.getInt(1); String delete =
					 * "delete from user_favor_words where wordid = "+wordid;
					 * //String delete =
					 * "delete from user_favor_words where word = '"
					 * +rs.getString
					 * (1)+"' and classification = '"+rs.getString(2)+"'";
					 * stmt.executeUpdate(delete); //String insert =
					 * "insert into user_favor_words values('"
					 * +word+"','"+classification+"','"+date+"',"+value+")";
					 * String insert =
					 * "insert into user_favor_words(userid,word,classification,date,value) values("
					 * +
					 * userid+",'"+word+"','"+classification+"','"+date+"',"+value
					 * +")"; stmt.executeUpdate(insert); } else { String insert
					 * =
					 * "insert into user_favor_words(userid,word,classification,date,value) values("
					 * +
					 * userid+",'"+word+"','"+classification+"','"+date+"',"+value
					 * +")"; stmt.executeUpdate(insert); } }
					 */
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("更新用户兴趣模型时，处理user_favor_words数据库的操作出现异常。");
		} finally {
			DatabaseOperate.closeResult(rsSelect);
			DatabaseOperate.closeState(stmtSelect);
			DatabaseOperate.closeState(stmtUpdate);
			DatabaseOperate.closeConn(conn);
		}
	}

	/**
	 * 
	 * @param startday
	 * @param endday
	 * @return
	 */
	public static int getIntervalDays(Date startday, Date endday) {

		if (startday.after(endday)) {
			Date cal = startday;
			startday = endday;
			endday = cal;
		}
		long sl = startday.getTime();
		long el = endday.getTime();
		long ei = el - sl;
		return (int) (ei / (1000 * 60 * 60 * 24));
	}

	/**
	 * 计算遗忘因子
	 * 
	 * @param day
	 *            间隔时间
	 * @return 遗忘因子值
	 */
	public static double coefficient(int day) {
		// System.out.println(Math.exp(-(Math.log(2)*day/7)));
		return Math.exp(-(Math.log(2) * day / 7));
	}

	/**
	 * 计算当前词语的权重值——是当前词语权重与所有词权重之和的比值
	 * 
	 * @param word
	 *            网页内容分词后的其中一个词语
	 * @param wl
	 *            网页内容分词后的所有词语的列表
	 * @return word的权重
	 */
	public static double compute(Word word, WordList wl) {
		// String[] titleterms = Filter.filter(title);
		double tf = 0;
		double counts = 0;
		for (int i = 0; i < wl.totalwords(); i++) {
			counts += wl.getWord(i).getweight();
			// if(word.equalsIgnoreCase(wl.getWord(i).getword()))
			// {
			// // tf =
			// (double)wl.getWord(i).getweight()*ComputeWeight.cmpstring(titleterms,
			// word);
			// tf = (double)wl.getWord(i).getweight();
			// }
			tf = word.getweight();
		}
		return tf / counts;
	}

}
