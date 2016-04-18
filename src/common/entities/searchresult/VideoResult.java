package common.entities.searchresult;

public class VideoResult {
	private String title;
	private String video_url;
	private String image_url;
	private String source;
	
	public String getTitle() {
		return this.title;
	}
	
	public String getVideoUrl() {
		return this.video_url;
	}
	
	public String getImageUrl() {
		return this.image_url;
	}
	
	public String getSource() {
		return null == this.source ? "百度": this.source;
	}
	
	public VideoResult(String title, String video_url, String image_url) {
		this.title = title;
		this.video_url = video_url;
		this.image_url = image_url;
	}

}
