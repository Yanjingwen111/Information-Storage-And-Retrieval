package PreProcessData;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import Classes.*;

/**
 * This is for INFSCI-2140 in 2020
 *
 */

public class StopWordRemover {
	//you can add essential private methods or variables.
	private HashSet<String> stopwords;

	public StopWordRemover( ) {
		// load and store the stop words from the fileinputstream with appropriate data structure
		// that you believe is suitable for matching stop words.
		// address of stopword.txt should be Path.StopwordDir
		try {
			this.stopwords = new HashSet<>();
			BufferedReader stopReader = new BufferedReader(new FileReader(Path.DataTextDir));
			String line;
			while((line = stopReader.readLine()) != null)
				stopwords.add(line);
		}
		catch (IOException ex){
			ex.printStackTrace();
		}
		
	}

	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword( char[] word ) {
		return this.stopwords.contains(String.valueOf(word));
	}
}
