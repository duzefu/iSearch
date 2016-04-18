package struts.actions.android;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

public class AndroidResponser {

	public static void response(List<Object> resObjList) throws IOException {

		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		OutputStream outs = response.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(outs);

		if (null != resObjList && !resObjList.isEmpty()){
			Iterator<Object> it=resObjList.iterator();
			while(it.hasNext()){
				Object o=it.next();
				oos.writeObject(o);
			}
		}
		
		oos.flush();
		outs.flush();
		oos.close();
		outs.close();

		return;
	}

	public static void response(Object resObj) throws IOException {

		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		OutputStream outs = response.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(outs);
		
		if (null != resObj)		oos.writeObject(resObj);
		
		oos.flush();
		oos.close();
		outs.flush();
		outs.close();
		return;
	}
}
