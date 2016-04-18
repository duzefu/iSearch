package common.functions.schedule;

public class EngineScore  implements Comparable<EngineScore>{
	
	private String name;
	private String subject;
	private double score;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public int compareTo(EngineScore o) {
		if(score>o.getScore())
			return -1;
		else if(score<o.getScore())
			return 1;
		else return 0;
	}
	
}
