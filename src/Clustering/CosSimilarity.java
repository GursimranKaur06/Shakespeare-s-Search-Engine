package Clustering;


import InfoRetrievalEngine.PostingsList;
import InfoRetrievalEngine.CreateIndex;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mini
 */
public class CosSimilarity extends SimilarityMethods {
    
    Map<String,Double> idf;                                     // Map from term to it's idf weight
    
    
    CosSimilarity(CreateIndex index)                            // Create idf weights map
    {
        idf = new HashMap<String, Double>();
        int N = index.getTotalDocs();                           // Total docs in the corpus
        
        for(Map.Entry<String, PostingsList> entry : index.InvertedIndex.entrySet())         // Iterate over all unique terms in the corpus
        {
            double ni = entry.getValue().getDtf();
            double TermIdf = Math.log((N+1.0) / (ni+0.5));
            idf.put(entry.getKey(), TermIdf);
        }
    }
    
    
    @Override
    double score(Map<String, Double> Dv1, Map<String, Double> Dv2)
    {
        Set<String> TermsUnion = new HashSet<>();                    // Union of terms in both doc vectors
        
        double Num = 0.0, Den1 = 0.0, Den2 = 0.0;
        
        TermsUnion.addAll(Dv1.keySet());                             // Add terms of doc vecs to set
        TermsUnion.addAll(Dv2.keySet());      
        
        for(String term : TermsUnion)
        {
            Double TermIdf = idf.get(term);
            double tf1, tf2;
            
            tf1 = Dv1.getOrDefault(term, 0.0);                      // Freq of term in doc vec 1
            tf2 = Dv2.getOrDefault(term, 0.0);                      // Freq of term in doc vec 2
            
            Num  += tf1 * tf2 * TermIdf * TermIdf;
            Den1 += tf1 * tf1 * TermIdf * TermIdf;
            Den2 += tf2 * tf2 * TermIdf * TermIdf;
        }
        return Num/(Math.sqrt(Den1 * Den2));
    }
}
