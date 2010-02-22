/**
 * Class keeps track of a unigram, bigram, and trigram model. When predicting
 * it takes a look at all three, if unigram and bigram match, it uses that,
 * otherwise it uses trigram.
 * 
 * @author Michael Molignano, Chris Pardy, Rich Pavis, John Sandbrook
 *
 */
public class CombinedModel implements Model {

	private UnigramModel um;
	private BigramModel bm;
	private TrigramModel tm;
	
	public CombinedModel(){
		this.um = new UnigramModel();
		this.bm = new BigramModel();
		this.tm = new TrigramModel();
	}
	
	@Override
	public String predict(String previousChars, boolean peek) {
		String up = this.um.predict(previousChars, peek);
		String bp = this.bm.predict(previousChars, peek);
		String tp = this.tm.predict(previousChars, peek);
		
		// Return tp unless up and bp are the same
		if (up.equals(bp)) {
			return up;
		}
		
		return tp;
	}

	@Override
	public void train(String trainFile) {
		this.um.train(trainFile);
		this.bm.train(trainFile);
		this.tm.train(trainFile);
	}

	@Override
	public void peek(String testFile) {
		this.um.peek(testFile);
		this.bm.peek(testFile);
		this.tm.peek(testFile);
	}

}
