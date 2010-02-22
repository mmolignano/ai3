
public interface Model {

	public String predict(String previousChars, boolean peek);
	
	public void train(String train_file);
	
	public void peek(String test_file);
	
}
