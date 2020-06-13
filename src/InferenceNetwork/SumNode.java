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
public class SumNode extends BeliefNode {
    
    public SumNode(ArrayList<? extends QueryNode> c)
    {
        super(c);
    }
    
    public double score(long DocId)
    {
        return Math.log((children.stream().mapToDouble(c -> Math.exp(c.score(DocId))).sum())/children.size());
    }
}
