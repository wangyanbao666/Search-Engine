package core;

import jdbm.helper.FastIterator;
import jdbm.htree.HTree;
import org.htmlparser.util.ParserException;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Test1 {
    private Main main;
    Test1(Main main) throws IOException {
        this.main = main;
        this.main.init();
    }
    public void testCrawl() throws ParserException, IOException, ParseException, java.text.ParseException {
        main.crawl(30);
    }
    public void readData() throws IOException {
        HTree idUrl = main.getIdUrl();
        HTree idWord = main.getIdWord();
        HTree subLinks = main.getSubLinks();
        Hashtable parsedForwardIndex = main.parseForwardIndex();
        Hashtable parsedPageInfo = main.parsePageInfo();

        Object urlIndex;
        FastIterator iter = idUrl.keys();
        FileWriter myWriter = new FileWriter("spider_result.txt");

        while ((urlIndex = iter.next()) !=null){
            String url = (String) idUrl.get(urlIndex);
            Hashtable pInfo = (Hashtable) parsedPageInfo.get(urlIndex);
            Hashtable pWordCount = (Hashtable) parsedForwardIndex.get(urlIndex);

            String title = (String) pInfo.get("Title");
            int contentLength = (int) pInfo.get("Content-Length");
            Date lastModified = (Date) pInfo.get("Last-Modified");
            boolean success = (boolean) pInfo.get("Success");
            String words = "";
            StringBuilder links = new StringBuilder();

//                words, links
            ArrayList wordCountString = new ArrayList();
            int count = 0;
            for (Object wordId: pWordCount.keySet()){
                String word = (String) idWord.get(wordId);
                String s = word + " " + pWordCount.get(wordId);
                wordCountString.add(s);
                count++;
                if (count>=10){
                    break;
                }
            }
            count = 0;
            words = String.join("; ",wordCountString);
            System.out.println(words);
            Vector childLinks = (Vector) subLinks.get(urlIndex);
            for (Object link: childLinks){
                links.append(link).append("\n");
                count++;
                if (count>=10){
                    break;
                }
            }


            myWriter.write("Page Title: "+title+"\n");
            myWriter.write("URL: "+url+"\n");
            myWriter.write("Last Modification Data: "+lastModified+"\t"+"size of page: "+contentLength+"\n");
            myWriter.write(words+"\n");
            myWriter.write(links+"\n");
        }
        myWriter.close();
    }

    public void finish() throws IOException {
        main.clearAll();
    }

    public static void main(String[] args) throws ParserException, IOException, ParseException, java.text.ParseException {
        String link = "http://www.cse.ust.hk";
        String stopWordDirectory = "C:\\Users\\29836\\OneDrive\\Desktop\\search engine\\labs\\project\\src\\main\\java\\org\\example\\stopwords.txt";
        Main m = new Main("test phase1",link, stopWordDirectory);
        Test1 t = new Test1(m);
        t.testCrawl();
        t.readData();
//        t.finish();
    }
}
