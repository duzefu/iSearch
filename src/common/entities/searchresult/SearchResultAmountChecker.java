package common.entities.searchresult;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import server.commonutils.LogU;

public class SearchResultAmountChecker {
	private List<Result> res = new LinkedList<Result>();
	private Set<String> filterEname = new HashSet<String>();
	private Set<String> filterThem = new HashSet<String>();
	private int totalNum;

	public SearchResultAmountChecker(List<Result> res, int totalNum) {
		super();
		this.res = res;
		this.totalNum = totalNum;
	}

	public SearchResultAmountChecker(List<Result> res, Set<String> filterEname,
			Set<String> filterThem, int totalNum) {
		super();
		this.res = res;
		this.filterEname = filterEname;
		this.filterThem = filterThem;
		this.totalNum = totalNum;
	}

	public SearchResultAmountChecker(List<Result> res, Set<String> filterEname,
			int totalNum) {
		super();
		this.res = res;
		this.filterEname = filterEname;
		this.totalNum = totalNum;
	}

	public final boolean resultIsEnough() {

		if (totalNum <= 0)
			return true;
		if (null == res || res.isEmpty())
			return false;

		int amount = 0;
		if (null == filterEname || filterEname.isEmpty())
			amount = res.size();
		else
			amount = getCountOfFilterEngine(res, filterEname);
		return amount >= totalNum;
	}

	private static int getCountOfFilterEngine(List<Result> res,
			Set<String> filterEname) {

		int ret = 0;

		if (null == res)
			return ret;
		Iterator<Result> it = res.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			if (null == r)
				continue;
			if (r.isFromTargetEngine(filterEname))
				++ret;
		}

		return ret;
	}
}
