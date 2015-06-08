package prog;

public class EventArg {
	String content;
	String type;
	String word;
	
	public EventArg(String s){
		content = s;
		this.setType();
		this.setWord();
	}
	
	public void setType(){
		if(content != null) type = content.substring(0, content.indexOf(":"));
		else type = null;
	}
	
	public void setWord(){
		if(content != null) word = content.substring(content.indexOf(":")+1);
		else type = null;
	}

}
