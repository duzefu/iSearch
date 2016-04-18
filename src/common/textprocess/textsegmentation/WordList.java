/*
* Copyright (c) 2013,Intelligent retrieval project team,ASE lab,Xidian university
* All rights reserved.
*
* Filename: WordList.java
* Summary: present the sentence which being segemented
*
* Current Version: 0.1
* Author: Bryan Zou
* Completion Date: 2012.5.8
*
*/

package common.textprocess.textsegmentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class WordList {

	private List<Word> wordlist = new LinkedList<Word>();
		
	/**
	 * null constructor method
	 */
	public WordList(){}
	
	public List<String> getAllWords(){
	
		List<String> ret=new ArrayList<String>();
		if(null==wordlist) return ret;
		for(Word curWord: this.wordlist){
			ret.add(curWord.getword());
		}
		return ret;
	}
	
	public void sortDesc(){
		
		Collections.sort(wordlist, new Comparator<Word>(){
			
			@Override
			public int compare(Word o1, Word o2) {
				double w1=o1.getweight(), w2=o2.getweight();
				if(w1<w2) return 1;
				else if(w1>w2) return -1;
				return 0;
			}
		});
	}
	
	/**
	 * get word by param index
	 * @param index
	 * @return
	 */
	public Word getWord(int index){
		int size = getWordListSize();
		if (size!=-1 && index<size) {
			return wordlist.get(index);
		}
        return null;
    }
	
	public int getWordListSize(){
		return wordlist==null? -1:wordlist.size();
	}
	/**
	 * get the number of words in this list
	 * @return int
	 */
    public int totalwords(){
        return wordlist.size();
    }
    
    /**
     * add a word to this list
     * @param word
     */
    public void addWord(Word word){
    	wordlist.add(word);
    }
    
    /**
     * remove a word from this list by Word
     * @param word
     */
    public void removeWord(Word word){
    	wordlist.remove(word);
    }

    /**
     * remove a word from this list by index
     * @param index
     */
    public void removeWord(int index){
    	wordlist.remove(index);
    }
    
    /**
     * clear the wordlist
     */
    public void clear(){
    	wordlist.clear();
    }
}
