package com.hexisoft.nlp.base;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class NewWord {
	private int _id;
	private String _word;
	private String _explains;
	private int _re_degree;
	private Date _add_time;
	private String _source;
	private String _memo;
	
	public NewWord(int id, String word, String explains, int re_degree, Date add_time, String source, String memo) {
		_id = id; _word = word; _explains = explains; _re_degree = re_degree; 
		_add_time = add_time;
		_source = source; _memo = memo;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String get_word() {
		return _word;
	}

	public void set_word(String _word) {
		this._word = _word;
	}

	public String get_explains() {
		return _explains;
	}

	public void set_explains(String _explains) {
		this._explains = _explains;
	}

	public int get_re_degree() {
		return _re_degree;
	}

	public void set_re_degree(int _re_degree) {
		this._re_degree = _re_degree;
	}

	public Date get_add_time() {
		return _add_time;
	}

	public void set_add_time(Date _add_time) {
		this._add_time = _add_time;
	}

	public String get_source() {
		return _source;
	}

	public void set_source(String _source) {
		this._source = _source;
	}

	public String get_memo() {
		return _memo;
	}

	public void set_memo(String _memo) {
		this._memo = _memo;
	}

	public JSONObject toJson() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("id", _id);
			obj.put("word", _word);
			obj.put("explains", _explains);
			obj.put("re_degree", _re_degree);
			obj.put("add_time", _add_time.getTime());
			obj.put("source", _source);
			obj.put("memo", _memo);
		} catch (JSONException e) {
			
		}
		return obj;		
	}
}
