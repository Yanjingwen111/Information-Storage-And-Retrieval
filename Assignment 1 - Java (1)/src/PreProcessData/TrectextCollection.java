package PreProcessData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import Classes.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is for INFSCI-2140 in 2020
 *
 */
public class TrectextCollection implements DocumentCollection {
	//you can add essential private methods or variables
	private BufferedReader readTrectex;

	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrectextCollection() throws IOException {
		// This constructor should open the file existing in Path.DataTextDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!
		try {
			readTrectex = new BufferedReader(new FileReader(Path.DataTextDir));
		}
		catch (IOException ex){
			ex.printStackTrace();
		}
	}

	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NTT: remember to close the file that you opened, when you do not use it any more
		try{
			Map<String, Object> document = new LinkedHashMap<String, Object>();
			String line;
			String DOCNO = "";
			String text = "";
			Boolean contentText = null;
			while((line = readTrectex.readLine()) != null){
				int len = line.length();
				
				if(len > 8 && line.substring(0,7).equals("<DOCNO>")) {
					DOCNO = line.split("</")[0].substring(8).trim();
					document.put(DOCNO, "");
					continue;
				}
				
				else if(len > 12 && line.substring(0,11).equals("<DATE_TIME>")) {
					document.put("date", line.split("</")[0].substring(12).trim().toCharArray());
				}
				
				else if (len > 11 && line.substring(0,10).equals("<HEADLINE>")) {
					document.put("headline", line.split("</")[0].substring(11).trim().toCharArray());
				}
				
				else if(line.equals("<TEXT>")) {
					contentText = true;
					continue;
				}
				
				else if(line.equals("</TEXT>")) {
					contentText = false;
				}
				
				else if(line.equals("</DOC>")) {
					return document;
				}
				
				if(contentText != null){
					if(contentText) {
						text += line.trim();
					} else {
						document.put(DOCNO, text.toCharArray());
						contentText = null;
					}
				}

			}
		}
		catch (IOException ex){
			ex.printStackTrace();
		}
		return null;
	}
	//test
	 public static void main(String [] args) throws IOException{
	       TrectextCollection text1 = new TrectextCollection();
	       Map<String, Object> doc = null;
	       doc = text1.nextDocument();
	       System.out.println(doc);
	 }
}
