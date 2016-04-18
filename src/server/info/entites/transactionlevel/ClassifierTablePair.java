package server.info.entites.transactionlevel;

public class ClassifierTablePair {

	protected String word;
	protected double weight;
	protected String subject;
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public void setWeight(Double weight){
		if(null==weight) this.weight=0;
		else this.weight=weight.doubleValue();
	}
	public void setWeight(Integer weight){
		if(null==weight) this.weight=0;
		else this.weight=weight.doubleValue();
	}
	
}
