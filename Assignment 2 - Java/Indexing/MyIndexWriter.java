package Indexing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import Classes.Path;

public class MyIndexWriter {
	// I suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
	
	//map of term, posting, transformation files
	Map<String, Terms_CF> terms_map = new HashMap<String, Terms_CF>();
	Map<Integer, TreeMap<Integer, Integer>> posting_map = new TreeMap<Integer, TreeMap<Integer, Integer>>();
	Map<String, Integer> transformation_map  = new HashMap<String, Integer>();
	
	Map<Integer, String> Posting = new HashMap<Integer, String>();
	
	//record count of document, file and term
	int countDoc = 0;
	int countFile = 0;
	int countTerm = 0;
	
	//document type:web/text
	private String docType;
	
	//FileWriter of term, posting, transformation files
	private FileWriter term_fw;
	private FileWriter posting_fw;
	private FileWriter transformation_fw;	
	
	private FileWriter final_fw;
	private BufferedReader br;
	private String line;
	
	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		docType = type;
		
		//According to different type, create new txt file for term, posting and transformation.
		if (type.equals("trecweb")) {
			term_fw = new FileWriter(Path.IndexWebDir + "DictionaryTerm.txt");
			posting_fw = new FileWriter("webPosting1.txt");
			transformation_fw = new FileWriter(Path.IndexWebDir + "Transform.txt");
		} else {
			term_fw = new FileWriter(Path.IndexTextDir + "DictionaryTerm.txt");
			posting_fw = new FileWriter("textPosting1.txt");
			transformation_fw = new FileWriter(Path.IndexTextDir + "Transform.txt");
		}
	}
		
	
	public void IndexADocument(String docno, char[] content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader		
		
		countDoc++;
		
		//build transformation map, key is docno, value is count of doc
		transformation_map.put(docno, countDoc);
		
		//change type of content from Array into String
		String ch = new String(content);
		StringTokenizer token = new StringTokenizer(ch, " ");
		
		while (token.hasMoreElements()) {
			String new_term = token.nextToken();
			
			//if there is new term
			if(terms_map.containsKey(new_term) && posting_map.containsKey(terms_map.get(new_term).getTermID())) {
				Terms_CF temp = terms_map.get(new_term);
				//CF++
				temp.cal_CF();	
				TreeMap<Integer, Integer> temp_map = posting_map.get(temp.getTermID());
				//if there is new document
				if (temp_map.containsKey(countDoc)) {
					//document count++
					temp_map.put(countDoc, temp_map.get(countDoc) + 1);
				} else {
					temp_map.put(countDoc, 1);
				}
			} else if (terms_map.containsKey(new_term) && !posting_map.containsKey(terms_map.get(new_term).getTermID())) {
				Terms_CF temp = terms_map.get(new_term);
				temp.cal_CF();
				int term_id = terms_map.get(new_term).getTermID();
				TreeMap<Integer, Integer> new_map = new TreeMap<Integer, Integer>();
				new_map.put(countDoc, 1);
				//update posting
				posting_map.put(term_id, new_map);
			} else {
				countTerm++;
				Terms_CF new_term2 = new Terms_CF(countTerm, 1);
				terms_map.put(new_term, new_term2);
				TreeMap<Integer, Integer> new_map = new TreeMap<Integer, Integer>();
				new_map.put(countDoc, 1);
				posting_map.put(countTerm, new_map);
			}
		}
		
		//divide trecweb into blocks, every block's size: 20000
		if (docType.equals("trecweb") && countDoc % 20000 == 0) {

			if (countFile >= 1) {
				posting_fw = new FileWriter("webPosting" + Integer.toString(countFile + 1) +".txt");
			}
			
			for (int i : posting_map.keySet()) {
				posting_fw.append(i + "\n");
				TreeMap<Integer, Integer> temp_map = posting_map.get(i);
				
				for (int j : temp_map.keySet()) {
					posting_fw.append(j + " " + temp_map.get(j) + ";");
				}
				posting_fw.append("\n");
			}
			posting_fw.close();
			posting_map.clear();
			countFile++;
		}
		
		//divide trectext into blocks, every block's size: 55000
		if (docType.equals("trectext") && countDoc % 55000 == 0) {
			
			if (countFile >= 1) {
				posting_fw = new FileWriter("textPosting" + Integer.toString(countFile + 1) + ".txt");
			}
			
			for (int i : posting_map.keySet()) {
				posting_fw.append(i + "\n");
				TreeMap<Integer, Integer> temp_map = posting_map.get(i);
				
				for (int j : temp_map.keySet()) {
					posting_fw.append(j + " " + temp_map.get(j) + ";");
				}
				posting_fw.append("\n");
			}
			posting_fw.close();
			posting_map.clear();
			countFile++;
		}
	}
	
	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.
		
		//last block
		if (docType.equals("trectext")) {
			posting_fw = new FileWriter("textPosting" + Integer.toString(countFile + 1) + ".txt");
		} else {
			posting_fw = new FileWriter("webPosting" + Integer.toString(countFile + 1) +".txt");
		}
		
		for (int i : posting_map.keySet()) {
			posting_fw.append(i + "\n");
			TreeMap<Integer, Integer> temp_map = posting_map.get(i);
			
			for (int j : temp_map.keySet()) {
				posting_fw.append(j + " " + temp_map.get(j) + ";");
			}
			posting_fw.append("\n");
		}
		posting_fw.close();
		posting_map.clear();
		
		//update term and transformation 
		for (String i : terms_map.keySet()) {
			term_fw.append(i + " ");
			term_fw.append(terms_map.get(i).getTermID() + " ");
			term_fw.append(terms_map.get(i).getCf() + "\n");
		}
		term_fw.close();
		terms_map.clear();
		
		for (String i : transformation_map.keySet()) {
			transformation_fw.append(i + " ");
			transformation_fw.append(transformation_map.get(i) + "\n");
		}
		transformation_fw.close();
		transformation_map.clear();
		
		if (docType.equals("trecweb")) {
			final_fw = new FileWriter(Path.IndexWebDir + "Posting.txt");
		} else {
			final_fw = new FileWriter(Path.IndexTextDir + "Posting.txt");
		}
		
		//Total web document count: 198361 and block count:20000, total text document count: 503473 and block size: 55000
		//so it can be divided into 10
		for (int i = 1; i < 11; i++) {
			if (docType.equals("trecweb")) {
				br = new BufferedReader(new FileReader("webPosting" + i + ".txt"));
				line = br.readLine();
			} else {
				br = new BufferedReader(new FileReader("textPosting" + i + ".txt"));
				line = br.readLine();
			}
			
			while (line != null) {
				//id
				int id = Integer.parseInt(line);
				//content
				line = br.readLine();
				
				if (Posting.containsKey(id)) {
					Posting.put(id, Posting.get(id) + line);
				} else {
					Posting.put(id, line);
				}
				//next line		
				line = br.readLine();
			}
			br.close();
		}
		
		for (int i : Posting.keySet()) {
			final_fw.append(i + "\n" + Posting.get(i) + "\n");
		}
		final_fw.close();
	}

}
