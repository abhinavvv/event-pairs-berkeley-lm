package prog;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Experiments {
		public static void discriminate(String testFile, PrintWriter writer, Compute c) throws IOException{
		
			System.out.println(c.wi.numWords());
			writer.println(c.wi.numWords());
			
			HashMap<String, Integer> results = new HashMap<String,Integer>();
			String[] scores = {"logpWins","logpLoss","logpTies","cpWins","cpLoss","cpTies"}; 
			for(String type: scores)
				results.put(type, 0);
			int count = 0;
			for (String line : Files.readAllLines(Paths.get(Compute.getFilename()+"refined_test_files/"+testFile))) {
				count ++;
				List<String> words = Arrays.asList(line.split("\\s+"));
				
				double[] orgResults =  new double[2];
				orgResults = c.overallScore(words);
				
				System.out.println("Orginial");
				writer.println("Orginial");
				System.out.printf("Logp = %f  Cp = %f \n ", orgResults[0],orgResults[1]);
				writer.printf("Logp = %f  Cp = %f \n ", orgResults[0],orgResults[1]);
				
				System.out.println("Permuted");
				writer.println("Permuted");
				
				for(int i=0; i<5;i++){
					double[] permutedResults =  new double[2];
					
					List<String> events = new  ArrayList<String>(words);
					Collections.copy(events,words);
					Collections.shuffle(events);
					
					permutedResults = c.overallScore(events);
					
					System.out.printf("Logp = %f  Cp = %f \n ", permutedResults[0],permutedResults[1]);
					writer.printf("Logp = %f  Cp = %f \n ", permutedResults[0],permutedResults[1]);
					
					results = c.updateScores(orgResults,permutedResults,results,"logp");
					results = c.updateScores(orgResults,permutedResults,results,"cp");
				}	
				System.out.println("--------------\n");
				writer.println("--------------\n");
			}
			
			System.out.printf("Total number of lines = %d\n",count);
			writer.printf("Total number of lines = %d\n",count);
			
			System.out.println("------ CP --------");
			writer.println("------ CP --------");
			
			System.out.printf("Wins = %d, Ties = %d, Loss = %d \n",results.get("cpWins"),
					results.get("cpTies"),results.get("cpLoss"));
			writer.printf("Wins = %d, Ties = %d, Loss = %d \n",results.get("cpWins"),
					results.get("cpTies"),results.get("cpLoss"));
			
			System.out.println("--------- Logp --------");
			writer.println("--------- Logp --------");
			
			System.out.printf("Wins = %d, Ties = %d, Loss = %d \n",results.get("logpWins"),
					results.get("logpTies"),results.get("logpLoss"));
			writer.printf("Wins = %d, Ties = %d, Loss = %d \n",results.get("logpWins"),
					results.get("logpTies"),results.get("logpLoss"));
		
			
			writer.close();
	}
		
		
	public static void Cloze(String testFile) throws IOException{
		int totalDeltaCp=0,totalDeltaPmi=0,totalDeltaLogp=0,totalLength=0,totalLines=0,perfectTotalCp=0,
		perfectTotalPmi=0,perfectTotalLogp=0;
		double averageDeltaTotalCp,averageDeltaTotalPmi,averageDeltaTotalLogp;
		
		int lineNumber;
		for (String line : Files.readAllLines(Paths.get(Compute.getFilename()+"refined_test_files/"+testFile))) {
			List<String> words = Arrays.asList(line.split("\\s+"));
			lineNumber = 0;
			int deltaCp = 0;
			int deltaPmi = 0;
			int deltaLogp = 0;
			int perfectLineCp = 0;
			int perfectLinePmi = 0;
			int perfectLineLogp = 0;
			if(words.size() > 50 || words.size() == 0){
				System.out.println("Skipped");
				continue;
			}
			else{
				totalLines +=1;
				totalLength += words.size(); 
				
			}
			
//	        # Loop for a single file(plucking out verbs).
			for(int i=0;i<words.size();i++){
				List<String> eventsTemp =  new ArrayList<String>();
				Collections.copy(eventsTemp,words);
				int actualPosition = i;
				String actualEvent = eventsTemp.get(i);
//				# remove event
				eventsTemp.remove(i);
				System.out.printf("Line number = %i, Actual Event position = %i, Actual Event = %s ", 
						lineNumber,i,actualEvent);
				double maxCp = -1000000.0;
			    double maxPmi = -1000000.0;
			    double maxLogp = -1000000.0;
			    
			    int minPositionCp = actualPosition;
			    int minPositionPmi = actualPosition;
			    int minPositionLogp = actualPosition;
//			    # loop for trying out the verb at each position(puttting verbs).
			    for(int j=0;j<eventsTemp.size()+1;j++){
			    	eventsTemp.add(j, actualEvent);
			    	double logLine = 0.0;
			    	double pmiLine = 0.0;
			    	double cpLine = 0.0;
//			    	 #Find Measure(LP, pmi, CP)
			    	
			    }
			    
				
				
				
				
				
				
			}
			lineNumber +=1;
		}
		

//		        # Loop for a single file(plucking out verbs).
//		        for i in range(len(events)):
//		            events_temp = list(events)
//		            actual_position = i
//		            actual_event = events_temp[actual_position]
//		            # remove event
//		            del events_temp[actual_position]
//		            print "Line ", ct, " event ", i
//		            print actual_position, actual_event
//		            max_cp = -1000000.0
//		            max_pmi = -1000000.0
//		            max_logp = -1000000.0
//
//		            min_position_cp = actual_position
//		            min_position_pmi = actual_position
//		            min_position_logp = actual_position
//
//		            # loop for trying out the verb at each position(puttting verbs).
//		            for j in range(len(events_temp)+1):
//		                events_temp.insert(j, actual_event)
//		                # print events_temp
//		                logp_line = 0.0
//		                pmi_line = 0.0
//		                cp_line = 0.0
//
//
//		                #Find Measure(LP, pmi, CP)
//		                for k in range(len(events_temp)-1):
//		                    e1 = events_temp[k]
//		                    e2 = events_temp[k+1]
//		                    cp_line += calculate_cp(e1, e2, pmi_dict, bigram_dict, unigram_dict)
//		                    pmi_line += get_pmi(e1, e2, pmi_dict, bigram_dict, unigram_dict)
//		                    logp_line += get_logp(e1, e2, bigram_dict, unigram_dict)
//
//		                if cp_line > max_cp:
//		                    max_cp = cp_line
//		                    min_position_cp = j
//		                if pmi_line > max_pmi:
//		                    max_pmi = pmi_line
//		                    min_position_pmi = j
//		                if logp_line > max_logp:
//		                    max_logp = logp_line
//		                    min_position_logp = j
//
//		                del events_temp[j]
//		                # print "Trying at position: ", j
//		                # print cp_line, " ", pmi_line, " ", logp_line
//
//		            #Now find delta for line.
//		            print "Placed at(CP): ", min_position_cp
//		            print "Placed at(PMI): ", min_position_pmi
//		            print "Placed at(LogP): ", min_position_logp
//
//		            delta_cp += abs(actual_position - min_position_cp)
//		            delta_pmi += abs(actual_position - min_position_pmi)
//		            delta_logp += abs(actual_position - min_position_logp)
//
//		            if actual_position == min_position_cp:
//		                perfect_line_cp += 1
//		            if actual_position == min_position_pmi:
//		                perfect_line_pmi += 1
//		            if actual_position == min_position_logp:
//		                perfect_line_logp += 1
//
//		            # insert event back.
//		            events_temp.insert(actual_position, actual_event)
//
//		        # Average for line
//		        avg_delta_cp_line = float(delta_cp)/len_line
//		        average_delta_total_cp += avg_delta_cp_line
//		        perfect_total_cp += perfect_line_cp
//		        print "Line ", ct, " Mean position for line(CP): ", avg_delta_cp_line, " Perfect for line(CP): ", perfect_line_cp
//
//		        avg_delta_pmi_line = float(delta_pmi)/len_line
//		        average_delta_total_pmi += avg_delta_pmi_line
//		        perfect_total_pmi += perfect_line_pmi
//		        print "Line ", ct, " Mean position for line(PMI): ", avg_delta_pmi_line, " Perfect for line(PMI): ", perfect_line_pmi
//
//		        avg_delta_logp_line = float(delta_logp)/len_line
//		        average_delta_total_logp += avg_delta_logp_line
//		        perfect_total_logp += perfect_line_logp
//		        print "Line ", ct, " Mean position for line(LogP): ", avg_delta_logp_line, " Perfect for line(LogP): ", perfect_line_logp
//
//		        #Add for the global deltas.
//		        total_delta_cp += delta_cp
//		        total_delta_pmi += delta_pmi
//		        total_delta_logp += delta_logp
//
//		    # Average for whole test set.
//		    avg_total_bytotal_cp = float(total_delta_cp)/total_length
//		    avg_total_bytotal_pmi = float(total_delta_pmi)/total_length
//		    avg_total_bytotal_logp = float(total_delta_logp)/total_length
//
//		    avg_total_by_lines_cp = float(average_delta_total_cp)/total_lines
//		    avg_total_by_lines_pmi = float(average_delta_total_pmi)/total_lines
//		    avg_total_by_lines_logp = float(average_delta_total_logp)/total_lines
//
//		    print "Total Stats:"
//		    print "Number of documents: ", total_lines, " ", total_length
//		    print "Average Mean Positional score for CP ", avg_total_bytotal_cp
//		    print "Average Mean Positional score for PMI ", avg_total_bytotal_pmi
//		    print "Average Mean Positional score for LOGP ", avg_total_bytotal_logp
//		    print "Average Mean Positional score for CP(By line average total) ", avg_total_by_lines_cp
//		    print "Average Mean Positional score for PMI(By line average total) ", avg_total_by_lines_pmi
//		    print "Average Mean Positional score for LOGP(By line average total) ", avg_total_by_lines_logp
//		    print "Perfect Total(CP)", perfect_total_cp
//		    print "Perfect Total(PMI)", perfect_total_pmi
//		    print "Perfect Total(LogP)", perfect_total_logp
//
//
//		    w = open(results_file, "wb")
//		    w.write(test_file + "\n")
//		    w.write("Total Stats:" + "\n")
//		    w.write("Number of documents: " + str(total_lines) + " #Events" + str(total_length) + "\n")
//		    w.write("Average Mean Positional score for CP " + str(avg_total_bytotal_cp) + "\n")
//		    w.write("Average Mean Positional score for PMI " + str(avg_total_bytotal_pmi) + "\n")
//		    w.write("Average Mean Positional score for LOGP " + str(avg_total_bytotal_logp) + "\n")
//		    w.write("Average Mean Positional score for CP(By line average total) " + str(avg_total_by_lines_cp) + "\n")
//		    w.write("Average Mean Positional score for PMI(By line average total) " + str(avg_total_by_lines_pmi) + "\n")
//		    w.write("Average Mean Positional score for LOGP(By line average total) " + str(avg_total_by_lines_logp) + "\n")
//		    w.write("Perfect Total(CP)" + str(perfect_total_cp) + "\n")
//		    w.write("Perfect Total(PMI)" + str(perfect_total_pmi) + "\n")
//		    w.write("Perfect Total(LogP)" + str(perfect_total_logp) + "\n")
	}
		
	public static void normalDiscriminate(String arpaFile, String outputFile, String testFile) throws IOException{
		Compute c = new Compute(arpaFile);
		PrintWriter writer = new PrintWriter(new FileWriter(Compute.getFilename()+"refined_gt_result_files/"+ outputFile));
		discriminate(testFile,writer,c);
	}
	
	public static void combinedDiscriminate(String arpaFile, String skippedArpaFile, String testFile, String skippedTestFile,
			String outputFile) throws IOException{
		
		Combined comb = new Combined(arpaFile,skippedArpaFile);
		
		double lamda = comb.getLamda(skippedTestFile);
		System.out.println(lamda);
//		
//		Compute c = new Compute(arpaFile,skippedArpaFile,lamda);
//		
//		PrintWriter writer = new PrintWriter(new FileWriter(Compute.getFilename()+"refined_kn_result_files/"+
//		outputFile));
//		writer.printf("Lamda = %f\n", lamda);
//		
//		
//		discriminate(testFile,writer,c);
		
	}

	public static void main(String[] args) throws Exception {

		
		// Travel!!!!
//		normalDiscriminate("travel_refined.v.o2.ov.kn.arpa","travel_refined_v_discriminate.txt","travel_refined_verbs_test.txt");
//		normalDiscriminate("travel_refined.vo.o2.ov.kn.arpa","travel_refined_vo_discriminate.txt","travel_refined_vo_test.txt");
//		normalDiscriminate("travel_refined.vs.o2.ov.kn.arpa","travel_refined_vs_discriminate.txt","travel_refined_vs_test.txt");
//		normalDiscriminate("travel_refined.vso.o2.ov.kn.arpa","travel_refined_vso_discriminate.txt","travel_refined_vso_test.txt");
		

		// Sports!!!!!
		
//		normalDiscriminate("sports.v.o2.ov.add1.arpa","sports_v_discriminate.txt","sports_verbs_test.txt");
//		normalDiscriminate("sports.vo.o2.ov.add1.arpa","sports_vo_discriminate.txt","sports_representation_vo_test.txt");
//		normalDiscriminate("sports.vs.o2.ov.add1.arpa","sports_vs_discriminate.txt","sports_representation_vs_test.txt");
//		normalDiscriminate("sports.vso.o2.ov.add1.arpa","sports_vso_discriminate.txt","sports_representation_vso_test.txt");
		
	
		
		//Travel!!!!
		
//		combinedDiscriminate("travel_refined.v.o2.ov.kn.arpa","travel_refined.skipped.v.o2.ov.kn.arpa","travel_refined_verbs_test.txt",
//				"travel_refined_verbs_test_skipped.txt","travel_redefined_v_discriminate_skipped.txt");
//		combinedDiscriminate("travel_refined.vso.o2.ov.kn.arpa","travel_refined.skipped.vso.o2.ov.kn.arpa","travel_refined_vso_test.txt",
//				"travel_refined_vso_test_skipped.txt","travel_refined_vso_discriminate_skipped.txt");
//		combinedDiscriminate("travel_refined.vs.o2.ov.kn.arpa","travel_refined.skipped.vs.o2.ov.kn.arpa","travel_refined_vs_test.txt",
//				"travel_refined_vs_test_skipped.txt","travel_refined_vs_discriminate_skipped.txt");
//		combinedDiscriminate("travel_refined.vo.o2.ov.kn.arpa","travel_refined.skipped.vo.o2.ov.kn.arpa","travel_refined_vo_test.txt",
//				"travel_refined_vo_test_skipped.txt","travel_refined_vo_discriminate_skipped.txt");
		
		
		
		//Sports!!!!
		
		
//		combinedDiscriminate("sports.v.o2.ov.add1.arpa","sports.skipped.v.o2.ov.add1.arpa","sports_verbs_test.txt",
//				"sports_verbs_test_skipped.txt","sports_v_discriminate_skipped.txt");
//		combinedDiscriminate("sports.vso.o2.ov.add1.arpa","sports.skipped.vso.o2.ov.add1.arpa","sports_representation_vso_test.txt",
//				"sports_representation_vso_test_skipped.txt","sports_vso_discriminate_skipped.txt");
//		combinedDiscriminate("sports.vs.o2.ov.add1.arpa","sports.skipped.vs.o2.ov.add1.arpa","sports_representation_vs_test.txt",
//				"sports_representation_vs_test_skipped.txt","sports_vs_discriminate_skipped.txt");
//		combinedDiscriminate("sports.vo.o2.ov.add1.arpa","sports.skipped.vo.o2.ov.add1.arpa","sports_representation_vo_test.txt",
//				"sports_representation_vo_test_skipped.txt","sports_vo_discriminate_skipped.txt");
		 
//		Combined comb = new Combined("travel.v.o2.ov.gt.arpa","travel.skipped.v.o2.ov.gt.arpa");
//		
//		
//		double lamda = comb.getLamda("travel_text_verbs_test_skipped.txt");
//		
//		Compute c = new Compute("travel.v.o2.ov.gt.arpa","travel.skipped.v.o2.ov.gt.arpa",lamda);
//		
//		PrintWriter writer = new PrintWriter(new FileWriter(Compute.getFilename()+"gt_result_files/"+
//		"travel_v_discriminate_skipped.txt"));
//		writer.printf("Lamda = %f\n", lamda);
//		
//		
//		discriminate("travel_text_verbs_test.txt",writer,c);
		
		
		// TODO Auto-generated method stub
		
//		
		Compute c = new Compute("refined_kn_arpa_files/travel_refined.v.o2.ov.kn.arpa",
				"refined_kn_arpa_files/travel_refined.v.o2.ov.kn.arpa",0.8);
		c.getEventParis("travel_refined_verbs_test_skipped.txt", "travel_refined_verbs_pairs_skipped.txt");
		
//		Compute c = new Compute("kn_arpa_files/travel.vso.o2.ov.kn.arpa");
//		c.getEventParis("travel_representation_vs_test.txt", "travel_vso_pairs.txt");
		
		

	}

}
