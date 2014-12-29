import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class PDFTextSimilarityTest {
	
	private PDFTextSimilarity testObj;
	
	@Before
    public void setup()
    {
		testObj = new PDFTextSimilarity();
    }
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testCreateEnglishDictionaryNotEmpty() throws IOException {
		HashSet<String> englishDict = PDFTextSimilarity.createEnglishDictionary("englishDictionary.txt");
		assertNotEquals(0, englishDict.size());
	}
	
	@Test
	public void testCreateEnglishDictionaryContainsEnglishWord() throws IOException {
		HashSet<String> englishDict = PDFTextSimilarity.createEnglishDictionary("englishDictionary.txt");
		assertEquals(true, englishDict.contains("hello"));
	}
	
	@Test
	public void testCreateEnglishDictionaryDoesntHaveSingleLetterWords() throws IOException {
		HashSet<String> englishDict = PDFTextSimilarity.createEnglishDictionary("englishDictionary.txt");
		assertEquals(false, englishDict.contains("a"));
	}
	
	@Test
	public void testCreateEnglishDictionaryDoesntHaveCapitalizedWords() throws IOException {
		HashSet<String> englishDict = PDFTextSimilarity.createEnglishDictionary("englishDictionary.txt");
		assertEquals(false, englishDict.contains("Hello"));
	}
	
	@Test
	public void testExceptionForIncorrectDictionaryLocation() throws IOException {
		exception.expect(IOException.class);
		HashSet<String> englishDict = PDFTextSimilarity.createEnglishDictionary("faultyLocation");
	}
	
	@Test
	public void testExtractFrequencies() throws IOException {
		HashSet<String> englishDict = PDFTextSimilarity.createEnglishDictionary("englishDictionary.txt");
		PDFTextSimilarity.extractFrequencies("0004.txt", englishDict);
		BufferedReader bfr = new BufferedReader(new FileReader("0004.words.txt"));
		String firstLine = bfr.readLine();
		assertEquals("total: 6", firstLine);
	}
	
	
}
