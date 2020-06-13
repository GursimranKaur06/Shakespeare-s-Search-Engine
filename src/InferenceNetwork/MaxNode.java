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
public class MaxNode extends BeliefNode {
    
    public MaxNode(ArrayList<? extends QueryNode> c)
    {
        super(c);
    }
    
    @Override
    public double score(long DocId)
    {
        return children.stream().mapToDouble(c -> c.score(DocId)).max().getAsDouble();
    }
    
    
}
