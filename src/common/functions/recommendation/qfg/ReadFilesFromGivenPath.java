//package common.functions.recommendation.qfg;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//
//import server.info.config.ConfigFilePath;
//
///**
// * ReadFilesFromGivenPath is used to get query info from file.
// * 
// * @author jtr
// * @version 1.0
// * @since 2014.5.19
// */
//public class ReadFilesFromGivenPath {
//	
//	
//	private String fileName = ConfigFilePath.getConfigFileRoot()
//			+ "Sougou-Logs/SougouQ.reduced";
//	private String newLine;
//	private AllUsersQuerySession qs = new AllUsersQuerySession();
//
//	// constructor
//	public ReadFilesFromGivenPath(String fileName) {
//		if (fileName != null && fileName != "") {
//			this.fileName = fileName;
//		}
//	}
//
//	// getter and setter
//	public AllUsersQuerySession GetAllUsersQuerySession() {
//		return qs;
//	}
//
//	/**
//	 * 实现从文件中读入数据，并按用户归类，每一个查询词下所有的查询记录按时间排序
//	 * 并找到其中时间间隔超过30分钟的查询记录
//	 */
//	public void MiningUsefulInfo() {
//		try {
//			// open file and read line by line
//			BufferedReader br = new BufferedReader(new FileReader(fileName));
//			String strOrig = null;
//			//逐行把搜索日志转成三元组（用户、查询词、时间），添加到以用户为标识的Map中
//			while ((strOrig = br.readLine()) != null
//					&& (newLine = new String(strOrig)) != null) {
//				DealWithUserQuery();
//			}
//			br.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		qs.FinishJob();
//		qs.PrintMyself();
//	}
//
//	// private method
//	private void DealWithUserQuery() {
//		String[] tempStringGroup = newLine.split("\t");
//		String query=tempStringGroup[2], cookie=tempStringGroup[1], time=tempStringGroup[0];
//		if(null!=query&&query.length()>2) query=query.substring(1,query.length()-1);
//		else return;
//		QueryTriple tempQueryTriple = new QueryTriple(query, cookie, time);
//		qs.AddUserQuery(tempQueryTriple);
//	}
//}
