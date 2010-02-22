
/**
 * Represent a model to train and then predict characters from a test set
 * 
 * @author Michael Molignano, Chris Pardy, Rich Pavis, John Sandbrook
 *
 */
public interface Model {

	/**
	 * Method to predict the next character, defined by each model
	 * 
	 * @param previousChars	previous string of characters from the test set
	 * @param peek	whether of not the peek function has been used
	 * 
	 * @return	next character to guess in the test set
	 */
	public String predict(String previousChars, boolean peek);
	
	/**
	 * Train the model with the train file. This is defined by the specific model
	 * 
	 * @param train_file	path to the train file
	 */
	public void train(String train_file);
	
	/**
	 * Called when the model is allowed to peek at the test set. This keeps track
	 * of how many of each character are left in the test set but now what the
	 * order is.
	 * 
	 * @param test_file	path to the test file
	 */
	public void peek(String test_file);
	
}
