package InferenceNetwork;

import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Map;
import java.util.AbstractMap;
import java.util.Comparator;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mini
 */
public class InferenceNetwork {
    
    
    public ArrayList <Map.Entry<Integer, Double>> runQuery(QueryNode QNode, int K) throws java.io.IOException
    {
        PriorityQueue<Map.Entry<Integer, Double>> result = new PriorityQueue<>(Map.Entry.<Integer, Double>comparingByValue());
        
        ProximityNode.index.loadMaps();
        
        while (QNode.hasMore())
        {
            int docId = ((int)QNode.nextCandidate());           // Get the next doc to score
            
            QNode.skipTo(docId);                                // Ask all children to skip to document that isbeing scored
            
            Double curScore = QNode.score(docId);               // Get the combined score for the document
            
            if (curScore != null)
            {
                result.add(new AbstractMap.SimpleEntry<Integer, Double>(docId, curScore));      // Add document and score to results
                if (result.size() > K)
                {
                    result.poll();
                }
            }
            QNode.skipTo(docId+1);                              // Ask all children to move their pointer to the next document
        }
        
        ArrayList<Map.Entry<Integer, Double>> scores = new ArrayList<Map.Entry<Integer, Double>>();
        scores.addAll(result);
        scores.sort(Map.Entry.<Integer, Double>comparingByValue(Comparator.reverseOrder()));    // Arrange scores in descending order
        return scores;
    }
    
}
