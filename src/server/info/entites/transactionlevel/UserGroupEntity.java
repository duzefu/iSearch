package server.info.entites.transactionlevel;

import java.util.ArrayList;
import java.util.List;

public class UserGroupEntity {

	protected List<Integer> uidlist;
	protected String groupRemark;
	
	public List<Integer> getUidlist(){
		if(null==uidlist) uidlist=new ArrayList<Integer>();
		return uidlist;
	}

	public String getGroupRemark() {
		return groupRemark;
	}

	public void setGroupRemark(String groupRemark) {
		this.groupRemark = groupRemark;
	}
	
	
}
