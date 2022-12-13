package hexi.nlp.base;

public class Pos {
	
	private String mTag;
	private double mProb;
	
	public Pos(String tag, double prob) {
		mTag = tag;
		mProb = prob;
	}

	public String getTag() {
		return mTag;
	}

	public void setTag(String tag) {
		mTag = tag;
	}

	public double getProb() {
		return mProb;
	}

	public void setProb(double prob) {
		mProb = prob;
	}
	
	public String toString() {
		return mTag+" "+mProb;
	}

}
