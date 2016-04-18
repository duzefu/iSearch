package common.textprocess.similarity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import server.commonutils.CharUtil;
import common.functions.recommendation.qfg.QueryTriple;
import common.textprocess.textsegmentation.IK;

/**	This is from the paper:
 * 	Beyond the Session Timeout: Automatic Hierarchical Segmentation of Search Topics in Query Logs
 * 	section 4.2.2
 * 	@author jtr
 * 	@version 1.0
 * 	@since 2014.5.19
 */
public class EditFeatures implements Comparable<EditFeatures>{
	//data
	private double lev = 0;
	private double edlevGT2 = 0;
	private double charPov = 0;
	private double charSuf = 0;
	private double wordPov = 0;
	private double wordSuf = 0;
	private double commonWords = 0;
	private double wordJDistance = 0.0;
	private QueryTriple query1 = null;
	private QueryTriple query2 = null;
	private List<String> queryPre = null;
	private List<String> querySuf = null;
	private QueryType type;
	private int minQueryLength;
	private int minWordSize;
	
	public EditFeatures(){
		
	}
	
	public double getLev() {
		return lev;
	}

	public void setLev(double lev) {
		this.lev = lev;
	}

	public double getEdlevGT2() {
		return edlevGT2;
	}

	public void setEdlevGT2(double edlevGT2) {
		this.edlevGT2 = edlevGT2;
	}

	public double getCharPov() {
		return charPov;
	}

	public void setCharPov(double charPov) {
		this.charPov = charPov;
	}

	public double getCharSuf() {
		return charSuf;
	}

	public void setCharSuf(double charSuf) {
		this.charSuf = charSuf;
	}

	public double getWordPov() {
		return wordPov;
	}

	public void setWordPov(double wordPov) {
		this.wordPov = wordPov;
	}

	public double getWordSuf() {
		return wordSuf;
	}

	public void setWordSuf(double wordSuf) {
		this.wordSuf = wordSuf;
	}

	public double getCommonWords() {
		return commonWords;
	}

	public void setCommonWords(double commonWords) {
		this.commonWords = commonWords;
	}

	public double getWordJDistance() {
		return wordJDistance;
	}

	public void setWordJDistance(double wordJDistance) {
		this.wordJDistance = wordJDistance;
	}

	public QueryTriple getQuery1() {
		return query1;
	}

	public boolean isSimiliar() {

		if (this.getQuery1() == null || this.getQuery2() == null)
			return false;
		this.resetValue();
		this.CalculateAll();
		double weight = this.charPov + this.charSuf + this.commonWords
				+ this.edlevGT2 + this.lev + this.wordJDistance + this.wordPov
				+ this.wordSuf;
		return weight / 8 > 0.5 ? true : false;
	}
	
	protected void resetValue(){
		
		this.charPov=0;
		this.charSuf=0;
		this.commonWords=0;
		this.edlevGT2=0;
		this.lev=0;
		this.wordJDistance=0;
		this.wordPov=0;
		this.wordSuf=0;
	}
	
	public void setQuery1(QueryTriple query1) {
		
		boolean splitReq=false;
		String newWord=null==query1?null:query1.getQueryContent();
		if(null!=query1){
			if(null!=newWord&&!newWord.isEmpty()){
				if(null==this.query1) splitReq=true;
				else if(!newWord.equals(this.query1.getQueryContent())) splitReq=true;
			}
		}
		this.query1= query1;
		if(splitReq) queryPre = IK.fenci(query1.getQueryContent());
		if(CharUtil.isEnglishPattern(newWord)) this.type=QueryType.ENGLISH;
		else this.type=QueryType.CHINESE_CHAR;
		this.minQueryLength=this.getMinQueryLength();
		this.minWordSize=this.getMinWordLength();
	}

	private int getMinQueryLength(){
		
		int nsq1=query1==null||query1.getQueryContent()==null?0:query1.getQueryContent().length();
		int nsq2=this.query2==null||this.query2.getQueryContent()==null?0:this.query2.getQueryContent().length();
		return nsq1<nsq2?nsq1:nsq2;
	}
	
	private int getMinWordLength(){
		
		int nsq1=queryPre==null?0:queryPre.size(), nsq2=null==querySuf?0:querySuf.size();
		return nsq1<nsq2?nsq1:nsq2;
	}
	
	public QueryTriple getQuery2() {
		return query2;
	}

	public void setQuery2(QueryTriple query2) {
		
		boolean splitReq=false;
		String newWord=null==query2?null:query2.getQueryContent();
		if(null!=query2){
			if(null!=newWord&&!newWord.isEmpty()){
				if(null==this.query2) splitReq=true;
				else if(!newWord.equals(this.query2.getQueryContent())) splitReq=true;
			}
		}
		this.query2 = query2;
		if(splitReq) querySuf = IK.fenci(query2.getQueryContent());
		if(CharUtil.isEnglishPattern(newWord)) this.type=QueryType.ENGLISH;
		else this.type=QueryType.CHINESE_CHAR;
		
		this.minQueryLength=this.getMinQueryLength();
		this.minWordSize=this.getMinWordLength();
	}

	public enum QueryType { ENGLISH, CHINESE_CHAR, CHINESE_WORD };
	
	public void print(){
		if(query1!=null&&query2!=null){
			query1.PrintMyself();
			System.out.println("-----------------TO----------------------");
			query2.PrintMyself();
			System.out.println("lev="+lev);
			System.out.println("edlevGT2="+edlevGT2);
			System.out.println("charPov="+charPov);
			System.out.println("charSuf="+charSuf);
			System.out.println("wordPov="+wordPov);
			System.out.println("wordSuf="+wordSuf);
			System.out.println("commonWords="+commonWords);
			System.out.println("wordJDistance="+wordJDistance);
		}
	}
	
	//constructor
	public EditFeatures(QueryTriple query1, QueryTriple query2, QueryType type)
	{
		this.query1 = query1;
		this.query2 = query2;
		this.type = type;
		SplitQueryByWord();
		this.minQueryLength=this.getMinQueryLength();
		this.minWordSize=this.getMinWordLength();
	}
	
	//public method
	public void CalculateAll()
	{
		if(null==queryPre||null==querySuf||queryPre.isEmpty()||querySuf.isEmpty()) return;
		CalculateLD();
		CalculateEdlevGT2();
		CalculateCommonW();
		CalculateWordPov();
		CalculateWordSuf();
		CalculateCharPov();
		CalculateCharSuf();
		CalculateWordR();
	}
	
	//private method
	private void SplitQueryByWord()
	{
		//only used in Chinese
		queryPre = IK.fenci(query1.getQueryContent());
		querySuf = IK.fenci(query2.getQueryContent());
	}

	private double LDEnglishVersion()
	{
		String left = query1.getQueryContent();
		String right = query2.getQueryContent();
		int preLength = left.length();
		int sufLength = right.length();
		
		if(0 == preLength || 0 == sufLength)
		{
			return Math.max(preLength, sufLength);
		}
		
		int[][] LD = new int[preLength + 1][sufLength + 1];
		
		//init
		for(int i = 0; i <= preLength; i++) LD[i][0] = i;
		for(int i = 0; i <= sufLength; i++) LD[0][i] = i;
		
		//calculate
		char ch1;
		char ch2;
		int temp;
		for(int i = 1; i <= preLength; i++)
		{  
			ch1 = left.charAt(i - 1);
		    for(int j = 1; j <= sufLength; j++)
		    {  
		    	ch2 = right.charAt(j - 1);
		    	temp = ch1 == ch2 ? 0 : 1;
		    	LD[i][j] = Math.min(
		    			Math.min(LD[i-1][j] + 1, LD[i][j - 1] + 1), 
		    			LD[i- 1][j - 1] + temp); 
		    }
		  } 
		return (double)LD[preLength][sufLength]/(this.minQueryLength==0?1:this.minQueryLength);
	}
	
	private double LDChineseCharVersion()
	{
		return LDEnglishVersion();
	}
	
	/**
	 * ldValueC
	 * @return
	 */
	private double LDChineseWordVersion()
	{
		SplitQueryByWord();
		/* This is an implementation of LD algorithm */
		int preLength = queryPre.size();
		int sufLength = querySuf.size();
		
		if(0 == preLength || 0 == sufLength)
		{
			return Math.max(preLength, sufLength);
		}
		
		int[][] LD = new int[preLength + 1][sufLength + 1];
		
		//init
		for(int i = 0; i <= preLength; i++) LD[i][0] = i;
		for(int i = 0; i <= sufLength; i++) LD[0][i] = i;
		
		//calculate
		String ch1;
		String ch2;
		int temp;
		for(int i = 1; i <= preLength; i++)
		{  
			ch1 = queryPre.get(i - 1);
		    for(int j = 1; j <= sufLength; j++)
		    {  
		    	ch2 = querySuf.get(j - 1);
		    	temp = ch1.compareTo(ch2) == 0 ? 0 : 1;
		    	LD[i][j] = Math.min(
		    			Math.min(LD[i-1][j] + 1, LD[i][j - 1] + 1), 
		    			LD[i- 1][j - 1] + temp); 
		    }
		  } 
		return (double)LD[preLength][sufLength]/(this.minQueryLength==0?1:this.minQueryLength);
	}

	/**
	 * ld入口
	 */
	private void CalculateLD()
	{
		switch(this.type)
		{
			case ENGLISH:
				lev = 1-LDEnglishVersion();
				break;
			case CHINESE_CHAR:
				lev = 1-LDChineseCharVersion();
				break;
			case CHINESE_WORD:
				lev = 1-LDChineseWordVersion();
				break;
			default:
				;
		}
	}

	private void CalculateEdlevGT2()
	{
		int length=0==this.minQueryLength?1:this.minQueryLength;
		this.edlevGT2 = lev*length > 2 ? 1 : 0;
	}
	
	/**
	 * wordCLeft，根据两个查询词中单词（分词结果）出现重复次数来计算
	 */
	private void CalculateCommonW()
	{
		int preSize=queryPre.size();
		preSize=preSize==0?1:preSize;
		for(int i = 0; i < queryPre.size(); i++)
			this.commonWords += (querySuf.contains(queryPre.get(i)) ? 1 : 0);
		this.commonWords/=preSize;
	}

	/**
	 * wordCRight，从前向后逐一对比单词（分词结果）是否相同
	 */
	private void CalculateWordPov()
	{
		for(int i = 0, j = 0; i < queryPre.size() && j < querySuf.size(); i++, j++)
		{
			this.wordPov += queryPre.get(i).equals(querySuf.get(j)) ? 1 : 0;
		}
		this.wordPov/=(this.minWordSize==0?1:this.minWordSize);
	}

	/**
	 * 从后往前逐一对比单字相同个数
	 */
	private void CalculateWordSuf()
	{
		for(int i = queryPre.size() - 1, j = querySuf.size() - 1; i >=0 && j >= 0; i--, j--)
		{
			this.wordSuf += queryPre.get(i).equals(querySuf.get(j)) ? 1 : 0;
		}
		this.wordSuf/=(this.minWordSize==0?1:this.minWordSize);
	}

	/**
	 * 根据查询词（不分词）中的字符，逐一对比，统计相同的次数
	 */
	private void CalculateCharPov()
	{
		for(int i = 0, j = 0; i < query1.getQueryContent().length() && j < query2.getQueryContent().length(); i++, j++)
		{
			this.charPov += query1.getQueryContent().charAt(i) == query2.getQueryContent().charAt(j) ? 1 : 0;
		}
		this.charPov/=(this.minQueryLength==0?1:this.minQueryLength);
	}
	
	/**
	 * 从后往前按查询词的字符统计其相同次数
	 */
	private void CalculateCharSuf()
	{
		for(int i = query1.getQueryContent().length() - 1, j = query2.getQueryContent().length()  - 1
				; i >= 0 && j >= 0; i--, j--)
		{
			this.charSuf += query1.getQueryContent().charAt(i) == query2.getQueryContent().charAt(j) ? 1 : 0;
		}
		this.charSuf/=(0==this.minQueryLength?1:this.minQueryLength);
	}

	/**
	 * 两个查询词的分词结果去重后的交集大小/并集大小
	 */
	private void CalculateWordR()
	{
		List<String> tempQuery1 = new ArrayList<String>(queryPre);
		List<String> tempQuery2 = new ArrayList<String>(querySuf);
		
		HashSet<String> temp = new HashSet<String>(tempQuery1);  
		tempQuery1.clear();  
		tempQuery1.addAll(temp); 
		
		temp = new HashSet<String>(tempQuery2);
		tempQuery2.clear();  
		tempQuery2.addAll(temp); 
		
		double numerator = 0;
		for(int i = 0; i < tempQuery1.size(); i++)
			numerator += (tempQuery2.contains(tempQuery1.get(i)) ? 1 : 0);
		
		
		List<String> unionQuery = new ArrayList<String>(tempQuery1);
		unionQuery.addAll(tempQuery2);
		temp = new HashSet<String>(unionQuery);
		double denominator = temp.size();
		
		wordJDistance = numerator / denominator;
	}
	
	public static void main(String[] argv){
		
		String q="西安电子科技大学",q_="西安电子科技大学研究生";
		QueryTriple q1=new QueryTriple(), q2=new QueryTriple();
		q1.setQueryContent(q);
		q1.setQueryTime("00:00:01");
		q1.setQueryUser("123456");
		q2.setQueryContent(q_);
		q2.setQueryTime("00:00:12");
		q2.setQueryUser("123456");
		EditFeatures f=new EditFeatures();
		f.setQuery1(q1);
		f.setQuery2(q2);
		f.CalculateAll();
		System.exit(0);
	}

	@Override
	public int compareTo(EditFeatures o) {
		
		double thisWeight=this.getAverageWeight(), otherWeight=o.getAverageWeight();
		if(thisWeight<otherWeight) return -1;
		else if (thisWeight>otherWeight) return 1;
		return 0;
	}
	
	public double getAverageWeight()
	{
		
		double ret=0;
		Double dval=null;
		
		dval=this.getCharPov();
		ret+=null==dval?0:dval.doubleValue();
		
		dval=this.getCharSuf();
		ret+=null==dval?0:dval.doubleValue();
		
		dval=this.getCommonWords();
		ret+=null==dval?0:dval.doubleValue();
		
		dval=this.getEdlevGT2();
		ret+=null==dval?0:dval.doubleValue();
		
		dval=this.getLev();
		ret+=null==dval?0:dval.doubleValue();
		
		dval=this.getWordJDistance();
		ret+=null==dval?0:dval.doubleValue();
		
		dval=this.getWordPov();
		ret+=null==dval?0:dval.doubleValue();
		
		dval=this.getWordSuf();
		ret+=null==dval?0:dval.doubleValue();
		
		ret/=8;
		return ret;
		
	}
}
