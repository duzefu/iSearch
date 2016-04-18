package db.dbhelpler;

public class InterestVo {
	private String name;
	private Double value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public InterestVo() {
		super();
	}

	public InterestVo(String name, Double value) {
		super();
		this.name = name;
		this.value = value;
	}

	@Override
	public String toString() {
		return "InterestVo [name=" + name + ", value=" + value + "]";
	}
	
}
