package Indexing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

public class PreProcessedCorpusReader {
	//read file
	private BufferedReader reader;
	
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
		// You can use your own version, or download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp
		// Close the file when you do not use it any more
		reader = new BufferedReader(new FileReader(Path.ResultHM1 + type));
	}
	

	public Map<String, Object> NextDocument() throws IOException {
		// read a line for docNo and a line for content, put into the map with <docNo, content>
		Map<String, Object> doc = new HashMap<>();
		String docNo = " ";
		Object content = " ";
		
		if((docNo = reader.readLine())!=null && (content = reader.readLine()) != null){
			doc.put(docNo.trim(), content.toString().toCharArray());
		} else {
			return null;
		}
		return doc;
	}
}
