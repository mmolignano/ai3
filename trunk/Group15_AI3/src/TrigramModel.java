import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class TrigramModel implements Model {

	public BigramModel bm;
	public HashMap<String,HashMap<String,HashMap<String,Integer>>> fmap;
	
	public TrigramModel(){
		this.bm = new BigramModel();
		this.fmap = new HashMap<String,HashMap<String,HashMap<String,Integer>>>();
	}
	
	@Override
	public String predict(String previousChars) {
		String bp = this.bm.predict(previousChars);
		if (previousChars.length() > 2){
			String hp = previousChars.substring(previousChars.length()-3,previousChars.length()-2);
			String tp = previousChars.substring(previousChars.length()-2,previousChars.length()-1);
			String pc = previousChars.substring(previousChars.length()-1,previousChars.length());
			if (this.fmap.containsKey(hp)){
				if (this.fmap.get(hp).containsKey(tp)){
					if (this.fmap.get(hp).get(tp).containsKey(pc)){
						this.fmap.get(hp).get(tp).put(pc,this.fmap.get(hp).get(tp).get(pc) + 1);
					}else{
						this.fmap.get(hp).get(tp).put(pc,1);
					}
				}else{
					HashMap<String,Integer> th = new HashMap<String,Integer>();
					th.put(pc,1);
					this.fmap.get(hp).put(tp,th);
				}
			}else{
				HashMap<String,Integer> th1 = new HashMap<String,Integer>();
				HashMap<String,HashMap<String,Integer>> th2 = new HashMap<String,HashMap<String,Integer>>();
				th1.put(pc,1);
				th2.put(tp,th1);
				this.fmap.put(hp,th2);
			}
		}
		if (previousChars.length() > 1){
			String tp = previousChars.substring(previousChars.length()-2,previousChars.length()-1);
			String pc = previousChars.substring(previousChars.length()-1,previousChars.length());
			if (this.fmap.containsKey(tp)){
				if (this.fmap.get(tp).containsKey(pc)){
					String m = null;
					int occur = 0;
					for (String k : this.fmap.get(tp).get(pc).keySet()){
						if(this.fmap.get(tp).get(pc).get(k) > occur){
							m = k;
							occur = this.fmap.get(tp).get(pc).get(k);
						}
					}
					return m;
				}else{
					return bp;
				}
			}else{
				return bp;
			}
		}else{
			return bp;
		}
	}

	@Override
	public void train(String trainFile) {
		this.bm.train(trainFile);
		try{
			FileReader fr = new FileReader(trainFile);
			String tp = null;
			String pc = null;
			int c = fr.read();
			while (c > -1){
				String s = Character.toString((char) c);
				if (!(s.equals(",") || s.equals(" ") || s.equals("\n") || s.equals("\t") || s.equals("\r"))){
					if (tp == null){
						tp = s;
					}else if (pc == null){
						pc = s;
					}else{
						if (this.fmap.containsKey(tp)){
							if (this.fmap.get(tp).containsKey(pc)){
								if (this.fmap.get(tp).get(pc).containsKey(s)){
									this.fmap.get(tp).get(pc).put(s, this.fmap.get(tp).get(pc).get(s) + 1);
								}else{
									this.fmap.get(tp).get(pc).put(s, 1);
								}
							}else{
								HashMap<String,Integer> th = new HashMap<String,Integer>();
								th.put(s, 1);
								this.fmap.get(tp).put(pc, th);
							}
						}else{
							HashMap<String,Integer> th1 = new HashMap<String,Integer>();
							HashMap<String,HashMap<String,Integer>> th2 = new HashMap<String,HashMap<String,Integer>>();
							th1.put(s,1);
							th2.put(pc, th1);
							this.fmap.put(tp, th2);
						}
						tp = pc;
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

}
