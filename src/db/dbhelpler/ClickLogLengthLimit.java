package db.dbhelpler;

import db.hibernate.tables.isearch.ClickLog;

public class ClickLogLengthLimit {

	final protected static int ABSTRACT_LENGTH_MAX=255;
	final protected static int QUERY_LENGTH_MAX=255;
	final protected static int URL_LENGTH_MAX=255;
	final protected static int TITLE_LENGTH_MAX=255;
	
	public static boolean process(ClickLog target){
		
		if(null==target) return false;
		
		String str=null;
		str=target.getAbstr();
		if(null!=str&&str.length()>=ABSTRACT_LENGTH_MAX) target.setAbstr(str.substring(0, ABSTRACT_LENGTH_MAX-1));
		str=target.getQuery();
		if(null!=str&&str.length()>=QUERY_LENGTH_MAX) target.setQuery(str.substring(0,QUERY_LENGTH_MAX-1));
		str=target.getTitle();
		if(null!=str&&str.length()>=TITLE_LENGTH_MAX) target.setTitle(str.substring(0, TITLE_LENGTH_MAX-1));
		str=target.getUrl();
		if(null!=str&&str.length()>=URL_LENGTH_MAX) return false;
		
		return true;
	}
	
}
