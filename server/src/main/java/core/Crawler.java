package core;

import java.io.IOException;
import java.util.Vector;
import org.htmlparser.beans.StringBean;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import java.util.StringTokenizer;
import org.htmlparser.beans.LinkBean;
import org.json.simple.parser.ParseException;

import java.net.URL;


public class Crawler
{
	private String url;
	private LinkExtractor linkExtractor;
	private StringExtractor stringExtractor;
	private StopStem stemmer;

	Crawler(String _url, String stopWordDirectory) throws IOException {
		url = _url;
		linkExtractor = new LinkExtractor(url);
		stringExtractor = new StringExtractor(url);
		stemmer = new StopStem(stopWordDirectory);

	}
	public Vector<String> extractWords() throws ParserException, IOException {
		// extract words in url and return them
		// use StringTokenizer to tokenize the result from StringBean
		// ADD YOUR CODES HERE
		String s = stringExtractor.extractStrings(false);
		Vector<String> v = new Vector<>();
		StringTokenizer st = new StringTokenizer(s);
		while (st.hasMoreTokens()){
			String word = st.nextToken();
			String stemmedWord = stemmer.stem(word);
			if (stemmedWord.length() > 0){
				v.add(stemmedWord);
			}
		}
		return v;
	}
	public Vector<String> extractLinks() throws ParserException, ParseException {
		// extract links in url and return them
		// ADD YOUR CODES HERE
	    Vector<String> v_link = linkExtractor.extractLinks();
		return v_link;
	}

}

	
