# Search-Engine
## **Overall Structure**:
The project can be divided into two main components: the frontend, which handles the user interface, and the backend, which retrieves data and calculates scores. The frontend is built using the React framework, providing users with a search bar to input their queries. Upon submission, a request is sent to the backend to compute the most relevant pages and return the corresponding data to the frontend. The frontend then dynamically generates the search results based on the received information.

The web crawler is integrated with the backend to facilitate seamless data retrieval. Before executing a search query, ensure that the database is set up by running the web crawler independently. You can specify the number of pages to crawl or enter -1 as a parameter to crawl all traversed pages.

The backend is designed using the Spring framework and comprises three main classes: Controller, Service, and Repository. The Repository class is responsible for loading tables from the database and provides several getter functions that the Service class can use to access the data. The scoring logic, which includes query string processing, title matching, and tf-idf weight calculation, is implemented in the Service layer.

The Controller class manages incoming requests from the frontend, extracts the query from the request, forwards the query to the Service, and returns a response containing the search results to the client.

In summary, this project consists of a frontend user interface developed with React and a backend system built using the Spring framework, including a web crawler for data retrieval. The backend comprises three main classes (Controller, Service, and Repository) that handle incoming requests, data access, and search result scoring. To use the system, ensure the database is established by running the web crawler before attempting a search.

## **Database structure (Tables)**:
Forward Index: A hash table with URL IDs as keys and values as nested hash tables, where the nested hash table's keys are word IDs and values represent the frequency of word occurrences.

Inverted Index: A hash table with word IDs as keys and values as nested hash tables, where the nested hash table's keys are URL IDs and values represent the frequency of word occurrences.

Phrase2InvertedIndex: A hash table with 2-word phrase IDs as keys and values as nested hash tables, where the nested hash table's keys are URL IDs and values represent the frequency of phrase occurrences.

Phrase3InvertedIndex: A hash table with 3-word phrase IDs as keys and values as nested hash tables, where the nested hash table's keys are URL IDs and values represent the frequency of phrase occurrences.

WordId: A hash table with words as keys and corresponding word IDs as values.

IdWord: A hash table with word IDs as keys and corresponding words as values.

UrlId: A hash table with URLs as keys and corresponding URL IDs as values.

IdUrl: A hash table with URL IDs as keys and corresponding URLs as values.

MaxWordUrl: A counter for the total number of words and URLs, serving as an auto-increment key for both words and URLs.

SubLinks: A hash table with URL IDs as keys and values representing the child links of the corresponding page.

ParentLinks: A hash table with URL IDs as keys and values representing the parent links of the corresponding page.

PageInfo: A hash table with URL IDs as keys and values as nested hash tables containing page title, modification date, and content length.

WordTfIdf: A hash table with words as keys and values as nested hash tables, where the nested hash table's keys are URL IDs and values represent the corresponding TF-IDF weight.

Phrase2TfIdf: A hash table with 2-word phrases as keys and values as nested hash tables, where the nested hash table's keys are URL IDs and values represent the corresponding TF-IDF weight.

Phrase3TfIdf: A hash table with 3-word phrases as keys and values as nested hash tables, where the nested hash table's keys are URL IDs and values represent the corresponding TF-IDF weight.

DocWordTfIdf: A hash table with URL IDs as keys and values as nested hash tables, where the nested hash table's keys are words and values represent the TF-IDF weight of the corresponding document.

DocPhrase2TfIdf: A hash table with URL IDs as keys and values as nested hash tables, where the nested hash table's keys are 2-word phrases and values represent the TF-IDF weight of the corresponding document.

DocPhrase3TfIdf: A hash table with URL IDs as keys and values as nested hash tables, where the nested hash table's keys are 3-word phrases and values represent the TF-IDF weight of the corresponding document.

## Crawler:
Main class: 
public Main(String recordmanager, String link, String stopWordDirectory){
The entry point of the program. When initializing a new main object, need to pass in the name of a recordManager, the starting crawling link, and the absolute path to stopWordDirectory.

Functions in Main class:
public void init() throws IOException {
Load all the tables (as described above) from the database using the function loadHTree() or create them if they do not exist in the database:
public HTree loadHTree(String objectname) throws IOException {

public void crawl(int num) throws ParserException, IOException, ParseException, java.text.ParseException {
The crawl() function is the most important function which is responsible for crawling the data from the url. The parameter num specifies the limitation for crawled pages for testing purpose. In this function we will be able to get some specific information about the page including page title, last modification date, and content length with the help of the class ResponseParser. And we will also get the words and child links using the class Crawler.

public void updateHashTable(Vector<String> words, boolean isWord, int index) throws IOException {
the function update hashTable() is to update the urlId, idUrl, forwardIndex, and subLinks tables.

public Hashtable parseForwardIndex() throws IOException {
public Hashtable parsePageInfo() throws IOException {
These two functions convert HTree forwardIndex and pageInfo to hashmaps.

public void finish() throws IOException
commit the changes to the database.

public void clearRecord(String objectname) throws IOException
clear a specific table stored in the database to make it convenient for testing.

public void clearAll() throws IOException {
clear all the tables.

public void calculateWordWeight() throws IOException {
responsible for calculating weight of word, 2-word-phrases and 3-word-phrases and store the weights into the database. The formula for weight calculation is:
weight=(tf_ij)/(tf_maxi )*log_2⁡〖N/D〗
Where tf_ij is the term frequency of term j in document i, tf_maxi is the maximum term frequency in document i, N is the total number of documents, D is the number of documents that contain the term. 

Note: The calculation is achieved by using those inverted index tables so this function can be executed independently after crawling. It is easier to calculate again if there is any logic change.

LinkExtractor, StringExtractor, StopStem and Crawler are modified based on the codes from labs.
There is no change made to LinkExtractor, StringExtractor and StopStem.
In Crawler, it calls the function in LinkExtractor and StringExtractor to get the links and a bag of words from the page. After that, remove stopwords and implement stemming to those words returned from StringExtractor.


Class ResponseParser:
It only has one function getResponse():
public static Hashtable<String, String> getResponse(String link) throws ParserException, ParseException {
With the help of the library htmlparser, we are able to get all the information from the response header and the page title. It will return a hashtable that contains all these information.


## **Scoring logic**:
The given process outlines a search algorithm that involves the following steps:
Preprocess the input query by performing stemming and removing stop words. This step helps in reducing the query to its essential terms and discarding irrelevant or common words.
Filter out documents that contain at least one word from the preprocessed query. This ensures that only relevant documents are considered for further analysis.
Compare the query terms with the document titles. Assign a score based on the following criteria: a. +2000 points if a single word from the query appears in the title. b. +4000 points if a 2-word phrase from the query appears in the title. c. +5000 points if a 3-word phrase from the query appears in the title.
Calculate word-level cosine similarity between the query and documents using their respective term frequency-inverse document frequency (tf-idf) representations. Add the similarity score multiplied by 500 to the overall score for each document.
Calculate phrase-level cosine similarity between the query and documents using their tf-idf representations. Add the similarity score multiplied by 1000 for 2-word phrases or 1500 for 3-word phrases to the overall score for each document.
To calculate the similarity scores, multiple tables are used, including wordsTfIdf, phrase2TfIdf, phrase3TfIdf, docWordTfIdf, docPhrase2TfIdf, and docPhrase3TfIdf. The "getNGrams" function in the Utils class extracts 2-word and 3-word phrases from the query. Then, three count hashtables are created to store the number of occurrences of words and phrases in the query.
The docTfIdfs and counts hashtables are used to calculate the denominator of the similarity scores, which is the square root of the sum of all tf-idfs in a document multiplied by the square root of the sum of all occurrences in the query. The numerator is calculated by summing up the product of the number of occurrences of words or phrases in the query and their corresponding weight in the tf-idf tables, if present in the document.

## **Installation Procedure**
Please follow the instructions in the readme.doc. Generally, these are the stops need to be done to successfully run the program:
	Open the backend project in intellij and add all libraries required.
	Run the crawler to establish the database.
	Run the backend java spring web development project so that it can receive the request from our backend.
	Run the client react project and go to localhost:3000. You will see the UI and can test the search result by typing in a query in the search bar and clicking the “Go” button.

## **Testing of functions implemented**
Conclusion
Strength:
Weakness:






