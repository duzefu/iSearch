package db.dao;

public interface ClassifierDBDao {
	
	public static enum ClassifierTableNames{education,employment,financial,health,it,literature,military,sports,tourism};
	
	public double getWeightOfWord(String word, ClassifierTableNames tableName);

}
