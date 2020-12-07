package com.example.android.popularmovies.Ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class VectorSpaceModelImp {

    private static HashMap<Integer, HashMap<String, Double>> tfDocs;
    private static HashMap<Integer, Integer> docSizeMap;
    private static HashSet<String> allTerms;
    private static HashMap<String, Integer> df;
    private static HashMap<String, Double> idf;
    private static HashMap<Integer, HashMap<String, Double>> tfIDFScores;

    private static int totalNoOfDocs = 0;

    private static ArrayList<Document> documents;



    public VectorSpaceModelImp(){
        initialize();
    }

    public static void initialize(){
        tfDocs = new HashMap<>();
        docSizeMap = new HashMap<>();
        allTerms = new HashSet<>();
        df = new HashMap<>();
        idf = new HashMap<>();
        tfIDFScores = new HashMap<>();
    }

    public static HashMap<String, Double> exec(ArrayList<Document> documents, ArrayList<String> words_pos){
        calculateTermFrequency(documents);
        calculateDocumentFrequency(documents);
        computeInverseDocumentFrequency();
        setTfIDFScores();
        HashMap<String, Double> wordTFidfscore = new HashMap<>();
        for (String s: words_pos){
            wordTFidfscore.put(s, tfIDFScores.get(0).get(s));
        }
        System.out.println(tfIDFScores);
        return wordTFidfscore;
    }

    public static void calculateTermFrequency(ArrayList<Document> documents){
        for (Document d: documents){
            HashMap<String, Double> map = new HashMap<>();
            tfDocs.put(d.getId(), map);
            TermFrequency(d);
        }
        calculateNormalizedTermFrequency();
        //System.out.println(tfDocs);
    }

    public static void calculateNormalizedTermFrequency(){
        Iterator iterator = tfDocs.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry mapElement = (Map.Entry)iterator.next();
            int docid = (int)mapElement.getKey();
            Iterator iterator1 = tfDocs.get(docid).entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry mapElement2 = (Map.Entry)iterator1.next();
                tfDocs.get(docid).put((String)mapElement2.getKey(), (tfDocs.get(docid).get(mapElement2.getKey()))/docSizeMap.get(docid));
            }
        }
    }

    public static void TermFrequency(Document document){
        String[] texts = document.getText().replaceAll("[^a-zA-Z ]", "").split(" ");
        docSizeMap.put(document.getId(), texts.length);
        for (int i = 0; i < texts.length; i++){
            allTerms.add(texts[i]);
            if (tfDocs.get(document.getId()).containsKey(texts[i])){
                tfDocs.get(document.getId()).put(texts[i], tfDocs.get(document.getId()).get(texts[i])+1);
            } else tfDocs.get(document.getId()).put(texts[i], 1.0);
        }
    }

    public static void calculateDocumentFrequency(ArrayList<Document> documents){
        Iterator iterator = allTerms.iterator();
        while (iterator.hasNext()){
            String term = (String) iterator.next();
            for (Document d: documents){
                if (checkTermPresence(d, term)){
                    if (df.containsKey(term)){
                        df.put(term, df.get(term)+1);
                    } else df.put(term, 1);
                }
            }
        }
    }

    public static boolean checkTermPresence(Document document, String term){
        boolean res = false;
        String[] terms = document.getText().replaceAll("[^a-zA-Z ]", "").split(" ");
        for (int i = 0; i < terms.length; i++){
            if (terms[i].equalsIgnoreCase(term))
            {
                res = true;
                break;
            }
        }
        return res;

    }

    public static void computeInverseDocumentFrequency(){
        Iterator iterator = allTerms.iterator();
        //System.out.println(df.get("on"));
        double no;

        while (iterator.hasNext()){
            String term = (String) iterator.next();
            no = (double)totalNoOfDocs/df.get(term);
            double idfvalue = Math.log(no)/Math.log(2);
            //System.out.println(term+"---"+idfvalue);
            idf.put(term, idfvalue);
        }
    }

    public static void setTfIDFScores(){
        Iterator iterator = tfDocs.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry mapElement = (Map.Entry)iterator.next();
            int docid = (int)mapElement.getKey();
            HashMap<String, Double> idfm = new HashMap<>();
            tfIDFScores.put(docid, idfm);
            Iterator iterator1 = tfDocs.get(docid).entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry mapElement2 = (Map.Entry)iterator1.next();
                double tfidfscore = tfDocs.get(docid).get(mapElement2.getKey()) * idf.get(mapElement2.getKey());
                tfIDFScores.get(docid).put((String)mapElement2.getKey(), tfidfscore);
            }
        }
    }

    public static String getDocText(int id){
        for (Document d: documents){
            if (d.getId() == id)
                return d.getText();
        }
        return "";
    }

    public static boolean checkPresernce(String term, String[] tms){
        boolean res = false;
        for (int i = 0; i < tms.length; i++){
            if (tms[i].equalsIgnoreCase(term)){
                res = true;
                break;
            }
        }
        return res;
    }
}
