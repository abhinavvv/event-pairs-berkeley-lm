package prog;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;






import org.apache.commons.lang3.*;

import edu.berkeley.nlp.lm.io.LmReaders;
import edu.berkeley.nlp.lm.*;


public class Compute{
	
	private static String filename = "/Users/abhinavvv/Documents/Works/Java/berkeleylm-1.1.5/data/";
	LmReaders lm;
	ArrayEncodedProbBackoffLm<String> myArpaLm,myArpaLmSkipped;
	WordIndexer <String> wi,wiSkipped;
	double lamda;
	
	Compute(String arpaFile){
		lm = new LmReaders();
		myArpaLm = LmReaders.readArrayEncodedLmFromArpa(getFilename()+arpaFile, false);
		wi = myArpaLm.getWordIndexer();	
		lamda = 0.0;
	}
	
	Compute(String arpaFile, String skippedArpaFile, double weight){
		lm = new LmReaders();
		myArpaLm = LmReaders.readArrayEncodedLmFromArpa(getFilename()+arpaFile, false);
		myArpaLmSkipped = LmReaders.readArrayEncodedLmFromArpa(getFilename()+skippedArpaFile, false);
		wi = myArpaLm.getWordIndexer();	
		wiSkipped = myArpaLmSkipped.getWordIndexer();
		lamda = weight;
	}
	
	
	public double getPmi(int[] bigrams){
		int [] reverse_bigrams = bigrams.clone();
		int [] unigram1 = new int[1];
		int [] unigram2 = new int[1];
		double P_ab,P_ba,P_a,P_b;
		
		ArrayUtils.reverse(reverse_bigrams);
		unigram1[0] = bigrams[0];
		unigram2[0] = bigrams[1];
		
		if(lamda==0.0){
			P_ab = myArpaLm.getLogProb(bigrams);
			P_ba =  myArpaLm.getLogProb(reverse_bigrams);
			P_a = myArpaLm.getLogProb(unigram1);
			P_b = myArpaLm.getLogProb(unigram2);
		}
		else{
			P_ab = getCombinedLogProb(bigrams);
			P_ba = getCombinedLogProb(reverse_bigrams);
			P_a = getCombinedLogProb(unigram1);
			P_b = getCombinedLogProb(unigram2);
		}
		
		return (P_ab + P_ba) - (P_a * P_b);

	}
	
	public double getCp(int[] bigrams){
		
			double P_ab,P_ba;
			int [] reverse_bigrams = bigrams.clone();
			
			ArrayUtils.reverse(reverse_bigrams);
			
			if(lamda ==0.0){
				P_ab = myArpaLm.getLogProb(bigrams);
				P_ba = myArpaLm.getLogProb(reverse_bigrams);
			}
			else{
				P_ab = getCombinedLogProb(bigrams);
				P_ba = getCombinedLogProb(reverse_bigrams);
			}
			
			
			double pmi = getPmi(bigrams);
	//		System.out.printf("Pmi = %f and Cp = %f\n", pmi,(pmi + P_ab - P_ba));
			return (pmi + P_ab - P_ba);
		}
	
	
	public double[] overallScore(List<String> words){
		double[] results = new double[2];
		for(int i=0;i<words.size()-1;i++){ 
			int[] bigrams = new int[2];
	
			
			bigrams[0] = wi.getIndexPossiblyUnk(words.get(i));
			
			bigrams[1] = wi.getIndexPossiblyUnk(words.get(i+1));
			if(lamda == 0.0)
					results[0]+= myArpaLm.getLogProb(bigrams);
			else
				results[0]+= getCombinedLogProb(bigrams);
			
			results[1] += getCp(bigrams);
		}
		return results;
	
	}
	
	public HashMap<String,Integer> updateScores(double[] orgResults, double[] permutedResults, HashMap<String,Integer> results,String type){
		
		int i = type.equals("logp") ? 0 : 1;
		
		if(permutedResults[i] > orgResults[i])
			results.put(type+"Loss", results.get(type+"Loss")+1);
		else if(permutedResults[i] < orgResults[i]) 
			results.put(type+"Wins", results.get(type+"Wins")+1);
		else
			results.put(type+"Ties", results.get(type+"Ties")+1); 
		
		return results;
	}
	
	public double getCombinedLogProb(int[] ngrams){
		return (lamda * myArpaLm.getLogProb(ngrams)) + ((1-lamda)*myArpaLmSkipped.getLogProb(ngrams));
	}
	
	
	public void getEventParis(String testFile,String outputFile) throws IOException{
		PrintWriter writer = new PrintWriter(new FileWriter(Compute.getFilename()+"event_pairs_file/"+
				outputFile));
		for (String line : Files.readAllLines(Paths.get(Compute.getFilename()+"refined_test_files/"+testFile))) {
			List<String> words = Arrays.asList(line.split("\\s+"));
			for(int j=0;j<words.size()-1;j++){
				
				int[] bigrams = new int[2];
				
				bigrams[0] = wi.getIndexPossiblyUnk(words.get(j));
				
				bigrams[1] = wi.getIndexPossiblyUnk(words.get(j+1));
				
				
				writer.printf("%s %s = %f , %f \n", words.get(j),words.get(j+1),myArpaLm.getLogProb(bigrams),getCp(bigrams));
				System.out.printf("%s %s = %f , %f \n", words.get(j),words.get(j+1),myArpaLm.getLogProb(bigrams),getCp(bigrams));
				
				
			}
			
		}
		writer.close();
	}

	
	public static String getFilename() {
		return filename;
	}


//	public static void setFilename(String filename) {
//		Compute.filename = filename;
//	}

}


