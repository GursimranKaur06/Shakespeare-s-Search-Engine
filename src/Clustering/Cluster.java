package Clustering;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mini
 */
public class Cluster {
    
    int ClusterId;
    Map<Long, Map<String, Double>> DocVecs;         // Map of docs in the cluster
    Linkage linkage;
    SimilarityMethods Similarity;
    
    Cluster(int ClusterId, SimilarityMethods Similarity, Linkage linkage)
    {
        DocVecs = new HashMap<Long, Map<String, Double>>();
        this.ClusterId = ClusterId;
        this.Similarity = Similarity;
        this.linkage = linkage;
    }
    
    public int getId()
    {
        return ClusterId;
    }
    
    public Set<Long> getDocIds()                        // Get set of all doc ids in the cluster
    {
        return DocVecs.keySet();
    }
    
    void add(long DocId, Map<String, Double> DV)        // Add a document vector to the cluster
    {
        DocVecs.put(DocId, DV);
    }
    
    public double score(Map<String, Double> DV)         // Get the score of a document for the cluster 
    {
         switch(linkage)
         {
             case Single:                               // Match new doc with most similar doc
             {
                 double max = -99.9;
                 for(Map<String, Double> DocVec : DocVecs.values())
                 {
                     double score = Similarity.score(DocVec, DV);
                     if(score > max)
                         max = score;
                 }
               return max;
             }
             
             case Complete:                             // Match new doc with least similar doc
             {
                 double min = 99.9;
                 for(Map<String, Double> DocVec : DocVecs.values())
                 {
                     double score = Similarity.score(DocVec, DV);
                     if(score < min)
                         min = score;
                 }
               return min;
             }
             
             case Average:                              // Sum of similarities / Total num of docs in cluster
             {
                 double sum = 0.0;
                 for(Map<String, Double> DocVec : DocVecs.values())
                 {
                     sum += Similarity.score(DocVec, DV);
                 }
               return sum/DocVecs.size();
             }
             
             case Mean:                                 // Compare doc with centroid of cluster
             {
                 Map<String, Double> Centroid = new HashMap<>();
                 int TotalDocs = DocVecs.size();
                 for(Map<String, Double> DocVec : DocVecs.values())
                 {
                     for(String term : DocVec.keySet())
                     {
                         Centroid.put(term, (Centroid.getOrDefault(term, 0.0) + DocVec.get(term)));     // Union of all docs
                     }
                 }
                 
                  Centroid.replaceAll((k,v) -> v = (Centroid.get(k)/TotalDocs));                        // Count for a term = cumulative count/Total docs
                  return Similarity.score(Centroid, DV);
             }
                 
             default:
                 return -99.9;
                 
         }
    }
}
