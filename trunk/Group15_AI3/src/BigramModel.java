import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class BigramModel implements Model {

	private UnigramModel um;
	private HashMap<String,HashMap<String,Integer>> fmap;
	private HashMap<String, Integer> peekmap;
	
	public BigramModel(){
		this.um = new UnigramModel();
		this.fmap = new HashMap<String,HashMap<String,Integer>>();
		this.peekmap = new HashMap<String, Integer>();
	}
	
	@Override
	public String predict(String previousChars, boolean peek) {
		String up = this.um.predict(previousChars, peek);
		if (previousChars.length() > 1){
			String tp = previousChars.substring(previousChars.length()-2, previousChars.length()-1);
			String pc = previousChars.substring(previousChars.length() - 1, previousChars.length());
			if (this.fmap.containsKey(tp)){
				if(this.fmap.get(tp).containsKey(pc)){
					this.fmap.get(tp).put(pc, this.fmap.get(tp).get(pc) + 1);
				}else{
					this.fmap.get(tp).put(pc, 1);
				}
			}else{
				HashMap<String,Integer> th = new HashMap<String,Integer>();
				th.put(pc, 1);
				this.fmap.put(tp, th);
			}
			if (peek) {	// Reduce amount of this character left in test data
				this.peekmap.put(pc, this.peekmap.get(pc) - 1);
			}
		}
		if (previousChars.length() > 0){
			String pc = previousChars.substring(previousChars.length() - 1, previousChars.length());
			if (this.fmap.containsKey(pc)){
				String m = null;
				int occur = 0;
				for(String k:this.fmap.get(pc).keySet()){
					if (peek) {
						if (this.fmap.get(pc).get(k) * this.peekmap.get(k) > occur) {
							m = k;
							occur = this.fmap.get(pc).get(k) * this.peekmap.get(k);
						}
					} else {
						if(this.fmap.get(pc).get(k) > occur){
							m = k;
							occur = this.fmap.get(pc).get(k);
						}
					}
				}
				return m;
			}else{
				return up;
			}
		}else{
			return up;
		}
	}

	@Override
	public void train(String trainFile) {
		this.um.train(trainFile);
		try{
			FileReader fr = new FileReader(trainFile);
			String pc = null;
			int c = fr.read();
			while(c > -1){
				String s = Character.toString((char)c);
				if (!(s.equals(",") || s.equals(" ") || s.equals("\n") || s.equals("\t") || s.equals("\r"))){
					if (pc == null){
						pc = s;
					}else{
						if(this.fmap.containsKey(pc)){
							if(this.fmap.get(pc).containsKey(s)){
								this.fmap.get(pc).put(s,this.fmap.get(pc).get(s) + 1);
							}else{
								this.fmap.get(pc).put(s, 1);
							}
						}else{
							HashMap<String,Integer> th = new HashMap<String,Integer>();
							th.put(s, 1);
							this.fmap.put(pc,th);
						}
						pc = s;
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
