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
public class AndNode extends BeliefNode{
    
    public AndNode(ArrayList<? extends QueryNode> c)
    {
        super(c);
    }
    
    @Override
    public double score(long DocId)
    {
        return children.stream().mapToDouble(c -> c.score(DocId)).sum();
    }
    
}
