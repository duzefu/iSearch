package server.info.config.agent;

/**
 * 在这个类里面存放所有Agent向DF注册的服务的名称
 * Agent向DF注册服务时，以及其他Agent通过DF根据服务名字来查找Agent时，需要设置服务名称，统一使用这些字符串
 * 
 * 使用DF的的原因如下：
 * 		1) Agent应该集中在一个类中创建，后续代码便于统一管理；
 * 		2) 各个Agent都需要保存与他需要交互的Agent的AID（发送消息时要用）；
 * 以上两点可能：
 * 		1) 是互相矛盾的；
 * 			（例如可以由Interface Agent来创建Login Agent，同时把AID池设置好；但是不可能所有的Agent都放在Interface Agent类里面创建，例如Interface Agent不必管理成员搜索引擎Agent）
 * 		2) 导致代码依赖严重，紧耦合；
 * 			（例如设置一个Agent Factory类来创建Agent，此时这个类还需要为所有存在交互关系的Agent设置它们的静态成员变量——AID数组，一旦系统增加Agent或Agent交互关系改变，代码修改不方便）
 * 
 * 使用JADE的目录服务来解决这个问题：
 * 		1) 通过Agent Factory来集中创建Agent，在服务器启动后就创建；
 * 		2) 各个Agent的setup()函数中，都向DF注册服务；这样多个相同的Agent向DF注册相同名字的服务，用这个名字就可以一次找到所有这种Agent；
 * 		3) 各个Agent的takeDown()函数中，把自己在DF中注册的服务撤销；
 * 		4) 各个Agent在setup()时，为自己设置一个周期行为，定期向DF查询目前提供相关服务的Agent的AID。
 * @author zcl
 */
public class ServiceNamesOfAgent {

	public final static String INTERFACE="g-interface";
	public final static String LOGIN="user-login";
	public final static String CLICKLOG="click-log";
	public final static String REGISTER="user-register";
	public final static String MEMBER_SERACH="member-search";
	public final static String RESULT_MREGE="result-merge";
	public final static String SEARCH_ENTRY="search-entry"; 
	public final static String RESULT_RECOMMENDATION="result-recomm";
	public final static String QUERY_RECOMMENDATION="q-recomm";
	public final static String QUERY_GROUP_REOMMENDATION="gq-recomm";
	public final static String QUERY_QFG_RECOMMENDATION="qfgq-recomm";
	public final static String GROUP_DIVIDE = "group-divide";
	
}
