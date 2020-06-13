package InferenceNetwork;

import InferenceNetwork.ProximityNode;
import InfoRetrievalEngine.ReadFromDisk;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mini
 */
public class TermNode extends ProximityNode{
    
    String name;                // Name of the query word
    
    public TermNode(String name) throws java.io.IOException
    {
        pl = getpl.searchLookupTable(name);         // Get the postings list of the word from index
        ctf = pl.getCtf();
    } 
    
}
