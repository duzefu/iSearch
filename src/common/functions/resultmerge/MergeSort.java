/*
* Copyright (c) 2012,Intelligent retrieval project team,ASE lab,Xidian university
* All rights reserved.
*
* Filename: MergeSort.java
* Summary: algorithm for sorting results
*
* Current Version: 1.0
* Author: XiBin
* Completion Date: 2012.9.1
*
* Superseded versions: 0.9
* Original Author: XiBin
* Completion Date: 2012.7.30
*/

package common.functions.resultmerge;

import java.util.Comparator;

import common.entities.searchresult.*;
/**
 * 对结果条目对比权重，根据Collections.sort重载方法来实现
 * @author CXL
 *
 */
public class MergeSort implements Comparator<Result>{
	@Override
	public int compare(Result R1, Result R2) {
		Result r1=R1;
		Result r2=R2;
		if(r1.getValue()<r2.getValue())
			return 1;
		else if(r1.getValue()==r2.getValue())
			return 0;
		else
			return -1;
	}
}