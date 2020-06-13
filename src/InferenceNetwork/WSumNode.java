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
public class WSumNode extends BeliefNode {
    
    ArrayList<Double> weights;

    public WSumNode(ArrayList<? extends QueryNode> children, ArrayList<Double> weights)
    {
        super(children);
        this.weights = new ArrayList<Double>(weights);
    }
    
    @Override
    public double score(long DocId)
    {
        Double score = 0.0;
        Double weight = 1.0;
        Double TotalWeight = 0.0;
        
        for (QueryNode child: children)
        {
            score += weight * Math.exp(child.score(DocId));
            TotalWeight = TotalWeight + weight;
        }
        
        Double wsum = score / TotalWeight;
        return Math.log(wsum);
    }
}
