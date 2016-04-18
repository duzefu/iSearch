package common.textprocess.textclassifier;

import common.entities.searchresult.Result;
import common.textprocess.textsegmentation.CreateWordList;
import common.textprocess.textsegmentation.Word;
import common.textprocess.textsegmentation.WordList;

public class ClassifyResult {
	public String classification;
	public double probility;
	public ClassifyResult(String classification,double probility)
	{
		this.classification = classification;
		this.probility = probility;
	}
	
	public static void addResultClassification(String query , Result result){
		
		if(null==result||null!=result.getClassification()) return;
		String[] info = new String[2];
		info[0] = result.getTitle();
		info[1] = result.getAbstr();
		WordList wl = CreateWordList.get(info);
		int weight = 1;
		if (wl.getWord(0)!=null) {
			weight = wl.getWord(0).getweight();
		}
		wl.addWord(new Word(query, weight));
		String classificationt = BayesClassifier.bayes(wl);
		result.setClassification(classificationt);
	}
}
