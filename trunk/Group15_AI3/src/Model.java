
public interface Model {

	public String predict(String previousChars);
	
	public void train(String train_file);
	
}
