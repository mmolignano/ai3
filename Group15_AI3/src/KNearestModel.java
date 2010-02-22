import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class to represent the KNearest Model. This class keeps track of past
 * characters in a map based upon their string order. When it is time to
 * predict a new character, it looks at the k closest nodes in the map
 * and uses the most from that group.
 * 
 * @author Michael Molignano, Chris Pardy, Rich Pavis, John Sandbrook
 *
 */
public class KNearestModel implements Model {

	private int k;
	private ArrayList<Node> nodes;
	private ArrayList<String> charMap;
	private Node prev;
	private UnigramModel um;
	
	private HashMap<String, Integer> peekmap;
	
	public KNearestModel(int k){
		this.k = k;
		this.nodes = new ArrayList<Node>();
		this.charMap = new ArrayList<String>();
		this.prev = null;
		this.um = new UnigramModel();
		
		this.peekmap = new HashMap<String, Integer>();
	}
	
	@Override
	public String predict(String previousChars, boolean peek) {
		String up = this.um.predict(previousChars, peek);
		if (previousChars.length() > 0){
			String pre = previousChars.substring(previousChars.length() - 1,previousChars.length());
			if (peek) {	// Reduce amount of this character left in test data
				this.peekmap.put(pre, this.peekmap.get(pre) - 1);
			}
			if (!(this.charMap.contains(pre))){
				this.charMap.add(pre);
			}
			int code = this.charMap.indexOf(pre);
			if (this.prev != null){
				this.prev.setNxtChar(code);
			}
			this.prev = new Node(code,previousChars.length(),this.k);
			this.prev.connect(nodes);
			return this.charMap.get(this.prev.predict());
		}else{
			return up;
		}
	}

	@Override
	public void train(String trainFile) {
		this.um.train(trainFile);
		try{
			FileReader fr = new FileReader(trainFile);
			int pos = 0;
			Node prev = null;
			int c = fr.read();
			while (c > -1){
				String s = Character.toString((char) c);
				if (!(s.equals(",") || s.equals("\n") || s.equals("\t") || s.equals("\r") || s.equals(" "))){
					if (!(charMap.contains(s))){
						charMap.add(s);
					}
					int code = charMap.indexOf(s);
					if (prev != null){
						prev.setNxtChar(code);
					}
					prev = new Node(code,pos,this.k);
					this.nodes.add(prev);
					pos = pos + 1;
				}
				c = fr.read();
			}
			fr.close();
			for (Node n : this.nodes){
				n.connect(nodes);
			}
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
