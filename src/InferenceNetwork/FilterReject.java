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
public class FilterReject extends Filter {
    
    public FilterReject(ProximityNode PN, QueryNode Q)
    {
        super(PN, Q);
    }
    
    @Override
    public long nextCandidate()
    {
        return query.nextCandidate();
    }
    
    @Override
    public double score(long docId)
    {
        if (filter.getCurDoc() != docId)
            return query.score(docId);
        else
            return 0;
    }
    
}
