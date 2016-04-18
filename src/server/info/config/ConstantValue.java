//package server.info.config;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Set;
//
//import org.dom4j.Document;
//import org.dom4j.DocumentException;
//import org.dom4j.io.SAXReader;
//import org.dom4j.Element;
//
//import server.commonutils.CharUtil;
//import server.commonutils.MyStringChecker;
//import server.info.config.ConfigFilePath;
//
///**
// * 管理一些常量值，如搜索引擎名字、主题类别名字、搜索引擎得分
// * （已废除）如果以后需要恢复调度，应该重新考虑如何与EngineFactory结合起来
// * 如果恢复：
// * 		1）可以从文件读入（一次性，在服务器启动后读入）或从数据库读入（动态性比较强）
// * 		2）读入时，搜索引擎应该以EngineFactory为准，文件或数据库中的名字适应它，即使文件中的搜索引擎名字多，多出来的应该被视为无效的
// * @author zhou
// */
//public class ConstantValue {
//
//	/*
//	 *  这个类中的数据应该与ConstString中的数据分开，
//	 * 这个类中的数据应该是从特定的地方读入的（文件、数据库），
//	 * 如果要从数据库中读数据，这个类的初始化应该延迟到Hibernate配置结束之后，
//	 * 而ConstString中的数据应该是第一个初始化的，不能依赖于别的类。
//	 */
//	private static int classCount;
//	private static int engineCount;
//	
//	private static String rootDirectory = ConfigFilePath.getLogRootPath();
//	private static String engineFilepath = rootDirectory + "EngineList.xml";
//	private static String subjectFilepath = rootDirectory + "SubjectList.xml";
//	private static String conceptFilepath = rootDirectory + "conceptScore.xml";
//	private static String logFilepath = rootDirectory + "logScore.xml";
//	private static String logXMLFilepath = logFilepath;
//	private static String conceptXMLFilepath = conceptFilepath;
//	
//	public static String[] classNames;
//	public static String[] engineNames;
//	public static double[][] concept_score;
//	public static double[][] log_score;
//
//	/**
//	 * 从文件中读入： 1）搜索引擎能力评价矩阵； 2）搜索引擎中英文名称。
//	 */
//	public static void init() {
//
//		initClasses(subjectFilepath);
//		initEngine(engineFilepath);
//		concept_score = new double[engineNames.length][classNames.length];
//		log_score = new double[engineNames.length][classNames.length];
//		initMatrix(concept_score, conceptXMLFilepath);
//		initMatrix(log_score, logXMLFilepath);
//	}
//
//
//	private static Map<String, String> getEngNameCnToEn() {
//		if (null == engNameCnToEn)
//			engNameCnToEn = new HashMap<String, String>();
//		return engNameCnToEn;
//	}
//
//	/**
//	 * 项目中分类主题的初始化
//	 * @param filepath
//	 */
//	private static void initClasses(String filepath) {
//		SAXReader sax = new SAXReader();
//		File file = new File(filepath);
//		try {
//			Document doc = sax.read(file);
//			Element root = doc.getRootElement();
//			Element subjectListRoot = root.element("SubjectList");
//
//			Iterator iterator = subjectListRoot.elementIterator("Subject");
//			ArrayList subjectlist = new ArrayList<String>();
//
//			while (iterator.hasNext()) {
//				Element elem = (Element) iterator.next();
//				subjectlist.add(elem.getText());
//
//			}
//
//			classNames = new String[subjectlist.size()];
//			for (int i = 0; i < subjectlist.size(); i++)
//				classNames[i] = (String) subjectlist.get(i);
//
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//
//		classCount = null == classNames ? 0 : classNames.length;
//
//	}
//
//	// 得到成员搜索引擎信息
//	private static void initEngine(String filepath) {
//
//		SAXReader sax = new SAXReader();
//		File file = new File(filepath);
//		try {
//			Document doc = sax.read(file);
//			Element root = doc.getRootElement();
//			Element enginelistRoot = root.element("EngineList");
//			Iterator iterator = enginelistRoot.elementIterator("Engine");
//			ArrayList enginelist = new ArrayList<String>();
//
//			while (iterator.hasNext()) {
//				Element elem = (Element) iterator.next();
//				String enName = elem.elementText("en_Name"), cnName = elem
//						.elementText("cn_Name");
//				enginelist.add(enName);
//				ConstantValue.getEngNameEnToCn().put(enName, cnName);
//				ConstantValue.getEngNameCnToEn().put(cnName, enName);
//			}
//
//			engineNames = new String[enginelist.size()];
//			for (int i = 0; i < enginelist.size(); i++)
//				engineNames[i] = (String) enginelist.get(i);
//
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//
//		engineCount = engineNames == null ? 0 : engineNames.length;
//	}
//
//	private static void initMatrix(double[][] scores, String filepath) {
//		SAXReader sax = new SAXReader();
//		File file = new File(filepath);
//		if (file.exists()) {
//			try {
//				Document doc = sax.read(file);
//				Element root = doc.getRootElement();
//				Iterator iterator = root.elementIterator("Score");
//				int i = 0;
//				while (iterator.hasNext()) {
//					Element elem = (Element) iterator.next();
//					for (int j = 0; j < classNames.length; j++) {
//						String str = elem.elementText(classNames[j]);
//
//						if (str.length() < 7)
//							str += "1111";
//
//						double value = Double.parseDouble(str.substring(0, 5));
//
//						scores[i][j] = value;
//					}
//					i++;
//
//				}
//
//			} catch (DocumentException e) {
//				e.printStackTrace();
//			}
//
//		}
//		return;
//	}
//
//	public static int getClassCount() {
//
//		if (classCount <= 0)
//			classCount = null == classNames ? 0 : classNames.length;
//		return classCount;
//	}
//
//	public static int getEngineCount() {
//
//		if (engineCount <= 0)
//			engineCount = null == engineNames ? 0 : engineNames.length;
//		return engineCount;
//	}
//
//	public static String getClassName(int i) {
//
//		if (i < 0 || null == classNames || classNames.length == 0
//				|| i >= classNames.length)
//			return null;
//		return classNames[i];
//	}
//
//	public static String getEngineName(int i) {
//
//		if (i <= 0 || null == engineNames || engineNames.length == 0
//				|| i >= engineNames.length)
//			return null;
//		return engineNames[i];
//	}
//	
//}
