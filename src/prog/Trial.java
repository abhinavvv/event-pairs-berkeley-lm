package prog;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.lang3.*;

import edu.berkeley.nlp.lm.io.LmReaders;
import edu.berkeley.nlp.lm.*;


public class Trial {
	
	public static void main(String[] args) throws Exception {
		Compute c = new Compute("travel.vo.o2.ov.kn.arpa");
		int count = 0;
		
		HashMap<String, Integer> results = new HashMap<String,Integer>();
		String[] scores = {"logpWins","logpLoss","logpTies","cpWins","cpLoss","cpTies"}; 
		for(String type: scores)
			results.put(type, 0);
		System.exit(0);
		for (String line : Files.readAllLines(Paths.get(c.getFilename()+"travel_representation_vo_test.txt"))) {
			count ++;
			List<String> words = Arrays.asList(line.split("\\s+"));
			
			double[] orgResults =  new double[2];
			orgResults = c.overallScore(words);
			System.out.println("Orginial\n");
			System.out.printf("Logp = %f  Cp = %f \n ", orgResults[0],orgResults[1]);
			System.exit(0);
//			System.out.println("Permuted\n");
//			for(int i=0; i<5;i++){
//				double[] permutedResults =  new double[2];
//				
//				List<String> events = new  ArrayList<String>(words);
//				Collections.copy(events,words);
//				Collections.shuffle(events);
//				
//				permutedResults = overallScore(wi,events,myArpaLm);
//				System.out.printf("Logp = %f  Cp = %f \n ", permutedResults[0],permutedResults[1]);
//				
//				results = updateScores(orgResults,permutedResults,results,"logp");
//				results = updateScores(orgResults,permutedResults,results,"cp");
//							
//				
//			}
//			
			System.out.println("-------------------------\n");
			
		}
		System.out.printf("Total number of lines = %d\n",count);
		for(Map.Entry<String, Integer> s : results.entrySet()){
			System.out.printf("%s = %d \n",s.getKey(),s.getValue());
		}
//		System.out.println("Hello World");
		// TODO Auto-generated method stub

	}

}










