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
public class FilterRequire extends Filter {
    
    public FilterRequire(ProximityNode PN, QueryNode Q)
    {
        super(PN, Q);
    }
    
    @Override
    public long nextCandidate()
    {
        return Math.max(filter.nextCandidate(), query.nextCandidate());
    }
    
    @Override
    public double score(long DocId)
    {
        if (filter.getCurDoc() == DocId)
            return query.score(DocId);
        else 
            return 0;
        
    }
    
}
