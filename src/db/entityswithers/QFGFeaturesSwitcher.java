package db.entityswithers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import server.commonutils.TimeUtil;
import common.functions.recommendation.qfg.QueryTriple;
import common.textprocess.similarity.EditFeatures;
import db.hibernate.tables.isearch.QfgFeatures;
import db.hibernate.tables.isearch.Queries;

public class QFGFeaturesSwitcher {

	public static QfgFeatures qfgFeaturesEntityToPojo(EditFeatures entity, String cookie, Queries query1, Queries query2){
		
		if(null==entity||null==cookie||cookie.isEmpty()||null==query1||null==query2) return null;
		
		QfgFeatures ret=new QfgFeatures();
		ret.setCharPov(entity.getCharPov());
		ret.setCharSuf(entity.getCharSuf());
		ret.setCommonWords(entity.getCommonWords());
		ret.setCookie(cookie);
		ret.setEdlevGt2(entity.getEdlevGT2());
		ret.setWordJDistance(entity.getWordJDistance());
		ret.setWordSuf(entity.getWordSuf());
		ret.setQueriesByQueryFirst(query1);
		ret.setQueriesByQuerySecond(query2);
		
		return ret;
	}
	
	public static void qfgFeaturesPojoToEntity(List<EditFeatures> ret, List<QfgFeatures> flist){
		
		if(null==flist || null==ret) return;
		
		Iterator<QfgFeatures> it=flist.iterator();
		while(it.hasNext())
		{
			QfgFeatures ft=it.next();
			EditFeatures ef=QFGFeaturesSwitcher.qfgFeaturesPojoToEntity(ft);
			if(null!=ef) ret.add(ef);
		}
	}
	
	public static EditFeatures qfgFeaturesPojoToEntity(QfgFeatures pojo){
		
		if(null==pojo) return null;
		Double dval=null;
		
		EditFeatures ret =new EditFeatures();
		
		dval=pojo.getCharPov();
		ret.setCharPov(null==dval?0:dval.doubleValue());
		
		dval=pojo.getCharSuf();
		ret.setCharSuf(null==dval?0:dval.doubleValue());
		
		dval=pojo.getCommonWords();
		ret.setCommonWords(null==dval?0:dval.doubleValue());
		
		dval=pojo.getEdlevGt2();
		ret.setEdlevGT2(null==dval?0:dval.doubleValue());
		
		dval=pojo.getWordJDistance();
		ret.setWordJDistance(null==dval?0:dval.doubleValue());
		
		dval=pojo.getWordPov();
		ret.setWordPov(null==dval?0:dval.doubleValue());
		
		dval=pojo.getWordSuf();
		ret.setWordSuf(null==dval?0:dval.doubleValue());
		
		QueryTriple query1=new QueryTriple(), query2=new QueryTriple();
		query1.setQueryContent(pojo.getQueriesByQueryFirst().getQuery());
		query1.setQueryUser(pojo.getCookie());
		Date dateq1=pojo.getQueriesByQueryFirst().getDate();
		query1.setQueryTime(TimeUtil.formatTimeString(dateq1));
		ret.setQuery1(query1);
		
		query2.setQueryContent(pojo.getQueriesByQuerySecond().getQuery());
		query2.setQueryUser(pojo.getCookie());
		Date dateq2=pojo.getQueriesByQuerySecond().getDate();
		query2.setQueryTime(TimeUtil.formatTimeString(dateq2));
		ret.setQuery2(query2);
		
		return ret;
	}
}
