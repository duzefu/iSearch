package agent.data.inblackboard;

import java.util.concurrent.CountDownLatch;

public class ClickLogData extends BlackboardBaseData {
	private int userid;
	private String query;
	private String title;
	private String clickAddr;
	private String data;
	private String source;
	private String abstr;
	
	public ClickLogData() {
		super();
	}
	public ClickLogData(CountDownLatch doneSig,int userid, String query, String title,
			String clickAddr, String data, String source,String abstr) {
		super(doneSig);
		this.userid = userid;
		this.query = query;
		this.title = title;
		this.clickAddr = clickAddr;
		this.data = data;
		this.abstr = abstr;
	}
	
	public String getAbstr() {
		return abstr;
	}
	public void setAbstr(String abstr) {
		this.abstr = abstr;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getClickAddr() {
		return clickAddr;
	}
	public void setClickAddr(String clickAddr) {
		this.clickAddr = clickAddr;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
}
