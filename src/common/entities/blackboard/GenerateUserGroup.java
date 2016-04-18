package common.entities.blackboard;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.CategoryInfo;
import server.info.config.ConfigFilePath;
import server.info.config.SpringBeanNames;
import server.info.entites.transactionlevel.UserInterestValueEntity;
import common.functions.recommendation.group.UserGroupDivider;
import common.textprocess.userXMLfilehelpler.UserXMLHelper;
import common.utils.filelock.FileLockUtil;
import common.utils.filelock.FileOperationCallback;
import db.dao.UserGroupDao;

public class GenerateUserGroup {

	/**
	 * 获取用户的群组
	 * 
	 * @param userid
	 */
	public static void getGroup(int userid) {

		if (userid <= 0)
			return;

		UserXMLHelper.getInstance().checkUserXML(userid);// 拿到该用户的用户文件
		Map<String, String> strweight = getClassifyWeight(String
				.valueOf(userid));
		if (null == strweight)
			return;
		// 将获取的String类型的权值改为Double类型
		Map<String, Double> categoryToWeight = new HashMap<String, Double>();
		Iterator<Entry<String, String>> iterStrWeight = strweight.entrySet()
				.iterator();
		while (iterStrWeight.hasNext()) {
			Entry<String, String> pair = iterStrWeight.next();
			String className = pair.getKey(), weightInStr = pair.getValue();
			if (null != weightInStr && null != className) {
				double w = Double.parseDouble(weightInStr);
				categoryToWeight.put(className, w);
			}
		}// 转换完毕

		// 首先要确定用户应该属于的组的ID，如果相应的组信息不存在，还应该在数据库中建立相应的信息
		UserGroupDivider ugdivier = UserGroupDivider.getInstance();
		Set<Integer> uGroupIdSet = new HashSet<Integer>();
		ugdivier.getUserGroupID(categoryToWeight, uGroupIdSet);

		// 将用户的所属的组的信息更新
		UserGroupDao ugDao = (UserGroupDao) SpringBeanFactoryUtil
				.getBean(SpringBeanNames.USER_GROUP_DAO_BEAN_NAME);
		ugDao.updateUserGroup(userid, uGroupIdSet);

		return;
	}

	public static void getGroupCXL(int userid, List<UserInterestValueEntity> ret) {

		if (userid <= 0)
			return;

		Map<String, Double> categoryToWeight = getClassifyWeightCXL(userid,ret);
		
		// 首先要确定用户应该属于的组的ID，如果相应的组信息不存在，还应该在数据库中建立相应的信息
		UserGroupDivider ugdivier = UserGroupDivider.getInstance();
		Set<Integer> uGroupIdSet = new HashSet<Integer>();
		ugdivier.getUserGroupID(categoryToWeight, uGroupIdSet);

		// 将用户的所属的组的信息更新
		UserGroupDao ugDao = (UserGroupDao) SpringBeanFactoryUtil
				.getBean(SpringBeanNames.USER_GROUP_DAO_BEAN_NAME);
		ugDao.updateUserGroup(userid, uGroupIdSet);

		return;
	}

	private static Map<String, Double> getClassifyWeightCXL(int userid, List<UserInterestValueEntity> ret) {
		Map<String, Double> answer = new HashMap<String, Double>();
		if (ret!=null) {
			for (UserInterestValueEntity interest : ret) {
				answer.put(CategoryInfo.getENString(interest.getCategory_id()), interest.getValue());
			}
		}

		return answer;
	}

	/**
	 * 根据传入的用户ID，查找该用户的用户文件，
	 * 
	 * @param fileName
	 * @return 返回该用户文件的classiName（类别名，英文） 和 对应的权值
	 */
	public static Map<String, String> getClassifyWeight(String fileName) {

		String fileStart = ConfigFilePath.getUserXMLFileRoot();
		String fileEnd = ".xml";
		File file = new File(fileStart + fileName + fileEnd);

		Map<String, String> ret = new HashMap<String, String>();
		boolean result = false;
		while (!result) {
			result = FileLockUtil.getInstance().readFile(file, ret,
					new FileOperationCallback() {

						private boolean getClassificationWeightInUserXML(
								File file, Map<String, String> retData) {

							if (null == file || !file.exists()
									|| null == retData)
								return false;

							boolean ret = false;
							try {
								Document document = null;
								SAXReader reader = new SAXReader();
								document = reader.read(file);
								List classificationList = document
										.selectNodes("/user/classification");
								// 遍历用户xml文件中每一个classification
								Iterator<Element> ite = classificationList
										.iterator();
								while (ite.hasNext()) {
									Element classify = ite.next();
									retData.put(classify
											.attributeValue("classiName"),
											classify.attributeValue("weight"));
								}
								ret = true;
							} catch (Exception e) {
								e.printStackTrace();
							}

							return ret;
						}

						@Override
						public boolean doOperation(File file, Object data) {

							if (null == file || null == data || !file.exists())
								return false;

							boolean ret = false;
							Map<String, String> resultData = (Map<String, String>) data;
							ret = getClassificationWeightInUserXML(file,
									resultData);

							return ret;
						}
					});// result 生成结束

			/**
			 * 尝试读取失败，则重新生成userXML文件，再通过while循环读取 注意，不要在上面的回调函数里面直接调用下面的方法生成文件
			 * 因为： 1. 上面的回调函数获取的是读锁，没有阻止其他线程读文件，这里的写操作会引起读写冲突 2.
			 * 下面的方法会获取写锁。但是回调函数中，线程已经获得了这个文件的锁，再请求锁的时候线程会陷入死锁
			 */

			if (!result) {
				UserXMLHelper.getInstance().createUserXMLFile(fileName);
			}
		}

		return ret;
	}

}
