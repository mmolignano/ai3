import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.*;


public class AI3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length < 2){
			System.out.println("Usage: [Model] Train Test");
		}else{
			Model l = new UnigramModel();
			Pattern pat = Pattern.compile("^(\\d+)nearest$",Pattern.CASE_INSENSITIVE);
			Matcher m = pat.matcher(args[0]);
			if (args.length == 3){
				if (args[0].equalsIgnoreCase("bigram")){
					l = new BigramModel();
				}else if (args[0].equalsIgnoreCase("trigram")){
					l = new TrigramModel();
				}else if (args[0].equalsIgnoreCase("stargram")){
					l = new StargramModel();
				}else if (m.matches()){
					int k = Integer.parseInt(m.group(1));
					l = new KNearestModel(k);
				}
			}
			String train;
			String test;
			if (args.length == 2){
				train = args[0];
				test = args[1];
			}else{
				train = args[1];
				test = args[2];
			}
			l.train(train);
			String p = "";
			try{
				FileReader fr = new FileReader(test);
				int c = fr.read();
				int total = 0;
				int guessed = 0;
				while (c > -1){
					String s = Character.toString((char) c);
					if (!(s.equals(",") || s.equals("\n") || s.equals("\r") || s.equals("\t") || s.equals(" "))){
						total = total + 1;
						String pre = l.predict(p);
						if (pre.equals(s)){
							guessed = guessed + 1;
						}
						System.out.print("Prediction: ");
						System.out.print(pre);
						System.out.print("\n");
						System.out.print("Actual: ");
						System.out.print(s);
						System.out.print("\n");
						p  = p + s;
					}
					c = fr.read();
				}
				fr.close();
				float correct = ((float)guessed/(float)total) * 100;
				System.out.print("Correctly Guessed ");
				System.out.print(correct);
				System.out.print("%\n");
			}catch (FileNotFoundException e){
				System.err.println("Error, could not open test file");
				System.exit(-1);
			}catch (IOException e){
				System.err.println("Error, could not read test data");
			}
		}
	}

}
