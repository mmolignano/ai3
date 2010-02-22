import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.util.HashMap;

/**
 * Class for the Stargram model. This class finds the longest previous string
 * that matches what is currently being read in by the test set, both from
 * previous characters in the test set but also from the train set.
 * 
 * @author Michael Molignano, Chris Pardy, Rich Pavis, John Sandbrook
 *
 */
public class StargramModel implements Model {

	private UnigramModel um;
	private String traindat;
	
	private HashMap<String, Integer> peekmap;
	
	public StargramModel(){
		this.um = new UnigramModel();
		this.traindat = "";
		
		this.peekmap = new HashMap<String, Integer>();
	}
	
	@Override
	public String predict(String previousChars, boolean peek) {
		String up = this.um.predict(previousChars, peek);
		if (peek) {	// Reduce amount of this character left in test data
			if (previousChars.length() > 0){
				String pc = previousChars.substring(previousChars.length() - 1, previousChars.length());
				this.peekmap.put(pc, this.peekmap.get(pc) - 1);
			}
		}
		int len = Math.min(this.traindat.length() - 1,previousChars.length());
		HashMap<String,Integer> fmap = new HashMap<String,Integer>();
		int flen = 1;
		while (len > 0){
			fmap.clear();
			String comps = previousChars.substring(previousChars.length() - len, previousChars.length());
			int cpos = this.traindat.length() - (len + 1);
			Boolean foundMatch = false;
			while (cpos >= 0){
				if (this.traindat.substring(cpos,cpos+len).equals(comps)){
					String s = this.traindat.substring(cpos+len,cpos+len+1);
					if (fmap.containsKey(s)){
						fmap.put(s, fmap.get(s) + 1);
					}else{
						fmap.put(s,1);
					}
					foundMatch = true;
					flen = len;
				}
				cpos = cpos - 1;
			}
			if (foundMatch){
				break;
			}
			len = len - 1;
		}
		len = previousChars.length() - 1;
		while (len >= flen){
			String comps = previousChars.substring(previousChars.length() - len, previousChars.length());
			int cpos = previousChars.length() - (len + 1);
			Boolean foundMatch = false;
			Boolean fmclear = true;
			while (cpos >= 0){
				if (previousChars.substring(cpos,cpos+len).equals(comps)){
					String s = previousChars.substring(cpos+len,cpos+len+1);
					if (len > flen && fmclear){
						fmap.clear();
						fmclear = false;
					}
					if (fmap.containsKey(s)){
						fmap.put(s,fmap.get(s) + 1);
					}else{
						fmap.put(s,1);
					}
					foundMatch = true;
				}
				cpos = cpos - 1;
			}
			if (foundMatch){
				break;
			}
			len = len - 1;
		}
		String m = null;
		int occur = 0;
		for(String k : fmap.keySet()){
			if (peek) {
				if (fmap.get(k) * peekmap.get(k) > occur){
					m = k;
					occur = fmap.get(k) * peekmap.get(k);
				}
			} else {
				if (fmap.get(k) > occur){
					m = k;
					occur = fmap.get(k);
				}
			}
		}
		if (m == null){
			return up;
		}else{
			return m;
		}
	}

	@Override
	public void train(String trainFile) {
		this.um.train(trainFile);
		try{
			FileReader fr = new FileReader(trainFile);
			int c = fr.read();
			while (c > -1){
				String s = Character.toString((char) c);
				if (!(s.equals(",") || s.equals("\n") || s.equals("\t") || s.equals(" ") || s.equals("\r"))){
					this.traindat = this.traindat + s;
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
		this.um.peek(testFile);
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
