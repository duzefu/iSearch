package outdated.sougoulogrelate;

public class SougouLogEntity {

	private String time;
	private String cookie;
	private String query;
	private int clickRank;
	private int rank;
	private String url;
	
	public SougouLogEntity(){
		
	}
	
	public SougouLogEntity(String lineInFile){
		
		if(null==lineInFile) return;
		String[] logArr=lineInFile.split("\t");
		if(logArr.length!=6) return;
		
		this.time=logArr[0];
		this.cookie=logArr[1];
		this.query=logArr[2];
		this.rank=Integer.parseInt(logArr[3]);
		this.clickRank=Integer.parseInt(logArr[4]);
		this.url=logArr[5];
		
	}
	
	public void init(String lineInFile){
		
		if(null==lineInFile) return;
		StringBuilder sb=new StringBuilder(lineInFile);
		int startQuery=sb.indexOf("["), endQuery=sb.indexOf("]");
		for(int i=startQuery+1;i!=endQuery;++i){
			if(" ".equals(sb.charAt(i))) sb.insert(i, '+');
		}
		String[] logArr=sb.toString().split("\t| ");
		if(logArr.length!=6) return;
		
		this.time=logArr[0];
		this.cookie=logArr[1];
		this.query=logArr[2];
		this.query=this.query.substring(1, this.query.length()-1);
		this.rank=Integer.parseInt(logArr[3]);
		this.clickRank=Integer.parseInt(logArr[4]);
		this.url=logArr[5];
		if(!url.matches("^http")) this.url="http://"+url;
		
	}
	
	public String getTime() {
		return time;
	}
	public String getCookie() {
		return cookie;
	}
	public String getQuery() {
		return query;
	}
	public Integer getClickRank() {
		return clickRank;
	}
	public Integer getRank() {
		return rank;
	}
	public String getUrl() {
		return url;
	}
	
}
