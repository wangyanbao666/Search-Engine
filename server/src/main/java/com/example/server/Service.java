package com.example.server;

import core.StopStem;
import core.Utils;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
public class Service {
    private final Repository repository;
    private StopStem stemmer;
    @Autowired
    Service(Repository repository) throws IOException {
        this.repository = repository;
        stemmer = new StopStem("C:\\Users\\29836\\OneDrive\\Desktop\\search engine\\project\\server\\src\\main\\java\\core\\stopwords.txt");
    }

    private ArrayList<Hashtable> calculateScore(ArrayList<String> words, ArrayList<String> phrase2, ArrayList<String> phrase3, String query) throws IOException {
//   return an arraylist ordered by score (desc)
        HTree pageInfo = repository.getPageInfo();
        HTree idUrl = repository.getIdUrl();
        HTree subLinks = repository.getSubLink();
        HTree parentLinks = repository.getParentLink();
        HTree forwardIndex = repository.getForwardIndex();
        HTree wordsTfIdf = repository.getWordTfIdf();
        HTree phrase2TfIdf = repository.getPhrase2TfIdf();
        HTree phrase3TfIdf = repository.getPhrase3TfIdf();
        HTree docWordTfIdf = repository.getDocWordTfIdf();
        HTree docPhrase2TfIdf = repository.getDocPhrase2TfIdf();
        HTree docPhrase3TfIdf = repository.getDocPhrase3TfIdf();
        Hashtable<Object, Object> wordCount = new Hashtable<>();
        HTree idWord = repository.getIdWord();
        Hashtable phrase2Count = new Hashtable<>();
        Hashtable phrase3Count = new Hashtable<>();

        for (String word: words){
            wordCount.putIfAbsent(word, 0);
            wordCount.put(word, (int)wordCount.get(word)+1);
        }
        for (String word: phrase2){
            phrase2Count.putIfAbsent(word, 0);
            phrase2Count.put(word, (int)phrase2Count.get(word)+1);
        }
        for (String word: phrase3){
            phrase3Count.putIfAbsent(word, 0);
            phrase3Count.put(word, (int)phrase3Count.get(word)+1);
        }

//        FastIterator it = docPhrase2TfIdf.keys();
//        Object key;
//        while ((key = it.next())!=null){
//            System.out.println(key);
//            System.out.println(docPhrase2TfIdf.get(key));
//        }

//       an arraylist that contains all documents that contain any word in the query
        ArrayList<Integer> al = new ArrayList<Integer>();
        for (String word: words){
            Hashtable wordInfo = (Hashtable) wordsTfIdf.get(word);
            if (wordInfo != null){
                Iterator iter = wordInfo.keySet().iterator();
                while (iter.hasNext()){
                    int docId = (int) iter.next();
                    if (!al.contains(docId)){
                        al.add(docId);
                    }
                }
            }
        }
        for (String phrase: phrase2){
            Hashtable phraseInfo = (Hashtable) phrase2TfIdf.get(phrase);
            if (phraseInfo != null){
                Iterator iter = phraseInfo.keySet().iterator();
                while (iter.hasNext()){
                    int docId = (int) iter.next();
                    if (!al.contains(docId)){
                        al.add(docId);
                    }
                }
            }
        }
        for (String phrase: phrase3){
            Hashtable phraseInfo = (Hashtable) phrase3TfIdf.get(phrase);
            if (phraseInfo != null){
                Iterator iter = phraseInfo.keySet().iterator();
                while (iter.hasNext()){
                    int docId = (int) iter.next();
                    if (!al.contains(docId)){
                        al.add(docId);
                    }
                }
            }
        }

        Hashtable<Integer, Double> scores = new Hashtable<Integer, Double>();
        for (int urlId: al){
            double totalScore = 0;
            //        1. calculate title score
            double partialScore = 0;
            Hashtable page = (Hashtable) pageInfo.get(urlId);
            String title = (String) page.get("Title");
            int count = checkMatch(words, title);
            int count2 = checkMatch(phrase2, title);
            int count3 = checkMatch(phrase3, title);
            if (count3 > 0){
                partialScore = 500;
            } else if (count2 > 0) {
                partialScore = 300;
            }
            else if (count > 0){
                partialScore = 100;
            }
            totalScore += partialScore;

            //        2. calculate single word similarity
            Hashtable thisDocWordTfIdf = (Hashtable) docWordTfIdf.get(urlId);
//            cos similarity, needs all weights in a doc
            double sumSquareD = 0;
//            all weights in the query (the number of appearance of a word)
            double sumSquareQ = 0;
            Set docWordSet = thisDocWordTfIdf.keySet();
//            calculate sum of all squared weights in a doc
            for (Object word: docWordSet){
                sumSquareD += ((double) thisDocWordTfIdf.get(word))*((double) thisDocWordTfIdf.get(word));
            }
            double singleWordPartialScore = 0;
            double weightSum = 0;
            for (String word: words){
                if (wordsTfIdf.get(word) != null){
                    int countInQuery = (int) wordCount.get(word);
                    sumSquareQ += countInQuery*countInQuery;
                    double weight = 0;
                    Hashtable wordInfo = (Hashtable) wordsTfIdf.get(word);
                    if (wordInfo.get(urlId) != null){
                        weight = (double) wordInfo.get(urlId);
                    }
//                    System.out.println(word+": "+countInQuery);
                    weightSum += countInQuery * weight;
                }
            }
            if (weightSum != 0) {
                singleWordPartialScore = weightSum / (Math.sqrt(sumSquareQ)*Math.sqrt(sumSquareD)) * 500;
                totalScore += singleWordPartialScore;
            }

            //        3. calculate 2-word-phrase similarity
            Hashtable thisDocPhrase2TfIdf = (Hashtable) docPhrase2TfIdf.get(urlId);
//            cos similarity, needs all weights in a doc
            double phrase2SumSquareD = 0;
//            all weights in the query (the number of appearance of a word)
            double phrase2SumSquareQ = 0;
            Set docPhrase2Set = thisDocPhrase2TfIdf.keySet();
//            calculate sum of all squared weights in a doc
            for (Object p2: docPhrase2Set){
                phrase2SumSquareD += ((double) thisDocPhrase2TfIdf.get(p2))*((double) thisDocPhrase2TfIdf.get(p2));
            }
            double phrase2PartialScore = 0;
            double phrase2WeightSum = 0;
            for (String p2: phrase2){
                if (phrase2TfIdf.get(p2) != null){
                    int countInQuery = (int) phrase2Count.get(p2);
                    phrase2SumSquareQ += countInQuery*countInQuery;
                    double weight = 0;
                    Hashtable phrase2Info = (Hashtable) phrase2TfIdf.get(p2);
                    if (phrase2Info.get(urlId) != null){
                        weight = (double) phrase2Info.get(urlId);
                    }
                    phrase2WeightSum += countInQuery * weight;
                }
            }

            if (phrase2WeightSum != 0){
                phrase2PartialScore = phrase2WeightSum / (Math.sqrt(phrase2SumSquareQ)*Math.sqrt(phrase2SumSquareD)) * 10000;
                totalScore += phrase2PartialScore;
            }

            //        4. calculate 3-word-phrase similarity
            Hashtable thisDocPhrase3TfIdf = (Hashtable) docPhrase3TfIdf.get(urlId);
//            cos similarity, needs all weights in a doc
            double phrase3SumSquareD = 0;
//            all weights in the query (the number of appearance of a word)
            double phrase3SumSquareQ = 0;
            Set docPhrase3Set = thisDocPhrase3TfIdf.keySet();
//            calculate sum of all squared weights in a doc
            for (Object p3: docPhrase3Set){
                phrase3SumSquareD += ((double) thisDocPhrase3TfIdf.get(p3))*((double) thisDocPhrase3TfIdf.get(p3));
            }
            double phrase3PartialScore = 0;
            double phrase3WeightSum = 0;
            for (String p3: phrase3){
                if (phrase3TfIdf.get(p3) != null){
                    int countInQuery = (int) phrase3Count.get(p3);
                    phrase3SumSquareQ += countInQuery*countInQuery;
                    double weight = 0;
                    Hashtable phrase3Info = (Hashtable) phrase3TfIdf.get(p3);
                    if (phrase3Info.get(urlId) != null){
                        weight = (double) phrase3Info.get(urlId);
                    }
                    phrase3WeightSum += countInQuery * weight;
                }
            }
            if (phrase3WeightSum != 0){
                phrase3PartialScore = phrase3WeightSum / (Math.sqrt(phrase3SumSquareQ)*Math.sqrt(phrase3SumSquareD)) * 20000;
                totalScore += phrase3PartialScore;
            }
            scores.put(urlId, totalScore);
        }

//        sort the arrayList based on the score
        ArrayList<Integer> keys = new ArrayList<>(scores.keySet());
        Collections.sort(keys, Comparator.comparing(scores::get));
        Collections.reverse(keys);


        ArrayList<Hashtable> result = new ArrayList();
        for (int urlId: keys){
            //        get words count
            Hashtable<Integer,Integer> wordsCount = (Hashtable) forwardIndex.get(urlId);
            ArrayList<Integer> wordList = new ArrayList<>(wordsCount.keySet());
            Collections.sort(wordList, Comparator.comparing(wordsCount::get));
            Collections.reverse(wordList);
            ArrayList keywords = new ArrayList();
            for (int i=0;i<Math.min(5,wordList.size());i++){
                int id = wordList.get(i);
                String word = (String) idWord.get(id);
                keywords.add(String.format("%s: %d",word,wordsCount.get(id)));
            }
            System.out.println(keywords);
            ArrayList<String> sublinks = new ArrayList<String>((Vector)subLinks.get(urlId));
            String link = (String) idUrl.get(urlId);
            ArrayList<Integer> parentlinksid = (ArrayList<Integer>) parentLinks.get(link);
            ArrayList<String> parentlinks = new ArrayList<String>();
            for (int id: parentlinksid){
                parentlinks.add((String) idUrl.get(id));
            }
            Hashtable page = (Hashtable) pageInfo.get(urlId);
            page.put("url", idUrl.get(urlId));
            page.put("sublinks", sublinks);
            page.put("parentlinks", parentlinks);
            page.put("score", scores.get(urlId));
            page.put("keywords", keywords);
            result.add(page);
        }
        return result;
    }

    public ArrayList<Hashtable> getResult(String query) throws IOException {
//    get the page information with the arrayList returned from calculateScore()
        Hashtable texts = getArrays(query);
        ArrayList words = (ArrayList) texts.get("words");
        ArrayList phrase2 = (ArrayList) texts.get("phrase2");
        ArrayList phrase3 = (ArrayList) texts.get("phrase3");
        ArrayList<Hashtable> topMatch = calculateScore(words, phrase2, phrase3, query);
//        for (Hashtable info: topMatch){
//            System.out.println(info);
//        }
        return topMatch;
    }

    private Hashtable getArrays(String query){
        ArrayList words = new ArrayList();
        ArrayList phrase2 = new ArrayList();
        ArrayList phrase3 = new ArrayList();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        Matcher matcher = pattern.matcher(query);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Term surrounded by double quotes
                String text = matcher.group(1);
                if (text.split(" ").length == 1){
                    text = stemmer.stem(text);
                    words.add(text);
                }
                else if (text.split(" ").length == 2) {
                    String[] phrase2words = text.split(" ");
                    phrase2words[0] = stemmer.stem(phrase2words[0]);
                    phrase2words[1] = stemmer.stem(phrase2words[1]);
                    if (phrase2words[0].length() == 0){
                        words.add(phrase2words[1]);
                    }
                    else if (phrase2words[1].length() == 0){
                        words.add(phrase2words[0]);
                    }
                    else {
                        phrase2.add(String.join(" ", phrase2words).strip());
                    }
                }
                else if (text.split(" ").length == 3) {
                    String[] phrase2words = text.split(" ");
                    phrase2words[0] = stemmer.stem(phrase2words[0]);
                    phrase2words[1] = stemmer.stem(phrase2words[1]);
                    phrase2words[2] = stemmer.stem(phrase2words[2]);
                    String processedText = String.join(" ", phrase2words).strip();
                    if (processedText.split(" ").length == 1){
                        words.add(processedText);
                    }
                    else if (processedText.split(" ").length == 2){
                        phrase2.add(processedText);
                    }
                    else {
                        phrase3.add(processedText);
                    }
                }
            } else {
                String text = matcher.group(2);
                words.add(text);
            }
        }
        Hashtable texts = new Hashtable<>();
        texts.put("words", words);
        texts.put("phrase2", phrase2);
        texts.put("phrase3", phrase3);
        return texts;
    }

    private int checkMatch(ArrayList<String > words, String title){
        int count = 0;
//        String processedTitle = title.replaceAll("\\P{Alnum}", " ");
        String[] rowWords = title.split(" ");
        ArrayList<String> processedWords = new ArrayList();
        for (String word: rowWords){
            word = stemmer.stem(word);
            if (word.length() > 0){ processedWords.add(word); }
        }
        String processedTitle2 = String.join(" ", processedWords);
        for (String word: words){
            if (processedTitle2.contains(word)){
                count++;
            }
        }
        return count;
    }
}
