package InferenceNetwork;

import InferenceNetwork.QueryNode;
import InferenceNetwork.ProximityNode;
import InfoRetrievalEngine.Posting;
import InfoRetrievalEngine.PostingsList;
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
public class OrderedWindow extends ProximityNode {
    
    int WindowSize;
    
    OrderedWindow(int WindowSize, ArrayList<? extends QueryNode> children)
    {
        this.WindowSize = WindowSize;
        pl = new PostingsList();            // Postings List for the window
        createPostingsList(children);
    }
    
    void createPostingsList(ArrayList<? extends QueryNode> children)
    {
        QueryNode qn = children.get(0);
        PostingsList TraversePl = ((ProximityNode)children.get(0)).pl;      // Traverse the pl of the first word

        while(TraversePl.hasMore())
        {
            long curDoc = qn.nextCandidate();                               // Get next doc of first word

            boolean SameDoc = true;
            
            for(QueryNode child : children)                                 // Ask every child to skip to current doc
            {
                child.skipTo(curDoc);
                if(((ProximityNode)child).getCurDoc() != curDoc)
                {
                    SameDoc = false;                                        // If the current doc doesn't exist in a child, i.e, window can't exist
                    break;
                }
            }
            
            if(SameDoc == true)                                             // If all children are on the same doc
            {
                Posting post = TraversePl.getCurPosting();
                
                for(int nextId : post.positions)                            // Traverse over all positions of the current doc posting
                {
                    boolean flag = true;                                    // Flag to check if all children have the correct position in the window
                    int i = nextId;
                    for(int x=1; x<children.size(); x++)                    
                    {
                        QueryNode child = children.get(x);

                        if(((ProximityNode)child).pl.getCurPosting().hasPosition(i+WindowSize)) // If child has the correct position in the window
                            i += WindowSize;
                        
                        else
                            flag = false;
                    }
                    
                    if(flag == true)
                        pl.add(curDoc, nextId);
                }
            }
            
            qn.skipTo(curDoc + 1);              // Skip to next document of the first child
            
        }
        
        ctf = pl.getCtf();
    }
}
