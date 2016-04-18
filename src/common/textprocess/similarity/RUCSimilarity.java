package common.textprocess.similarity;

import ruc.irm.similarity.word.hownet2.concept.XiaConceptParser;
import server.commonutils.LogU;

public class RUCSimilarity {
    
	private XiaConceptParser xParser = null;
    
    private RUCSimilarity(){
    	xParser=XiaConceptParser.getInstance();
    }
    
    private static RUCSimilarity instance;
    
    private  static RUCSimilarity instance(){
    	
    	if(null==instance){
    		synchronized (RUCSimilarity.class) {
				if(null==instance) instance=new RUCSimilarity();
			}
    	}
    	
    	return instance;
    }
    
    private double calculate(String w1, String w2){
    	return xParser.getSimilarity(w1, w2);
    }
    
    public static double getSimilarity(String w1, String w2){
    	
    	double ret=0.0;
    	if(null==w1||null==w2) return ret;
    	
    	ret= RUCSimilarity.instance().calculate(w1, w2);
    	
    	return ret;
    }
    
}
