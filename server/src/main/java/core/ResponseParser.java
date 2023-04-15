package core;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.http.HttpHeader;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class ResponseParser {
    public static Hashtable<String, String> getResponse(String link) throws ParserException, ParseException {
        Hashtable h = new Hashtable();
        try {
            Parser parser = new Parser();
            parser.setResource(link);

            NodeFilter nodeFilter = new NodeClassFilter(TitleTag.class);
            NodeList nodeList = parser.parse(nodeFilter);
            String title = ((TitleTag) nodeList.elementAt(0)).getTitle();
//            System.out.println(title);

            Parser parser2 = new Parser();
            parser2.setResource(link);
            int htmlContentLength = 0;
            // Parse the entire HTML content
            NodeList fullNodeList = parser2.parse(null);
            // Print the HTML content
            htmlContentLength = fullNodeList.toHtml().length();
//            System.out.println(htmlContentLength);

            HttpURLConnection connection = (HttpURLConnection) parser.getConnection();
            String response = HttpHeader.getResponseHeader(connection);
            String[] responseLines = response.split("\n");
            String field = "";
            String value = "";
            String date = "";
            String lastModified = "";
            boolean modifiedDateFlag = false;
            int contentLength = 0;

            for (String line: responseLines){
                String[] info = line.split(":");
                if (info.length>1){
                    field = info[0].strip();
                    value = info[1].strip();
                }
                if (field.equals("Last-Modified")){
                    modifiedDateFlag = true;
                    lastModified = value;
                }
                else if (field.equals("Date")){
                    date = value;
                }
                else if (field.equals("Content-Length")){
                    contentLength = Integer.parseInt(value);
                }
            }

            if (contentLength == 0){
                contentLength = htmlContentLength;
            }

            if (!modifiedDateFlag){
                lastModified = date;
            }

            SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH");
            Date lastModifiedDate = new Date();
            if (lastModified.length() > 0){
                lastModifiedDate = formatter.parse(lastModified);
            }

            h.put("Last-Modified", lastModifiedDate);
            h.put("Content-Length", contentLength);
            h.put("Title", title);
            h.put("Success", true);
        }
        catch (Exception e){
            Date lastModifiedDate = new Date();
            h.put("Success", false);
            h.put("Last-Modified", lastModifiedDate);
            h.put("Content-Length", 0);
            h.put("Title", "");
        }

        return h;
    }

    public static void main(String[] args) throws ParserException, ParseException {
        ResponseParser rp = new ResponseParser();
        rp.getResponse("https://en.wikipedia.org/wiki/HTree");
    }
}

