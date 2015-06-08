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
		
		
	public static void Cloze(String testFile,Compute c) throws IOException{
		int totalDeltaCp=0,totalDeltaLogp=0,totalLength=0,totalLines=0,perfectTotalCp=0,
				perfectTotalLogp=0;
		double averageDeltaTotalCp=0.0,averageDeltaTotalLogp=0.0;
		
		int lineNumber =0;
		for (String line : Files.readAllLines(Paths.get(Compute.getFilename()+"refined_test_files/"+testFile))) {
			List<String> words = Arrays.asList(line.split("\\s+"));
			int deltaCp = 0;
			int deltaLogp = 0;
			int perfectLineCp = 0;
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
				List<String> eventsTemp =  new ArrayList<String>(words);
				Collections.copy(eventsTemp,words);
				int actualPosition = i;
				String actualEvent = eventsTemp.get(i);
//				# remove event
				eventsTemp.remove(i);
				System.out.printf("\n Line number = %d, Actual Event position = %d, Actual Event = %s ", 
						totalLines,i,actualEvent);
				double maxCp = -1000000.0;
			    double maxLogp = -1000000.0;
			    
			    int minPositionCp = actualPosition;
			    int minPositionLogp = actualPosition;
//			    # loop for trying out the verb at each position(puttting verbs).
			    System.out.println("\nPrinting positions");
			    for(int j=0;j<eventsTemp.size()+1;j++){
			    	eventsTemp.add(j, actualEvent);
			    	double logpLine = 0.0;
			    	double cpLine = 0.0;
			    	
//			    	 #Find Measure(LP, pmi, CP)
			    	double[] results = new double[2];
			    	results = c.overallScore(eventsTemp);
			    	logpLine = results[0];
			    	cpLine = results[1];
			    	
			    	if(cpLine > maxCp){
			    		maxCp = cpLine;
			            minPositionCp = j;
			    	}
			    	if(logpLine > maxLogp){
			    		maxLogp = logpLine;
			            minPositionLogp = j;
			    	}
			    	
			    	eventsTemp.remove(j);
			    	
			    }
			    
			    System.out.printf("\n Position : Actual =%d,  Logp = %d and by CP = %d",actualPosition, minPositionLogp,minPositionCp);
			    deltaCp += Math.abs(actualPosition - minPositionCp);
			    deltaLogp += Math.abs(actualPosition - minPositionLogp);
			    
			    if(minPositionCp == actualPosition){
			    	perfectLineCp +=1;
			    }
			    if(minPositionLogp == actualPosition){
			    	perfectLineLogp +=1;
			    }
			    
			    eventsTemp.add(actualPosition, actualEvent);
				
			}
			double averageDeltaCpLine = deltaCp/words.size();
			averageDeltaTotalCp += averageDeltaCpLine;
			perfectTotalCp += perfectLineCp;
			
			System.out.printf("\n  CP : Line number  = %d, Mean Position = %f, Number of perfect lines = %d",
					totalLines, averageDeltaCpLine,perfectLineCp); 
			
			double averageDeltaLogpLine = deltaLogp/words.size();
			averageDeltaTotalLogp += averageDeltaLogpLine;
			perfectTotalLogp += perfectLineLogp;
			
			System.out.printf("\n Logp : Line number  = %d, Logp Mean Position = %f, Number of perfect lines = %d",
					totalLines, averageDeltaLogpLine,perfectLineLogp); 
			
			lineNumber +=1;
//			#Add for the global deltas.
	        totalDeltaCp += deltaCp;
	        totalDeltaLogp += deltaLogp;
	        
		}
//        Average delta(penalizing) by each of them
		System.out.printf("\n Line number and total lines = %d %d",lineNumber,totalLines);
		System.out.println("\n Statistics:");
		System.out.printf("\nNumber of documents = %d ",totalLines);
		System.out.println("\n ---------------- By total Length of line --------------");
		double sub = totalDeltaCp/totalLength;
		System.out.printf("\n Average Mean Positional score for CP = %f",sub);
		double sub1 = totalDeltaLogp/totalLength;
		System.out.printf("\n Average Mean Positional score for Logp = %f",sub1);
		System.out.println("\n ---------------- By Number of Lines -----------");
		System.out.printf("\n Line wise Average Mean Positional score for CP = %f",averageDeltaTotalCp/totalLines);
		System.out.printf("\n Line wise Average Mean Positional score for Logp = %f",averageDeltaTotalLogp/totalLines);
		System.out.println("\n ---------------- Perfect Lines -----------");
		System.out.printf("Perfect lines for CP = %d", perfectTotalCp);
		System.out.printf("Perfect lines for Logp = %d", perfectTotalLogp);
		
		
	}
		
	public static void normalDiscriminate(String arpaFile, String outputFile, String testFile) throws IOException{
		Compute c = new Compute(arpaFile);
		PrintWriter writer = new PrintWriter(new FileWriter(Compute.getFilename()+"refined_add_result_files/"+ outputFile));
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
		normalDiscriminate("refined_add_arpa_files/travel_refined.v.o2.ov.add1.arpa","travel_refined_v_discriminate.txt","travel_refined_verbs_test.txt");
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
//		Compute c = new Compute("refined_kn_arpa_files/travel_refined.v.o2.ov.kn.arpa",
//				"refined_kn_arpa_files/travel_refined.v.o2.ov.kn.arpa",0.8);
//		c.getEventParis("travel_refined_verbs_test_skipped.txt", "travel_refined_verbs_pairs_skipped.txt");
		
//		Compute c = new Compute("kn_arpa_files/travel.vso.o2.ov.kn.arpa");
//		c.getEventParis("travel_representation_vs_test.txt", "travel_vso_pairs.txt");
		
		
//		!!!!!!!! CLOZE !!!!!!
		
//		Cloze("travel_refined_verbs_test_skipped.txt",c);
		

	}

}
