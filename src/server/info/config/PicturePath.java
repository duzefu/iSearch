package server.info.config;

import server.info.config.LangEnvironment.ClientType;
import server.info.config.LangEnvironment.LangEnv;

public class PicturePath {

	/**
	 * 获取网页上图片的相对路径，相对于项目根目录WebRoot，
	 * 如果使用这个路径的文件不位于项目根目录下，应该在获得的路径前拼接../得到正确的路径
	 * @param content
	 * @return
	 */
	public final static String getWebpageContent(PictureType content){
		return LangEnvironment.currentEnv(ClientType.web).equals(LangEnv.cn)?PATH_CN[content.ordinal()]:PATH_EN[content.ordinal()];
	}
	public final static String getWebpageContent(PictureType content, LangEnv lang){
		return (null==lang?(LangEnvironment.currentEnv(ClientType.web)):lang).equals(LangEnv.cn)?PATH_CN[content.ordinal()]:PATH_EN[content.ordinal()];
	}
	
	public enum PictureType{
		/*1*/result_from_picture,					//搜索结果来自于图片路径
};
private final static String PATH_CN[]={
		/*1*/"images/source2.png",
};
private final static String PATH_EN[]={
		/*1*/"images/source2.png",
};
}
