package db.dao;

import java.util.Date;
import java.util.List;

public interface HotwordsDao {
	
	/**
	 * 从数据库中获取实时热点词
	 * @param ret 用于返回热点词，不能为null
	 * @param time 获取这个时间的实时热点词（只考虑到日期）
	 * @return 本次获得的实时热点词的数量
	 */
	public int getWords(List<String> ret, Date time);
	
	/**
	 * 更新数据库中的实时热点词
	 * @param words 实时热点词
	 * @param time 这些实时热点词所属的时间
	 */
	public void updateHotwords(List<String> words, Date time);
}
