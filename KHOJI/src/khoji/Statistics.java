/*
 * Copyright 2014 Mohd Azeem.
 */

package khoji;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Mohd Azeem
 */
public class Statistics {
    private int total_no_of_docs;
    private ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> inverted_index = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,Integer> doc_length_table = new ConcurrentHashMap<>();    

    /**
     *
     * @param total_no_of_docs is the number of documents in corpus
     * @param index is the inverted index
     * @param doc_len_table the the hashtable contains the mapping between documents and their lengthS
     */
    public Statistics(int total_no_of_docs, ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> index, ConcurrentHashMap<String,Integer> doc_len_table) {
        this.total_no_of_docs = total_no_of_docs;
        this.inverted_index = index;
        this.doc_length_table = doc_len_table;        
    }

    public ConcurrentHashMap<String, Integer> getDoc_length_table() {
        return doc_length_table;
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> getInverted_index() {
        return inverted_index;
    }

    public int getTotal_no_of_docs() {
        return total_no_of_docs;
    }
}
