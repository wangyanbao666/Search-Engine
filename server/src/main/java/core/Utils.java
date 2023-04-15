package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Utils {
    public static ArrayList<String> getNGrams(List words, int n){
        ArrayList<String> ngrams = new ArrayList<String>();
        for (int i=0; i<words.size()-n+1;i++){
            String s = String.join(" ",words.subList(i,i+n));
            ngrams.add(s);
        }
        return ngrams;
    }

    public static void main(String[] args) {
        Vector<String> words = new Vector<>();
        words.add("sss");
        words.add("ppp");
        words.add("kkk");
        words.add("wsw");
        System.out.println(getNGrams(words,3));
    }
}
