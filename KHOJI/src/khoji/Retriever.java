package khoji;

/*
 * Copyright 2014 Mohd Azeem.
 *
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mohd Azeem
 */
public class Retriever {

    private final String query;
    private final String index_path;
    private String[] query_terms;
    private static ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> inverted_index;
    private static ConcurrentHashMap<String, Integer> doc_legth_table;
    private int total_no_of_docs;

    Retriever(String query_string, String inverted_index_path) {
        query = query_string;
        index_path = inverted_index_path;
    }

    public ConcurrentHashMap<String, Float> Retrieve() throws IOException, ClassNotFoundException {
        query_terms = query.toLowerCase().split(" ");

        // load statistics from disk to statistics1 object
        FileInputStream fileInDocLenTable = new FileInputStream(index_path + "\\docLenTable.kh");
        FileInputStream fileInNumOfDoc = new FileInputStream(index_path + "\\numOfDocs.kh");
        FileInputStream fileInIndex = new FileInputStream(index_path + "\\index.kh");

        ObjectInputStream in_index = new ObjectInputStream(fileInIndex);
        ObjectInputStream in_doc_len_table = new ObjectInputStream(fileInDocLenTable);
        ObjectInputStream in_num_of_docs = new ObjectInputStream(fileInNumOfDoc);
        try {
            inverted_index = (ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>) in_index.readObject();
            doc_legth_table = (ConcurrentHashMap<String, Integer>) in_doc_len_table.readObject();
            total_no_of_docs = (int) in_num_of_docs.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Retriever.class.getName()).log(Level.SEVERE, null, ex);
        }
        in_index.close(); in_doc_len_table.close(); in_num_of_docs.close();
        fileInIndex.close();fileInDocLenTable.close(); fileInNumOfDoc.close();
//            System.out.println("current_statistics:"); System.out.println(inverted_index);
        ConcurrentHashMap<String, Float> WeightingTable = new ConcurrentHashMap<>();
        for (String cur_term : query_terms) {
//            System.out.println(cur_term); System.out.println(inverted_index);
            if (inverted_index == null) {
                System.out.println("can't read the statistics");
                return null;
            }
//            System.out.println((inverted_index.get("NIT")));
            if (inverted_index.containsKey(cur_term)) {
                Set<String> list_of_Docs_With_this_term = (inverted_index.get(cur_term)).keySet();
                for (String cur_doc : list_of_Docs_With_this_term) {
                    if (WeightingTable.containsKey(cur_doc)) {
                        WeightingTable.put(cur_doc, WeightingTable.get(cur_doc) + tfIdfWeight(cur_term, cur_doc));
                    } else {
                        WeightingTable.put(cur_doc, tfIdfWeight(cur_term, cur_doc));
                    }
//                    System.out.println("hello");
                }
            } 
        }
        //for debugging
//        System.out.println(inverted_index);
//        System.out.println(doc_legth_table);
//        System.out.println(total_no_of_docs);

        System.out.println(WeightingTable);
        return WeightingTable;
    }

    private float tfIdfWeight(String cur_term, String cur_document)
            throws IOException {
        String term = cur_term;
        float TermFrq = 0;
        float TotalTermsInDoc = 0;
        float NumOfDocs_in_corpus = 0;
        float num_of_docs_contain_the_term = 0;

        TermFrq = ((inverted_index.get(term)).get(cur_document));
        TotalTermsInDoc = (float) doc_legth_table.get(cur_document);
        NumOfDocs_in_corpus = total_no_of_docs;
        num_of_docs_contain_the_term = (inverted_index.get(term)).size();

        if (TotalTermsInDoc <= 0) {
            TotalTermsInDoc = 1;
        }
        if (num_of_docs_contain_the_term <= 0) {
            num_of_docs_contain_the_term = 1;
        }
        if(num_of_docs_contain_the_term == NumOfDocs_in_corpus)
            NumOfDocs_in_corpus++;

//        float TF = (TermFrq) / TotalTermsInDoc;
//        float IDF = (float) Math.log((float) NumOfDocs_in_corpus / (float) num_of_docs_contain_the_term);        
        // modified formula
        float TF = (float) ((TermFrq) / Math.log(TotalTermsInDoc+1));
        float IDF = (float) NumOfDocs_in_corpus / (float) num_of_docs_contain_the_term;
//        System.out.println("TF="+TF+"  IFD="+IDF);
        return (TF * IDF);
    }
}
