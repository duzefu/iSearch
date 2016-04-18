package struts.actions.web;

import java.io.IOException;

import org.apache.struts2.ServletActionContext;

import server.info.config.SessionAttrNames;

public class UserLogOut {
	
	public void execute() throws IOException
	{
		ServletActionContext.getRequest().getSession().setAttribute(SessionAttrNames.USERNAME_ATTR, null);//把会话中的用户名属性清除
		ServletActionContext.getResponse().sendRedirect("./search.action");
		//		String responseText=null;
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("text/html; charset=UTF-8");
//		PrintWriter out = null;
//		try {
//			out = response.getWriter();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	    JSONObject json=new JSONObject();
//	    m_enuLang=(LangEnv)session.getAttribute(SessionAttrNames.LANG_ATTR);
//	    JsDataBundler.getJsonWhileLogout(json, m_enuLang);
//		responseText = json.toString();
//		out.print(responseText);
//		out.flush();
//		out.close();
	}

}
