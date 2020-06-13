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
public abstract class Filter implements QueryNode {
    
    QueryNode query = null;
    ProximityNode filter;
    
    public Filter(ProximityNode PN, QueryNode Q)
    {
        filter = PN;
        query = Q;
    }
    
    @Override
    public boolean hasMore()
    {
        return query.hasMore();
    }
    
    @Override
    public void skipTo(long DocId)
    {
        filter.skipTo(DocId);
        query.score(DocId);
    }
    
}
