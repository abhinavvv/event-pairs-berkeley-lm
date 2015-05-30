package prog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import edu.berkeley.nlp.lm.ArrayEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.WordIndexer;
import edu.berkeley.nlp.lm.io.LmReaders;

public class Combined {
	private static String filename = "/Users/abhinavvv/Documents/Works/Java/berkeleylm-1.1.5/data/";
	LmReaders lm,lmSkipped;
	ArrayEncodedProbBackoffLm<String> myArpaLm;
	ArrayEncodedProbBackoffLm<String> myArpaLmSkipped;
	WordIndexer <String> wi;
	WordIndexer <String> wiSkipped;
	
	double minLamda,overallSkippedLogp, overallLogp, overallCombinedLogp, skippedPpl, combinedPpl, ppl,minPpl;
	int wordCount,total;
	
	Combined(String arpaFile,String skippedarpaFile){
		lm = new LmReaders();
		lmSkipped = new LmReaders();
		myArpaLm = LmReaders.readArrayEncodedLmFromArpa(getFilename()+"refined_kn_arpa_files/"+arpaFile, false);
		myArpaLmSkipped = LmReaders.readArrayEncodedLmFromArpa(getFilename()+"refined_kn_arpa_files/"+skippedarpaFile, false);
		wi = myArpaLm.getWordIndexer();
		wiSkipped = myArpaLmSkipped.getWordIndexer();
		minLamda = 0.0;
		minPpl = 10000000.0;
	}
	
	
	public double getLamda(String testFile) throws IOException{
		for(double lamda=0.0; lamda<=1.0; lamda=lamda+0.01){
			overallLogp = 0.0;
			overallSkippedLogp = 0.0;
			overallCombinedLogp = 0.0;
			wordCount = 0; 
			total = 0;
			ppl = 0.0;
			skippedPpl = 0.0;
			combinedPpl = 0.0;
			for (String line : Files.readAllLines(Paths.get(Compute.getFilename()+"refined_test_files/"+testFile))) {
				total += 1;
				
				List<String> words = Arrays.asList(line.split("\\s+"));
				
				double logp = 0.0;
				double skippedLogp = 0.0;
				double mixedLogp = 0.0;
				
				wordCount += words.size();
						
				for(int j=0;j<words.size()-1;j++){
					
					int[] bigramsOrg = new int[2];
					int[] bigramsSkipped = new int[2];
					
					bigramsOrg[0] = wi.getIndexPossiblyUnk(words.get(j));
					
					
					bigramsOrg[1] = wi.getIndexPossiblyUnk(words.get(j+1));
					
					double pOrg = myArpaLm.getLogProb(bigramsOrg);
					 
					
					bigramsSkipped[0] = wiSkipped.getIndexPossiblyUnk(words.get(j));
					
					
					bigramsSkipped[1] = wiSkipped.getIndexPossiblyUnk(words.get(j+1));
					
					double pSkipped = myArpaLmSkipped.getLogProb(bigramsSkipped);
					logp += pOrg;
					skippedLogp += pSkipped;
					
					double pMixed = (lamda * Math.pow(10.0,pOrg)) + ((1-lamda) * Math.pow(10.0,pSkipped));
					mixedLogp += Math.log10(pMixed);

				}
				
				overallLogp += logp;
				overallSkippedLogp += skippedLogp;
				overallCombinedLogp += mixedLogp;
			}
			
			double crossEntropy = -(overallLogp)/(wordCount* Math.log10(2.0));
			double skippedCrossEntropy = -(overallSkippedLogp)/(wordCount * Math.log10(2.0));
			double combinedCrossEntropy = -(overallCombinedLogp)/(wordCount * Math.log10(2.0));
			
			ppl = Math.pow(2.0, crossEntropy);
			skippedPpl = Math.pow(2.0,skippedCrossEntropy);
			combinedPpl = Math.pow(2.0, combinedCrossEntropy);
			
			System.out.printf("---------------- Lamda = %f -------------------\n",lamda);
			System.out.printf("Total Sentences = %d \n", total);
			System.out.printf("Overall Logp = %f, Overall Skipped Logp = %f, Overall Combined Logp = %f \n",
					overallLogp,overallSkippedLogp,overallCombinedLogp);
			System.out.printf("Overall Ppl = %f, Skipped Ppl = %f, Combined Ppl = %f \n",
					ppl,skippedPpl,combinedPpl);
			
			if(combinedPpl < minPpl){
				minPpl = combinedPpl;
				minLamda = lamda;
			}
				
		}
		
		System.out.printf("Minimum Lamda = %f",minLamda);	
		return minLamda;
	}
		public static String getFilename() {
		return filename;
	}

}
