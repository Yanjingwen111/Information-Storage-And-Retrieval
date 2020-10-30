package Search;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import Classes.Path;
import Classes.Query;
import Classes.Stemmer;

public class ExtractQuery {
	private BufferedReader reader;
	//store stop words
	private HashSet<String> stopSet = new HashSet<>();
	//digit number
	private Pattern digit = Pattern.compile("\\d+");
	private boolean titleOnly;
    private boolean nextQuery;
    
    public ExtractQuery() {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		try {
            this.reader = Files.newBufferedReader(Paths.get(Path.TopicDir));
            this.titleOnly = true;
            @SuppressWarnings("resource")
			Stream<String> stopWord = Files.lines(Paths.get(Path.StopwordDir));
            stopWord.forEach(s -> this.stopSet.add(s.trim().toLowerCase()));
        } catch (Exception e) {
            e.printStackTrace();
            this.reader = null;
        }
	}
	
	public boolean hasNext() throws IOException {
		if (reader == null) {
            nextQuery = false;
        } else {
        	nextQuery = moveStart();
        }
		if (!nextQuery) {
			if (this.reader == null) {
				return nextQuery;
			} 
	        this.reader.close();	
        }
        return nextQuery;
	}
	
	private String PreProcess(String preContent) {
        String regex = "\r\n\t.,;:\"()?! ";
        //tokenized
        StringTokenizer st = new StringTokenizer(preContent, regex);
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            String normal = stringNorm(st.nextToken());
            if (normal.isEmpty()) continue;
            sb.append(normal);
            sb.append(' ');
        }
        return sb.toString();
    }
	
	public Query next() throws IOException {
		Query query = new Query();
        if (!this.nextQuery) {
        	return query;
        }
        query.SetTopicId(popTopicId());
        String original = processTopic(this.titleOnly);
        query.SetQueryContent(PreProcess(original));
        this.nextQuery  = false;
        return query;
	}
	
	//normalize String
    private String stringNorm(String token) {
    	//transform tokens to lowercase
        token = token.toLowerCase();
        //remove stop words
        if (this.stopSet.contains(token)) {
        	return "";
        } 
        char[] tokenLuc = token.toCharArray();
        Stemmer st = new Stemmer();
        st.add(tokenLuc, tokenLuc.length);
        st.stem();
        return st.toString();
    }
    
    //move current pointer at end of <num>
    private String popTopicId() throws IOException {
    	String data = this.reader.readLine();
    	Matcher m = this.digit.matcher(data);
    	if (m.find()) {
    		return m.group(0).trim();
    		}
    	return "UNKNOWN";
    }
    
    //move reader pointer to start
    private boolean moveStart() throws IOException {
        String data;
        while ((data = this.reader.readLine()) != null) {
            if (data.trim().toLowerCase().startsWith("<top>")) {
            	return true;
            }      
        }
        return false;
    }
    
    //populate the whole query if at end of <num> 
    private String processTopic(boolean titleOnly) {
        StringBuilder sb = new StringBuilder();
        boolean titleExist = false;
        try {
            String line;
            while (!((line = this.reader.readLine().trim()).startsWith("</top>"))) {
                if ((titleOnly && titleExist) || line.isEmpty()) {
                	continue;
                } 
                String lucene = line.toLowerCase().trim();
                sb.append(' ');
                if (lucene.startsWith("<title>")) {
                    line = line.substring(8);
                    if (titleOnly) {
                        sb.append(line);
                        titleExist = true;
                        continue;
                    }
                } else if (lucene.startsWith("<desc>") || lucene.startsWith("<narr>")) {
                    continue;
                }
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
