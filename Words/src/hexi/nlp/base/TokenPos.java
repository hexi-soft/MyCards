package hexi.nlp.base;

public class TokenPos{
	String token;
	String pos;
	String tag; //chunk BIO tag
	
	public TokenPos(String token, String pos) {
		this.token = token;
		this.pos = pos;
	}
	@Override
	public String toString() {
		return token+":"+pos;
	}
}
