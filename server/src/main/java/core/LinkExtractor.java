package core;// HTMLParser Library $Name: v1_6 $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser
// Copyright (C) 2012 Pengfei Zhao
//
//

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpHeaders;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import org.htmlparser.beans.LinkBean;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.http.HttpHeader;
import org.htmlparser.util.ParserException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * LinkExtractor extracts all the links from the given webpage
 * and prints them on standard output.
 */


public class LinkExtractor
{
	private String link = "";
	public LinkExtractor(String url){
		link = url;
	}
	
	public Vector<String> extractLinks() throws ParserException, ParseException {
		// extract links in url and return them
		// ADD YOUR CODES HERE

	    Vector<String> v_link = new Vector<String>();
	    LinkBean lb = new LinkBean();
	    lb.setURL(link);
	    URL[] URL_array = lb.getLinks();
	    for(int i=0; i<URL_array.length; i++){
			if (!v_link.contains(URL_array[i].toString())){
				v_link.add(URL_array[i].toString());
			}
	    }

		return v_link;
	}
	
    public static void main (String[] args) throws ParserException, ParseException {
        String url = "https://tpc.googlesyndication.com/sodar/sodar2/225/runner.html";
        LinkExtractor extractor = new LinkExtractor(url);
        extractor.extractLinks();
        
    }
}
