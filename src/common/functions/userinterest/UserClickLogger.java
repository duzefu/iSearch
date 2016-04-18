package common.functions.userinterest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;
import common.textprocess.textclassifier.BayesClassifier;
import common.textprocess.textsegmentation.*;
import db.dao.CategoryDao;
import db.dbhelpler.UserInterestValueHelper;
import db.jdbc.DatabaseOperate;

public class UserClickLogger {

	/**
	 * 用户点击以后，更新click_log表以及user_favor_words表——用户点击记录 以及 用户兴趣模型
	 * @param userid 用户ID——非登录用户也有效
	 * @param query 查询词
	 * @param title 点击结果的标题
	 * @param abstr 点击结果的摘要
	 * @param url 点击结果的URL
	 * @param date 日期
	 * @return
	 */
	public int record(int userid, String query, String title, String abstr,
			String url, String date,String sources) {
		int count = 0;
		String classification = null;
		WordList wl = null;

		//先判断本次添加的记录，数据库表里是否已经存在，若存在，则更新该记录的value值，若不存在，则添加记录

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseOperate.getConnection();
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String tablename="click_log";
		try {
		String select_sql = "select * from "+tablename+" where userid = " + userid
				+ " and query = '" + query + "' and url='" + url
				+ "' order by id DESC";

			rs = stmt.executeQuery(select_sql);
			// 当前查询词已经在点击记录表中存在
			if (rs.next()) {
				int value = rs.getInt("value");
				value = value + 1;
				String update_sql = "update "+tablename+" set value = " + value
						+ ",date ='" + date + "'" + "where userid = " + userid
						+ " and query = '" + query + "' and url='" + url + "'";
				stmt.executeUpdate(update_sql);
			} else {
				//当前是新的查询词
				String[] webContent=new String[2];
				webContent[0]=title;
				webContent[1]=abstr;
//				String[] webContent=GetWebText.gettext(url);
				//把网页的内容分词，并计算权重,选取权重最高的前100个分词，并将其和对应的权重放到wordlist中
				wl = CreateWordList.get(webContent);
				wl.addWord(new Word(query,wl.getWord(0).getweight()));//赋予查询词最高权重
				//贝叶斯方法分类网页
				classification = BayesClassifier.bayes(wl);
				CategoryDao cdao=(CategoryDao) SpringBeanFactoryUtil.getBean(SpringBeanNames.CATEGORY_DAO_BEAN_NAME);
				Integer cid=cdao.getCategoryIDByName(classification);
				System.out.println("用户点击的页面类属：" + classification);

				//2014-10-20 zcl 是更新用户兴趣模型
				UserInterestModel.udpateUserFavorWord(userid, wl, classification);
				//2015-10-26 cxl 使用数据库user_favor_words表来更新用户的兴趣数据库user_interest_value
				UserInterestValueHelper.updateUserInterestByInterestWords(userid);
				int newValue=1;
				
				// 以下暂时将user_clickhistory表修改为重新定义的一个数据库表click_log，用来记录用户点击记录（by许静20121126下午）
				String sql = "insert into "+tablename+" (userid,query,title,abstr,url,classification,value,date,category_id ) values("
						+ userid
						+ ",'"
						+ query
						+ "','"
						+ title
						+ "','"
						+ abstr
						+ "','"
						+ url
						+ "','"
						+ classification
						+ "',"
						+ newValue
						+ ",'" + date
						+ "'," + cid+")";
				try {
					stmt.executeUpdate(sql);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// //待分析关联词
		// Insert.analysisword(wl,id);
		// //String select =
		// "select * from user_clickhistory where userid = "+userid;
		// String select = "select * from click_log where userid = "+userid;
		// try {
		// rs = stmt.executeQuery(select);
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// try {
		// while(rs.next())
		// {
		// count++;
		// }
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println("该用户累计有 "+(count)+"记录 ");

		/**
		 * 关联词分析
		 */
		// if(count>=20)
		// {
		// int number = GetNumber.get();
		//
		// //
		// GetItemSet.getitemset();
		// Relevance.getrelevance(query, classification, number);
		//
		//
		//
		// EmptyDB.delete();
		// }
		finally {
			DatabaseOperate.closeResult(rs);
			DatabaseOperate.closeState(stmt);
			DatabaseOperate.closeConn(conn);
		}
		return count;
	}
}
