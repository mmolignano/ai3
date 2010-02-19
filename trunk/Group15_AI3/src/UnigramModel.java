import java.io.*;
import java.util.HashMap;


public class UnigramModel implements Model {

	private HashMap<String,Integer> fmap;
	
	public UnigramModel(){
		fmap = new HashMap<String,Integer>();
	}
	
	@Override
	public String predict(String previousChars) {
		if (previousChars.length() > 0){
			String pc = previousChars.substring(previousChars.length() - 1, previousChars.length());
			if (this.fmap.containsKey(pc)){
				this.fmap.put(pc, this.fmap.get(pc) + 1);
			}else{
				this.fmap.put(pc, 1);
			}
		}
		String m = null;
		int occur = 0;
		for(String k : this.fmap.keySet()){
			if(this.fmap.get(k) > occur){
				occur = this.fmap.get(k);
				m = k;
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
				String s = Character.toChars(c).toString();
				if (!(s.equals(",") || s.equals("\n") || s.equals(" ") || s.equals("\t") || s.equals("\r"))){
					if (this.fmap.containsKey(s)){
						this.fmap.put(s, this.fmap.get(s) + 1);
					}else{
						this.fmap.put(s, 1);
					}
				}
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

}
