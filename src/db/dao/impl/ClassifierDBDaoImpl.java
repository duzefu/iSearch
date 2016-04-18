package db.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import com.hp.hpl.jena.rdf.listeners.NullListener;

import server.commonutils.MyStringChecker;
import server.info.entites.transactionlevel.ClassifierTablePair;
import db.dao.ClassifierDBDao;
import db.hibernate.tables.classifier.Education;
import db.hibernate.tables.classifier.Employment;
import db.hibernate.tables.classifier.Financial;
import db.hibernate.tables.classifier.Health;
import db.hibernate.tables.classifier.It;
import db.hibernate.tables.classifier.Literature;
import db.hibernate.tables.classifier.Military;
import db.hibernate.tables.classifier.Sports;
import db.hibernate.tables.classifier.Tourism;

public class ClassifierDBDaoImpl implements ClassifierDBDao {

	private SessionFactory classifierSessionFactory;

	public SessionFactory getClassifierSessionFactory() {
		return classifierSessionFactory;
	}

	public void setClassifierSessionFactory(
			SessionFactory classifierSessionFactory) {
		this.classifierSessionFactory = classifierSessionFactory;
	}

	private final static Map<ClassifierTableNames, String> enuToClassName=new HashMap<ClassifierTableNames, String>();
	static{
		enuToClassName.put(ClassifierTableNames.education, "Education");
		enuToClassName.put(ClassifierTableNames.employment, "Employment");
		enuToClassName.put(ClassifierTableNames.financial, "Financial");
		enuToClassName.put(ClassifierTableNames.health, "Health");
		enuToClassName.put(ClassifierTableNames.it, "It");
		enuToClassName.put(ClassifierTableNames.literature, "Literature");
		enuToClassName.put(ClassifierTableNames.military, "Military");
		enuToClassName.put(ClassifierTableNames.sports, "Sports");
		enuToClassName.put(ClassifierTableNames.tourism, "Tourism");
	}
	
	@Override
	public double getWeightOfWord(String word, ClassifierTableNames tableName) {
		
		if (MyStringChecker.isBlank(word) || null == tableName)
			return 0;

		double ret = 0.0;
		Object res = null;
		String tableClass=enuToClassName.get(tableName);
		
		Iterator<Object> iterRes = classifierSessionFactory.getCurrentSession()
				.createQuery(	"from " + tableName + " c where c.word = :word")
				.setParameter("word", word).list().iterator();
		if (iterRes.hasNext()) res = iterRes.next();
		ClassifierTablePair pair = objectCast(res, tableName);
		if(null!=pair) ret=pair.getWeight();
		return ret;
	}
	
	protected ClassifierTablePair objectCast(Object res, ClassifierTableNames name){
		
		if (null == res || null == name)
			return null;

		ClassifierTablePair ret=new ClassifierTablePair();
		try {
			switch (name) {
			case education:
				Education edu=(Education) res;
				ret.setWeight(edu.getWeight());
				ret.setWord(edu.getWord());
				break;
			case employment:
				Employment employ=(Employment) res;
				ret.setWeight(employ.getWeight());
				ret.setWord(employ.getWord());
				break;
			case financial:
				Financial financial=(Financial) res;
				ret.setWeight(financial.getWeight());
				ret.setWord(financial.getWord());
				break;
			case health:
				Health heal=(Health)res;
				ret.setWeight(heal.getWeight());
				ret.setWord(heal.getWord());
				break;
			case it:
				It it=(It)res;
				ret.setWeight(it.getWeight());
				ret.setWord(it.getWord());
				break;
			case literature:
				Literature liter = (Literature) res;
				ret.setWeight(liter.getWeight());
				ret.setWord(liter.getWord());
				break;
			case military:
				Military mil= (Military) res;
				ret.setWeight(mil.getWeight());
				ret.setWord(mil.getWord());
				break;
			case sports:
				Sports sp = (Sports) res;
				ret.setWeight(sp.getWeight());
				ret.setWord(sp.getWord());
				break;
			case tourism:
				Tourism tour = (Tourism) res;
				ret.setWeight(tour.getWeight());
				ret.setWord(tour.getWord());
				break;
			default:
				break;
			}
		} catch (Exception e) {
			
		}

		return ret;
	}

}
