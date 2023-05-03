package com.example.server;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Objects;

@org.springframework.stereotype.Repository
public class Repository {
    public HTree getWordTfIdf() {
        return wordTfIdf;
    }

    public HTree getPhrase2TfIdf() {
        return phrase2TfIdf;
    }

    public HTree getPhrase3TfIdf() {
        return phrase3TfIdf;
    }

    public HTree getUrlId() {
        return urlId;
    }

    public HTree getIdUrl() {
        return idUrl;
    }

    public HTree getPageInfo() {
        return pageInfo;
    }

    public HTree getDocWordTfIdf() {
        return docWordTfIdf;
    }

    public HTree getDocPhrase2TfIdf() {
        return docPhrase2TfIdf;
    }

    public HTree getDocPhrase3TfIdf() {
        return docPhrase3TfIdf;
    }
    public HTree getSubLink() {
        return subLink;
    }

    public HTree getParentLink() {
        return parentLink;
    }

    private HTree wordTfIdf;
    private HTree phrase2TfIdf;
    private HTree phrase3TfIdf;
    private HTree urlId;
    private HTree idUrl;
    private HTree pageInfo;
    private HTree docWordTfIdf;
    private HTree docPhrase2TfIdf;
    private HTree docPhrase3TfIdf;
    private HTree subLink;

    public HTree getIdWord() {
        return idWord;
    }

    private HTree idWord;
    private HTree parentLink;

    public HTree getForwardIndex() {
        return forwardIndex;
    }

    private HTree forwardIndex;
    private HTree userHistory;
    private RecordManager recman;


    Repository() throws Exception {
        recman = RecordManagerFactory.createRecordManager("project");
        urlId = loadHTree("url-url_id");
        idUrl = loadHTree("url_id-url");
        wordTfIdf = loadHTree("wordTfIdf");
        phrase2TfIdf = loadHTree("phrase2TfIdf");
        phrase3TfIdf = loadHTree("phrase3TfIdf");
        docWordTfIdf = loadHTree("docWordTfIdf");
        docPhrase2TfIdf = loadHTree("docPhrase2TfIdf");
        docPhrase3TfIdf = loadHTree("docPhrase3TfIdf");
        pageInfo = loadHTree("pageInfo");
        subLink = loadHTree("subLinks");
        parentLink = loadHTree("parentLinks");
        forwardIndex = loadHTree("forwardIndex");
        idWord = loadHTree("word_id-word");
        userHistory = loadHTree("userHistory");
    }

    public boolean checkExist(String username, String password) throws IOException {
        if (userHistory.get(username) == null){
            return false;
        }
        return true;
    }

    public boolean checkPassword(String username, String password) throws IOException {
        if (!checkExist(username, password)){
            return false;
        }
        else {
            String truePassword = (String) ((Hashtable) (userHistory.get(username))).get("password");
            return Objects.equals(truePassword, password);
        }
    }

    public boolean createUser(String username, String password) throws IOException {
        if (checkExist(username, password)){
            return false;
        }
        else {
            Hashtable newUser = new Hashtable();
            newUser.put("password", password);
            Hashtable history = new Hashtable();
            newUser.put("history", history);
            userHistory.put(username, newUser);
            recman.commit();
            return true;
        }
    }

    public HTree loadHTree(String objectname) throws Exception {
        HTree hashTable;
        long recid = recman.getNamedObject(objectname);
        if (objectname == "userHistory"){
            if (recid == 0) {
                hashTable = HTree.createInstance(recman);
                recman.setNamedObject( objectname, hashTable.getRecid() );
            }
            else {
                hashTable = HTree.load(recman, recid);
            }
        }
        else {
            if (recid != 0){
                hashTable = HTree.load(recman, recid);
            }
            else
            {
                throw new Exception("Please do web crawling first");
            }
        }
        return hashTable;
    }

}
