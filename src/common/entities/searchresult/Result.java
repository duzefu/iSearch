package common.entities.searchresult;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import jdk.nashorn.internal.ir.ReturnNode;
import server.commonutils.MyStringChecker;
import server.engine.api.EngineFactory;
import server.engine.api.EngineFactory.EngineName;
import server.info.config.VisibleConstant;
import server.info.config.VisibleConstant.ContentNames;
import server.info.entites.transactionlevel.ClickRecordEntity;

public class Result implements Serializable, Comparable<Object> {

	private static final long serialVersionUID = 1L;
	private String title;
	private String abstr;
	private String link;
	private String date;
	private String spare;
	private String source; 
	private double value;
	private String classification;
	private String[] array = new String[10];
	private Set<String> m_setSrcCnName;// 记录了来源搜索引擎的名称（中文）
	private Set<String> m_setSrcEnName;// 记录了来源搜索引擎的名称（英文）

	public String[] getArray() {
		return array;
	}

	/**
	 * 结果来源拆分，存在数组里
	 * 
	 * @param source
	 */
	public void setArray(String source) {
		this.array = source.split(" ");
	}

	public Result(String title, String abstr, String link, String source) {
		this.title = title;
		this.abstr = abstr;
		this.link = link;
		setSource(source);
	}

	public Result(String title, String abstr, String link, String source,
			double value) {
		this.title = title;
		this.abstr = abstr;
		this.link = link;
		this.source = source;
		this.value = value;
	}

	public Result(String title, String abstr, String link, String date,
			String spare, String source) {
		this.title = title;
		this.abstr = abstr;
		this.link = link;
		this.date = date;
		this.spare = spare;
		setSource(source);
	}

	public String getTitle() {
		return title;
	}

	public String getAbstr() {
		return abstr;
	}

	public String getLink() {
		return link;
	}

	public String getDate() {
		return date;
	}

	public String getSpare() {
		return spare;
	}

	public final String getSource() {
		return source;
	}

	public double getValue() {
		return value;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAbstr(String abstr) {
		this.abstr = abstr;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setSource(String source) {
		this.source = source;
		setSourceEngineName();
	}

	private final void setSourceEngineName() {

		if (MyStringChecker.isBlank(source))
			return;

		m_setSrcCnName = new HashSet<String>();
		m_setSrcEnName = new HashSet<String>();
		Set<String> allEngName = EngineFactory.getAllEngineCnName();
		Iterator<String> iterAllEngName = allEngName.iterator();
		String source = this.getSource();
		while (iterAllEngName.hasNext()) {
			String curEname = iterAllEngName.next();
			if (source.contains(curEname)) {
				m_setSrcCnName.add(curEname);
				m_setSrcEnName.add(EngineFactory.getEnEngineName(curEname));
			}
		}
	}

	public void setSpare(String spare) {
		this.spare = spare;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	/**
	 * 以中文字符串形式，给出搜索结果所有来源的搜索引擎名称
	 * 
	 * @return
	 */
	public Set<String> getSourceEngineCnName() {

		if (null == m_setSrcCnName || m_setSrcCnName.isEmpty())
			setSourceEngineName();
		return m_setSrcCnName;
	}

	public Set<String> getSourceEngineEnName() {

		if (null == m_setSrcEnName || m_setSrcEnName.isEmpty())
			setSourceEngineName();
		return m_setSrcEnName;
	}

	/**
	 * 检查当前这个结果是不是可用的（标题与URL是必须的，如果没有就认为是错误的结果）
	 * 
	 * @return
	 */
	public boolean isUsable() {

		return null != title && !title.isEmpty() && null != link
				&& !link.isEmpty();
	}

	/**
	 * 检查当前的结果是不是来自特定的搜索引擎（集合）
	 * 
	 * @param engNames
	 *            搜索引擎英文或中文名称
	 * @return 可能有错误，暂时不使用
	 */
	/*
	 * public boolean isFromTargetEngine(Set<String> engNames,Set<String>
	 * themNames){
	 * 
	 * boolean ret=false; // if(null==engNames||engNames.isEmpty()) return ret;
	 * if (null==engNames || null==themNames) {//为空判断 return ret; }else if
	 * (engNames.size()==0 && themNames.size()==0) {//没有选择引擎筛选和主题筛选 ret = true;
	 * }else{//只选择了按照引擎筛选 Set<String> srcEn=getSourceEngineEnName(),
	 * srcCn=getSourceEngineCnName(); Iterator<String> it=engNames.iterator();
	 * while(it.hasNext()){ String tarEname=it.next();
	 * if(StringChecker.isBlank(tarEname)) continue; if (engNames.size()>0 &&
	 * themNames.size()>0) { //即选择了主题，又选择了引擎
	 * if((isFromTargetEngine(srcEn,tarEname)||isFromTargetEngine(srcCn,
	 * tarEname))&&isFromTargetThem(themNames, classification)){ ret=true;
	 * break; } }else if(engNames.size()>0){ //只选择了引擎
	 * if(isFromTargetEngine(srcEn,tarEname)||isFromTargetEngine(srcCn,
	 * tarEname)){ ret=true; break; } }else{ //只选择了主题
	 * if(isFromTargetThem(themNames, classification)){ ret=true; break; } } } }
	 * return ret;
	 * 
	 * }
	 */
	public boolean isFromTargetThem(Set<String> themNames) {
		boolean ret = false;
		if (null == themNames || themNames.isEmpty())
			return ret;
		Iterator<String> it = themNames.iterator();
		while (it.hasNext()) {
			String tarThem = it.next();
			if (MyStringChecker.isBlank(tarThem))
				continue;
			if ((isFromTargetThem(themNames, tarThem))) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	public boolean isFromTargetThem(Set<String> themNames, String desName) {
		return themNames.contains(desName);
	}

	public boolean isFromTargetEngine(EngineName enuName){
		return isFromTargetEngine(EngineFactory.getEnNameString(enuName));
	}
	
	/**
	 * 检查当前的结果是不是来自特定的搜索引擎
	 * 
	 * @param engName
	 *            搜索引擎英文或中文名称
	 * @return
	 */
	public boolean isFromTargetEngine(String engName) {

		if (MyStringChecker.isBlank(engName))
			return false;
		return isFromTargetEngine(getSourceEngineEnName(), engName)
				|| isFromTargetEngine(getSourceEngineCnName(), engName);

	}

	private final boolean isFromTargetEngine(Set<String> srcEnames,
			String tarEname) {

		return srcEnames.contains(tarEname);
	}

	/**
	 * 根据用户查询日志，格式化Result对象，用于界面显示（网页）
	 * 
	 * @param log
	 *            通过ClickLogDao从数据库中查询的结果对象
	 */
	public void formatClickRecommResult(ClickRecordEntity log) {

		if (null == log)
			return;
		abstr = log.getAbstr();
		link = log.getUrl();
		source = VisibleConstant.getWebpageContent(ContentNames.result_recomm_src);
		title = log.getTitle();
		value = log.getWeight();
	}

	public boolean isFromTargetEngine(Set<String> filterEnames) {
		boolean ret = false;
		if (null == filterEnames || filterEnames.isEmpty())
			return ret;
		Iterator<String> it = filterEnames.iterator();
		while (it.hasNext()) {
			String tarEng = it.next();
			if (MyStringChecker.isBlank(tarEng))
				continue;
			if ((isFromTargetEngine(getSourceEngineEnName(), tarEng))
					|| (getClassification().equals(tarEng))) {
				ret = true;
				break;
			} 
		}
		return ret;

		/*
		 * boolean ret=false; if(null==filterEnames||filterEnames.isEmpty())
		 * return ret;
		 * 
		 * Set<String> srcEn=getSourceEngineEnName(),
		 * srcCn=getSourceEngineCnName(); Iterator<String>
		 * it=filterEnames.iterator(); while(it.hasNext()){ String
		 * tarEname=it.next(); if(StringChecker.isBlank(tarEname)) continue;
		 * if(isFromTargetEngine(srcEn,tarEname)||isFromTargetEngine(srcCn,
		 * tarEname)||(getClassification().equals(tarEname))){ ret=true; break;
		 * } }
		 

		return ret;*/
	}
	
	/**
	 * 获得当前搜索结果在来源的搜索引擎中的位置
	 * 如果这条结果是合成后的（source中已经有了两个或以上成员搜索引擎），会返回0
	 * 这个函数主要是用来计算结果的初始权重使用
	 * 这个函数的实现与source成员变量的格式严重关联，后续如果修改的时候要注意
	 * @return
	 */
	public int getPosition(){
		if(MyStringChecker.isBlank(source)||m_setSrcCnName.size()!=1) return 0;
		int startIndex=source.indexOf("("), endIndex=source.indexOf(")",startIndex);
		return Integer.parseInt(source.substring(startIndex+1,endIndex));
	}
	
	public Iterator<EngineName> getSourceEngineIterator(){
		Set<EngineName> srcSet=new HashSet<EngineFactory.EngineName>();
		for(Iterator<String> it=m_setSrcCnName.iterator();it.hasNext();){
			String eng=it.next();
			srcSet.add(EngineFactory.getInnerEngineName(eng));
		}
		return srcSet.iterator();
	}
	
	public boolean isRecommendation(){
		return null!=source&&source.equals(VisibleConstant.getWebpageContent(ContentNames.result_recomm_src));
	}
	
	/**
	 * 过渡函数，以后不应该使用这种方式，得到成员搜索引擎名字-位置信息
	 * @return
	 */
	public final Map<EngineName, Integer> getSrcToPos(){
		
		//为了避免安卓端出问题，这里总是重新构造Map，而不是添加成员变量
		//以后这个函数还是保留，但是实现方式应该改变
		Map<EngineName, Integer> ret=new HashMap<EngineFactory.EngineName, Integer>();
		setArray(source);
		for(String item: array){
			int left=item.indexOf("("), right=item.indexOf(")", left);
			String engName=item.substring(0, left).trim();
			int pos=Integer.parseInt(item.substring(left+1, right).trim());
			ret.put(EngineFactory.getInnerEngineName(engName), pos);
		}
		return ret;
	}
	
	/**
	 * 得到当前结果的来源搜索引擎-位置信息的迭代器，迭代的内容按EngineName枚举变量自然排序，
	 * 这是为了让搜索结果页面上显示的“来自”后面的成员搜索引擎名字保证持一定的顺序
	 * @return 迭代器，不要用来执行删除操作，应该只用来迭代读取数据；如果来源信息没有正确设置，会返回null
	 */
	public final Iterator<Entry<EngineName, Integer>> getOrderedSrcToPosIterator(){
		
		//如果以后修改Result类，这个变量是可以避免反复计算的，方法如下：
		/*
		 * 在Result类直接加成员变量，类型可以是List<Entry<EngineName, Integer>>
		 * 在Result类的函数中，一旦修改了来源信息，就把这个成员变量同时清空
		 * 当前这个函数获取时，如果List<Entry<EngineName, Integer>>类型的成员变量不是null，直接就可以得到它的迭代器；
		 * 否则重新对来源信息进行排序，得到List<Entry<EngineName, Integer>>，再返回
		 */
		Map<EngineName, Integer> srcToPos=getSrcToPos();
		if(null==srcToPos||srcToPos.isEmpty()) return null;
		List<Entry<EngineName, Integer>> ls=new ArrayList<Entry<EngineName,Integer>>(srcToPos.entrySet());
		Collections.sort(ls, new Comparator<Entry<EngineName, Integer>>() {
			@Override
			public int compare(Entry<EngineName, Integer> o1,
					Entry<EngineName, Integer> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		return ls.iterator();
	}
}
