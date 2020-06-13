package Clustering;

import InfoRetrievalEngine.CreateIndex;
import java.io.File;
import java.io.FileWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Scanner;

/**
 *
 * @author mini
 */
public class Clustering {
    
    public static void main(String[] args) throws java.io.IOException, java.io.FileNotFoundException
    {
        CreateIndex index = new CreateIndex();
        index.parseFile();                                    // To create the inverted index
        index.loadMaps();                                     // To load document vectors to map from file
        int TotalDocs = index.getTotalDocs();
        Map<Integer, Cluster> Clusters = new HashMap<>();     // Map from cluster id to cluster

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the type of Linkage - Single, Complete, Average, Mean");
        Linkage linkage = Linkage.valueOf(sc.nextLine());     // Get linkage type from user    
         
        System.out.println("Enter the threshold value");
        Double threshold = sc.nextDouble();                   // Get threshold value from user
        
        int ClusterId = 0;
        SimilarityMethods CosSim = new CosSimilarity(index);
        
        for(long DocId = 0; DocId<TotalDocs; DocId++)
        {
            double score = 0.0;
            int BestCluster = -1;
            
            for(Cluster cluster : Clusters.values())         // Calculate the most similar cluster for the doc
            {
                int CId = cluster.getId();
                double CurScore = cluster.score(index.getDocVec(DocId));
                
                if(CurScore > score)
                {
                    score = CurScore;
                    BestCluster = CId;
                }
            }
            
            if(score > threshold)                            // Add doc to best matching cluster if score is above threshold
            {
                Clusters.get(BestCluster).add(DocId, index.getDocVec(DocId));
            }
            
            else                                             // Make a new cluster with the doc
            {
                ClusterId++;                                
                Cluster c = new Cluster(ClusterId, CosSim, linkage);    // Create a new cluster
                c.add(DocId, index.getDocVec(DocId));                   // Add doc to new cluster
                Clusters.put(ClusterId, c);                             // Add cluster to map of clusters
                
            }
            
        }
        
        Map<Long, String> SceneIds = index.getSceneIds();
        TreeMap<Integer, Cluster> sorted = new TreeMap<>(Clusters);     // Sort the clusters based on ids
        
        File file = new File("Cluster-<"+threshold+">.out");            // Write clusters to file
        FileWriter writer = new FileWriter(file, false);
        
        for (Map.Entry<Integer, Cluster> entry : sorted.entrySet())
        {
            for(Long doc : entry.getValue().getDocIds())
            {
                writer.write(entry.getValue().getId() + "     " + SceneIds.get(doc) + "\n");
            }

        }
        writer.flush();
        writer.close();
        
        System.out.println("no of clusters "+Clusters.size());
        Clusters.keySet().stream().sorted().forEach((CId) -> {
            Cluster c = Clusters.get(CId);
            c.getDocIds().forEach((DId) -> System.out.println(c.getId() + " " + SceneIds.get(DId)));
        });
    }
}
