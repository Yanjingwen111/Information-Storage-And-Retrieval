package PreProcessData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import Classes.Path;

/**
 * This is for INFSCI 2140 in 2018
 *
 */
public class TrecwebCollection implements DocumentCollection {
	// Essential private methods or variables can be added.	
	private BufferedReader readTrecweb;
	private String line;
	
	// YOU SHOULD IMPLEMENT THIS METHOD.
	public TrecwebCollection() throws IOException {
		// 1. Open the file in Path.DataWebDir.
		// 2. Make preparation for function nextDocument().
		// NT: you cannot load the whole corpus into memory!!
		
		readTrecweb = new BufferedReader(new FileReader(Path.DataWebDir));
		line = readTrecweb.readLine();
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD.
	public Map<String, Object> nextDocument() throws IOException {
		// 1. When called, this API processes one document from corpus, and returns its doc number and content.
		// 2. When no document left, return null, and close the file.
		// 3. the HTML tags should be removed in document content.
		
		if (line == null) {
			readTrecweb.close();
			return null;
		}
		
		Map<String, Object> document = new LinkedHashMap<String, Object>();
		boolean DOC = true;
		boolean DOCHDR = false;		
		String DOCNO = null;
		Object content = "";
		
		while (DOC == true) {
			int l = line.length();
			if (line.equals("<DOC>")) {
				DOC = true;
				line = readTrecweb.readLine();
				continue;
			} 
			else if (line.equals("</DOC>")) {
				DOC = false;
				line = readTrecweb.readLine();
				continue;
			} 
			else if (line.equals("</DOCHDR>")) {
				DOCHDR = true;
				line = readTrecweb.readLine();
				continue;
			}
			else if (line.equals("</DOC>")) {
				DOCHDR = false;
				line = readTrecweb.readLine();
				continue;
			} 
			else if (l > 8 && line.substring(0,7).equals("<DOCNO>")) {
				DOCNO = line.split("</")[0].substring(8).trim();
			}
			
			if (DOCHDR == true) {
				StringBuilder sb = new StringBuilder(line);

				while (sb.toString().contains("<") && sb.toString().contains(">")) {
					if (sb.indexOf(">") <= sb.indexOf("<")) {
						sb = sb.delete(0, sb.indexOf(">") + 1);
					} else {
						sb = sb.delete(sb.indexOf("<"), sb.indexOf(">") + 1);
					}
				}
				
				if (sb.toString().contains("<")) {
					sb = sb.delete(sb.indexOf("<"), sb.length() + 1);
				}
				if (sb.toString().contains(">")) {
					sb = sb.delete(0, sb.indexOf(">") + 1);
				}				
				content = content +" " + sb.toString();
			}
			
			line = readTrecweb.readLine();
		}		
		content = content.toString().toCharArray();
		document.put(DOCNO, content);
		return document;		
	}
	//test
	public static void main(String [] args) throws IOException{
	       TrecwebCollection text1 = new TrecwebCollection();
	       Map<String, Object> doc = null;
	       doc = text1.nextDocument();
	       System.out.println(doc);
	 }
}