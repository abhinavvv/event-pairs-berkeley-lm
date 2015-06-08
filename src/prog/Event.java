package prog;

import java.util.ArrayList;

public class Event {
	String content;
	String verb;
	ArrayList<EventArg> arguments = new ArrayList<EventArg>(); 
	int argsNum;
	boolean args;
	
	public Event(String e){
		content = e;
		this.setVerb();
		args = false;
		
	}
	
	public Event(String e,boolean enable){
		args = true;
		content = e;
		this.setVerb();
		this.setArgs();
		argsNum = arguments.size();
	}
		
	
	
	public void setVerb(){
		verb = args ? content.substring(0, content.indexOf("|")-1) : content; 
	}
	
	public void setArgs(){
		String all_args = content.substring(content.indexOf("(")+1, content.indexOf(")"));
		if(all_args.length() > 1){
			String[] args_array = all_args.split(" , ");
			if(args_array.length > 1){ //more than one arguments
				for(int i=0; i<args_array.length; i++){ //remove spaces
					args_array[i] = args_array[i].replaceAll(" ", "");
					arguments.add(new EventArg(args_array[i]));
				}
			}
			else { //only one argument
				arguments.add(new EventArg(all_args));
			}
		}
	}
	
}
