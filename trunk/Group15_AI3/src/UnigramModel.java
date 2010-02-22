import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Class to represent the Unigram model. This class keeps track of how many of
 * each character has been seen in the train and test set. It uses this to
 * predict the next character based on what has occured the most in the past.
 * 
 * @author Michael Molignano, Chris Pardy, Rich Pavis, John Sandbrook
 *
 */
public class UnigramModel implements Model {

	private HashMap<String,Integer> fmap;
	
	// Used when peeking at the test set
	private HashMap<String, Integer> peekmap;
	
	public UnigramModel(){
		fmap = new HashMap<String,Integer>();
		peekmap = new HashMap<String, Integer>();
	}
	
	@Override
	public String predict(String previousChars, boolean peek) {
		if (previousChars.length() > 0){
			String pc = previousChars.substring(previousChars.length() - 1, previousChars.length());
			if (this.fmap.containsKey(pc)){
				this.fmap.put(pc, this.fmap.get(pc) + 1);
			}else{
				this.fmap.put(pc, 1);
			}
			if (peek) {	// Reduce amount of this character left in test data
				this.peekmap.put(pc, this.peekmap.get(pc) - 1);
			}
		}
		String m = null;
		int occur = 0;
		for(String k : this.fmap.keySet()){
			if (peek) {
				if (this.peekmap.get(k) > occur) { // This character is left in the test set
					occur = this.peekmap.get(k);
					m = k;
				}
			} else {
				if(this.fmap.get(k) > occur){
					occur = this.fmap.get(k);
					m = k;
				}
			}
		}
		return m;
	}

	@Override
	public void train(String trainFile) {
		try{
			FileReader fr = new FileReader(trainFile);
			int c = fr.read();
			while (c > -1){
				String s = Character.toString((char) c);
				if (!(s.equals(",") || s.equals("\n") || s.equals(" ") || s.equals("\t") || s.equals("\r"))){
					if (this.fmap.containsKey(s)){
						this.fmap.put(s, this.fmap.get(s) + 1);
					}else{
						this.fmap.put(s, 1);
					}
				}
				c = fr.read();
			}
			fr.close();
		}catch (FileNotFoundException e){
			System.err.println("Error, could not open train file");
			System.exit(-1);
		}catch (IOException e){
			System.err.println("Error, could not read train data");
			System.exit(-1);
		}
	}

	@Override
	public void peek(String testFile) {
		try{
			FileReader fr = new FileReader(testFile);
			int c = fr.read();
			while (c > -1){
				String s = Character.toString((char) c);
				if (!(s.equals(",") || s.equals("\n") || s.equals(" ") || s.equals("\t") || s.equals("\r"))){
					if (this.peekmap.containsKey(s)){
						this.peekmap.put(s, this.peekmap.get(s) + 1);
					}else{
						this.peekmap.put(s, 1);
					}
				}
				c = fr.read();
			}
			fr.close();
		}catch (FileNotFoundException e){
			System.err.println("Error, could not open test file");
			System.exit(-1);
		}catch (IOException e){
			System.err.println("Error, could not read test data");
			System.exit(-1);
		}
	}

}
