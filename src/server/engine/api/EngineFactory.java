package server.engine.api;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import server.commonutils.MyStringChecker;

public class EngineFactory {
	
	private final static String API_PACKAGE_PATH="server.engine.api";
	
	/*
	 * 这个类是基础类，应该尽可能的考虑效率
	 * 所以下面用了数组来管理，后面修改这个类时，注意这些数组是有对应关系的
	 * 相关函数也应该尽可能写得让编译器生成内联函数（尽可能加final，并且函数执行过程尽量简单）
	 * 
	 * 对于接受字符串的函数，这个类不检查，不捕捉异常，便于及早发现代码错误
	 * 这个类抛出的异常基本上可以确定是其他地方写的代码有BUG
	 */
	
	/*
	 * 搜索引擎名称-编号映射表
	 * 用于将各类字符串名字互相转换，通过字符串得到内部编号（下面各个数组的索引号），从而进一步得到各类信息
	 * 目前系统里面有三类可能的字符串：
	 * 		1）中文名字
	 * 		2）枚举变量对应的名字（也是以前代码使用的名字，也被用在了html的元素中）
	 *		3）网页上要显示的英文名字（在英文环境时要用，与前面一种的区别是首字母是大写的）
	 *	这两个Hash表存放的是1与3情况的名字，对于2，可以直接得到枚举变量；
	 * 这三种类型的字符串都是有必要的，用途分别是：
	 * 		1与3都是可见的名字，除了在页面上可以看到的地方，其他地方一律不应该用这种字符串形式；
	 * 		2能与枚举变量直接对应，对于不需要显示在页面上的，而又必须以字符串存储的（如数据库或html元素中），就用这个
	 */
	private static Map<String, Integer> cnNameToNo=new HashMap<String, Integer>();
	private static Map<String, Integer> enNameToNo=new HashMap<String, Integer>();
	private static List<EngineName> allEngines;
	/**
	 * 搜索引擎名称（枚举）
	 * 变量名不要乱改，为了兼容以前的代码，枚举变量 toString()以后就是以前代码使用的搜索引擎名字
	 */
	public static enum EngineName {
		/*1*/baidu,
		/*2*/youdao,
		/*3*/sougou,
		/*4*/yahoo,
		/*5*/bing
	};
	/**
	 * 搜索引擎中文名称数组，用于内部计算完成后，转换产生网页上显示的名称
	 * 在以前的代码中，Result类的source域就使用了这些字符串
	 */
	private final static String cnNameArr[]={
		/*1*/"百度",
		/*2*/"有道",
		/*3*/"搜狗",
		/*4*/"雅虎",
		/*5*/"必应"
	};
	/**
	 * 搜索引擎可显示的英文名字，用于英文环境下显示的内容
	 */
	private final static String enNameArr[]={
		/*1*/"Baidu",
		/*2*/"Youdao",
		/*3*/"Sogou",
		/*4*/"Yahoo!",
		/*5*/"Bing"
	};
	/**
	 * 搜索引擎API类路径
	 */
	private final static String ENGINE_CLASS_PATH[] = {
		/*1*/API_PACKAGE_PATH+".Baidu",
		/*2*/API_PACKAGE_PATH+".Youdao",
		/*3*/API_PACKAGE_PATH+".Sogou",
		/*4*/API_PACKAGE_PATH+".Yahoo",
		/*5*/API_PACKAGE_PATH+".Bing"
	};
	/**
	 * 搜索结果中，点击成员搜索引擎名字就跳转到相应的成员搜索引擎结果页面，这是基础URL
	 */
	private final static String RESULTPAGE_URL_BASE[]={
		/*1*/"http://www.baidu.com/s?wd=",
		/*2*/"http://www.youdao.com/search?q=",
		/*3*/"http://www.sogou.com/web?query=",
		/*4*/"https://search.yahoo.com/search?toggle=1&cop=mss&ei=UTF-8&fr=yfp-t-308&fp=1&p=",
		/*5*/"http://cn.bing.com/search?q="
	};
	private final static Color COLOR_IN_PIEGRAPH[]={
		/*1*/Color.BLUE,
		/*2*/Color.RED,
		/*3*/Color.CYAN,
		/*4*/Color.ORANGE,
		/*5*/Color.GREEN
	};
	
	private static Class classArr[] = new Class[ENGINE_CLASS_PATH.length];
	
	/*
	 * 以下成员变量完全是为了兼容现在代码而设置
	 */
	private static Set<String> engCnName;
	private static Set<String> engEnName;
	
	static{
		try{
			allEngines=new ArrayList<EngineName>();
			EngineName[] allVal=EngineName.values();
			for(int i=0;i<ENGINE_CLASS_PATH.length;++i){
				String path=ENGINE_CLASS_PATH[i];
				classArr[i]=Class.forName(path);
				cnNameToNo.put(cnNameArr[i], i);
				enNameToNo.put(enNameArr[i], i);
				allEngines.add(allVal[i]);
			}
			
			/******为了保证兼容以前代码的部分*****/
			engCnName=new HashSet<String>();
			engEnName=new HashSet<String>();
			for(int i=0;i<cnNameArr.length;++i){
				engCnName.add(cnNameArr[i]);
			}
			EngineName names[]=EngineName.values();
			for(int i=0;i<names.length;++i){
				engEnName.add(names[i].toString());
			}
			/******为了保证兼容以前代码的部分*****/
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 尝试将字符串转成内部使用的枚举类型，如果字符串是错的，则返回null，
	 * 在处理客户端来的数据时应该用这个函数来转换，并检查返回值，
	 * 对于内部数据，确定字符串不可能是错的时，可以用getInnerEngineName函数来转换，
	 * 对于怎样对待错误的字符串名字，应该由调用者来考虑
	 * @param name 搜索引擎名字
	 * @return
	 */
	public final static EngineName tryParseString(String name){
		
		EngineName ret=null;
		try{
			if(!MyStringChecker.isBlank(name)) ret=getInnerEngineName(name.trim());
		}catch(Exception e){
			
		}
		return ret;
	}
	/**
	 * 用于把搜索引擎名称转为服务器内部使用的枚举类型（三种形式的字符串都可以），
	 * 这个函数应该是在系统内部需要转换时使用，确定参数不会有错时使用，
	 * @param strName 搜索引擎名称（不能传服务器不支持的名字，也不能传空指针，会导致这个函数抛异常）
	 * @return 返回搜索引擎对应的枚举类型
	 */
	public final static EngineName getInnerEngineName(String strName){
		
		/*
		 * 注：这里不处理空指针异常，或者传入的名字异常，便于查错；
		 */
		int pos=getPosOfStrName(strName);
		return EngineName.values()[pos];
	}
	
	private final static int getPosOfStrName(String strName){
		
		Integer pos=cnNameToNo.get(strName);
		if(null!=pos) return pos.intValue();
		pos=enNameToNo.get(strName);
		if(null!=pos) return pos.intValue();
		EngineName eng=EngineName.valueOf(strName);//如果传入的字符串是错的，这里就会抛异常
		return eng.ordinal();
	}
	
	/**
	 * 获得搜索引擎的字符串形式的名字（中文）
	 * @param enuName
	 * @return
	 */
	public final static String getVisibleCnName(EngineName enuName){
		return cnNameArr[enuName.ordinal()];
	}
	/**
	 * 获得搜索引擎的字符串形式的名字（英文）
	 * @param enuName
	 * @return
	 */
	public final static String getVisibleEnName(EngineName enuName){
		return enNameArr[enuName.ordinal()];
	}
	/**
	 * 获得搜索引擎英文形式的名字（与枚举变量直接对应）
	 * @param enuName
	 * @return
	 */
	public final static String getEnNameString(EngineName enuName){
		return enuName.toString();
	}
	/**
	 * 获取搜索引擎对象
	 * @param name 搜索引擎名称
	 * @return
	 */
	public static AbstractEngine getEngine(EngineName name){
	
		AbstractEngine ret=null;
		
		try{
			ret=(AbstractEngine)classArr[name.ordinal()].newInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * 获得成员搜索引擎结果页面的链接
	 * @param query 查询词，直接拼接在基础URL后面，如果成员搜索引擎可能需要对中文进行编码（%20之类），在调函数之前做
	 * @param eng 成员搜索引擎名字
	 * @return 
	 */
	public final static String getResultPageUrl(EngineName eng, String query){
		/*
		 * 为了让这个函数更有可能变成内联函数，以目前这种方式实现
		 * 也不在这里做query=URLEncoder.encode(query)，如果有需要，在调用之前完成这个动作
		 */
		return null==eng?"":(RESULTPAGE_URL_BASE[eng.ordinal()]+(null==query?"":query));
	}
	
	public final static Color getShowColorOfEngine(EngineName engine){
		return COLOR_IN_PIEGRAPH[engine.ordinal()];
	}
	
	/*
	 * 以下函数完全是为了兼容现有代码而设置
	 */
	
	/**
	 * 获取搜索引擎对象（向以前的方式兼容）
	 * @param engineName 搜索引擎名字（中英文都可以）
	 * @return
	 */
	public static AbstractEngine engineFactory(String engineName) {
		
		EngineName name=getInnerEngineName(engineName);
		return getEngine(name);
	}

	/**
	 * 兼容以前的代码，获得搜索引擎对应的结果页面的链接
	 * @param eng 搜索引擎名字，中英文均可
	 * @param query 查询词
	 * @return
	 */
	public static String getResultPageUrl(String eng, String query){
		return getResultPageUrl(getInnerEngineName(eng), query);
	}
	
	/**
	 * 获取系统支持的所有搜索引擎的中文名称
	 * 不是通过深拷贝返回，所以返回的名称应该是只读，不要修改或删除
	 * @return
	 */
	public final static Set<String> getAllEngineCnName() {
		return engCnName;
	}
	/**
	 * 获取系统支持的所有搜索引擎的英文名称（不是页面上显示的版本，即百度是baidu而不是Baidu）
	 * 不是通过深拷贝返回，所以返回的名称应该是只读，不要修改或删除
	 * @return
	 */
	public final static Set<String> getAllEngineEnName() {
		return engEnName;
	}
	
	/**
	 * 获得搜索引擎的英文名称
	 * @param engineName 搜索引擎的名称（中英均可，但不能带有多余字符，包括空白字符）
	 * @return
	 */
	public final static String getEnEngineName(String engineName) {
		
		EngineName name=getInnerEngineName(engineName);
		return getEnNameString(name);
	}

	/**
	 * 获得搜索引擎的中文名称
	 * @param engineName
	 * @return
	 */
	public final static String getCnEngineName(String engineName) {

		EngineName innName=getInnerEngineName(engineName);
		return getVisibleCnName(innName);
	}
	
	public final static void getAllEngineNames(Set<EngineName> ret){
		
		if(null==ret) return;
		EngineName allName[]=EngineName.values();
		for(int i=0;i<allName.length;++i){
			ret.add(allName[i]);
		}
	}
	
	/**
	 * 获得一个迭代器，用来读取所有的搜索引擎名字；
	 * 注意迭代器应该是只读的，不要调用迭代器的remove函数，否则会导致出错
	 * @return
	 */
	public final static Iterator<EngineName> getAllEngineIterator(){
		return allEngines.iterator();
	}
	public final static Color getShowColorOfEngine(String engine){
		return getShowColorOfEngine(getInnerEngineName(engine));
	}
	
}
