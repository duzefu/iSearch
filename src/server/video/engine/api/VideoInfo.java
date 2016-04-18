package server.video.engine.api;

import java.io.Serializable;

public class VideoInfo implements Serializable{
	private String video_url;
	private String image_url;
	private String title;
	private String duration;
	private String site;
	private String playCount;
	private static String QUERY;
    private final static int SCORE = 1000;
	
	
	private int index;
	
	public String getVideoUrl() {
		return this.video_url;
	}
	public String getImageUrl() {
		return this.image_url;
	}
	public String getTitle() {
		return this.title;
	}
	public int getIndex() {
		return this.index;
	}
	public static String getQuery() {
		return QUERY;
	}
	public String getDuration() {
		return this.duration;
	}
	public String getSite() {
		return this.site;
	}
	public String getPlayCount() {
		return this.playCount;
	}
	public void setVideoUrl(String video_url) {
		this.video_url = video_url;
	}
	public static void setQuery(String query) {
		QUERY = query;
	}
	public void setImageUrl(String image_url) {
		this.image_url = image_url;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public void setPlayCount(String playCount) {
//		System.out.println(playCount);
		this.playCount = playCount;
	}
	
	@Override
	public String toString() {
		return String.format("video_url: %s  playCount: %s", video_url,playCount);
	}
	
	@Override
	public int hashCode() {
		return this.title.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof VideoInfo) {
			VideoInfo videoInfo = (VideoInfo)obj;
			return (videoInfo.title.equals(this.title) && videoInfo.video_url.equals(this.video_url));
		}
		else {
			return super.equals(obj);
		}
	}
	
	public int changeDuration() {
		if(this.duration == null) return 0;
		String reStrings[] = this.duration.split(":");
		if(reStrings == null) return 0;
		if(reStrings.length != 2) return 0;
		int lhs = 0;
        for(int i = 0; i < reStrings[0].length(); ++i) {
        	if(Character.isDigit(reStrings[0].charAt(i)))
        		lhs = lhs * 10 + Integer.parseInt(String.valueOf(reStrings[0].charAt(i)));
        }
        int rhs = 0;
        for(int i = 0; i < reStrings[1].length(); ++i) {
        	if(Character.isDigit(reStrings[1].charAt(i)))
        		rhs = rhs * 10 + Integer.parseInt(String.valueOf(reStrings[1].charAt(i)));
        }
        return lhs * 100 + rhs;
	}
	
	public int changePlayCount(){
		if(this.playCount == null) return 0;
		if(this.playCount.equals("")) return 0;
		String reString[] = this.playCount.split(",");
		int res = 0;
		for(String subString:reString) {
			for(int i = 0; i < subString.length(); ++i) {
				if(Character.isDigit(subString.charAt(i))) {
					res = res * 10 + Integer.parseInt(String.valueOf(subString.charAt(i)));
				}
			}
		}
//		System.out.print("Show PlayCount: ");
//		System.out.println(res);
		return res;
	}
	
	public double Weight(){
		double weight_1 = 0.3;
		double weight_2 = 0.7;
        return weight_1 * (SCORE - this.index) + this.changePlayCount() * weight_2;	
    }
}
