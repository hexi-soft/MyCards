package hexi.common;

public class Pair<T, U> {
	private T first;
	private U second;
	
	public Pair() {
		first = null;
		second = null;
	}
	
	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
	}
	
	public void setFirst(T newValue) {
		first = newValue; 
	}
	
	public void setSecond(U newValue) {
		second = newValue;
	}
	
	public T getFirst() {
		return first;
	}
	
	public U getSecond() {
		return second;
	}
	

}
