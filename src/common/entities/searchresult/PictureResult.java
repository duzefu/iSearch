package common.entities.searchresult;

public class PictureResult{
	private String title;
	private String link;
	
	public String getTitle(){
		return this.title;
	}
	
	public String getLink(){
		return this.link;
	}
	
	public PictureResult(String title,String link)
	{
		this.title = title;
		this.link =link;
	}
}