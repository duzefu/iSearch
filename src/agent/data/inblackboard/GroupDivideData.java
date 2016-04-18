package agent.data.inblackboard;

import java.util.concurrent.CountDownLatch;

public class GroupDivideData extends BlackboardBaseData {
	private int userId;
	public GroupDivideData(CountDownLatch doneSig, int userId) {
		super(doneSig);
		this.userId = userId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
