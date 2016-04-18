package server.engine.api;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import server.engine.api.PictureInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mysql.jdbc.Blob;

import java.util.regex.*;

public class PictureService {
	private int num=20;
	private List<PictureInfo> finalResult=new ArrayList<PictureInfo>();
	private static double baiduWeight=1.0;
	private static double bingWeight=1.2;
	private static double youdaoWeight=1.5;
	private static double sogoWeight=2.0;
	private static int numberOfPage=20; //put how many result in one page
	public Boolean baiduEngine=false;
	public Boolean bingEngine=false;
	public Boolean sogoEngine=false;
	public Boolean youdaoEngine=false;
	public Boolean yahooEngine=false;
	public List<PictureInfo> getResult(String query,int page)
	{
		List<PictureInfo> result =new ArrayList<PictureInfo>();
		List<PictureInfo> tempResult =new ArrayList<PictureInfo>();
		int numberOfPool=0;
		int requestPage=1; //成员搜索引擎的具体第几页
		int movePage=0;  //相对位置
		
		if (baiduEngine)numberOfPool+=20;
		if (youdaoEngine)numberOfPool+=20;
		if (sogoEngine)numberOfPool+=20;
		if (bingEngine)numberOfPool+=20;
		if (numberOfPool==0)return null;
		requestPage=numberOfPage*page/numberOfPool+1;  //request page of every engine
		
		result.addAll(getResultOfEngine(query,requestPage));
		
		result=pictureFuse(result);
		
		movePage=(numberOfPage*page)%numberOfPool/numberOfPage;
		if (movePage==0)   // eq 0 add the final 20 result
			{
			int check=result.size()-numberOfPage;
			if(check<0)check=0;			// prevent from arry out of range
			for(int i=check;i<result.size();i++)
			{
				tempResult.add(result.get(i));
			}
			}
		else          
		{
			int check1=(movePage-1)*numberOfPage;
			int check2=movePage*numberOfPage;
			if (check2>result.size())check2=result.size(); // prevent from arry out of range
			for (int i=check1;i<check2;i++)
			{
				tempResult.add(result.get(i));
			}
		}
		
		finalResult=downLoadPic(tempResult);
		return finalResult;
	}
	private List<PictureInfo> downLoadPic(List<PictureInfo> temp) {
		List<PictureInfo> result=new ArrayList();
		downLoadPicture[] downloadService=new downLoadPicture[20];
		for (int i=0;i<temp.size();i++)
		{
			if(temp.get(i).getEngine()==0||temp.get(i).getEngine()==2)
			{
				try
				{
					downloadService[i]=new downLoadPicture();
					downloadService[i].setEngine(temp.get(i).getEngine());
					downloadService[i].url1=temp.get(i).getPictureSrc();
					downloadService[i].start();
					downloadService[i].join(5000);
				}catch (Exception e)
				{}
			}
			
			
			
		}
		for (int i=0;i<temp.size();i++)
		{
			if(temp.get(i).getEngine()==0||temp.get(i).getEngine()==2)
			{
				if (downloadService[i].location==null)continue;
				temp.get(i).setPictureSrc(downloadService[i].location);
				temp.get(i).setPictureUrl(downloadService[i].location);
				
			}
			result.add(temp.get(i));
		}
		return result;
	}
	public List<PictureInfo> getResultOfEngine(String query,int page)
	{
		 List<PictureInfo> result=new  ArrayList<PictureInfo>();
		 List<PictureInfo> sogo=new  ArrayList<PictureInfo>();
		 List<PictureInfo> baidu=new  ArrayList<PictureInfo>();
		 List<PictureInfo> youdao=new  ArrayList<PictureInfo>();
		 List<PictureInfo> bing=new  ArrayList<PictureInfo>();
		 List<PictureInfo> so=new  ArrayList<PictureInfo>();
		 List<PictureInfo> sina=new  ArrayList<PictureInfo>();
		
		 if (sogoEngine) sogo=sogoResult(query,page);
		 if (baiduEngine)baidu=baiduResult(query,page);
		 if (bingEngine)bing=bingResult(query,page);
		 if (youdaoEngine)youdao=youdaoResult(query,page);
		
		 result.addAll(baidu);
		 result.addAll(bing);
		 result.addAll(youdao);
		 result.addAll(sogo);
		 
		// so=soResult(query,page);
		// result.addAll(sogo);
		// sina=sinaResult(query,page);
		 
		 // result=fuse(sogo,baidu,youdao);
		 return result;
	}
	private List<PictureInfo> pictureFuse(List<PictureInfo> result) //fuse
	{
		List<PictureInfo> tempResult=new  ArrayList<PictureInfo>();
		result=throwNoUseDate(result); //throw unexcept result and get the weight
		result=throwMutipleDate(result);
		while(result.size()>0)        //sort 
		{
			double tempWeight=result.get(0).weight;
			int tempPlace=0;
			for (int j=0;j<result.size();j++)
			{
				if (tempWeight>result.get(j).weight)
				{
					tempWeight=result.get(j).weight;
					tempPlace=j;
				}
			}
			tempResult.add(result.get(tempPlace));
			result.remove(tempPlace);
		}
		return tempResult;
		//finalResult=throwMutipleDate(finalResult);
	}
	private List<PictureInfo> throwMutipleDate(List<PictureInfo> result) {
		if (result.size()<=1)return result;
		List<PictureInfo> re=new ArrayList<PictureInfo>();
		int repeat=0;
		while (result.size()>1)
		{
			repeat=0;
			for (int i=1;i<result.size();i++)
			{
				if (result.get(0).getPictureOrigin()==result.get(i).getPictureOrigin())
				{
					result.get(i).weight/=2;
					result.remove(0);
					repeat=1;
					
					break;
				}
			}
			if (repeat==0)
			{
				re.add(result.get(0));
				result.remove(0);
			}
			
		}
		re.add(result.get(0));
		return re;
	}
	public List<PictureInfo> sinaResult(String query,int page)
	{
		
		String word="";
		try {
			word = URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String url="http://image.baidu.com/search/index?tn=baiduimage&st=-1&ipn=r&ct=201326592&nc=1&lm=-1&cl=2&ie=utf-8&word="+word+"&ie=utf-8&istype=2&fm=se0";
		String url="http://search.sina.com.cn/?c=img&q="+word+"&ie=gbk&page="+page+"&num=10&format=api_html&sort=rel";
		
		return getsinaResult(url);
	}
	public List<PictureInfo> soResult(String query,int page)
	{
		
		String word="";
		try {
			word = URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String url="http://image.baidu.com/search/index?tn=baiduimage&st=-1&ipn=r&ct=201326592&nc=1&lm=-1&cl=2&ie=utf-8&word="+word+"&ie=utf-8&istype=2&fm=se0";
		String url="http://image.so.com/j?q="+word+"&src=srp&sn="+(20*(page-1))+"&pn=20";
		
		return getsoResult(url);
	}
	public List<PictureInfo> youdaoResult(String query,int page)
	{
		
		String word="";
		try {
			word = URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String url="http://image.baidu.com/search/index?tn=baiduimage&st=-1&ipn=r&ct=201326592&nc=1&lm=-1&cl=2&ie=utf-8&word="+word+"&ie=utf-8&istype=2&fm=se0";
		String url="http://image.youdao.com/search?q="+word+"&keyfrom=image.nextPage&start="+(24*(page-1));
		
		return getyoudaoResult(url);
	}
	public List<PictureInfo> bingResult(String query,int page)
	{
		
		String word="";
		try {
			word = URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String url="http://image.baidu.com/search/index?tn=baiduimage&st=-1&ipn=r&ct=201326592&nc=1&lm=-1&cl=2&ie=utf-8&word="+word+"&ie=utf-8&istype=2&fm=se0";
		String url="http://cn.bing.com/images/async?q="+word+"&async=content&first="+(20*(page-1))+"&count=20";
		
		return getbingResult(url);
	}
	public List<PictureInfo> baiduResult(String query,int page)
	{
		
		String word="";
		try {
			word = URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String url="http://image.baidu.com/search/index?tn=baiduimage&st=-1&ipn=r&ct=201326592&nc=1&lm=-1&cl=2&ie=utf-8&word="+word+"&ie=utf-8&istype=2&fm=se0";
		String url="http://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&ct=201326592&is=&fp=result&queryWord="+word+"&cl=2&lm=-1&ie=utf-8&oe=utf-8&adpicid=&st=&z=&ic=&word="+word+"&s=&se=&tab=&width=&height=&face=&istype=&qc=&nc=1&fr=&pn="+(20*(page-1))+"&rn=48&gsm=5a&1458463437969=";
		
		
		return getbaiduResult(url);
	}
	public List<PictureInfo> sogoResult(String query,int page)
	{
		
		String word="";
		try {
			word = URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String url = "http://pic.sogou.com/pics?query=" + word;
		String url="http://pic.sogou.com/pics?query="+word+"&mood=0&picformat=0&mode=1&di=2&start="+(20*(page-1))+"&reqType=ajax&tn=0&reqFrom=result";
		return getsogoResult(url);
	}
	public List<Integer> getPageList(int page)
	{
		List<Integer> pageList=new ArrayList<Integer>();
		if (page<7)
		{
			for (int i=1;i<11;i++)
			{
				pageList.add(i);
			}
	
		}
		else{
			for (int i=page-5;i<page+5;i++)
			{
				pageList.add(i);

			}
		}
		
		return pageList;
	}
	private List<PictureInfo> getsinaResult(String url)
	{
		List<PictureInfo> result=new ArrayList<PictureInfo>();
		String htmlpage=getHTML(url, "utf-8");
		htmlpage=htmlpage.replace("\\/","/").replace("\\\"", "");
		Document doc = Jsoup.parse(htmlpage);
		Elements ele=doc.select("a");
		int i=0;
		Element t;
		while(i<ele.size()&&(t=ele.get(i))!=null)
		{
			
			Element title,_url,src;
			PictureInfo pic=new PictureInfo();
			_url=t.getElementsByTag("a").first();
			src=t.getElementsByTag("img").first();
			title=t.getElementsByTag("p").first();
			pic.setPictureUrl(_url.attr("href"));
			pic.setPictureSrc(src.attr("src"));
				pic.setPictureTitle(unicodeToString(title.text()));
			
			
			
			result.add(pic);
			
			
			i++;
		}
		
		
		return result;
	}
	private List<PictureInfo> getsoResult(String url)
	{
		List<PictureInfo> result=new ArrayList<PictureInfo>();
		String htmlpage=getHTML(url, "utf-8");
		String src1="\"thumb\":\"";
		String src2 = "\"";
		String title1="\"title\":\"";
		String title2="\",\"";
		String origin1="\"link\":\"";
		String origin2="\",\"";
		int index1=0,index2=0;
		String src,title,_url,origin;
		int i=0;

		while (htmlpage.contains(src1)&&i++<num) {
			
			PictureInfo pictureinfo=new PictureInfo();
			
		
			index1 = htmlpage.indexOf(title1);
			index2 = htmlpage.indexOf(title2, index1+title1.length());
			title = htmlpage.substring(index1 + title1.length(), index2);
			
			index1 = htmlpage.indexOf(src1);
			
			index2 = htmlpage.indexOf(src2, index1+src1.length());
			src = htmlpage.substring(index1 + src1.length(), index2);
			
			
				src=src.replace("\\/","/");
				
				_url=src;
			
			_url=src;
			
			pictureinfo.setPictureTitle(title);
			pictureinfo.setPictureSrc(src);
			pictureinfo.setPictureUrl(_url);
			if (src.startsWith("http"))
			result.add(pictureinfo);
			
			htmlpage = htmlpage.substring(index2+1);
		}
		return result;
	}
	private List<PictureInfo> getyoudaoResult(String url)
	{
		List<PictureInfo> result=new ArrayList<PictureInfo>();
		String htmlpage=getHTML(url, "utf-8");
		
		Document doc = Jsoup.parse(htmlpage);
		Element ele;
		int i=0;
		String origin1="\',\'";
		String origin2="\',";
		int index1,index2;
		while((ele=doc.getElementById("container"+i))!=null&&i<num)
		{
			
			Element title,_url,src;
			String origin;
			PictureInfo pic=new PictureInfo();
			_url=ele.getElementsByTag("a").first();
			src=ele.getElementsByTag("img").first();
			pic.setPictureUrl("http://image.youdao.com/"+_url.attr("href"));
			
			origin=_url.attr("onclick");
			origin=utf8Decode(origin);
			index1 = origin.indexOf(origin1);
			index2 = origin.indexOf(origin2, index1+origin1.length());
			origin = origin.substring(index1 + origin1.length(), index2);
			pic.setPictureOrigin(origin);
			
			pic.setPictureSrc(src.attr("src"));
			pic.setSizeh(Integer.parseInt(src.attr("sizeh")));
			pic.setSizew(Integer.parseInt(src.attr("sizew")));
			
			title=doc.getElementById("hiddenText"+i);
			pic.setPictureTitle(title.text());
			pic.setEngine(2);
			pic.setPlace(i+1);
			result.add(pic);
			
			
			i++;
		}
		
		
		return result;
		
		
		
	}
	private List<PictureInfo> getbingResult(String url)
	{
		List<PictureInfo> result=new ArrayList<PictureInfo>();
		String htmlpage=getHTML(url, "utf-8");
		String src0="src=\"";
		String src1 = "src2=\"";
		String src2 = "\"";
		String title1="t1=\"";
		String title2="\"";
		String url1="href=\"";
		String url2="\"";
		String origin1="imgurl:&quot;";
		String origin2="&quot;,";
		int index1=0,index2=0;
		String src,title,_url,origin;
		int i=0;

		while (htmlpage.contains(url1)&&i<num) {
			
			PictureInfo pictureinfo=new PictureInfo();
			
			index1 = htmlpage.indexOf(url1);
			index2 = htmlpage.indexOf(url2, index1+url1.length());
			_url = htmlpage.substring(index1 + url1.length(), index2);
		
			index1 = htmlpage.indexOf(title1);
			index2 = htmlpage.indexOf(title2, index1+title1.length());
			title = htmlpage.substring(index1 + title1.length(), index2);
			
			index1 = htmlpage.indexOf(origin1);
			index2 = htmlpage.indexOf(origin2, index1+origin1.length());
			origin = htmlpage.substring(index1 + origin1.length(), index2);
			
			index1 = htmlpage.indexOf(src1);
			if (index1==-1)index1=htmlpage.indexOf(src0);
			index2 = htmlpage.indexOf(src2, index1+src1.length());
			src = htmlpage.substring(index1 + src1.length(), index2);
			_url="http://cn.bing.com".concat(_url);
			
			_url=_url.replaceAll("&amp;", "&");
		
			pictureinfo.setPictureTitle(title);
			pictureinfo.setPictureSrc(src);
			pictureinfo.setPictureUrl(_url);
			pictureinfo.setEngine(1);
			pictureinfo.setPlace(i+1);
			
			if (src.startsWith("http"))
			{
				
				pictureinfo.setPictureOrigin(origin);
				result.add(pictureinfo);
				i++;
			}
			
			
			htmlpage = htmlpage.substring(index2+1);
		}
		return result;
	}
	private List<PictureInfo> getbaiduResult(String url)
	{
		List<PictureInfo> result=new ArrayList<PictureInfo>();
		String htmlpage=getHTML(url, "utf-8");
		int i=0;
		String src1 = "\"thumbURL\":\"";
		String src2 = "\",";
		String title1="\"fromPageTitleEnc\":\"";
		String title2="\",";
		String url1="\"objURL\":\"";
		String url2="\",";
		String origin1="\"objURL\":\"";
		String origin2="\",";
		int index1,index2;
		String src,title,_url,origin;
		

		while (htmlpage.contains(src1)&&i++<num) {
			PictureInfo pictureinfo=new PictureInfo();
			index1 = htmlpage.indexOf(src1);
			index2 = htmlpage.indexOf(src2, index1);
			src = htmlpage.substring(index1 + src1.length(), index2);
			index1 = htmlpage.indexOf(origin1);
			index2 = htmlpage.indexOf(origin2, index1);
			origin = htmlpage.substring(index1 + origin1.length(), index2);
		//	index1 = htmlpage.indexOf(url1);
		//	index2 = htmlpage.indexOf(url2, index1);
		//	_url = htmlpage.substring(index1 + url1.length(), index2);
			index1 = htmlpage.indexOf(title1);
			index2 = htmlpage.indexOf(title2, index1);
			title = htmlpage.substring(index1 + title1.length(), index2);
			//title=fromGBKtoUTF8(title);
			pictureinfo.setPictureTitle(title);
			origin=baiduDecode(origin);
			
			pictureinfo.setPictureUrl(src);
			pictureinfo.setPictureOrigin(origin);
			pictureinfo.setEngine(0);
			pictureinfo.setPlace(i);
		
			pictureinfo.setPictureSrc(src);
			result.add(pictureinfo);
			
			htmlpage = htmlpage.substring(index2);
		}
		return result;
	}
	private List<PictureInfo> getsogoResult(String url)
	{
		List<PictureInfo> result=new ArrayList<PictureInfo>();
		String htmlpage=getHTML(url, "GBK");
		int i=0;
		String src1 = "\"thumbUrl\":\"";
		String src2 = "\",\"";
		String title1="\"title\":\"";
		String title2="\",\"";
		String url1="\"pic_url\":\"";
		String url2="\",\"";
		String origin1="\"pic_url_noredirect\":\"";
		String origin2="\",\"";
		int index1,index2;
		String src,title,_url,origin;
	//	int begin = htmlpage.indexOf("imgTempData");
	//	int end = htmlpage.indexOf("fatalParamList");
	//	htmlpage = htmlpage.substring(begin, end);

		while (htmlpage.contains(src1)&&i++<num) {
			PictureInfo pictureinfo=new PictureInfo();
			index1 = htmlpage.indexOf(title1);
			index2 = htmlpage.indexOf(title2, index1);
			title = htmlpage.substring(index1 + title1.length(), index2);
			index1 = htmlpage.indexOf(src1);
			index2 = htmlpage.indexOf(src2, index1);
			src = htmlpage.substring(index1 + src1.length(), index2);
			index1 = htmlpage.indexOf(url1);
			index2 = htmlpage.indexOf(url2, index1);
			_url = htmlpage.substring(index1 + url1.length(), index2);
			index1 = htmlpage.indexOf(origin1);
			index2 = htmlpage.indexOf(origin2, index1);
			origin = htmlpage.substring(index1 + origin1.length(), index2);
			//title=fromGBKtoUTF8(title);
			pictureinfo.setPictureTitle(title);
			pictureinfo.setPictureSrc(src);
			pictureinfo.setPictureUrl(_url);
			pictureinfo.setPictureOrigin(origin);
			pictureinfo.setEngine(3);
			pictureinfo.setPlace(i);
			result.add(pictureinfo);
			
			htmlpage = htmlpage.substring(index2);
		}
		return result;
	}

	private  String getHTML(String pageURL, String encoding) {

		StringBuilder pageHTML = new StringBuilder();

		try {

			URL url = new URL(pageURL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			connection.setRequestProperty("User-Agent", "MSIE 7.0");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), encoding));

			String line = null;

			while ((line = br.readLine()) != null) {

				pageHTML.append(line);

				pageHTML.append("\r\n");

			}

			connection.disconnect();

		} catch (Exception e) {

			e.printStackTrace();

		}
		return pageHTML.toString();
	}
	private String fromGBKtoUTF8(String s)
	{
		//String unicode = new String(s.getBytes(),"GBK"); 
		//String utf8= new String(unicode.getBytes("UTF-8")); 
		String str="";
		try
		{
			str=URLEncoder.encode(s,"utf-8");
		}catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return str;
	}
	
	private String baiduDecode(String str)
	{
		  char[] table=new char[200];
		  for(int i=0;i<200;i++)table[i]=0;
		  String s="1";
		  s.substring(1);
		  if (s.equals(""))System.out.println("1n");
		  if(s==null)System.out.println("2n");
		  if(s.equals("\0"))System.out.println("3n");
		  table['w'] = 'a';  
		  table['k'] = 'b';  
		  table['v'] = 'c';  
		  table['1'] = 'd';  
		  table['j'] = 'e';  
		  table['u'] = 'f';  
		  table['2'] = 'g';  
		  table['i'] = 'h';  
		  table['t'] = 'i';  
		  table['3'] = 'j';  
		  table['h'] = 'k';  
		  table['s'] = 'l';  
		  table['4'] = 'm';  
		  table['g'] = 'n';  
		  table['5'] = 'o';  
		  table['r'] = 'p';  
		  table['q'] = 'q';  
		  table['6'] = 'r';  
		  table['f'] = 's';  
		  table['p'] = 't';  
		  table['7'] = 'u';  
		  table['e'] = 'v';  
		  table['o'] = 'w';  
		  table['8'] = '1';  
		  table['d'] = '2';  
		  table['n'] = '3';  
		  table['9'] = '4';  
		  table['c'] = '5';  
		  table['m'] = '6';  
		  table['0'] = '7';  
		  table['b'] = '8';  
		  table['l'] = '9';  
		  table['a'] = '0';  
		  String result="";
		  
		  while(!str.equals("")&&str!=null&&!str.equals("\0"))
		  {
			  if (str.startsWith("_z2C$q"))
			  {
				  result=result.concat(":");
				  str=str.substring(6);
			  }else if (str.startsWith("_z&e3B"))
			  {
				  result=result.concat(".");
				  str=str.substring(6);
			  }else if(str.startsWith("AzdH3F"))
			  {
				  result=result.concat("/");
				  str=str.substring(6);
			  }else if(table[str.charAt(0)]!=0)
			  {
				  result=result.concat(String.valueOf(table[str.charAt(0)]));
				  str=str.substring(1);
			  }else
			  {
				  result=result.concat(String.valueOf(str.charAt(0)));
				  str=str.substring(1);
			  }
			  
		  }
		  
		  return result;
	}
	public static String unicodeToString(String str) {
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))"); 
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
		ch = (char) Integer.parseInt(matcher.group(2), 16);
		str = str.replace(matcher.group(1), ch + ""); 
		}
		return str;
		}

	private String getOriginUrlBing(String url) //no use
	{
		String result=null;
		String htmlpage=getHTML(url, "utf8");
		
		Document doc = Jsoup.parse(htmlpage);
		Element ele;
		ele=doc.getElementsByClass("thumb").first();
		if (ele==null) return null;
		
		result=ele.attr("href");
		return result;
	}
	private String utf8Decode(String str)
	{
		String result=null;
		try
		{
			result=java.net.URLDecoder.decode(str, "utf8");;
		}catch (Exception e)
		{
			
		}
		return result;
	}
	private List<PictureInfo> throwNoUseDate(List<PictureInfo> result)
	{
		PictureInfo temp;
		double engineWeight;
		for (int i=0;i<result.size();i++)
		{
			temp=result.get(i);
			if (temp.getEngine()<0  || temp.getEngine()>3 || temp.getPlace()<1)
			{
				result.remove(i);  //delete unexcept thing
				i--;
				continue;
			}
			if (temp.getEngine()==0)
			{
				engineWeight=baiduWeight;
			}else if(temp.getEngine()==1)
			{
				engineWeight=bingWeight;
			}else if(temp.getEngine()==2)
			{
				engineWeight=youdaoWeight;
			}else 
			{
				engineWeight=sogoWeight;
			}
			temp.weight=engineWeight*temp.getPlace();
		}
		return result;
	}
	public int[] getResultDistribution(List<PictureInfo> results) {
		int resultsDistribution[]=new int[4];//0 baidu,1 bing ,2 youdao ,3 sogo
		for (int i=0;i<4;i++)  //4 should be change if the engine number change
		{
			resultsDistribution[i]=0;
		}
		if (results==null||results.isEmpty())return resultsDistribution;
		for (int i=0;i<results.size();i++)
		{
			if (results.get(i).getEngine()==0)resultsDistribution[0]++;
			if (results.get(i).getEngine()==1)resultsDistribution[1]++;
			if (results.get(i).getEngine()==2)resultsDistribution[2]++;
			if (results.get(i).getEngine()==3)resultsDistribution[3]++;
		}
		return resultsDistribution;
	}
}
