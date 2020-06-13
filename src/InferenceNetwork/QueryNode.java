package InferenceNetwork;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mini
 */
public interface QueryNode {
    
    long nextCandidate();
    
    boolean hasMore();
    
    void skipTo(long DocId);
    
    double score(long DocId);
    
}
