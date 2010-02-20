import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.util.HashMap;


public class StargramModel implements Model {

	private UnigramModel um;
	private String traindat;
	
	public StargramModel(){
		this.um = new UnigramModel();
		this.traindat = "";
	}
	
	@Override
	public String predict(String previousChars) {
		String up = this.um.predict(previousChars);
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
			if (fmap.get(k) > occur){
				m = k;
				occur = fmap.get(k);
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

}
