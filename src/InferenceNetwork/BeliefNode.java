package InferenceNetwork;

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
public abstract class BeliefNode implements QueryNode{
    
    ArrayList<QueryNode> children;
    
    public BeliefNode(ArrayList<? extends QueryNode> c)
    {
        children = new ArrayList<QueryNode>(c);
    }
    
    @Override
    public long nextCandidate()                 // To get the next doc to score
    {
        long min = Integer.MAX_VALUE;
        for(QueryNode child : children)         // Get next candidate from all children
        {
            long thisId = child.nextCandidate();
            if(thisId != -1 && thisId < min)
                min = thisId;                   // Select minimum doc id as next candidate
        }
        return min;
    };
    
    @Override
    public boolean hasMore()                    // To check if any documents are left to score
    {
        for(QueryNode child : children)
        {
            if(child.hasMore())
                return true;                    // Return true if even 1 child has an unscored document
        }
        
        return false;
    }
    
    @Override
    public void skipTo(long DocId)              // Skip to the document that is being scored currently
    {
        for(QueryNode child : children)
        {
            child.skipTo(DocId);
        }
    
    };
    
    
}
