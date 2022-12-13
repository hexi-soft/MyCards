package common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class SetUtil<T> extends HashSet<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	SetUtil<T> mDiffSet;
	
	public SetUtil<T> intersect(Collection<T> c) {
		SetUtil<T> r = new SetUtil<T>();
		mDiffSet = new SetUtil<T>();
		for(T o : c) {
			if (contains(o)) {
				r.add(o);
			}else {
				mDiffSet.add(o);
			}
		}
		return r;
	}
	
	public SetUtil<T> union(Collection<T> c) {
		for(T o : c) {
			add(o);
		}
		return this;
	}
	
	public SetUtil<T> intersect(T[] c) {
		mDiffSet = new SetUtil<T>();
		SetUtil<T> r = new SetUtil<T>();
		for(T o : c) {
			if (contains(o)) {
				r.add(o);
			}else {
				mDiffSet.add(o);
			}
		}
		return r;
	}
	
	public SetUtil<T> getDiffSet() {
		return mDiffSet;
	}
	
	public SetUtil<T> diffSet(Collection<T> c) {
		intersect(c);
		return mDiffSet;
	}
	
	public SetUtil<T> diffSet(T[] c) {
		intersect(c);
		return mDiffSet;
	}
	
	public static void main(String[] args) {
		ArrayList<String> words = new ArrayList<String>();
		words.add("apple");
		words.add("pear");
		words.add("orange");
		SetUtil<String> fruit = new SetUtil<String>();
		fruit.add("watermelon");
		fruit.add("banana");
		fruit.add("pear");
		fruit.add("apple");
		Collection<String> diffSet = fruit.diffSet(words);
		for(String f : diffSet) {
			System.out.print(f+" ");
		}
		Log.debug(diffSet.size());
	}
}
