/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryIndFeatures;

import InferenceNetwork.QueryNode;
import InferenceNetwork.QueryNode;
import InfoRetrievalEngine.CreateIndex;

/**
 *
 * @author mini
 */
public class PriorNode implements QueryNode
{
   
    private String name;
    private CreateIndex index;
    
    public PriorNode(String name, CreateIndex index)
    {
        this.name = name;
        this.index = index;
    }

    @Override
    public boolean hasMore()
    {
        return false;
    }

    @Override
    public long nextCandidate()
    {
        return -1;
    }
    
    @Override
    public void skipTo(long docId)
    {
        return;
    }
    
    @Override
    public double score(long docId)
    {
        Double score = index.getPrior(this.name, docId);
        return score;
    }
}