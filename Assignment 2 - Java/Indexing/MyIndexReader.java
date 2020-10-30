package Indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import Classes.Path;


public class MyIndexReader {
	//you are suggested to write very efficient code here, otherwise, your memory cannot hold our corpus...
	private HashMap<String, Terms_CF> dictionary = new HashMap<String, Terms_CF>();
	private TreeMap<Integer, String> posting = new TreeMap<Integer, String>();
	private HashMap<String, Integer> transformation = new HashMap<String, Integer>();
	
	private String dict_line;
	private String post_line;
	private String trans_line;
	
	private BufferedReader dict_reader;
	private BufferedReader post_reader;
	private BufferedReader trans_reader;
		
	public MyIndexReader( String type ) throws IOException {
		//read the index files you generated in task 1
		//remember to close them when you finish using them
		//use appropriate structure to store your index
		
		//if type is web/text, read corresponding txt
		if (type.equals("trecweb")) {
			dict_reader = new BufferedReader(new FileReader(Path.IndexWebDir + "DictionaryTerm.txt"));
			post_reader = new BufferedReader(new FileReader(Path.IndexWebDir + "Posting.txt"));
			post_reader.mark((int) (new File(Path.IndexWebDir + "Posting.txt").length() + 1));
			trans_reader = new BufferedReader(new FileReader(Path.IndexWebDir + "Transform.txt"));
		} else {
			dict_reader = new BufferedReader(new FileReader(Path.IndexTextDir + "DictionaryTerm.txt"));
			post_reader = new BufferedReader(new FileReader(Path.IndexTextDir + "Posting.txt"));
			post_reader.mark((int) (new File(Path.IndexTextDir + "Posting.txt").length() + 1));
			trans_reader = new BufferedReader(new FileReader(Path.IndexTextDir + "Transform.txt"));
		}
		
		//store dictionary 
		dict_line = dict_reader.readLine();	
		while (dict_line != null) {
			String[] split_dict = dict_line.split(" ");
			Terms_CF temp = new Terms_CF(Integer.parseInt(split_dict[1]), Integer.parseInt(split_dict[2]));
			dictionary.put(split_dict[0], temp);
			dict_line = dict_reader.readLine();
		}

		//store transformation
		trans_line = trans_reader.readLine();	
		while (trans_line != null) {
			String[] split_trans = trans_line.split(" ");
			transformation.put(split_trans[0], Integer.parseInt(split_trans[1]));
			trans_line = trans_reader.readLine();
		}

		//store posting
		post_line = post_reader.readLine();
		int i = 0;		
		while (i < 100000 && post_line != null) {
			int post_id = Integer.parseInt(post_line);
			post_line = post_reader.readLine();
			posting.put(post_id, post_line);
			post_line = post_reader.readLine();
			i++;
		}
	}
	
	//get the non-negative integer dociId for the requested docNo
	//If the requested docno does not exist in the index, return -1
	public int GetDocid( String docno ) {
		if (transformation.containsKey(docno)) {
			return transformation.get(docno);
		} else {
			return -1;
		}
	}

	// Retrieve the docno for the integer docid
	public String GetDocno( int docid ) {
		String res = null;
		
		for (String i : transformation.keySet()) {
			if (transformation.get(i) == docid) {
				res = i;
			}
		}
		
		return res;
	}
	
	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * 
	 * ...
	 * 
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 * 
	 * @param token
	 * @return
	 */
	public int[][] GetPostingList( String token ) throws IOException {
		//if dictionary has no token, return null
		if (!dictionary.containsKey(token)) {
			return null;
		}
		
		int[][] res = null;
		int ansterm_id = dictionary.get(token).getTermID();
		
		//if posting block contains ansterm_id, get result
		if (posting.containsKey(ansterm_id)) {
			StringTokenizer tokenrd = new StringTokenizer(posting.get(ansterm_id), ";");
			res = new int[tokenrd.countTokens()][2];
			
			int i = 0;
			while (tokenrd.hasMoreTokens()) {
				String[] token_split = tokenrd.nextToken().split(" ");
				res[i][0] = Integer.parseInt(token_split[0]);
				res[i][1] = Integer.parseInt(token_split[1]);
				i++;
			}
		} else {
			if (ansterm_id < posting.firstKey()){
				post_reader.reset();
				post_line = post_reader.readLine();
			}

			while (!posting.containsKey(ansterm_id)) {	
				posting.clear();
				
				int i = 0;	
				while (i < 100000 && post_line != null) {
					int post_id = Integer.parseInt(post_line);
					post_line = post_reader.readLine();
					posting.put(post_id, post_line);	
					post_line = post_reader.readLine();
					i++;
				}
			}
			
			StringTokenizer tokenrd = new StringTokenizer(posting.get(ansterm_id), ";");
			res = new int[tokenrd.countTokens()][2];
			
			int i = 0;
			while (tokenrd.hasMoreTokens()) {
				String[] token_split = tokenrd.nextToken().split(" ");
				res[i][0] = Integer.parseInt(token_split[0]);
				res[i][1] = Integer.parseInt(token_split[1]);
				i++;
			}
		}
		
		return res;
	}

	// Return the number of documents that contains the token.
	public int GetDocFreq( String token ) throws IOException {
		if (!dictionary.containsKey(token))
			return 0;
		
		int result = 0;
		int ansterm_id = dictionary.get(token).getTermID();

		if (posting.containsKey(ansterm_id)) {
			StringTokenizer tokenrd = new StringTokenizer(posting.get(ansterm_id), ";");
			result = tokenrd.countTokens();
		} else {
			if (ansterm_id < posting.firstKey()){
				post_reader.reset();
				post_line = post_reader.readLine();
			}
			
			while (!posting.containsKey(ansterm_id)) {
				posting.clear();
				
				int i = 0;
				
				while (i < 100000 && post_line != null) {
					int post_id = Integer.parseInt(post_line);
					post_line = post_reader.readLine();
					posting.put(post_id, post_line);
					
					post_line = post_reader.readLine();
					i++;
				}
			}
			StringTokenizer st = new StringTokenizer(posting.get(ansterm_id), ";");
			result = st.countTokens();
		}
		
		return result;
	}
	
	// Return the total number of times the token appears in the collection.
	public long GetCollectionFreq( String token ) throws IOException {
		if (!dictionary.containsKey(token)) {
			return 0;
		}
			
		return dictionary.get(token).getCf();
	}
	
	public void Close() throws IOException {
		post_reader.close();
		dict_reader.close();
		trans_reader.close();
	}
	
}