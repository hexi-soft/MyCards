package hexi.nlp.base;

public class Sentence {
	private String e;
	private String c;
	private String b;
	private int m;
	private String memo;
	
	public String toString() {
		return e;
	}
	
	public Sentence(String eng,String chi,String book,int module, String memo) {
		e = eng;
		c = chi;
		b = book;
		m = module;
		this.memo = memo;
	}

	public String getEn() {
		return e;
	}

	public void setEn(String e) {
		this.e = e;
	}

	public String getChi() {
		return c;
	}

	public void setChi(String c) {
		this.c = c;
	}

	public String getBook() {
		return b;
	}

	public void setBook(String b) {
		this.b = b;
	}

	public int getModule() {
		return m;
	}

	public void setModule(int m) {
		this.m = m;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
}
