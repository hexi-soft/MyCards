package hexi.nlp.base;

import java.util.List;

public class Word {
	
	private String mLemma;
	private String mExplain;
	private List<Pos> mPoses;
	private double mFreq;
	
	public Word(String lemma, String explain, List<Pos> poses, double freq) {
		mLemma = lemma;
		mExplain = explain;
		mPoses = poses;
		mFreq = freq;
	}
	
	public String getLemma() {
		return mLemma;
	}

	public void setLemma(String lemma) {
		mLemma = lemma;
	}

	public String getExplain() {
		return mExplain;
	}

	public void setExplain(String explain) {
		mExplain = explain;
	}

	public List<Pos> getPoses() {
		return mPoses;
	}

	public void setPoses(List<Pos> poses) {
		mPoses = poses;
	}

	public double getFreq() {
		return mFreq;
	}

	public void setFreq(double freq) {
		mFreq = freq;
	}

	@Override
	public String toString() {
		return "Word [mLemma=" + mLemma + ", mExplain=" + mExplain + ", mPoses=" + mPoses + ", mFreq=" + mFreq + "]";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
