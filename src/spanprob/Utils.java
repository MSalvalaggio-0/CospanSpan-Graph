package spanprob;

import java.io.*;

public class Utils {

	/**
	 * Print the SpanProb attribute in a txt file
	 * @param span
	 */
  public static void SpanProbTxt(SpanProb span) {
	    File fout = new File("SpanProb_Text.txt");
	    try(FileOutputStream fos = new FileOutputStream(fout)) {
	      try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))){
	        bw.write(span.toString());
	      }
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
  }
  
  /**
   * Print the SpanProb's transitions states in a csv file
   * @param span
   */
  public static void SpanProbToCSVForGephi(SpanProb span) {
	    File fout = new File("outGephi.csv");
	    try(FileOutputStream fos = new FileOutputStream(fout)) {
	      try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))){
	    	  for(Transition t : span.getTransitions()) {
	    		  bw.write(t.getFrom()+";"+t.getTo());
	    		  bw.write("\r\n");
	    	  }
	      }
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
  }
  
  /**
   * Print the SpanProb's transitions matrix to a csv file
   * @param text
   */
  public static void SpanProbMatrixToCSV(String text) {
	    File fout = new File("outMatrix.csv");
	    try(FileOutputStream fos = new FileOutputStream(fout)) {
	      try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))){
	    	  bw.write(text);
	      }
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    
  }
}






