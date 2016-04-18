package common.textprocess.userXMLfilehelpler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import common.entities.blackboard.Interest;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;
import db.dao.UserGroupDao;
import db.jdbc.DatabaseOperate;

public class GetDBData {

	UserGroupDao ugDao;
	
	private UserGroupDao getUgDao() {

		if (null == ugDao)
			ugDao = (UserGroupDao) SpringBeanFactoryUtil
					.getBean(SpringBeanNames.USER_GROUP_DAO_BEAN_NAME);
		return ugDao;
	}
	
	/**
	 * 在isearch的user_favor_words中，查找userid对应用户的所有兴趣词（所有类别）
	 * 
	 * @param userid
	 *            用户ID
	 * @return 所有兴趣词、类别、时间及权重信息组成的对象列表
	 * @throws SQLException
	 */
	public List<Interest> findInterests(Integer userid) throws SQLException {
		
		Connection connection = DatabaseOperate.getConnection();
		List<Interest> interests = new ArrayList<Interest>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String select = "select word,classification,value,date,category.category_name classification from user_favor_words, category where userid ='"
				+ userid + "' and value > 0.0 and category.id = user_favor_words.category_id";
		try {
			rs = stmt.executeQuery(select);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(null==rs) return interests;
		while (rs.next()) {
			Interest interest = new Interest();
			interest.setWordname(rs.getString("word"));
			interest.setOwerClassifi(rs.getString("classification"));
			interest.setValue(rs.getDouble("value"));
//			interest.setDate(rs.getString("date"));
			interests.add(interest);

		}
		rs.close();
		stmt.close();
		connection.close();
		return interests;
	}
	
	/**add by cxl
	 * 在isearch的user_favor_words中，查找userid对应用户的所有兴趣词（所有类别）从高到低排序
	 * 
	 * @param userid
	 *            用户ID
	 * @return 所有兴趣词、类别、时间及权重信息组成的对象列表
	 * @throws SQLException
	 */
	public List<Interest> findInterestsCXL(Integer userid) throws SQLException {
		
		Connection connection = DatabaseOperate.getConnection();
		List<Interest> interests = new ArrayList<Interest>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String select = "SELECT userid,classification,date,sum(value) as valuen,category.id as id FROM isearch.user_favor_words,isearch.category where userid='"+userid+"' and isearch.category.id=isearch.user_favor_words.category_id group by classification order by valuen desc";
		try {
			rs = stmt.executeQuery(select);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(null==rs) return interests;
		while (rs.next()) {
			Interest interest = new Interest();
			interest.setOwerClassifi(rs.getString("classification"));
			interest.setValue(rs.getDouble("valuen"));
			Calendar calendar = Calendar.getInstance();
			Date date = calendar.getTime();	
			interest.setDate(date);
			
//			interest.setDate(rs.getDate("date"));
			interest.setClassificationID(rs.getInt("id"));
			interests.add(interest);

		}
		rs.close();
		stmt.close();
		connection.close();
		return interests;
	}

}
