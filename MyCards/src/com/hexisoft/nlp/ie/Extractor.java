package com.hexisoft.nlp.ie;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.hexisoft.nlp.base.Chunk;
import com.hexisoft.nlp.base.MyTaggedWord;
import com.hexisoft.nlp.base.PosTagger;
import com.hexisoft.nlp.mycards.MyWebFragment;

import edu.stanford.nlp.ling.TaggedWord;

public class Extractor {

	static final String grammar = "(<DT>|<CD>)?(<JJ>)*(<NNS?>)*";
	static Pattern p = Pattern.compile(grammar);
	static final String grammar2 = "(<PRP>)";
	static Pattern p2 = Pattern.compile(grammar2);
	static PosTagger sTagger = MyWebFragment.tagger;
	
	static boolean is_of_np(String tags) {
		boolean r = false;
		Matcher m = p.matcher(tags);
		if (m.matches()) {
			r = true;
		}else {
			m = p2.matcher(tags);
			if (m.matches()) {
				r = true;
			}
		}
		return r;
	}
	
	static List<List<MyTaggedWord>> chunks_tag(List<List<TaggedWord>> twsList) {
		List<List<MyTaggedWord>> myTaggedWordsList = new ArrayList<List<MyTaggedWord>>();
		boolean out_chunk = true;
		for (List<TaggedWord> tws : twsList) {
			List<MyTaggedWord> myTaggedWords = new ArrayList<MyTaggedWord>();
			String tags = "";
			for (TaggedWord tw : tws) {
				MyTaggedWord mtw = new MyTaggedWord();
				mtw.setWord(tw.word());
				mtw.setTag(tw.tag());
				tags += "<"+tw.tag()+">";
				if (is_of_np(tags)) {
					if (out_chunk) {
						mtw.setMark("B");
						out_chunk = false;
					} else {
						mtw.setMark("I");
					}
				} else {
					mtw.setMark("O");
					out_chunk = true;
					tags = "";
				}
				myTaggedWords.add(mtw);
			}
			myTaggedWordsList.add(myTaggedWords);
		}
		return myTaggedWordsList;
	}

	public static List<Chunk> chunks(List<MyTaggedWord> mtws){
		List<Chunk> r = new ArrayList<Chunk>();
		boolean in_np = false;
		List<MyTaggedWord> tps = new ArrayList<MyTaggedWord>(); 
		for (MyTaggedWord mtw : mtws) {
			if (mtw.getMark().contentEquals("B")) {
				if (tps.size()>0) {
					Chunk chunk = new Chunk(tps);
					r.add(chunk);
				}
				tps = new ArrayList<MyTaggedWord>();
				tps.add(mtw);
				in_np = true;
			} else if(mtw.getMark().contentEquals("I")) {
				tps.add(mtw);
			} else {
				if (in_np) {
					in_np = false;
					Chunk chunk = new Chunk(tps);
					r.add(chunk);
					tps = new ArrayList<MyTaggedWord>();
					if (mtw.tag().length()>1) {
						tps.add(mtw);
					}
				} else {
					if (mtw.tag().length()>1) {
						tps.add(mtw);
					}
				}
			}
		}
		if (tps.size()>0) {
			Chunk chunk = new Chunk(tps);
			r.add(chunk);
		}
		return r;
	}
	
	public static String tags2type(String tags) {
		String type = "";
		String NP = "(<DT>|<CD>)?(<JJ>)*(<NNS?>)+";
		Pattern pn = Pattern.compile(NP);
		Matcher mn = pn.matcher(tags);
		if (mn.matches()) {
			type = "<n>";
		}else {
			type = tags;
		}
		return type;
	}
	
	public static void chunks2type(List<Chunk> sent) {
		for(Chunk chunk : sent) {
			chunk.setLabel(tags2type(chunk.getTags()));
		}
	}
	
	public static Chunk[] sent2triple(List<Chunk> sent) {
		Chunk[] triple = new Chunk[3];
		int n = sent.size();
		boolean find_entity = false;
		for (int i = 0, j = 0; i < 3 && j < n; ++j) {
			Chunk chunk = sent.get(j);
			if (!find_entity) {
				String type = chunk.getLabel();
				if (type.contentEquals("<n>")
						|| type.contentEquals("<PRP>")) {
					find_entity = true;
					triple[i++] = chunk;
				}else {
					continue;
				}
			}else {
				triple[i++] = chunk;
			}
		}
		return triple;
	}

	public static List<Chunk[]> sents2triples(String s) {
		Log.d("My", "sents2triples begins...");
		List<Chunk[]> triples = new ArrayList<Chunk[]>();
		List<List<TaggedWord>> twsList = sTagger.tag_d(s);
		Log.d("My", "size: "+twsList.size());
		List<List<MyTaggedWord>> sentList = chunks_tag(twsList);
		for(List<MyTaggedWord> tws : sentList) {
			List<Chunk> chunkList = chunks(tws);
			chunks2type(chunkList);
			Chunk[] triple = sent2triple(chunkList);
			triples.add(triple);
		}
		return triples;
	}
	
}
