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
		if (args.length < 3){
			System.out.println("Usage: Model Train Test [peek]");
			System.out.println("\tModel can be any of {unigram,bigram,trigram,stargram,*nearest} where * is replaced by an integer k");
			System.out.println("\tTrain and Test are the filepaths for the Train and Test data");
			System.out.println("\tpeek can be {true,false} depending on if you want the ability to peek at the test data when predicting");
		}else{
			Model l = new UnigramModel();
			Pattern pat = Pattern.compile("^(\\d+)nearest$",Pattern.CASE_INSENSITIVE);
			Matcher m = pat.matcher(args[0]);
			//if (args.length == 3){
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
			//}
			String train;
			String test;
//			if (args.length == 2){
//				train = args[0];
//				test = args[1];
//			}else{
				train = args[1];
				test = args[2];
//			}
			boolean peek = false;
			if (args.length == 4) {
				peek = Boolean.parseBoolean(args[3]);
			}
			l.train(train);
			String p = "";
			if (peek) { 	// TODO : Find credible way to put this in
				l.peek(test);
			}
			try{
				FileReader fr = new FileReader(test);
				int c = fr.read();
				int total = 0;
				int guessed = 0;
				while (c > -1){
					String s = Character.toString((char) c);
					if (!(s.equals(",") || s.equals("\n") || s.equals("\r") || s.equals("\t") || s.equals(" "))){
						total = total + 1;
						String pre = l.predict(p, peek);
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
