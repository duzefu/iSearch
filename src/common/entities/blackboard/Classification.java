package common.entities.blackboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class Classification extends Observable {

	private String classifiName;
	private Map<Integer, String> userMap = new HashMap<Integer, String>();
	private List<Interest> interests = new ArrayList<Interest>();

	public Map<Integer, String> getUserMap() {
		return userMap;
	}

	public void setUserMap(Map<Integer, String> userMap) {
		userMap = userMap;
	}

	public String getClassifiName() {
		return classifiName;
	}

	public void setClassifiName(String classifiName) {
		this.classifiName = classifiName;
	}

	public List<Interest> getInterests() {
		return interests;
	}

	public void setInterests(List<Interest> interests) {
		this.interests = interests;
	}

	// 注册用户的方法...{注册对应classification下的UserAgent}
	/* 重写父类Observable中的public void addObserver(Observer o) */
	@Override
	public void addObserver(Observer o) {
		super.addObserver(o);
	}

	// 删除用户的方法...
	/* 使用父类Observable中的public void deleteObserver(Observer o) */
	@Override
	public void deleteObserver(Observer o) {
		super.deleteObserver(o);
	}

	// 添加兴趣的方法...{添加对应classification下的Interest}
	public void addInterest(Interest interest) {
		boolean flag = false;
		// 先判断List<Interest> interests中是否已经包含同名的兴趣
		// for(int i=0;i<this.interests.size();i++){
		// if(interests.get(i).getWordname().equals(interest.getWordname())){
		// flag = false;
		// updateInterest(interest);
		// break;
		// }
		// else {
		// flag = true;
		// }
		// }
		//
		// if(flag){
		this.interests.add(interest);
		setChanged();
		notifyObservers("分类名为：" + this.classifiName + "添加了兴趣:"
				+ interest.getWordname() + "权重值为：" + interest.getValue()); // 使用父类Observable中的notifyObservers(Object
																			// arg)
		// }
	}

	// 删除兴趣的方法...
	public void deleteInterest(Interest interest) {
		for (int i = 0; i < this.interests.size(); i++) {
			if (interests.get(i).getWordname().equals(interest.getWordname())) {
				this.interests.remove(interest);
				setChanged();
				notifyObservers("分类名为：" + this.classifiName + "移除了兴趣"
						+ interest.getWordname());
				break;
			}
		}
	}

	// 更新兴趣权重的方法...
	// public void updateInterest(Interest interest) {
	// for(int i=0;i<this.interests.size();i++){
	// if(this.interests.get(i).getWordname().equals(interest.getWordname())){
	// Double value=this.interests.get(i).getValue()+interest.getValue();
	// this.interests.get(i).setValue(value);
	// setChanged();
	// notifyObservers("分类名为："+this.classifiName+"的兴趣"+interest.getWordname()+"权重值发生了变化");
	// }
	// }
	// }
}
