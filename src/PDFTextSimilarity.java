import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;


 
public class PDFTextSimilarity {
 
    /** The max number of input pdf files to be converted. */
    public static final int maxNumOfInputFiles = 2500;
 
    /**
     * Parses a PDF to a plain text file.
     * @param pdf the original PDF
     * @param txt the resulting text
     * @throws IOException
     */
    public void parsePdf(String pdf, String txt) throws IOException {
    	try {
    		PdfReader reader = new PdfReader(pdf);
    		System.out.println("converting " + pdf);
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
    	} catch (IOException exc) {
    		//the input file doesn't exist
    		return;
    	}
    }
 
    /**
     * Main method. Assumes that all input files have the naming convention of
     * number.pdf and all input files exist in the "inputPdfs" directory within the
     * src folder.
     * @param args no arguments needed
     */
    public static void main(String[] args) throws IOException {
    	//convert the input PDF files
    	for (int j = 0; j < maxNumOfInputFiles; j++) {
    		//make the input number have the correct format
    		@SuppressWarnings("unused")
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
    		new PDFTextSimilarity().parsePdf(curInput, curOutput);
    	}       
    }
}