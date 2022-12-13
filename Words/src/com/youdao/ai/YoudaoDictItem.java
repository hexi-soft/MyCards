package com.youdao.ai;

public class YoudaoDictItem {

	String query;
	String[] returnPhrase;
	String[] translation;
	Basic basic;
	
	public class Basic {
	    private String phonetic;
	    private String[] explains;
	    public void setPhonetic(String phonetic) {
	    	this.phonetic = phonetic;
	    }
	    public String getPhonetic() {
	    	return phonetic;
	    }
	    public void setExplain(String[] explains) {
	    	this.explains = explains;
	    }
	    public String[] getExplains() {
	    	return explains;
	    }
	    
	}
	
	public void setBasic(Basic basic) {
		this.basic = basic;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	public YoudaoDictItem(String query, String[] returnPhrases) {
        this.query = query;
        this.returnPhrase = returnPhrases;
    }

    public YoudaoDictItem(String query) {
        this.query = query;
    }

    public YoudaoDictItem() {
    	query = "";
    	returnPhrase = new String[0];
    	translation = new String[0];
    }
    
    public String getQuery() {
    	return query;
    }
    public String getReturnPhrase() {
    	return joinStrings(returnPhrase);
    }
    public String getTranslation() {
    	return joinStrings(translation);
    }
    public Basic getBasic() {
    	return basic;
    }
    
    public static String joinStrings(String[] strings) {
    	String r = "";
    	for (String s : strings) {
    		r += s + " ";
    	}
    	if (!r.isEmpty()) {
    		r = r.substring(0,r.length()-1);
    	}
    	return r;
    }
    
    public String toString() {
    	return "query: "+query+"\r\n"
    			+ "returnPhrases: "+joinStrings(returnPhrase)+"\r\n"
    			+ "translations: "+joinStrings(translation);
    }
    
	public static void main(String[] args) {
		String[] ss = {"a","b"};
		System.out.println(joinStrings(ss));

	}

}
