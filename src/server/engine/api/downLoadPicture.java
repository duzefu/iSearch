package server.engine.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

public class downLoadPicture extends Thread {
	public String url1;   //should be set
	public int engine=0;	//default 0 baidu.1 youdao
	private String Referer=null;
	public String path;		//real path
	public String imageName;
	public String location=null;		//request path
	public String dir="cache";
	private HttpServletRequest request=ServletActionContext.getRequest();
	public void run ()
	{

		 imageName = url1.substring(url1.lastIndexOf("/") + 1,   url1.length());
		File file=new File(request.getSession().getServletContext().getRealPath("/")+dir,imageName);
		try {
			
			
			FileOutputStream fo = new FileOutputStream(file);
			URL url = new URL(url1);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			if	(engine==2)Referer="http://image.so.com";
			if	(engine==0)Referer="http://image.baidu.com";
			connection.setRequestProperty("User-Agent", "MSIE 7.0");
			connection.setRequestProperty("Referer", Referer);
			
			
			//connection.setConnectTimeout(3000);
            InputStream in =connection.getInputStream();
        
            byte[] buf = new byte[1024];  
            int length = 0;  
            while ((length = in.read(buf, 0, buf.length)) != -1) {  
                fo.write(buf, 0, length);  
            }  
            location=dir+"/"+imageName;
           
    		path=request.getSession().getServletContext().getRealPath("/")+dir;
    		
            in.close();  
            fo.close();  
        } catch (Exception e){  
            e.printStackTrace();
           
        }  
		
	}
	public void setEngine(int i)
	{
		this.engine=i;
	}
	
	
}
