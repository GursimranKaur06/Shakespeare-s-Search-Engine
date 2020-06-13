package InferenceNetwork;

import InferenceNetwork.QueryNode;
import InferenceNetwork.BeliefNode;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mini
 */
public class OrNode extends BeliefNode{
    
    public OrNode(ArrayList<? extends QueryNode> c)
    {
        super(c);
    }
    
    @Override
    public double score(long DocId)
    {
        double Score = 1.0, NotScore = 0.0;
        
        for(QueryNode child : children)
        {
            NotScore = 1 - (Math.exp(child.score(DocId)));
            Score *= NotScore;
        }
        
        return Math.log(1-Score);
    }
    
}
