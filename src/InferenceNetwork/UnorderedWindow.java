package InferenceNetwork;

import InferenceNetwork.QueryNode;
import InferenceNetwork.ProximityNode;
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
public class UnorderedWindow extends ProximityNode {
    
    int WindowSize;
    
    UnorderedWindow(int WindowSize, ArrayList<? extends QueryNode> children)
    {
        this.WindowSize = WindowSize;
        pl = new PostingsList();
        createPostingsList(children);
    }
    
    void createPostingsList(ArrayList<? extends QueryNode> children)
    {
        int min = Integer.MAX_VALUE;
        PostingsList TraversePl = new PostingsList();
        QueryNode qn = children.get(0);
        for(QueryNode child : children)
        {
            if(((ProximityNode)child).getPlSize() < min)
            {
                min = ((ProximityNode)child).getPlSize();
                TraversePl = ((ProximityNode)child).pl;             // Traverse the pl with the least number of docs
                qn = child;                                         // Save the child node
            }
        }
        
        while(TraversePl.hasMore())
        {
            long curDoc = qn.nextCandidate();                       // Get current document of traversing pl

            boolean SameDoc = true;
            
            for(QueryNode child : children)                         // Ask all children to skip to the current document
            {
                child.skipTo(curDoc);
                if(((ProximityNode)child).getCurDoc() != curDoc)
                {
                    SameDoc = false;                                // If any child doens't have that document, i.e, window can't exist
                    break;
                }
            }
            
            
            if(SameDoc == true)                                     // If all children are on the same document
            {
                boolean PosEnd = false;                             // To keep track whether any child's positions are exhausted
                while(PosEnd == false)
                {
                    int MinPos = Integer.MAX_VALUE;
                
                    int MaxPos = -1;
                    QueryNode minNode = null;
                    ArrayList<Integer> visited = new ArrayList<Integer>();      // To keep track of visited positions for a window

                    for(QueryNode child : children)
                    {
                        int pos = ((ProximityNode)child).pl.getCurPosting().getCurPosition();
                        if(visited.contains(pos))
                        {
                            ((ProximityNode)child).pl.getCurPosting().skipTo(pos+1);        // If position has been visited, get the next position
                            pos = ((ProximityNode)child).pl.getCurPosting().getCurPosition();
                        }
                        else
                            visited.add(pos);
                        
                        if(pos < MinPos)
                        {
                            MinPos = pos;
                            minNode = child;                                                // Keep track of posting that has window's starting position
                        }
                        if(pos > MaxPos)
                            MaxPos = pos;
                    }

                    if((MaxPos - MinPos + 1) <= WindowSize)                                 // If the window observed is a valid window
                    {
                        pl.add(curDoc, MinPos);
                        for(QueryNode child : children)
                        {
                            ((ProximityNode)child).pl.getCurPosting().skipTo(MinPos+1);     // Ask all children to skip to next position
                            if(((ProximityNode)child).pl.getCurPosting().hasMore() == false)
                                PosEnd = true;
                        }
                    }
                    else
                    {
                        ((ProximityNode)minNode).pl.getCurPosting().skipTo(MinPos+1);       // Ask the min position's posting to skip to next position
                        if(((ProximityNode)minNode).pl.getCurPosting().hasMore() == false)
                                PosEnd = true;
                    }
                }
                
                
            }
            
            qn.skipTo(curDoc + 1);          // Skip the traversing PL to next doc
        }
        
        ctf = pl.getCtf();
    }
    
}
