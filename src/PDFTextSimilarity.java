import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Pattern;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
 
public class PDFTextSimilarity {
 
    /** The max number of input pdf files to be converted. */
    public static final int maxNumOfInputFiles = 2500;
    public static int numOfFiles = 0;
    public static ArrayList<String> orderOfFiles = new ArrayList<String>();
    public static HashMap<String, HashMap<String, Integer>> wordFreqDatabase 
    	= new HashMap<String, HashMap<String, Integer>>();
     
    /**
     * Parses a PDF to a plain text file.
     * @param pdf the original PDF
     * @param txt the resulting text
     * @param englishDict 
     * @throws IOException
     */
    public void parsePdf(String pdf, String txt, HashSet<String> englishDict) throws IOException {
    	try {
    		PdfReader reader = new PdfReader(pdf);
    		System.out.println("converting " + pdf);
    		numOfFiles++;
    		orderOfFiles.add(txt);
    		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            PrintWriter out = new PrintWriter(new FileOutputStream(txt));
            TextExtractionStrategy strategy;
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {      
            	try {
            		strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
            		out.println(strategy.getResultantText());
            	} catch (Exception exc) {
            		System.out.println("ArrayIndexOutOfBoundsException for " + pdf);
            	}                
            }
            out.flush();
            out.close();
            reader.close();
            extractFrequencies(txt, englishDict);
    	} catch (IOException exc) {
    		//the input file doesn't exist
    		return;
    	}
    }
 
    /**
     * Converts a text file into a xxxx.words file that contains the word frequencies
     * @param input the input text file
     * @param englishDict 
     * @throws IOException
     */
    public void extractFrequencies(String input, HashSet<String> englishDict) throws IOException {
    	    
    	BufferedReader reader = new BufferedReader(new FileReader(input));
    			
		HashMap<String, Integer> wordFreq = new HashMap<String, Integer>();
		
		//run through each line in the input file
		while (reader.ready()) {
			String curLine = reader.readLine();
			
			//split up the line into its words
			String[] lineParts = curLine.split(" ");
			
			//iterate through lineParts
			for (int i = 0; i < lineParts.length; i++) {
				String curWord = lineParts[i].toLowerCase();
				//check if valid word in english dictionary
				if (englishDict.contains(curWord)) {
					//update the word's frequency count
					if (wordFreq.containsKey(curWord)) {
						wordFreq.put(curWord, wordFreq.get(curWord) + 1);
					} else {
						wordFreq.put(curWord, 1);
					}
				}
			}			
		}
				
		//print out the hashmap to the output
		//new output filename
    	String outputName = input.split(Pattern.quote("."))[0].toString() + ".words.txt";
    	//save hashmap for this file to the database
    	wordFreqDatabase.put(outputName, wordFreq);	
    	@SuppressWarnings("resource")
    	PrintStream out = new PrintStream(new FileOutputStream(outputName));
    	for (String word : wordFreq.keySet()) {
    		int curFreq = wordFreq.get(word);
    		out.println(word + ": " + curFreq);
    	}
    	
	}
    
    private static HashSet<String> createEnglishDictionary() {
    	HashSet<String> englishDict = new HashSet<String>();
    	try {
            BufferedReader in = new BufferedReader(new FileReader(
                    "/usr/share/dict/words"));
            String str;
            while ((str = in.readLine()) != null) {
            	//to avoid single letter words from getting put in the dictionary
            	if (str.length() > 1) {
            		englishDict.add(str.toLowerCase());
            	}
            }
            in.close();
            return englishDict;
        } catch (IOException e) {
        	System.out.println("Dictionary creation failed!");
        }
		return englishDict;
	}

	/**
     * Main method. Assumes that all input files have the naming convention of
     * number.pdf and all input files exist in the "inputPdfs" directory within the
     * src folder.
     * @param args no arguments needed
     */
    public static void main(String[] args) throws IOException {
    	HashSet<String> englishDict = createEnglishDictionary();
    	
    	//convert the input PDF files
    	for (int j = 0; j < maxNumOfInputFiles; j++) {
    		//make the input number have the correct format
    		String inputNum = "";
    		if (j < 10) {
    			inputNum = "000" + j;
    		} else if (j < 100) {
    			inputNum = "00" + j;
    		} else if (j < 1000) {
    			inputNum = "0" + j;
    		} else {
    			inputNum += j;
    		}
    		String curInput = "inputPdfs/" + inputNum + ".pdf";
    		String curOutput = inputNum + ".txt";
    		new PDFTextSimilarity().parsePdf(curInput, curOutput, englishDict);
    	}
    	
    	//create similarity array
    	int[][] similarityArr = getSimilarityArr();
    	
    	//write similarityArr to an Std out and to an output file
    	@SuppressWarnings("resource")
		PrintStream out = new PrintStream(new FileOutputStream("fileComparisons.txt"));
    	for (int i = 0; i < numOfFiles; i++) {
    		for (int j = 0; j < numOfFiles; j++) {
    			System.out.print("|" + similarityArr[i][j]);
    			out.print("|" + similarityArr[i][j]);
    		}
    		System.out.println("|");
    		out.println("|");
    	}
    	
    }

	private static int[][] getSimilarityArr() {
		int[][] similarityArr = new int[numOfFiles][numOfFiles];
    	for (int i = 0; i < numOfFiles; i++) {
    		String curBaseFile = orderOfFiles.get(i);
    		for (int j = i + 1; j < numOfFiles; j++) {
    			String curCompFile = orderOfFiles.get(j);
    			int similarity = getSimilarity(curBaseFile, curCompFile);
    			similarityArr[i][j] = similarity;
    			similarityArr[j][i] = similarity;
    		}
    	}
		return similarityArr;
	}

	private static int getSimilarity(String curBaseFile, String curCompFile) {
		int countOfDuplicateWords = 0;
		for (String baseWord : wordFreqDatabase.get(curBaseFile.split(Pattern.quote("."))[0].toString() + ".words.txt").keySet()) {
			if (wordFreqDatabase.get(curCompFile.split(Pattern.quote("."))[0].toString() + ".words.txt").keySet().contains(baseWord)) {
				countOfDuplicateWords++;
			}
		}		
		return countOfDuplicateWords;
	}
	
}