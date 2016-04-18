package common.entities.blackboard;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.ConfigFilePath;
import server.info.config.SpringBeanNames;
import server.info.entites.transactionlevel.UserInterestValueEntity;
import common.textprocess.userXMLfilehelpler.UserXMLHelper;
import common.utils.filelock.FileLockUtil;
import common.utils.filelock.FileOperationCallback;
import db.dao.UserDao;
import db.dbhelpler.UserInterestValueHelper;

public class UserAgentThread extends Thread /* implements Runnable */{
	private boolean flag;

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	private Integer userThreadId;//其实就是用户ID了

	public Integer getUserThreadId() {
		return userThreadId;
	}

	public void setUserThreadId(Integer userThreadId) {
		this.userThreadId = userThreadId;
	}

	@Override
	public void run() {
		System.out.println("用户:   " + userThreadId + " 线程 Start!!!");
		List<UserInterestValueEntity> ret= null;
		ret = UserInterestValueHelper.getEntitys(userThreadId);
		if (ret==null) {
			UserInterestValueHelper.updateUserInterestByInterestWords(userThreadId);
			ret = UserInterestValueHelper.getEntitys(userThreadId);
		}
		
		if (ret!=null) {
			GenerateUserGroup.getGroupCXL(userThreadId,ret);// 计算目标用户的群组用户
		}else{
			//还没有用户兴趣
		}
		
		
		UserAgent ua = new UserAgent();// 构造用户Agent
		ua.setUserid(userThreadId);// 把threadId（其实就是userId）作为userAgent对象ua的属性userid值
		ua.setUsername(userThreadId.toString());// 把threadId（其实就是userId）转化为String类型作为userAgent对象ua的属性username值
		// 构成该登录用户对应的xml文件并读取该文件
		String fileStart = ConfigFilePath.getUserXMLFileRoot();
		String fileEnd = ".xml";
		File file = new File(fileStart + userThreadId + fileEnd);

		if (!file.exists()) {
			UserXMLHelper.getInstance().createUserXMLFile(
					userThreadId.toString());
			UserXMLHelper.getInstance().update(userThreadId);
		}

		Map<String, Object> data=new HashMap<String, Object>();
		data.put("useragent", ua);
		FileLockUtil.getInstance().writeFile(file, data, new FileOperationCallback() {
			
			@Override
			public boolean doOperation(File file, Object data) {
				
				boolean ret=false;
				if(null==file||null==data) return ret;
				Map<String, Object> mapdata=(Map<String, Object>)data;
				UserAgent ua=(UserAgent) mapdata.get("useragent");
				ret=UserGroupDividerCB(file, ua);
				return ret;
			}
		});
		System.out.println("线程" + userThreadId + "已终止！！！");
	}// run方法结束

	private boolean UserGroupDividerCB(File file, UserAgent ua){
		
		if(null==file) return false;
		
		Document document = null;
		SAXReader reader = new SAXReader();// 读取xml文档
		try {
			document = reader.read(file);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List classificationList = document
				.selectNodes("/user/classification");
		// 遍历用户xml文件中每一个classification
		Iterator<Element> iterator1 = classificationList.iterator();
		// 1.将用户xml文件中的每个classification信息更新至黑板中
		while (iterator1.hasNext()) {
			Element classification = iterator1.next();
			// 获取黑板上对应classification，完成相关操作
			for (int i = 0; i < Blackboard.getClassification().size(); i++) {
				if (Blackboard
						.getClassification()
						.get(i)
						.getClassifiName()
						.equals(classification
								.attributeValue("classiName"))) {
					// Blackboard.getClassification().get(i).setClassifiName(classification.attributeValue("classiName"));
					// 判断该classification是否存在子节点interestWords，//将该用户Agent注册进公共兴趣黑板中对应兴趣分类下
					Iterator iterator2 = classification
							.elementIterator("interestWords");
					if (iterator2.hasNext()) {
						if (!(Blackboard.getClassification().get(i)
								.getUserMap().containsKey(userThreadId))) {
							Blackboard
									.getClassification()
									.get(i)
									.getUserMap()
									.put(ua.getUserid(),
											ua.getUsername());
							Blackboard.getClassification().get(i)
									.addObserver(ua);
						} else {
							System.out.println("用户id为："
									+ userThreadId
									+ "在"
									+ Blackboard.getClassification()
											.get(i).getClassifiName()
									+ "这个分类中已经存在！！！");
						}
						System.out.println(Blackboard
								.getClassification().get(i)
								.getUserMap().size());
						System.out.println(Blackboard
								.getClassification().get(i)
								.getClassifiName()
								+ " have  "
								+ Blackboard.getClassification().get(i)
										.countObservers()
								+ "   Observers!!!");
					}// iterator2的if结束
				}// 判断黑板中分类名和本次遍历的Element classification名是否相等的if结束
			}// for循环结束
		}// iterator1的while循环结束
	//notated by cxl 2015-07-31现在并没有用
//	UserDao userdao=(UserDao)SpringBeanFactoryUtil.getBean(SpringBeanNames.USER_DAO_BEAN_NAME);
//	List<Integer> uidlist=userdao.getAllUserID();
	GenerateUserGroup.getGroup(userThreadId);// 计算目标用户的群组用户
	return true;
	}
}
