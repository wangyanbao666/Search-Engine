package core;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;
import org.htmlparser.util.ParserException;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class Main {
    private final String WORDINDEXSTRING = "wordIndex";
    private final String URLINDEXSTRING = "urlIndex";
    private final String PHRASE2INDEXSTRING = "phrase2Index";
    private final String PHRASE3INDEXSTRING = "phrase3Index";
    private String link;
    private RecordManager recman;
    private String recordmanager;
    private HTree wordId;
    private HTree idWord;
    private HTree urlId;
    private HTree idUrl;
    private HTree invertedIndex;
    private HTree phrase2InvertedIndex;
    private HTree phrase3InvertedIndex;
    private HTree forwardIndex;
    private HTree subLinks;
    private HTree parentLinks;
    private HTree maxWordUrl;

    private HTree wordTfIdf;
    private HTree phrase2TfIdf;
    private HTree phrase3TfIdf;

    public HTree getDocWordTfIdf() {
        return docWordTfIdf;
    }

    public HTree getDocPhrase2TfIdf() {
        return docPhrase2TfIdf;
    }

    public HTree getDocPhrase3TfIdf() {
        return docPhrase3TfIdf;
    }

    private HTree docWordTfIdf;
    private HTree docPhrase2TfIdf;
    private HTree docPhrase3TfIdf;

    public HTree getSubLinks() {
        return subLinks;
    }
    public HTree getWordId() {
        return wordId;
    }

    public HTree getIdWord() {
        return idWord;
    }

    public HTree getUrlId() {
        return urlId;
    }

    public HTree getIdUrl() {
        return idUrl;
    }

    public HTree getInvertedIndex() {
        return invertedIndex;
    }

    public HTree getForwardIndex() {
        return forwardIndex;
    }

    public HTree getMaxWordUrl() {
        return maxWordUrl;
    }

    public HTree getPageInfo() {
        return pageInfo;
    }

    private HTree pageInfo;
    private int wordIndex = 0;
    private int phrase2Index = 0;
    private int phrase3Index = 0;
    private int urlIndex = 0;
    private List<String> queue;
    private final String stopWordDirectory;


    public Main(String recordmanager, String link, String stopWordDirectory){
        this.link = link;
        this.recordmanager = recordmanager;
        this.stopWordDirectory = stopWordDirectory;
    }

    public HTree loadHTree(String objectname) throws IOException {
        HTree hashTable;
        long recid = recman.getNamedObject(objectname);
        if (recid != 0){
            hashTable = HTree.load(recman, recid);
            if (Objects.equals(objectname, "maxWordUrl")) {
                wordIndex = (int) hashTable.get(WORDINDEXSTRING);
                urlIndex = (int) hashTable.get(URLINDEXSTRING);
                phrase2Index = (int) hashTable.get(PHRASE2INDEXSTRING);
                phrase3Index = (int) hashTable.get(PHRASE3INDEXSTRING);
            }
        }
        else
        {
            hashTable = HTree.createInstance(recman);
            recman.setNamedObject( objectname, hashTable.getRecid() );
            if (Objects.equals(objectname, "maxWordUrl")) {
                hashTable.put(WORDINDEXSTRING,0);
                hashTable.put(URLINDEXSTRING,0);
                hashTable.put(PHRASE2INDEXSTRING,0);
                hashTable.put(PHRASE3INDEXSTRING,0);
            }
        }
        return hashTable;
    }
    public void init() throws IOException {
        queue = new ArrayList<>();
        queue.add(link);
        recman = RecordManagerFactory.createRecordManager(recordmanager);
        wordId = loadHTree("word-word_id");
        idWord = loadHTree("word_id-word");
        urlId = loadHTree("url-url_id");
        idUrl = loadHTree("url_id-url");
        invertedIndex = loadHTree("invertedIndex");
        forwardIndex = loadHTree("forwardIndex");
        maxWordUrl = loadHTree("maxWordUrl");
        pageInfo = loadHTree("pageInfo");
        subLinks = loadHTree("subLinks");
        parentLinks = loadHTree("parentLinks");
        wordTfIdf = loadHTree("wordTfIdf");
        phrase2TfIdf = loadHTree("phrase2TfIdf");
        phrase3TfIdf = loadHTree("phrase3TfIdf");
        phrase3InvertedIndex = loadHTree("phrase3InvertedIndex");
        phrase2InvertedIndex = loadHTree("phrase2InvertedIndex");
        docWordTfIdf = loadHTree("docWordTfIdf");
        docPhrase2TfIdf = loadHTree("docPhrase2TfIdf");
        docPhrase3TfIdf = loadHTree("docPhrase3TfIdf");
    }

    public void clearRecord(String objectname) throws IOException {
        HTree hashTable;
        hashTable = HTree.createInstance(recman);
        recman.setNamedObject( objectname, hashTable.getRecid() );
        if (Objects.equals(objectname, "maxWordUrl")) {
            hashTable.put(WORDINDEXSTRING,0);
            hashTable.put(URLINDEXSTRING,0);
            hashTable.put(PHRASE2INDEXSTRING,0);
            hashTable.put(PHRASE3INDEXSTRING,0);
        }
    }

    public void clearAll() throws IOException {
        this.clearRecord("word-word_id");
        this.clearRecord("word_id-word");
        this.clearRecord("url-url_id");
        this.clearRecord("url_id-url");
        this.clearRecord("invertedIndex");
        this.clearRecord("forwardIndex");
        this.clearRecord("maxWordUrl");
        this.clearRecord("pageInfo");
        this.clearRecord("subLinks");
        this.clearRecord("parentLinks");
        this.clearRecord("wordTfIdf");
        this.clearRecord("phrase2TfIdf");
        this.clearRecord("phrase3TfIdf");
        this.clearRecord("phrase2InvertedIndex");
        this.clearRecord("phrase3InvertedIndex");
        this.clearRecord("docWordTfIdf");
        this.clearRecord("docPhrase2TfIdf");
        this.clearRecord("docPhrase3TfIdf");
    }

    public void crawl(int num) throws ParserException, IOException, ParseException, java.text.ParseException {
//        todo: check the update time, get the title
        while (!queue.isEmpty() && urlIndex<num){
            String url = queue.get(0);
            queue.remove(0);
//            parse the url to get the modification date and content length
//            check whether the scraping is successful
            Hashtable info = ResponseParser.getResponse(url);
            if (!(boolean)info.get("Success")){
                continue;
            }

            Date lastModified = (Date) info.get("Last-Modified");
            String contentLengthString = String.valueOf(info.get("Content-Length"));
            boolean newUrl = true;

//            check whether to crawl
            if (urlId.get(url) != null){
                int id = (int) urlId.get(url);
                if (pageInfo.get(id) != null){
                    newUrl = false;
                    Date storedDate = (Date) ((Hashtable) pageInfo.get(id)).get("Last-Modified");
//                    Date storedDate = formatter.parse(storedDateString);
                    if (storedDate.compareTo(lastModified) == 0){
                        continue;
                    }
                }
            }

//            check whether the url is already stored
            if (newUrl){
                urlId.put(url,urlIndex);
                idUrl.put(urlIndex,url);
            }
            Crawler crawler = new Crawler(url, stopWordDirectory);
            // implement stemming in StringExtractor
            Vector<String> words = crawler.extractWords();
            Vector<String> links = crawler.extractLinks();
            // check the value of content-length
            if (Integer.parseInt(contentLengthString) == 0){
                int size = 0;
                for (String word: words){
                    size += word.length();
                }
                info.put("Content-Length",size);
            }
            pageInfo.put(urlIndex,info);
            ArrayList<String> phrase2 = Utils.getNGrams(words,2);
            ArrayList<String> phrase3 = Utils.getNGrams(words,3);
            updatePhraseIndex(phrase2, urlIndex, 2);
            updatePhraseIndex(phrase3, urlIndex, 3);
            updateHashTable(words, true, urlIndex);
            updateHashTable(links, false, urlIndex);

            for (String subUrl: links){
                if (urlId.get(subUrl) == null) {
                    queue.add(subUrl);
                }
            }

            if (newUrl){
                urlIndex++;
                System.out.println(urlIndex);
            }
        }
    }

    public void updatePhraseIndex(ArrayList<String> phrases, int index, int n) throws IOException {
        if (n==2){
            for (String phrase: phrases){
                if (phrase2InvertedIndex.get(phrase) == null){
                    phrase2InvertedIndex.put(phrase, new Hashtable<>());
                }
                Hashtable phraseHashTable = ((Hashtable)phrase2InvertedIndex.get(phrase));
                if (phraseHashTable.get(index) == null){
                    phraseHashTable.put(index, 1);
                }
                else {
                    int count = (int) phraseHashTable.get(index);
                    phraseHashTable.put(index, count+1);
                }
                phrase2InvertedIndex.put(phrase, phraseHashTable);
            }
        }
        else {
            for (String phrase: phrases){
                if (phrase3InvertedIndex.get(phrase) == null){
                    phrase3InvertedIndex.put(phrase, new Hashtable<>());
                }
                Hashtable phraseHashTable = ((Hashtable)phrase3InvertedIndex.get(phrase));
                if (phraseHashTable.get(index) == null){
                    phraseHashTable.put(index, 1);
                }
                else {
                    int count = (int) phraseHashTable.get(index);
                    phraseHashTable.put(index, count+1);
                }
                phrase3InvertedIndex.put(phrase, phraseHashTable);
            }
        }
    }
    public void updateHashTable(Vector<String> words, boolean isWord, int index) throws IOException {
        Hashtable<Integer, Integer> countWords = new Hashtable<Integer, Integer>();

        for (String word : words) {
            if (isWord) {
                if (wordId.get(word) == null) {
                    wordIndex++;
                    wordId.put(word, wordIndex);
                    idWord.put(wordIndex, word);
                    invertedIndex.put(wordIndex, new Hashtable());
                }
                int id = (int) wordId.get(word);
                countWords.putIfAbsent(id, 0);
                countWords.put(id, countWords.get(id) + 1);
            }
        }
        if (isWord){
            if (forwardIndex.get(index) == null){
                forwardIndex.put(index, countWords);
            }
            for (String word : words) {
                int id = (int) wordId.get(word);
                int num = countWords.get(id);
                Hashtable h = ((Hashtable) invertedIndex.get(id));
                h.put(index, num);
                invertedIndex.put(id, h);
            }
        }
        else {
            subLinks.put(index, words);
            for (String link: words){
                if (parentLinks.get(link) == null){
                    parentLinks.put(link, new ArrayList<>());
                }
                if (!((ArrayList) parentLinks.get(link)).contains(index)){
                    ((ArrayList) parentLinks.get(link)).add(index);
                }
            }
        }
    }

//    todo: calculate tf-idf for terms
    public void calculateWordWeight() throws IOException {
//        get max tf, total number is urlIndex, doc frequency is length of the hashtable
        FastIterator wordsIterator = invertedIndex.keys();
        Object id;
        while ((id = wordsIterator.next()) != null){
            String word = (String)idWord.get(id);
            Hashtable wordInfo = (Hashtable) invertedIndex.get(id);
            Hashtable<Integer, Double> wordScore = new Hashtable<>();
            int maxTf = 0;
            int docFreq = 0;
            Iterator iter = wordInfo.keySet().iterator();
            while (iter.hasNext()){
                docFreq++;
                int docId = (int)iter.next();
                int tf = (int)wordInfo.get(docId);
                if (tf > maxTf){
                    maxTf = tf;
                }
            }
            iter = wordInfo.keySet().iterator();
            while (iter.hasNext()){
                int docId = (int)iter.next();
                int tf = (int)wordInfo.get(docId);
                Hashtable docWordScore = new Hashtable<>();
                if (docWordTfIdf.get(docId) != null){
                    docWordScore = (Hashtable) docWordTfIdf.get(docId);
                }
                double tfNorm = (double) tf/ (double) maxTf;
                double idf = Math.log((double) (urlIndex)/ (double) docFreq)/Math.log(2);
                double score = tfNorm * idf / (Math.log(urlIndex)/Math.log(2));
                wordScore.put(docId, score);
                docWordScore.put(word, score);
                docWordTfIdf.put(docId, docWordScore);
            }
            wordTfIdf.put(word, wordScore);
        }

        FastIterator phrase2Iterator = phrase2InvertedIndex.keys();
        Object phrase2;
        while ((phrase2 = phrase2Iterator.next()) != null){
//            System.out.println(phrase2);
            Hashtable phrase2Info = (Hashtable) phrase2InvertedIndex.get(phrase2);
            Hashtable<Integer, Double> wordScore = new Hashtable<>();
            int maxTf = 0;
            int docFreq = 0;
            Iterator iter = phrase2Info.keySet().iterator();
            while (iter.hasNext()){
                docFreq++;
                int docId = (int)iter.next();
                int tf = (int)phrase2Info.get(docId);
                if (tf > maxTf){
                    maxTf = tf;
                }
            }
            iter = phrase2Info.keySet().iterator();
            while (iter.hasNext()){
                int docId = (int)iter.next();
                int tf = (int)phrase2Info.get(docId);
                Hashtable docPhrase2Score = new Hashtable<>();
                if (docPhrase2TfIdf.get(docId) != null){
                    docPhrase2Score = (Hashtable) docPhrase2TfIdf.get(docId);
                }
                double tfNorm = (double) tf/ (double) maxTf;
                double idf = Math.log((double) (urlIndex)/ (double) docFreq)/Math.log(2);
                double score = tfNorm * idf / (Math.log(urlIndex)/Math.log(2));
                wordScore.put(docId, score);
                docPhrase2Score.put(phrase2, score);
                docPhrase2TfIdf.put(docId, docPhrase2Score);
            }
            phrase2TfIdf.put(phrase2, wordScore);
        }

        FastIterator phrase3Iterator = phrase3InvertedIndex.keys();
        Object phrase3;
        while ((phrase3 = phrase3Iterator.next()) != null){
//            System.out.println(phrase3);
            Hashtable phrase3Info = (Hashtable) phrase3InvertedIndex.get(phrase3);
            Hashtable<Integer, Double> wordScore = new Hashtable<>();
            int maxTf = 0;
            int docFreq = 0;
            Iterator iter = phrase3Info.keySet().iterator();
            while (iter.hasNext()){
                docFreq++;
                int docId = (int)iter.next();
                int tf = (int)phrase3Info.get(docId);
                if (tf > maxTf){
                    maxTf = tf;
                }
            }
            iter = phrase3Info.keySet().iterator();
            while (iter.hasNext()){
                int docId = (int)iter.next();
                int tf = (int)phrase3Info.get(docId);
                Hashtable docPhrase3Score = new Hashtable<>();
                if (docPhrase3TfIdf.get(docId) != null){
                    docPhrase3Score = (Hashtable) docPhrase3TfIdf.get(docId);
                }
                double tfNorm = (double) tf/ (double) maxTf;
                double idf = Math.log((double) (urlIndex)/ (double) docFreq)/Math.log(2);
                double score = tfNorm * idf / (Math.log(urlIndex)/Math.log(2));
                wordScore.put(docId, score);
                docPhrase3Score.put(phrase3, score);
                docPhrase3TfIdf.put(docId, docPhrase3Score);
            }
            phrase3TfIdf.put(phrase3, wordScore);
        }

        int count = 0;
        FastIterator iterator = docWordTfIdf.keys();
        Object word;
        while ((word = iterator.next())!=null && count <=15){
            count++;
            System.out.println(docWordTfIdf.get(word));
        }
        count = 0;
        FastIterator iteratorPhrase2 = phrase2TfIdf.keys();
        Object p2;
        while ((p2 = iteratorPhrase2.next())!=null && count <=15){
            count++;
            System.out.println(phrase2TfIdf.get(p2));
        }
    }


    public Hashtable parseForwardIndex() throws IOException {
        Hashtable count = new Hashtable();
        Object s;
        FastIterator iter = forwardIndex.keys();
        while ((s = iter.next()) != null){
            Hashtable wordWithNums = (Hashtable) forwardIndex.get(s);
            count.put(s, wordWithNums);
        }
        return count;
    }

    public Hashtable parsePageInfo() throws IOException {
        Hashtable pInfo = new Hashtable();
        Object s;
        FastIterator iter = pageInfo.keys();
        while ((s = iter.next()) != null){
            Hashtable wordWithNums = (Hashtable) pageInfo.get(s);
            pInfo.put(s, wordWithNums);
        }
        return pInfo;
    }

    public void printTree(HTree tree) throws IOException {
        FastIterator iter = tree.keys();
        String key;
        while ((key = (String)iter.next())!=null){
            String s = "key: " + key;
            int id = (int) tree.get(key);
            s += " value: " + id;
            System.out.println(s);
        }
    }

    public void finish() throws IOException
    {
        maxWordUrl.put(WORDINDEXSTRING, wordIndex);
        maxWordUrl.put(URLINDEXSTRING, urlIndex);
        maxWordUrl.put(PHRASE2INDEXSTRING, phrase2Index);
        maxWordUrl.put(PHRASE3INDEXSTRING, phrase3Index);
//        printTree(idWord);
        recman.commit();
        recman.close();
    }
    public static void main(String[] args) throws IOException, ParserException, ParseException, java.text.ParseException {
        String link = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
        String stopWordDirectory = "C:\\Users\\29836\\OneDrive\\Desktop\\search engine\\project\\server\\src\\main\\java\\core\\stopwords.txt";
        Main m = new Main("project", link, stopWordDirectory);
        m.init();
        m.crawl(400);
        m.calculateWordWeight();
//        m.clearAll();
        m.finish();


    }
}