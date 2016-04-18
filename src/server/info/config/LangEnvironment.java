package server.info.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import server.commonutils.MyStringChecker;

/**
 * 指定当前系统的工作语言环境
 * 这个类与VisibleStringForClient以及PicturePathForClient联系密切，
 * 如果客户端类型变化，那两个类也肯定要修改，
 * 这里为了让两个类都共用一些信息才独立写这个类
 * @author zhou
 */
public class LangEnvironment {
	
	/**
	 * 获得相应类型客户端的语言环境
	 * @param type 客户端类型
	 * @return
	 */
	public final static LangEnv currentEnv(ClientType type){
		//故意不检查空指针异常，便于找到bug
		return CUR_LANG[type.ordinal()];
	}
	
	/**
	 * 把字符串表示的语言信息转为枚举类型
	 * @param lang 字符串，不能有错；否则应该使用tryParseLang函数
	 * @return
	 */
	public final static LangEnv parseLang(String lang){
		return LangEnv.valueOf(lang);
	}
	
	/**
	 * 把字符串表示的语言信息转为枚举类型
	 * @param lang 字符串，为null或有错时，会返回null
	 * @return
	 */
	public final static LangEnv tryParseLang(String lang){
		
		LangEnv ret=null;
		try {
			lang = lang.trim();
			if (!MyStringChecker.isBlank(lang))
				ret = LangEnv.valueOf(lang);
		} catch (Exception e) {

		}
		return ret;
	}
	
	private final static String CONFIG_FILE_PATH=ConfigFilePath.getConfigFileRoot()+"lang.conf";	//配置文件路径
	
	//系统可以支持的语言环境，变量名也是配置文件中等号右侧可以写的内容
	public enum LangEnv{cn, en};	
	//“客户端”类型，变量名也是配置文件中等号右侧可以写的内容
	public enum ClientType{web, android};
	//实际配置完成之后的值
	//这个值是ClientType对应位置类型客户端的语言环境
	private static LangEnv CUR_LANG[]={
		LangEnv.cn,
		LangEnv.cn
	};
	
	static{
		configLang();
	}
	
	private static void configLang(){
		//临时存储配置文件中的值，当配置文件读取完成且无误时，再写到CUR_LANG数组中
		LangEnv configValues[]=new LangEnv[ClientType.values().length];
		BufferedReader reader=null;
		try{
			File file=new File(CONFIG_FILE_PATH);
			if(file.exists()){
				reader=new BufferedReader(new FileReader(file));
				String line=null, left=null, right=null;
				while((line=reader.readLine())!=null){
					line=line.trim();
					if(line.isEmpty()||line.startsWith("#")) continue;
					String[] tmp=line.split("=");
					if(null==tmp||tmp.length!=2) return;
					left=tmp[0];
					right=tmp[1];
					configValues[ClientType.valueOf(left).ordinal()]=LangEnv.valueOf(right);
				}
				for(int i=0;i<CUR_LANG.length;++i){
					LangEnv confVal=configValues[i];
					if(null==confVal) break;
					CUR_LANG[i]=confVal;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(null!=reader)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
