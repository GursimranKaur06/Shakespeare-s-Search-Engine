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
public class NotNode extends BeliefNode{
    
    public NotNode(ArrayList<? extends QueryNode> c)
    {
        super(c);
    }
    
    @Override
    public double score(long DocId)
    {
        return Math.log(1 - Math.exp(children.get(0).score(DocId)));
    }
    
}
