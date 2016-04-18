/*
 * Copyright (c) 2013,Intelligent retrieval project team,ASE lab,Xidian university
 * All rights reserved.
 *
 * Filename: CreateWordList.java
 * Summary: create a wordlist of a web page
 *
 * Current Version: 0.1
 * Author: Bryan Zou
 * Completion Date: 2012.5.8
 *
 */
package common.textprocess.textsegmentation;

import java.util.List;

public class CreateWordList {

	/**
	 * create a wordlist of a web page
	 * 
	 * @param info
	 * @return
	 */
	public static WordList get(String[] info) {

		String title = null, absr = null;
		if (null == info || info.length < 2) {
			title = "";
			absr = "";
		} else {
			title = info[0];
			absr = info[1];
			title = null == title ? "" : title;
			absr = null == absr ? "" : absr;
		}
		WordList wl = new WordList();
		CreateWordList.init(wl, IK.fenci(info[0]), IK.fenci(info[1]));//将标题、摘要分词，加权，存放到wordlist中
		WordList wo = CreateWordList.sortAndchose(wl);//将wordlist中的权重为1的去除，并只取前100个
		return wo;
	}

	/**
	 * 词语列表初始化
	 * 
	 * @param title
	 *            网页标题分词结果
	 * @param terms
	 *            网页内容分词结果
	 * @return
	 */
	public static void init(WordList wl, List<String> title, List<String> terms) {
		// WordList wa = new WordList();

		if (null == wl)
			return;
		wl.clear();
		if (null != title) {
			for (int i = 0; i < title.size(); i++) {
				// wl.addWord(new Word(ti[i],1));
				// 新词则添加到列表中，标题部分词语初始权重为2，每次增大2
				if (CreateWordList.cmp(wl, title.get(i)) == -1) {
					wl.addWord(new Word(title.get(i), 2));
				} else {
					wl.getWord(CreateWordList.cmp(wl, title.get(i)))
							.updateweight(2);
				}
			}
		}
		// 处理网页内容的分词结果
		if (null != terms) {
			for (int i = 0; i < terms.size(); i++) {
				// wl.addWord(new Word(terms[i],1));
				// 内容部分词语初始权重为1，每次增大1
				if (CreateWordList.cmp(wl, terms.get(i)) == -1) {
					wl.addWord(new Word(terms.get(i), 1));
				} else {
					wl.getWord(CreateWordList.cmp(wl, terms.get(i)))
							.updateweight(1);
				}
			}
		}

	}

	/**
	 * 筛选词语，只选权重最高的，最多100个，去除权重为1的（只在内容中出现一次的）
	 * 
	 * @param wa
	 *            原始词语列表
	 * @return 处理后的词语列表
	 */
	public static WordList sortAndchose(WordList wa) {

		if (null == wa)
			return wa;
		WordList wo = new WordList();

		wa.sortDesc();
		// 最多选100个，其中权重为1的不要（只在内容中出现了一次）
		for (int i = 0; i < wa.totalwords(); i++) {
			if (wa.getWord(i).getweight() > 1) {//去除权重为1，的词（只在内容中出现了一次）
				wo.addWord(wa.getWord(i));
			}
			if (wo.totalwords() > 100) {
				break;
			}
		}
		return wo;
	}

	/**
	 * 从wordlist中查找词语
	 * 
	 * @param wa
	 *            词语列表
	 * @param term
	 *            待查找的词语
	 * @return 词语在列表中的位置，从0开始，没有则返回-1
	 */
	public static int cmp(WordList wa, String term) {
		int r = -1;
		for (int k = 0; k < wa.totalwords(); k++) {
			if (term.equalsIgnoreCase(wa.getWord(k).getword())) {
				r = k;
			}
		}
		return r;
	}
}
