package hexi.web.base;

public class Link {
	String _title;
	String _href;
	public Link(String title, String href){
		_title = title;
		_href = href;
	}
	
	public String get_title(){
		return _title;
	}
	
	public String get_href(){
		return _href;
	}

}

