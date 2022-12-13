package hexi.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Log;
import edu.stanford.nlp.ling.TaggedWord;
import hexi.nlp.base.Chunk;
import hexi.nlp.base.MyTaggedWord;

public class Extractor {
	static final String grammar = "(<DT>|<CD>)?(<JJ>)*(<NNS?>)*";
	static Pattern p = Pattern.compile(grammar);
	static final String grammar2 = "(<PRP>)";
	static Pattern p2 = Pattern.compile(grammar2);
	static PosTagger sTagger = new PosTagger(null);
				
	static boolean is_entity(ArrayList<TokenPos> tps) {
		int size = tps.size();
		if (size>0 && NLPHelper.is_entity_tag(tps.get(size-1).pos)) {
			return true;
		}else {
			return false;
		}
	}
	
	static boolean is_attribute(ArrayList<TokenPos> tps) {
		int size = tps.size();
		if (size>0 && NLPHelper.is_attribute_tag(tps.get(size-1).pos)) {
			return true;
		}else {
			return false;
		}
	}
	
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

	static void chunk_tag(TokenPos[] tps) {
		boolean out_chunk = true;
		for(TokenPos tp : tps) {
			if(NLPHelper.is_in_chunk(tp.pos)) {
				if (out_chunk) {
					tp.tag = "B";
					out_chunk = false;
				}else {
					tp.tag = "I";
				}
			}else {
				tp.tag = "O";
				out_chunk = true;
			}
		}
	}
	
	static TokenPos[] originize_pos(String tagged_s) {
		TokenPos[] tps = new TokenPos[0];
		if (!tagged_s.isEmpty()) {
			String[] token_tags = tagged_s.split("\\s");
			tps = new TokenPos[token_tags.length];
			for (int i = 0; i < token_tags.length; ++i) {
				String tt = token_tags[i];
				int idx_ul = tt.lastIndexOf('_');
				String token = tt.substring(0, idx_ul);
				String pos = tt.substring(idx_ul + 1);
				tps[i] = new TokenPos(token, pos);
			}
		}
		return tps;
	}

	static  Map<String,Object> get_triple(String sent) {
		Map<String,Object> triple = new HashMap<String,Object>();
		String tagged = sTagger.tag_s(sent);
		TokenPos[] tps = originize_pos(tagged);
		chunk_tag(tps);
		
		return triple;
	}
	
	static Object[] split_chunks(TokenPos[] tps) {
		Object[] chunks = new Object[3];
		int i = 0;
		boolean in_chunk = false;
		ArrayList<TokenPos> tokens = null;;
		ArrayList<TokenPos> relation = null;
		for(TokenPos tp : tps) {
			if (tp.tag.contentEquals("B")){
				in_chunk = true;
				tokens = new ArrayList<TokenPos>();
				tokens.add(tp);
			}else if(tp.tag.contentEquals("I")) {
				tokens.add(tp);
			}else if(tp.tag.contentEquals("O")) {
				if (in_chunk && i<2) {
					in_chunk = false;
					if (is_entity(tokens) || is_attribute(tokens)) {
						chunks[i++] = tokens;
					}
				}
				if(relation == null) {
					relation = new ArrayList<TokenPos>();
					relation.add(tp);
				}else {
					relation.add(tp);
				}
			}
		}
		if (tokens != null && (is_entity(tokens)|| is_attribute(tokens))) {
			chunks[1] = tokens;
		}
		chunks[2] = relation;
		return chunks;
	}
	
	static String label_entity_by_pos(String pos) {
		String label = pos;
		return label;
	}
	
	static Map<String,String> build_entity(ArrayList<TokenPos> tps){
		Map<String,String> entity = new HashMap<String,String>();
		int size = tps.size();
		TokenPos n = tps.get(size-1);
		String name = n.token;
		String label = label_entity_by_pos(n.pos);
		entity.put("name", name);
		entity.put("label", label);
		for(int i=0; i<size-1; ++i) {
			TokenPos tp = tps.get(i);
			String pos = tp.pos;
			if (pos.contentEquals("JJ")) {
				entity.put("attr_"+i, tp.token);
			}
		}
		return entity;
	}
	
	static String label_relation_by_poses(String rel) {
		String label = rel;
		return label;
	}
	
	static Map<String,Object> build_relation(ArrayList<TokenPos> tps){
		Map<String,Object> rel = new HashMap<String,Object>();
		int size = tps.size();
		String name = "";
		String label = "";
		for(int i=0; i<size; ++i) {
			TokenPos tp = tps.get(i);
			String token = tp.token;
			String pos = tp.pos;
			if (pos.length()>1) {
				name += token+" ";
				label += pos+"_";
			}
		}
		label = label.substring(0, label.length()-1);
		label = label_relation_by_poses(label);
		rel.put("name", name.trim());
		rel.put("label", label);
		return rel;
	}
	
	@SuppressWarnings("unchecked")
	public static Object[] getTriple(String sent) {
		Object[] triple = null;
		if (!sent.trim().isEmpty()) {
			triple = new Object[3];
			String ts = sTagger.tag_s(sent);
			Log.d(ts);
			TokenPos[] tps = originize_pos(ts);
			chunk_tag(tps);
			Object[] event = split_chunks(tps);
			for (int i = 0; i < 2; ++i) {
				ArrayList<TokenPos> o = (ArrayList<TokenPos>) event[i];
				triple[i] = build_entity(o);
			}
			triple[2] = build_relation((ArrayList<TokenPos>) event[2]);
		}
		return triple;
	}

	
	public static void tmain(String[] args) {
		String s = "Hongkong is located in South China.";
		s = "I'm going to Hongkong.";
		s = "She is a nice girl..!";
		s = "The little yellow dog barked at the cat.";
		s = " I like dogs.";
		s = "She likes me.";
		String ts = sTagger.tag_s(s);
		Log.d(ts);
		TokenPos[] tps = originize_pos(ts);
		chunk_tag(tps);
		Object[] event = split_chunks(tps);
		for(Object t : event) {
			ArrayList<TokenPos> o = (ArrayList<TokenPos>)t;
			Log.d(o);
		}
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

	public static void main(String[] args) {
		String s = "The little yellow dog barked at the cat.";
		s += " I'm an English teacher.";
		s += " They're ten lions.";
		s += " I have ten dragons.";
		s += " The book is red.";
		s += " I work at home.";
		s += " There is a pen on the desk.";
		s += " I do.";
		List<List<TaggedWord>> twsList = sTagger.tag_d(s);
		List<List<MyTaggedWord>> sentList = chunks_tag(twsList);
		for(List<MyTaggedWord> tws : sentList) {
			List<Chunk> chunkList = chunks(tws);
			chunks2type(chunkList);
			Chunk[] triple = sent2triple(chunkList);
			for(Chunk chunk : triple) {
				if (chunk != null) {
					Log.d(chunk+" : "+chunk.getLabel());
				}else {
					Log.d("<NULL>");
				}
			}
		}
	}

}

class TokenPos{
	String token;
	String pos;
	String tag; //chunk BIO tag
	
	public TokenPos(String token, String pos) {
		this.token = token;
		this.pos = pos;
		tag = "";
	}
	
	@Override
	public String toString() {
		return token+":"+pos+":"+tag;
	}
}
