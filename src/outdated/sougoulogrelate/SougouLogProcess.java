//package outdated.sougoulogrelate;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStreamReader;
//import java.util.Calendar;
//import java.util.List;
//
//import org.apache.log4j.Logger;
//
//import server.commonutils.LogU;
//import server.commonutils.TimeUtil;
//import server.info.config.ConfigFilePath;
//import server.info.entites.transactionlevel.CategoryEntity;
//import server.info.entites.transactionlevel.ClickRecordEntity;
//import common.functions.userinterest.UserInterestModel;
//import common.textprocess.textclassifier.BayesClassifier;
//import common.textprocess.textsegmentation.CreateWordList;
//import common.textprocess.textsegmentation.GetWebText;
//import common.textprocess.textsegmentation.IK;
//import common.textprocess.textsegmentation.Word;
//import common.textprocess.textsegmentation.WordList;
//import db.dao.CategoryDao;
//import db.dao.ClickLogDao;
//import db.dao.LogToWordDao;
//import db.dao.UserDao;
//import db.dao.WordsDivisionDao;
//
//public class SougouLogProcess {
//
//	private static SougouLogProcess sgLogProcessor;
//	private String logFilePath;
//	private Calendar date;
//
//	private UserDao userDao;
//	private ClickLogDao clickLogDao;
//	private WordsDivisionDao wdDao;
//	private LogToWordDao log2wDao;
//	private CategoryDao categoryDao;
//	private Logger log;
//	
//	private Logger getLog(){
//		if(null==log) log=LogU.getInfoLogger(this.getClass());
//		return log;
//	}
//	
//	public SougouLogProcess() {
//
//	}
//
//	public static SougouLogProcess getInstance() {
//		if (null == SougouLogProcess.sgLogProcessor) {
//			synchronized (SougouLogProcess.class) {
//				sgLogProcessor = new SougouLogProcess();
//			}
//		}
//		return sgLogProcessor;
//	}
//
//	public CategoryDao getCategoryDao() {
//		return categoryDao;
//	}
//
//	public void setCategoryDao(CategoryDao categoryDao) {
//		this.categoryDao = categoryDao;
//	}
//
//	public UserDao getUserDao() {
//		return userDao;
//	}
//
//	public void setUserDao(UserDao userDao) {
//		this.userDao = userDao;
//	}
//
//	public ClickLogDao getClickLogDao() {
//		return clickLogDao;
//	}
//
//	public void setClickLogDao(ClickLogDao clickLogDao) {
//		this.clickLogDao = clickLogDao;
//	}
//
//	public WordsDivisionDao getWdDao() {
//		return wdDao;
//	}
//
//	public void setWdDao(WordsDivisionDao wdDao) {
//		this.wdDao = wdDao;
//	}
//
//	public LogToWordDao getLog2wDao() {
//		return log2wDao;
//	}
//
//	public void setLog2wDao(LogToWordDao log2wDao) {
//		this.log2wDao = log2wDao;
//	}
//
//	public SougouLogProcess(String logFilePath) {
//		this.logFilePath = logFilePath;
//	}
//
//	public Calendar getDate() {
//		if (null == date) {
//			date = Calendar.getInstance();
//			date.set(Calendar.YEAR, 2015);
//			date.set(Calendar.MONTH, 3);
//			date.set(Calendar.DAY_OF_MONTH, 29);
//		}
//		return date;
//	}
//
//	public void setDate(Calendar date) {
//		this.date = date;
//	}
//
//	public String getLogFilePath() {
//		if (null == this.logFilePath)
//			logFilePath = ConfigFilePath.getLogRootPath() + "Sougou-Logs";
//		return logFilePath;
//	}
//
//	public void setLogFilePath(String logFilePath) {
//		this.logFilePath = logFilePath;
//	}
//
//	public boolean process() {
//
//		boolean ret = true;
//		File procFileRoot = null;
//		File[] files = null;
//		this.getLog().info("搜狗日志处理中.....");
//		try {
//			procFileRoot = new File(this.getLogFilePath());
//			files = procFileRoot.listFiles();
//		} catch (Exception e) {
//			LogU.printConsole(this.getClass().getName(), "文件初始处理失败。异常："
//					+ e.toString());
//			return false;
//		}
//
//		if (null == files || 0 == files.length) {
//			LogU.printConsole(this.getClass().getName(), "没有文件。");
//			return false;
//		}
//
//		for (File curFile : files) {
//			int count = 1;
//			this.getLog().info("文件正在处理，第" + count + "个......");
//			ret = this.processSingleFile(curFile);
//			if (!ret) {
//				LogU.printConsole(this.getClass().getName(), "文件-"
//						+ curFile.getName() + "-处理失败。");
//				return ret;
//			}
//		}
//
//		this.getLog().info("搜狗日志处理完成。");
//		return ret;
//	}
//
//	private boolean processSingleFile(File file) {
//
//		if (null == file || !file.exists()) {
//			LogU.printConsole(this.getClass().getName(), "当前处理的文件不存在。");
//			return false;
//		}
//		boolean ret = true;
//		int lineNo = 1;
//		try {
//			BufferedReader br = new BufferedReader(new InputStreamReader(
//					new FileInputStream(file)));
//			SougouLogEntity record = new SougouLogEntity();
//
//			for (String line = br.readLine(); line != null; line = br
//					.readLine()) {
//
//				try {
//					record.init(line);
//					if (null == record.getTime()) {
//						LogU.printConsole(this.getClass().getName(),
//								"文件：" + file.getName() + "的第" + lineNo
//										+ "行格式不正确。");
//						continue;
//					}
//
//					ClickRecordEntity clickEntity = new ClickRecordEntity();
//					this.initClickLogEntity(record, clickEntity);
//
//					int uid = this.userDao.getUserIdByCookieid(record
//							.getCookie());
//					if (uid <= 0)
//						continue;
//					clickEntity.setUid(uid);
////					搜狗日志处理部分功能暂时不再用了
////					if (clickLogDao.userLogExistByTime(uid,
////							clickEntity.getDatetime(),
////							clickEntity.getDatetime()))
////						continue;
//
//					String[] webContent = this.setEntityWebContent(record,
//							clickEntity);
//					if (null == webContent
//							|| (webContent[0] == null || webContent[0]
//									.isEmpty())
//							&& (webContent[1] == null || webContent[1]
//									.isEmpty()))
//						continue;
//
//					String classification = this.updateUserInterestModel(uid,
//							record.getQuery(), webContent);
//					int catetoryId = this.categoryDao
//							.getCategoryIDByName(classification);
//					if (catetoryId <= 0)
//						continue;
//					clickEntity.setCategoryId(catetoryId);
//
//					CategoryEntity classRec = categoryDao.get(classification);
//					int categoryId;
//					if (null == classRec)
//						continue;
//					else
//						categoryId = classRec.getId();
//
//					String web = "";
//					for (int i = 0; i < webContent.length; ++i) {
//						if (webContent[i] != null)
//							web += webContent[i];
//					}
//					List<String> dividedWords = IK.fenci(web);
//
//					Integer recordId = this.clickLogDao.add(clickEntity);
////					List<Integer> wordsId = this.wdDao.addAll(dividedWords);
////					if (recordId != null && wordsId != null)
////						this.log2wDao.addAll(recordId, wordsId);
//					this.getLog().info("第"+lineNo+"行处理完毕。");
//					++lineNo;
//				} catch (Exception e) {
//					e.printStackTrace();
//					LogU.printConsole(this.getClass().getName(),
//							"文件" + file.getName() + "处理第" + lineNo + "行时异常："
//									+ e.toString());
//				}
//			}
//
//			br.close();
//		} catch (Exception e) {
//			this.getLog().info(
//					"文件" + file.getName() + "处理时异常。" + e.toString());
//			ret = false;
//		}
//
//		return ret;
//	}
//
//	private void initClickLogEntity(SougouLogEntity record,
//			ClickRecordEntity entity) {
//
//		if (null == record || null == entity)
//			return;
//
//		entity.setClickRank(record.getClickRank());
//		Calendar date = this.getDate();
//		Calendar time = TimeUtil
//				.fromTimeStringToCalendar(record.getTime(), ":");
//		date.set(Calendar.HOUR_OF_DAY, TimeUtil.getHour(time));
//		date.set(Calendar.MINUTE, TimeUtil.getMinute(time));
//		date.set(Calendar.SECOND, TimeUtil.getSecond(time));
//		entity.setDatetime(date.getTime());
//		entity.setQuery(record.getQuery());
//		entity.setRank(record.getRank());
//		entity.setUrl(record.getUrl());
//
//	}
//
//	private String[] setEntityWebContent(SougouLogEntity record,
//			ClickRecordEntity entity) {
//
//		if (null == record || null == entity)
//			return null;
//
//		String[] webContent = GetWebText.gettext(record.getUrl());
//		entity.setTitle(null == webContent[0] ? record.getQuery()
//				: webContent[0]);
//		entity.setAbstr(null == webContent[1] ? record.getQuery()
//				: webContent[1]);
//		return webContent;
//	}
//
//	private String updateUserInterestModel(int uid, String query,
//			String[] webContent) {
//
//		if (uid <= 0 || null == query || query.isEmpty() || null == webContent
//				|| webContent.length < 1)
//			return null;
//
//		WordList wl = CreateWordList.get(webContent);
//		wl.addWord(new Word(query, wl.getWord(0).getweight()));
//		String classification = BayesClassifier.bayes(wl);
//		UserInterestModel.udpateUserFavorWord(uid, wl, classification);
//
//		return classification;
//	}
//
//}
