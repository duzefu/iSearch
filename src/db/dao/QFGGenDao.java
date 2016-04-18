package db.dao;

import java.util.List;
import java.util.Set;

import common.textprocess.similarity.EditFeatures;

public interface QFGGenDao {
	
	public int add(EditFeatures weight, String cookie);
	public EditFeatures getFeatures(String query1, String query2, String cookie);
	/**
	 * 根据查询流图中的第一个词获取数据
	 * @param ret
	 * @param wlist
	 * @return
	 */
	public boolean getFeaturesByFirstWord(List<EditFeatures> ret, Set<String> wlist);
	
}
