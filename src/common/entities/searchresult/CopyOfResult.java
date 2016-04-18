package common.entities.searchresult;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import server.commonutils.MyStringChecker;
import server.engine.api.EngineFactory;
import server.info.entites.transactionlevel.ClickRecordEntity;

public class CopyOfResult extends ResultBase implements Serializable,Comparable<Object>{

	private static final long serialVersionUID = 1L;
	private String title;
	private String abstr;
	private String link;
	private double value;
	private String classification;
	private String[] array= new String[10];
	
	public String[] getArray()
	{
		return array;
	}
	/**
	 * 结果来源拆分，存在数组里
	 * @param source
	 */
	public void setArray(String source)
	{
		this.array = source.split(" ");
	}
	
	public CopyOfResult(String title,String abstr,String link,String source)
	{
		this.title = title;
		this.abstr = abstr;
		this.link = link;
	}
	
	public CopyOfResult(String title,String abstr,String link,String source,double value)
	{
		this.title = title;
		this.abstr = abstr;
		this.link = link;
		this.value=value;
	}
	
	public CopyOfResult(String title,String abstr,String link,String date,String spare, String source)
	{
		this.title = title;
		this.abstr = abstr;
		this.link = link;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getAbstr()
	{
		return abstr;
	}
	
	public String getLink()
	{
		return link;
	}
	
	public double getValue()
	{
		return value;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public void setAbstr(String abstr)
	{
		this.abstr = abstr;
	}
	
	public void setLink(String link)
	{
		this.link = link;
	}
	
	private final void setSourceEngineName(){
		
		
		Set<String> allEngName=EngineFactory.getAllEngineCnName();
		Iterator<String> iterAllEngName=allEngName.iterator();
		while(iterAllEngName.hasNext()){
			String curEname=iterAllEngName.next();
		}
	}
	
	public void setValue(double value)
	{
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
	 * 检查当前这个结果是不是可用的（标题与URL是必须的，如果没有就认为是错误的结果）
	 * @return
	 */
	public boolean isUsable(){
		
		return null!=title&&!title.isEmpty()&&null!=link&&!link.isEmpty();
	}
	
	private final boolean isFromTargetEngine(Set<String> srcEnames, String tarEname){
		
		return srcEnames.contains(tarEname);
	}
	
}
