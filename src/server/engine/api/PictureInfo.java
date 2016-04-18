package server.engine.api;

public class PictureInfo {
	private String pictureUrl;
	private String pictureSrc;
	private String pictureTitle;
	private String pictureOrigin;
	private String pictureLocation;
	private int sizew=0;
	private int sizeh=0;
	private int engine;//0 baidu;1 bing;2 youdao;3 sogo
	private int place;//place on the page
	public double weight;
	public void setPictureUrl(String pictureUrl)
	{
		this.pictureUrl=pictureUrl;
	}
	public void setPictureSrc(String pictureSrc)
	{
		this.pictureSrc=pictureSrc;
	}
	public void setPictureTitle(String pictureTitle)
	{
		this.pictureTitle=pictureTitle;
	}
	public String getPictureUrl()
	{
		return pictureUrl;
	}
	public String getPictureSrc()
	{
		return pictureSrc;
	}
	public String getPictureTitle()
	{
		return pictureTitle;
	}
	public void setPictureOrigin(String pictureOrigin)
	{
		this.pictureOrigin=pictureOrigin;
	}
	public String getPictureOrigin()
	{
		return pictureOrigin;
	}
	public void setPictureLocation(String pictureLocation)
	{
		this.pictureLocation=pictureLocation;
	}
	public String getPictureLocation()
	{
		return pictureLocation;
	}
	public void setEngine(int engine)
	{
		this.engine=engine;
	}
	public int getEngine()
	{
		return engine;
	}
	public void setPlace(int place)
	{
		this.place=place;
	}
	public int getPlace()
	{
		return place;
	}
	public void setSizew(int sizew)
	{
		this.sizew=sizew;
	}
	public int getSizew()
	{
		return sizew;
	}
	public void setSizeh(int sizeh)
	{
		this.sizeh=sizeh;
	}
	public int getSizeh()
	{
		return sizeh;
	}
	public void getAll()
	{
		System.out.println("pictureUrl="+pictureUrl);
		System.out.println("pictureSrc="+pictureSrc);
		System.out.println("pictureTitle="+pictureTitle);
		System.out.println("pictureOrigin="+pictureOrigin);
		if (engine==0||engine==1)
		System.out.println("pictureLocation="+pictureLocation);
		System.out.println("sizew="+sizew);
		System.out.println("sizeh="+sizeh);
		System.out.println("engine="+engine);
		System.out.println("place="+place);
		
	}
}
