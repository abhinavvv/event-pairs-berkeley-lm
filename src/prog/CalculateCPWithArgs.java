package prog;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class CalculateCPWithArgs {
	
	static HashMap<String, Double> cp = new HashMap<String, Double>();
	static HashMap<String, Double> logp = new HashMap<String, Double>();
	static HashMap<String, Double> event_count_map = new HashMap<String, Double>();
	static HashMap<String, Double> pair_count_map = new HashMap<String, Double>();
	
	final static int window = 1; //skip-2 bigram
	private boolean args = false; 
	
	static boolean eventSimVerbOneArg(Event e1, Event e2) throws IOException{ //are these two events similar?
		if(!e1.verb.equals(e2.verb)) return false; //if the verbs of two events are not the same, return false
		
		if(e1.argsNum == 0 || e2.argsNum == 0) return true; //if one of the events has no arguments, assume they are the same
		
		//if they have at least one argument with same 'word', assume they are the same
		for(int i=0; i<e1.argsNum; i++){
			for(int j=0; j<e2.argsNum; j++){
				if(e1.arguments.get(i).word != "PRONOUN"){ //non-pronoun
					if(e1.arguments.get(i).word.equals(e2.arguments.get(j).word)){
						if(!e1.arguments.get(i).type.contains("subj") && !e2.arguments.get(j).type.contains("subj")) return true;
					}		
				}
			}
		}
		return false;
	}
	
	static ArrayList<Event> listEvents(String event_dir_path) throws IOException{ //create a list of all events in a directory
		ArrayList<Event> events_list = new ArrayList<Event>();
		for (String line : Files.readAllLines(Paths.get(event_dir_path))){
			List<String> words = Arrays.asList(line.split("\\s+"));
			for(int i=0;i<words.size();i++){
				Event event = new Event(words.get(i));	
				events_list.add(event);
			}
			
		}
//		File event_dir = new File(event_dir_path);
//		for(int i=0; i<event_dir.listFiles().length; i++){
//			FileInputStream in = new FileInputStream(event_dir.listFiles()[i]);
//		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//		    while(reader.ready()){
//		    	String line = reader.readLine();
//		    	System.out.println(line);
//		    	System.exit(0);
//		    	Event event = new Event(line);
//		    	events_list.add(event);
//		    }
//		    reader.close();
//		}
		return events_list;
	}
	
	static ArrayList<Event> listUniqEvents(String event_dir_path) throws IOException{ //create a list of unique events in a directory
		ArrayList<Event> uniq_list = new ArrayList<Event>();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (String line : Files.readAllLines(Paths.get(event_dir_path))){
			List<String> words = Arrays.asList(line.split("\\s+"));
			for(int i=-0;i<words.size();i++){
				Event event = new Event(words.get(i));
				String event_content = event.content;
		    	if(!map.containsKey(event_content)){
		    		map.put(event_content, 1);
		    		uniq_list.add(event);
		    	}
			}
			
		}
//		File event_dir = new File(event_dir_path);
//		for(int i=0; i<event_dir.listFiles().length; i++){
//			FileInputStream in = new FileInputStream(event_dir.listFiles()[i]);
//		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//		    while(reader.ready()){
//		    	Event event = new Event(reader.readLine());
//		    	String event_content = event.content;
//		    	if(!map.containsKey(event_content)){
//		    		map.put(event_content, 1);
//		    		uniq_list.add(event);
//		    	}
//		    }
//		}
		return uniq_list;
	}
	
	static double countOrderedPair(Event e1, Event e2, ArrayList<Event> event_list, int window) throws IOException{
		double count = 0;
		for(int i=0; i<event_list.size(); i++){
			Event event = event_list.get(i);
			if(eventSimVerbOneArg(e1, event)){ //if we find e1 (or an event similar to e1)
				for(int j=0; j<window; j++){ //window = 1 means no-skip bigram
					if(i+j+1<event_list.size()){ //if we are not at the end of the event list yet
						Event second = event_list.get(i+j+1);
						if(eventSimVerbOneArg(e2, second)) count++; //if e2 occurs within the window after observing e1
					}
				}
			}
		}
		return count;
	}
	
	static HashMap<String, Double> getMapAllOrderedPairCounts(ArrayList<Event> event_list, int window) throws IOException{
		HashMap<String, Double> map = new HashMap<String, Double>();
		
		double count = 0;
		for(int i=0; i<event_list.size(); i++){
			Event e1 = event_list.get(i);
			for(int j=0; j<window; j++){ //window = 1 means no-skip bigram
				if(i+j+1<event_list.size()){ //if we are not at the end of the event list yet
					Event e2 = event_list.get(i+j+1);
					StringBuffer str = new StringBuffer(e1.content);
					str.append("\t").append(e2.content);
					count ++; //count the number of all pairs
					if(!map.containsKey(str.toString())){
						double val = countOrderedPair(e1, e2, event_list, window);
						map.put(str.toString(), val);
					}
				}
			}
		}
		map.put("NumberOfAllPairs", count);
		return map;
	}
	
	static HashMap<String, Double> getMapAllEventCounts(ArrayList<Event> event_list) throws IOException{
		System.out.println("Creating hash map of counts...");
		HashMap<String, Double> map = new HashMap<String, Double>();
		for(int i=0; i<event_list.size(); i++){
			Event e = event_list.get(i);
			if(map.containsKey(e.content)){
				double count = map.get(e.content);
				count = count + 1;;
				map.put(e.content, count);
			}
			else {
				map.put(e.content, new Double(1));
			}
		}
		return map;
	}
	
	static double[] calculateCP(Event e1, Event e2, ArrayList<Event> event_list, int window, double allEventCount, double allPairCount, HashMap<String, Double> event_map, HashMap<String, Double> pair_count_map) throws IOException{
		double score = 0;
		double count_e1_e2 = 0; //ordered pair (e1, e2)
		double count_e2_e1 = 0; //ordered pair (e2, e1)
		
		if(pair_count_map.containsKey(e1.content+"\t"+e2.content)) count_e1_e2 = pair_count_map.get(e1.content+"\t"+e2.content);
		if(pair_count_map.containsKey(e2.content+"\t"+e1.content)) count_e2_e1 = pair_count_map.get(e2.content+"\t"+e1.content);
				
		double p_e1_e2 = (count_e1_e2 + count_e2_e1) / allPairCount; //unordered probability P(e1, e2)
		double p_e1e2 = count_e1_e2 / allPairCount; //ordered probability P(e1 -> e2)
		double p_e2e1 = count_e2_e1 / allPairCount; //ordered probability P(e2 -> e1)
		double p_e1 = 0;
		if(event_map.containsKey(e1.content)) p_e1 = event_map.get(e1.content)/allEventCount;
		double p_e2 = 0;
		if(event_map.containsKey(e2.content)) p_e2 = event_map.get(e2.content)/allEventCount;
		if(p_e1>0 && p_e2>0) score = Math.log(p_e1_e2 / (p_e1 * p_e2)); //PMI
		
		double ordered_score = 100; //infinity, if p_e2e1 is 0
		if(p_e2e1 > 0) ordered_score = Math.log(p_e1e2 / p_e2e1);
		score = score + ordered_score; //add ordered component of CP
		
		double[] results = new double[2];
		results[0] = score;
		results[1] = p_e1_e2;
		return results;
	}
	
	
	static HashMap<String,Double> updateCounts(Event e, HashMap<String,Double> map){
		if(map.containsKey(e.content)){
			double count = map.get(e.content);
			count = count + 1;;
			map.put(e.content, count);
		}
		else{
			map.put(e.content, new Double(1));
		}
		return map;
		
	}
	
	
	static double[] sentenceCp(List<String>words, ArrayList<Event> event_list,HashMap<String, Double> event_count_map,
			HashMap<String, Double> pair_count_map,HashMap<String, Double> cp,HashMap<String, Double> logp) throws IOException{
		
		double total_cp =0.0;
		double total_logp = 0.0;
		double[] total_results = new double[2];
		for(int i=0;i<words.size()-1;i++){
			Event e1 = new Event(words.get(i));
			Event e2 = new Event(words.get(i+1));
			if(cp.containsKey(e1.content+"\t"+e2.content)){
				total_cp += cp.get(e1.content+"\t"+e2.content);
				total_logp += logp.get(e1.content+"\t"+e2.content);
			}
			else{
				
				event_count_map = updateCounts(e1,event_count_map);
				event_count_map = updateCounts(e2,event_count_map);
				
				if(!event_list.contains(e1))
					event_list.add(e1);
				if(!event_list.contains(e2))
					event_list.add(e2);
				
				double updated_all_events_num = event_list.size();
				
				StringBuffer str = new StringBuffer(e1.content);
				str.append("\t").append(e2.content);
				
				
				pair_count_map.put(str.toString(), new Double(1));
				double count = pair_count_map.get("NumberOfAllPairs");
				pair_count_map.put("NumberOfAllPairs", count+1.0);
				double all_pairs_num = pair_count_map.get("NumberOfAllPairs");
//				HashMap<String, Double> updated_pair_count_map = getMapAllOrderedPairCounts(event_list, 1);
//				double updated_all_pairs_num = updated_pair_count_map.get("NumberOfAllPairs");
//
				double[] results = new double[2];
				results = calculateCP(e1, e2, event_list, window, updated_all_events_num, 
						all_pairs_num, event_count_map, pair_count_map);
				total_cp += results[0];
				total_logp += results[1];
				
				
			}
			
			
		}
		total_results[0] = total_cp;
		total_results[1] = total_logp;
		return total_results;
	}
	
	/* *** -------------------------------- main --------------------------------------- *** */ 	
	/*
	 * args[0]: input directory which contains events extracted from posts
	 * args[1]: output filename which will be a tab-separated text file
	 *  */
	public static void main(String[] args) throws IOException{
		String topics_path = "/Users/abhinavvv/Documents/Works/Java/berkeleylm-1.1.5/data/";
		String topicfile = "refined_test_files/travel_refined_verbs_train.txt";
		String camp_all = topics_path + topicfile; //"camp_all_events_PRreplaced"
		
		ArrayList<Event> event_list = listEvents(camp_all);
		
		
		event_count_map = getMapAllEventCounts(event_list);
		double all_events_num = event_list.size();
		ArrayList<Event> uniq_ev_list = listUniqEvents(camp_all);
		
		pair_count_map = getMapAllOrderedPairCounts(event_list, window);
		double all_pairs_num = pair_count_map.get("NumberOfAllPairs");
		System.out.println("\n all_events_num = " + all_events_num + " ----- all_pairs_num = " + all_pairs_num);
		System.out.println("\n uniq_ev_list size = " + uniq_ev_list.size());
		
		
		FileOutputStream out = new FileOutputStream(topics_path + "result/cp_pairs.tsv");
		FileOutputStream out1 = new FileOutputStream(topics_path + "result/logp_pairs.tsv");//tab separated : camp_all_events_CP_oneArg_v100.txt
		OutputStreamWriter writer = new OutputStreamWriter(out);
		OutputStreamWriter writer1 = new OutputStreamWriter(out1);
		for(int i=0; i<uniq_ev_list.size(); i++){
			Event e1 = uniq_ev_list.get(i);
			for(int j=i+1; j<uniq_ev_list.size(); j++){
				Event e2 = uniq_ev_list.get(j);
				double[] results = new double[2];
				if(pair_count_map.containsKey(e1.content+"\t"+e2.content)){
					
					 results = calculateCP(e1, e2, event_list, window, all_events_num, 
							all_pairs_num, event_count_map, pair_count_map);
					cp.put(e1.content+"\t"+e2.content, results[0]);
					logp.put(e1.content+"\t"+e2.content, results[1]);
					
					StringBuffer pair2 = new StringBuffer(e1.content);
					pair2.append("\t").append(e2.content).append("\t").append(results[1]).append("\n");
					writer1.append(pair2.toString());
					if(results[0] > 0.2){ //threshold
						StringBuffer pair1 = new StringBuffer(e1.content);
						pair1.append("\t").append(e2.content).append("\t").append(results[0]).append("\n");
						writer.append(pair1.toString());
					}
				}
				if(pair_count_map.containsKey(e2.content+"\t"+e1.content)){
					results = calculateCP(e2, e1, event_list, window, all_events_num, 
							all_pairs_num, event_count_map, pair_count_map);
					cp.put(e2.content+"\t"+e1.content, results[0]);
					logp.put(e2.content+"\t"+e1.content, results[1]);
					StringBuffer pair2 = new StringBuffer(e2.content);
					pair2.append("\t").append(e1.content).append("\t").append(results[1]).append("\n");
					writer1.append(pair2.toString());
					if(results[0] > 0.2){
						StringBuffer pair1 = new StringBuffer(e2.content);
						pair1.append("\t").append(e1.content).append("\t").append(results[0]).append("\n");
						writer.append(pair1.toString());
					}
				}
				writer.flush();
				writer1.flush();
			}
		}
		
		writer.close();
		String testFile = "refined_test_files/travel_refined_verbs_test.txt";
		int win,loss,no_exp,ties,logp_win,logp_ties,logp_loss;
		no_exp = 0;
		win =0;
		loss = 0;
		ties = 0;
		logp_win = 0;
		logp_loss = 0;
		logp_ties = 0;
		for (String line : Files.readAllLines(Paths.get(topics_path+testFile))){
			List<String> words = Arrays.asList(line.split("\\s+"));
			no_exp+=1;
			double[] org_results = sentenceCp(words, event_list,event_count_map,pair_count_map, cp,logp);
			
//			System.out.printf("\n Orginial CP : %f",total_cp);
			for(int i=0; i<5;i++){
				List<String> permuted_events = new  ArrayList<String>(words);
				Collections.copy(permuted_events,words);
				Collections.shuffle(permuted_events);
//				HashMap<String,Double> new_event_count_map = event_count_map;
//				HashMap<String,Double> new_pair_count_map = pair_count_map;
				double[] permuted_results = sentenceCp(permuted_events, event_list,event_count_map,
						pair_count_map, cp,logp);
//				System.out.printf("\n Permuted CP : %f",permuted_total_cp);
				if(org_results[0] > permuted_results[0]){
					win ++;
				}
				else if(permuted_results[0] > org_results[0]){
					loss++;
				}
				else
					ties++;
				
				if(org_results[1] > permuted_results[1]){
					logp_win ++;
				}
				else if(permuted_results[1] > org_results[1]){
					logp_loss++;
				}
				else{
					logp_ties ++;
				}
			}
			
			
			
			
			
		}
		
		System.out.printf("\n CP : Wins = %d, Loss = %d, Ties = %d", win,loss,ties);
		System.out.printf("\n Logp : Wins = %d, Loss = %d, Ties = %d", logp_win,logp_loss,logp_ties);
		
	}	
	
	

}
