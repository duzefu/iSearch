package struts.actions.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;
import org.json.JSONObject;

import server.commonutils.HotwordsUtil;
import server.commonutils.MyStringChecker;

public class RealHot{
	
	/**
	 * return real time hot points
	 * @throws IOException
	 */
	public String execute() throws Exception {
		
		List<String> hotwords = new LinkedList<String>();
		int count = HotwordsUtil.getHotwords(hotwords);

		JSONArray respArr = new JSONArray();
		if (count > 0) {
			for (Iterator<String> it = hotwords.iterator(); it.hasNext();) {
				String word = it.next();
				word = new String(word.getBytes("UTF-8"));
				if (MyStringChecker.isBlank(word)) continue;
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("hotword", word);// 注意这里的hotword要跟hotwords.js中getHotwordsHtmlText函数中的jsonArr[i*5+j].hotword匹配
				respArr.put(jsonObj);
			}
		}
		
		sendResponse(respArr);
		return null;
	}
	
	private void sendResponse(JSONArray arr){
		
		try {
			HttpServletResponse res = ServletActionContext.getResponse();
			res.reset();
			res.setContentType("text/html;charset=utf-8");
			PrintWriter pw = null;
			pw = res.getWriter();
			pw.print(arr);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}