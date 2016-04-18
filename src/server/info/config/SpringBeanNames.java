package server.info.config;

/**
 * Spring的配置文件beans.xml中配置的bean结点的id，用于以非注入的方式获取Spring的bean（java对象）
 * @author zhou
 *
 */
public class SpringBeanNames {

	final public static String CATEGORY_DAO_BEAN_NAME="CategoryDao";
	final public static String USER_DAO_BEAN_NAME="UserDao";
	final public static String QFG_DAO_BEAN_NAME="QfgGenDao";
	final public static String QUERIES_DAO_BEAN_NAME="QueriesDao";
	final public static String CLICK_LOG_DAO_BEAN_NAME="ClickLogDao";
	final public static String USER_GROUP_DAO_BEAN_NAME="UserGroupDao";
	final public static String HOTWORDS_DAO_BEAN_NAME="HotwordsDao";
	final public static String USER_INTEREST_VALUE_DAO_BEAN_NAME="UserInterestValueDao";
}
