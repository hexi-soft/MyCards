package hexi.web.base;

import java.sql.Date;

public class UserNewWord {
	private String uid;
	private String word;
	private Date addTime;
	private String memo;
	private String explains;
	private String source;

	public UserNewWord(String uid, String word, Date addTime, String memo, String explains, String source) {
		this.uid = uid;
		this.word = word;
		this.addTime = addTime;
		this.memo = memo;
		this.explains = explains;
		this.source = source;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getExplains() {
		return explains;
	}

	public void setExplains(String explains) {
		this.explains = explains;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
}
