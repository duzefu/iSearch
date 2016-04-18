package common.textprocess.userXMLfilehelpler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import server.commonutils.MyStringChecker;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.CategoryInfo;
import server.info.config.ConfigFilePath;
import server.info.config.SpringBeanNames;
import common.entities.blackboard.Interest;
import common.utils.filelock.FileLockUtil;
import common.utils.filelock.FileOperationCallback;
import db.dao.UserDao;

public class UserXMLHelper {

	private static UserXMLHelper userXMLHelper;

	private UserXMLHelper() {
	}

	public static UserXMLHelper getInstance() {

		if (userXMLHelper == null) {
			synchronized (UserXMLHelper.class) {
				userXMLHelper = new UserXMLHelper();
			}
		}

		return userXMLHelper;
	}

	private UserDao userDao;

	private UserDao getUserDao() {
		if (null == userDao)
			synchronized (this) {
				if(null==userDao) userDao = (UserDao) SpringBeanFactoryUtil
						.getBean(SpringBeanNames.USER_DAO_BEAN_NAME);
			}
		return userDao;
	}

	/**
	 * 检查用户XML文件是否存在，如果不存在就新建一个，并更新
	 * 
	 * @param userid
	 */
	public void checkUserXML(int userid) {

		File userXml = new File(ConfigFilePath.getUserXMLFileRoot() + userid + ".xml");
		if (!userXml.exists()) {
			createUserXMLFile(String.valueOf(userid));
			update(userid);
		}
	}

	/**
	 * 创建用户兴趣XML文件
	 * @param fileName
	 * @return
	 */
	public boolean createUserXMLFile(String fileName) {

		boolean ret = false;
		if (MyStringChecker.isBlank(fileName)) return false;

		try {

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userid", fileName);
			String filePath = ConfigFilePath.getUserXMLFileRoot() + fileName + ".xml";
			File file=new File(filePath);
			ret = FileLockUtil.getInstance().writeFile(file, data,
					new FileOperationCallback() {

						@Override
						public boolean doOperation(File file, Object data) {

							boolean ret = false;
							if (null == data) return ret;
							Map<String, Object> mapData = (Map<String, Object>) data;
							String userid = (String) mapData.get("userid");
							return createUserXMLFileProcess(file, userid);
						}

					});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private boolean createUserXMLFileProcess(File file, String userid) {

		boolean ret = false;

		if (null == file || null == userid || userid.isEmpty())
			return ret;
		try {
			if(!file.exists()||!file.isFile()){
				file.createNewFile();
			}
			FileOutputStream fouts = new FileOutputStream(file);
			// 用于处理xml文件的格式
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 编码格式
			format.setEncoding("utf-8");
			// Tab 缩进
			format.setIndentSize(4);

			XMLWriter writer = new XMLWriter(fouts, format);
			// 创建文档
			Document document = DocumentHelper.createDocument();
			// 创建根节点
			Element user = document.addElement("user");
			user.addComment("这是用户：" + userid + "的XML兴趣文件");
			user.addAttribute("userName", userid);

			Set<String> allCategory=new HashSet<String>();
			CategoryInfo.getAllCategoryEnName(allCategory);
			for (Iterator<String> it=allCategory.iterator();it.hasNext();) {
				String catName=it.next();
				Element classification = user.addElement("classification");
				classification.addComment("这是兴趣分类"
						+ catName + "节点");
				classification.addAttribute("classiName",
						catName);
				classification.addAttribute("weight", "0.00");
			}
			writer.write(document);
			writer.close();
			ret = true;
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}

	public boolean update() {

		boolean ret = false;
		try {
			List<Integer> uidlist = this.getUserDao().getAllUserID();
			if (null == uidlist)
				return ret;
			ret = update(uidlist);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public boolean update(List<Integer> uidlist) {

		if (null == uidlist)
			return false;
		if (uidlist.isEmpty())
			return true;

		boolean ret = false;
		for (int i = 0; i < uidlist.size(); ++i) {
			ret = update(uidlist.get(i));
		}

		return ret;

	}

	public boolean update(int uid) {

		boolean ret = false;
		List<Interest> interestList = new ArrayList<Interest>();
		GetDBData gd = new GetDBData();

		try {
			// 查找user_favor_words表中对应用户id为userList.get(i).getUserid()的兴趣词word
			interestList = gd.findInterests(uid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// 若该用户存在兴趣记录，则初始化用户xml文件中interestWords标签
		if (null != interestList && interestList.size() > 0)
			ret = updateUserXMLProcess(uid, interestList);
		return ret;
	}

	private boolean updateUserXMLProcess(int uid, List<Interest> interestList) {

		boolean ret = false;
		if (uid <= 0 || null == interestList)
			return ret;
		if (interestList.isEmpty())
			return true;

		File file = new File(ConfigFilePath.getUserXMLFileRoot() + uid + ".xml");
		if (!file.exists())
			createUserXMLFile(String.valueOf(uid));

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("interest", interestList);
		data.put("uid", uid);
		ret = FileLockUtil.getInstance().writeFile(file, data,
				new FileOperationCallback() {

					@Override
					public boolean doOperation(File file, Object data) {

						if (null == data)
							return false;
						Map<String, Object> mdata = (Map<String, Object>) data;
						List<Interest> interestList = (List<Interest>) mdata
								.get("interest");
						int uid = (int) mdata.get("uid");
						if (null == interestList || interestList.isEmpty())
							return true;
						return updateUserXMLCB(file, interestList, uid);
					}

				});

		return ret;
	}

	private boolean updateUserXMLCB(File file, List<Interest> interestList,
			int uid) {

		boolean ret = false;

		Document document = null;
		// 读取xml文档
		SAXReader reader = new SAXReader();
		while (null == document) {
			try {
				document = reader.read(file);
			} catch (Exception e) {

			}
			if (null == document)
				createUserXMLFileProcess(file, String.valueOf(uid));
		}
		DelInterestWords(document);
		ret = GenNewUserXMLData(file, document, interestList);

		return ret;
	}

	private boolean GenNewUserXMLData(File file, Document document,
			List<Interest> interestList) {

		/**
		 * 下面是重新生成用户兴趣XML文件的过程 步骤如下： 一、 第一层for循环——逐一遍历user_favor_words中取得的各个兴趣词
		 * 二、 while循环——逐一检查userXML文件中的各个类别，并处理： 1.
		 * 如果当前的兴趣词是属于当前类别的，在userXML文件中，当前类别结点下创建一个兴趣词结点；
		 * 并且根据user_favor_words中的记录添加时间、权值属性。 2. 类别权重直接累加各兴趣词的权重。
		 */

		Interest interest = null;
		/************ 第一步 *******************/
		for (int j = 0; j < interestList.size(); j++) {
			interest = interestList.get(j);
			// 用户XML中所有类别结点形成链表
			List list = document.selectNodes("/user/classification");
			/**************** 第二步 ***************/
			// 逐一遍历每一个类别
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				Element classification = (Element) iterator.next();
				// 处理当前类别的权重
				Attribute classify_weight = classification.attribute("weight");
				/********************** 第二步-1 *****************/
				if (classification.attributeValue("classiName").equals(
						interest.getOwerClassifi())) {
					// 在当前类别结点下添加一个兴趣词结点
					Element interestWords = DocumentHelper
							.createElement("interestWords");
					interestWords.addAttribute("interestName", interestList
							.get(j).getWordname());
					String weight = interestList.get(j).getValue().toString();
					interestWords.addAttribute("weight", weight);
					interestWords.addAttribute("time", "");
					classification.add(interestWords);

					/********************* 第二步-2 ***************/
					double value = Double.parseDouble(classify_weight
							.getValue());
					classify_weight.setValue(String.valueOf(value
							+ Double.parseDouble(weight)));
					break;
				}// if结束
			}// while结束
		}// for结束

		// 用户XML文件已经重新生成，下面是格式化XML文件
		try {
			// 新建format用来格式化xml文件
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8"); // 编码格式
			format.setIndentSize(4); // Tab 缩进
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			writer.write(document);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	private void DelInterestWords(Document doc) {

		if (null == doc)
			return;
		List classificationList = doc.selectNodes("/user/classification");
		Iterator<Element> itera = classificationList.iterator();
		while (itera.hasNext()) {
			Element classi = itera.next();
			// 遍历当前类别下的每一个兴趣词结点
			Iterator itera2 = classi.elementIterator("interestWords");

			// 移除所有interestWords节点
			while (itera2.hasNext()) {
				Element word = (Element) itera2.next();
				classi.remove(word);
			}
			Attribute classi_weight = classi.attribute("weight");
			classi_weight.setValue("0.00");
		}
	}

}
