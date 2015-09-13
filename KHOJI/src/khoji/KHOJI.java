/*
 * Copyright 2014 Mohd Azeem.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package khoji;

import gui.AboutFrame;
import gui.ConfirmCrawlingFrame;
import gui.InstallationForm;
import gui.SearchQueryFrame;
import gui.ShowResultFrame;
import gui.WelcomeAgainFrame;
import gui.askCorpusPathFrame;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.plaf.ProgressBarUI;

/**
 *
 * @author Mohd Azeem
 */
public class KHOJI {

    static final String installation_folder_path = "C:\\Khoji";
    static String corpus_path;

    private static void startKhoji() {
        File installation_folder = new File(installation_folder_path);
        if (installation_folder.exists()) {
            // render welcome again window
            WelcomeAgainFrame.run_frame();
        } else {
            // render installation window
            InstallationForm.run_frame();
        }
    }

    /*  controller which is called by the view 'InstallaationForm'.
     * This controller creates the directory 'Khoji' then renders next view to ask for crawling
     */
    public static void installButtonController() {
        File installation_folder = new File(installation_folder_path);
        installation_folder.mkdir();
        WelcomeAgainFrame.run_frame();
    }

    public static void homeButtonController() {
        WelcomeAgainFrame.run_frame();
    }

    //    controller which provides the information about khoji project;
    //    this method is called by the view when 'about' button is clicked
    public static void aboutButtonController() {
        AboutFrame.runFrame();
    }

    //    This controller renders the askCorpusPath form to get the path of corpus
    public static void runButtonController() {
        File index_folder = new File(installation_folder_path + "\\index.kh");
        if (index_folder.exists()) {
            ConfirmCrawlingFrame.runFrame();
        } else {
            askCorpusPathFrame.runFrame();
        }
    }

    // takes the use response for crawling from ConfirmCrawlingFrame
    public static void ConfirmCrawlingController(boolean status) {
        if (status == true) {
            askCorpusPathFrame.runFrame();
        } else // do not crawl, just search. render SearchQueryForm
        {
            SearchQueryFrame.runFrame();
        }

    }

    //  This controller starts crawling by takin the corpus path from askCorpusPathFrame as string
    public static void goCrawlButtonController(String path) {
        // crawl
        System.out.println("corpus path in controller:" + path);
        Crawler my_crawler = new Crawler(path, installation_folder_path);
        try {
            if (my_crawler.crawl()) {
                JOptionPane.showMessageDialog(null, "Crawling completed successfully!");
                SearchQueryFrame.runFrame();
            } else {
                JOptionPane.showMessageDialog(new askCorpusPathFrame(), "There is any error to crawl this directory", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            Logger.getLogger(KHOJI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // controller which gets query string from the view and retrieving
    public static void queryController(String query) throws ClassNotFoundException {
        ProgressBarUI progress_bar;
        ConcurrentHashMap<String, Float> results_table;
        try {
            Retriever my_query_retriever = new Retriever(query, installation_folder_path);            
            results_table = my_query_retriever.Retrieve();            
            ShowResultFrame.runFrame(DataManipulator.getRankedDocuments(results_table));
            

//            String first_ranked_doc = new String();
////            String second_ranked_docs = new String();
////            String third_ranked_docs = new String();
//            Object[] weights_array = results_table.values().toArray();
//            Arrays.sort(weights_array);
//            int num_of_results = weights_array.length;
//            // sort the results based on the weights and save it in a array according to their rank
//            for (Map.Entry<String, Float> entry : results_table.entrySet()) {
//                if (weights_array[num_of_results - 1] == entry.getValue()) {
//                    first_ranked_doc = entry.getKey();
//                }
//                
//            }
//            System.out.println("1st rank result is   " + first_ranked_doc);
//            System.out.println(rankWiseDocsArray);
//            Object[] results_weights_array =  results_table.values().toArray();
//            Arrays.sort(results_weights_array);
//            int num_of_results = results_weights_array.length;
//            ArrayList top_results = new ArrayList();
//            System.out.println("weight[-1]:  "+results_weights_array[results_weights_array.length-1]);
//            System.out.println("weight[-1]:  "+results_weights_array[results_weights_array.length-2]);
//            for (int i= num_of_results-1; i > 0; i--) {
//                Object val = results_weights_array[i];
//                System.out.println("hey3");
//                top_results.add(0, results_table.);
//                System.out.println("weight:  "+results_weights_array[i]);
        } catch (IOException ex) {
            Logger.getLogger(KHOJI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        // render startKhoji
        startKhoji();
    }
}
