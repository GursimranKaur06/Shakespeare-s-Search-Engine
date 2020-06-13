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
public class WAndNode extends BeliefNode {
    
    ArrayList<Double> weights;
    
    public WAndNode(ArrayList<? extends QueryNode> children, ArrayList<Double> weights)
    {
        super(children);
        this.weights = new ArrayList<Double>(weights);
    }

    @Override
    public double score(long DocId)
    {
        double score = 0.0;
        int i = 0;
        for (QueryNode child: children)
        {
            score += weights.get(i) * child.score(DocId);
        }
        return score;
    }

}
