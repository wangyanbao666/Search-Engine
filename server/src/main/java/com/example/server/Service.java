package com.example.server;

import core.StopStem;
import core.Utils;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

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
        HTree wordsTfIdf = repository.getWordTfIdf();
        HTree phrase2TfIdf = repository.getPhrase2TfIdf();
        HTree phrase3TfIdf = repository.getPhrase3TfIdf();
        HTree docWordTfIdf = repository.getDocWordTfIdf();
        HTree docPhrase2TfIdf = repository.getPhrase2TfIdf();
        HTree docPhrase3TfIdf = repository.getPhrase3TfIdf();
        Hashtable<Object, Object> wordCount = new Hashtable<>();
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

//        FastIterator it = docWordTfIdf.keys();
//        Object key;
//        while ((key = it.next())!=null){
//            System.out.println(key);
//            System.out.println(docWordTfIdf.get(key));
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
                partialScore = 5000;
            } else if (count2 > 0) {
                partialScore = 4000;
            }
            else if (count > 0){
                partialScore = 2000;
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
                    System.out.println(word+": "+countInQuery);
                    weightSum += countInQuery * weight;
                }
            }
            singleWordPartialScore = weightSum / (Math.sqrt(sumSquareQ)*Math.sqrt(sumSquareD)) * 500;
            totalScore += singleWordPartialScore;

            scores.put(urlId, totalScore);

            //        3. calculate 2-word-phrase similarity

            //        4. calculate 3-word-phrase similarity
        }

//        sort the arrayList based on the score
        ArrayList<Integer> keys = new ArrayList<>(scores.keySet());
        Collections.sort(keys, Comparator.comparing(scores::get));

        ArrayList<Hashtable> result = new ArrayList();
        for (int urlId: keys){
            Hashtable page = (Hashtable) pageInfo.get(urlId);
            page.put("url", idUrl.get(urlId));
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
        for (Hashtable info: topMatch){
            System.out.println(info);
        }
        return topMatch;
    }

    private Hashtable getArrays(String query){
        ArrayList words = new ArrayList();
        String processedQuery = query.replaceAll("\\P{Alnum}", " ");
        String[] rowWords = processedQuery.split(" ");
        for (String word: rowWords){
            word = stemmer.stem(word);
            if (word.length() > 0){ words.add(word); }
        }
        ArrayList phrase2 = Utils.getNGrams(words, 2);
        ArrayList phrase3 = Utils.getNGrams(words, 3);
        Hashtable texts = new Hashtable<>();
        texts.put("words", words);
        texts.put("phrase2", phrase2);
        texts.put("phrase3", phrase3);
        return texts;
    }

    private int checkMatch(ArrayList<String > words, String title){
        int count = 0;
        for (String word: words){
            if (title.contains(word)){
                count++;
            }
        }
        return count;
    }
}
