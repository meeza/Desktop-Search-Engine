package khoji;

/*
 * Copyright 2014 Mohd Azeem.
 *
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Mohd Azeem
 */
public class Crawler {
    
    // path of the directory in which the files and data are stored and Search Engine has to work on that data
    private String corpus_path;
    private String index_path;
    protected static int total_no_of_docs = 0;

    /**
     * It is the Inverted Index which contains the mapping between tokens and second Hashtable.
     * second Hashtable is the mapping between document and term frequency
     * inverted_index is equivalent to ::: {term : {docId: TermFrequency}}
     */
    public static ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> inverted_index;
    
//    private static final String docLengthTable_path = new String("D:\\Khoji\\stats.kh");
    private static ConcurrentHashMap<String,Integer> doc_length_table = new ConcurrentHashMap<>();
    /**
     *
     * @param data_path is the absolute path from where the Search Engine will rad all the files and crawl on them
     * @param source_directory is the absolute path where the statistics files are saved
     */
    Crawler(String corpus_folder, String installation_folder_path){
        corpus_path = corpus_folder; 
        index_path = installation_folder_path;
        System.out.println(index_path+"  corpus path:  "+corpus_folder);
//        System.out.println(source_directory +"\\statistics.kh");
    }
    
    private ArrayList listFileInFolder(final File folder, ArrayList<File> List_of_Files)
        throws IOException
    {
        for (final File fileEntry : folder.listFiles())
	    {
	        if (fileEntry.isDirectory())
	        {
	            listFileInFolder(fileEntry, List_of_Files);
	        } 
	        else
	        {
	        	List_of_Files.add(fileEntry);
	        }
	    }
	    total_no_of_docs = List_of_Files.size();
	    return List_of_Files;
    
    }

    /**
     *
     * @return 
     * @throws IOException
     */
    public boolean crawl()
            throws IOException
    {
        inverted_index = new ConcurrentHashMap<>();
        // create a file for 
        File source_folder = new File(corpus_path);
        ArrayList <File> List_of_Files_in_folder = new ArrayList<>();
        listFileInFolder(source_folder, List_of_Files_in_folder);   
        for (int i = 0; i < List_of_Files_in_folder.size(); i++) {
            /* CurFilePath contains the file path(in String) of *this file of List_of_Files_in_folder
             * getAbsolutePath() is a method of Class File
             * and get(int index) is the method of class List
             * */
            String CurFilePath = (List_of_Files_in_folder.get(i)).getAbsolutePath();

            /*	here I am passing Index and file-path to add_to_Index() 
             * and add_to_Index() will append the Index (creates the Index for *this file)
             * */
            add_to_Index(CurFilePath, inverted_index);
        }
        
        /*
        *Crawling is done. so we do Serialization of Index into index_path
        */
//        Statistics current_crawled_data = new Statistics(total_no_of_docs, inverted_index, doc_length_table);
        try {
            // save inverted index into disk as the serialized object
            FileOutputStream fileOut1 = new FileOutputStream(index_path+"\\index.kh");
            ObjectOutputStream index_out = new ObjectOutputStream(fileOut1);
            index_out.writeObject(inverted_index);
            index_out.close();
            fileOut1.close();
            // save doc_length_table into disk as the serialized object
            FileOutputStream fileOut2 = new FileOutputStream(index_path+"\\docLenTable.kh");
            ObjectOutputStream doc_len_table_out = new ObjectOutputStream(fileOut2);
            doc_len_table_out.writeObject(doc_length_table);
            doc_len_table_out.close();
            fileOut2.close();
            // save total_no_of_documents into a file as a serialized object
            FileOutputStream fileOut3 = new FileOutputStream(index_path+"\\numOfDocs.kh");
            ObjectOutputStream num_of_docs_out = new ObjectOutputStream(fileOut3);
            num_of_docs_out.writeObject(total_no_of_docs);
            num_of_docs_out.close();
            fileOut3.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void add_to_Index(String CurFilePath, ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> inverted_index)
            throws IOException
    {
        if(CurFilePath.endsWith("txt"))
        {
            System.out.println("reading .txt file");
            String string_containing_line;
            String[] str_arr;
            BufferedReader in = new BufferedReader(new FileReader(CurFilePath));
            int total_words = 0;
            while ((string_containing_line = in.readLine()) != null) 
            {
                str_arr = (string_containing_line.toLowerCase().split("[^a-z]"));
                int terms_in_line = str_arr.length;
                total_words = total_words + terms_in_line;

                for (int i = 0; i < str_arr.length; i++) {
                    String term = new String(str_arr[i]);
                    if (inverted_index.containsKey(term)) // is this term present in Index?
                    {
                        ConcurrentHashMap<String, Integer> Doc_hashtable;
                        Doc_hashtable = inverted_index.get(term);
                        if (inverted_index.get(term).containsKey(CurFilePath)) //is this file (value) present in Index ?
                        {
                            Integer TF_of_the_term_in_this_doc = (Integer)inverted_index.get(term).get(CurFilePath);
                            TF_of_the_term_in_this_doc ++;
                            Doc_hashtable.put(CurFilePath, TF_of_the_term_in_this_doc);
                            inverted_index.put(term, Doc_hashtable);
                        } else // this file is not present in Index
                        {
                            Integer TF_of_this_term_in_this_doc = 1;
                            Doc_hashtable.put(CurFilePath, TF_of_this_term_in_this_doc);
                            inverted_index.put(term, Doc_hashtable);

                        }
                    }
                    else // Index does not contain this term
                    {
                        ConcurrentHashMap<String, Integer> Doc_hashtable = new ConcurrentHashMap<String, Integer>();
                        Integer TF = 1;
                        Doc_hashtable.put(CurFilePath, TF);
                        inverted_index.put(term, Doc_hashtable);
                    }
                }
                doc_length_table.put(CurFilePath, new Integer(total_words));
            }
//            System.out.println(inverted_index);
        }
        else if(CurFilePath.endsWith("pdf")){
            System.out.println("reading .pdf file");
            String text = null;
            PDFdoc cur_doc = new PDFdoc();
            try {
                text = cur_doc.extractPDF(CurFilePath);
            } catch (Exception e) {
                System.err.println();
            }
            String[] tokens_arr;
            if(text== null)
            {   
                System.out.println("problem to read pdf");
                return;                
            }
            tokens_arr = text.toLowerCase().split("[^a-z]");
            int total_words = tokens_arr.length;
            for (String tokens_arr1 : tokens_arr) {
                String term = tokens_arr1;
                if (inverted_index.containsKey(term)) // is this term present in Index?
                {
                    ConcurrentHashMap<String, Integer> Doc_hashtable;
                    Doc_hashtable = inverted_index.get(term);
                    if (inverted_index.get(term).containsKey(CurFilePath)) //is this file (value) present in Index ?
                    {
                        Integer TF_of_the_term_in_this_doc = (Integer)inverted_index.get(term).get(CurFilePath);
                        TF_of_the_term_in_this_doc ++;
                        Doc_hashtable.put(CurFilePath, TF_of_the_term_in_this_doc);
                        inverted_index.put(term, Doc_hashtable);
                    }
                    else // Index does not contain this term
                    {
                        Integer TF_of_this_term_in_this_doc = 1;
                        Doc_hashtable.put(CurFilePath, TF_of_this_term_in_this_doc);
                        inverted_index.put(term, Doc_hashtable);
                        
                    }
                }
                else // Index does not contain this term
                {
                    ConcurrentHashMap<String, Integer> Doc_hashtable = new ConcurrentHashMap<>();
                    Integer TF = 1;
                    Doc_hashtable.put(CurFilePath, TF);
                    inverted_index.put(term, Doc_hashtable);
                }
            }
//            System.out.println(inverted_index);
            doc_length_table.put(CurFilePath, new Integer(total_words));
        
        }
        else
            System.out.println("file format not supported");
    }
}
