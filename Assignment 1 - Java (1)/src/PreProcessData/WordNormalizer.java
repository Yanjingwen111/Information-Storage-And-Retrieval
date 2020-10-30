package PreProcessData;
import Classes.Stemmer;

/**
 * This is for INFSCI-2140 in 2020
 *
 */
public class WordNormalizer {
	//you can add essential private methods or variables

	// YOU MUST IMPLEMENT THIS METHOD
	public char[] lowercase( char[] chars ) {
		//transform the uppercase characters in the word to lowercase
		for (int i = 0; i < chars.length; i++)
			chars[i] = Character.toLowerCase(chars[i]);
		return chars;
	}

	public String stem(char[] chars)
	{
		//use the stemmer in Classes package to do the stemming on input word, and return the stemmed word
		String str="";
		Stemmer stemmer = new Stemmer();
        stemmer.add(chars, chars.length);
        stemmer.stem();
        str = stemmer.toString();
        return str;
	}

}
