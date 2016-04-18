package common.functions.schedule;

public class ClassScore  implements Comparable<ClassScore>{
	
	private String classname;
	private int score;
	
	public String getName() {
		return classname;
	}
	public void setName(String name) {
		this.classname = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}

	
	@Override
	public int compareTo(ClassScore o) {
		
		if(score>o.getScore())
			return -1;
		else if(score<o.getScore())
			return 1;
		else return 0;
	}
	
}
