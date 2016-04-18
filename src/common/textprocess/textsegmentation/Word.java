/*
* Copyright (c) 2013,Intelligent retrieval project team,ASE lab,Xidian university
* All rights reserved.
*
* Filename: Word.java
* Summary: present the word which being segemented
*
* Current Version: 0.1
* Author: Bryan Zou
* Completion Date: 2012.5.8
*
*/

package common.textprocess.textsegmentation;

public class Word {
	//the word itselef
	private String word;
	// 词的内容和权重值
	//the weight of word ,in another word,the times this word appeared in page
	private int weight;
	
	/**
	 * constructor method
	 */
	public Word(){}
	
	/**
	 * constructor method
	 * @param word
	 * @param weight
	 */
	public Word(String word,int weight)
	{
		this.word = word;
		this.weight = weight;
	}
	
	/**
	 * get word
	 * @return String
	 */
	public String getword()
	{
		return word;
	}
	
	/**
	 * get weight of this word
	 * @return int
	 */
	public int getweight()
	{
		return weight;
	}
	
	/**
	 * 更新词语权重，直接加参数
	 * @param i 权重累加值
	 */
	public void updateweight(int i)
	{
		weight = weight+i;;
	}
	
	/**
	 * set word
	 * @param word
	 */
	public void setword(String word)
	{
		this.word = word;
	}
	
	/**
	 * set the weight of word
	 * @param weight
	 */
	public void setweight(int weight)
	{
		this.weight = weight;
	}
}
