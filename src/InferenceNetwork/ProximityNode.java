package InferenceNetwork;

import BM25Ranking.Retrieval;
import InfoRetrievalEngine.CreateIndex;
import InfoRetrievalEngine.Posting;
import InfoRetrievalEngine.PostingsList;
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
public abstract class ProximityNode implements QueryNode{
    
    PostingsList pl;
    int ctf;                                               // CTF of new posting list of window
    
    static CreateIndex index = new CreateIndex();          // Index object to be accessed by all term nodes
    static Retrieval model = new Retrieval();              // Scoring model to use
    static ReadFromDisk getpl = new ReadFromDisk();        // Object to get posting list

    public ProximityNode()
    {}
    
    @Override
    public long nextCandidate()                             // Get the current document of the postings list
    {
        Posting posting = pl.getCurPosting();
        
        if(posting != null)
            return posting.getDocId();
        
        return -1;
    }
    
    
    @Override
    public boolean hasMore()                                // Check if the current doc pointer has not reached the end of the postings list
    {
        return pl.hasMore();
    }
    
    @Override
    public void skipTo(long DocId)                         // Go to a particular document
    {
       pl.skipTo(DocId);
    }
    
    @Override
    public double score(long DocId)                        // Score a doc with dirichlet
    {
        double score = 0, mu = 1500.0;
        try
        {
            long dl = index.getDocLength(DocId);
            long CollSize = index.getCollSize();
            Posting post = pl.getCurPosting();

            if(post !=null && post.getDocId() == DocId)
            {
                int tf = post.getCount();
                score = model.dirichlet(ctf, tf, dl, CollSize, mu);
            }
                
            else
                score = model.dirichlet(ctf, 0, dl, CollSize, mu);
            
            
        }catch (Exception e) {e.printStackTrace();}
        
        return score;
    }
    
    
    int getPlSize()
    {
        return pl.plist.size();
    }
    
    long getCurDoc()
    {
        return pl.getCurDoc();
    }
}

    