package PreProcessData;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * This is for INFSCI-2140 in 2020
 *
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	//you can add essential private methods or variables
	private List<String> tokens = new LinkedList<String>();
	private Iterator<String> tkIterator;
	// YOU MUST IMPLEMENT THIS METHOD
	public WordTokenizer( char[] texts ) {
		boolean end = false;
		String word = "";
		for (int i = 0; i < texts.length; i++) {
			if (end == true) {
				tokens.add(word);
				word = "";
				end = false;
			}
			if ((texts[i] >= '0' && texts[i] <= '9') || (texts[i] >= 'A' && texts[i] <= 'Z') || (texts[i] >= 'a' && texts[i] <= 'z')) {
				word = word + String.valueOf(texts[i]);
			}
			else if (!word.equals("")) {
				end = true;
			}
		}
		tkIterator = tokens.iterator();
	}
	public char[] nextWord() {
		if (tkIterator.hasNext()) {
			return tkIterator.next().toString().toCharArray();
		}else {
			return null;
		
		}
	}
	// test
	public static void main(String [] args) throws IOException{
	     TrectextCollection text1 = new TrectextCollection();
	     WordNormalizer normalizer = new WordNormalizer();
	     StopWordRemover stopwordRemoverObj = new StopWordRemover();
	     
	     Map<String, Object> doc = null;
	     doc = text1.nextDocument();
	     String docno = doc.keySet().iterator().next();
	     char[] content = (char[]) doc.get(docno);
	     
	     WordTokenizer tokens = new WordTokenizer(content);
	     char[] word = null;
	     while ((word = tokens.nextWord()) != null) {
	      word = normalizer.lowercase(word);
	      word = normalizer.stem(word).toCharArray();
	      if (!stopwordRemoverObj.isStopword(word)) {
	       System.out.println(word);
	      }
	      
	  }
	     
	 }

}
