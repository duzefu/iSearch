/*
* Copyright (c) 2013,Intelligent retrieval project team,ASE lab,Xidian university
* All rights reserved.
*
* Filename: IK.java
* Summary: using open-source IKanalyzer implementation of segmentation
*
* Current Version: 0.1
* Author: Bryan Zou
* Completion Date: 2013.3.7
*
*/

package common.textprocess.textsegmentation;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.wltea.analyzer.core.*;

import server.commonutils.LogU;
//分词
public class IK {
	
	public static List<String> fenci(String text)
	{
		if(null==text||0==text.length()) return null;
		StringReader sr=new StringReader(text);  
        IKSegmenter ik=new IKSegmenter(sr, true); 
      //为true，使用智能分词策略
      // 非智能分词：细粒度输出所有可能的切分结果 智能分词： 合并数词和量词，对分词结果进行歧义判断
        Lexeme lex=null;     
        List<String> ret=new ArrayList<String>();
        if(null==ik){
        	ret.add(text);
        	return ret;
        }
        
        try{
        	while(true){
        		lex=ik.next();
        		if(null==lex) break;
				String temp = lex.getLexemeText();
				if(null!=temp&&temp.length()>=1)
				{
					ret.add(lex.getLexemeText());
				}
        	}
        	}catch(Exception e){
        		e.printStackTrace();
    			ret.clear();
    			ret.add(text);
    			return ret;
        	}
        
        if(!ret.isEmpty())
        {
        	return ret;
        }
        else
        {
        	ret.add(text);
        	return ret;
        }
	}
}
