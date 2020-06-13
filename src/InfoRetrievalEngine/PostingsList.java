package InfoRetrievalEngine;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mini
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;


// Class to create the list of postings for a single word

public class PostingsList
{
    public ArrayList<Posting> plist = new ArrayList();         //List of postings
    int ctf = 0;
    int dtf = 0;
    int i;
    int curIndex = 0;
    
    public PostingsList()
    {
    
    }
    
    public PostingsList(long docid, int pos)
    {
        Posting NewPosting=new Posting(docid,pos);      // Automatically create new posting when a new postings list is created 
        plist.add(NewPosting); 
        //curIndex++;
    }
    
    
    
    public void add(long docid, int pos)                       // Add a posting to already existing list
    {
        if(check(docid))                                // Check if a posting with docid is already present
        {
            plist.get(i).positions.add(pos);            // Add new position to posting's position list
            plist.get(i).count++;
        }
        else
        {
            Posting posting = new Posting(docid, pos);  // Add new position to docid posting
            plist.add(posting);
//            curIndex++;
        }
        
    }
    
    
    
    public boolean check(long docid)
    {
        //System.out.println("Doc id "+doc);
       for(i=0; i<plist.size(); i++)
       {
          if(plist.get(i).getDocId()==docid)        // If there already exists a posting with the current doc id
          {
             return true; 
          }
       }
       return false;
    }
    
    
    
    public Posting getPosting(long docId)                  // Return a posting with a specific doc id
    {
        for(i=0; i<plist.size(); i++)
       {
          if(plist.get(i).getDocId() == docId)        
          {
             return plist.get(i);
          }
       }
        
        return null;
    }
    
    public Posting getCurPosting()                         // Return the current posting
    {
        Posting post = null;
        try
        {
            post = plist.get(curIndex);
            
        } catch(IndexOutOfBoundsException e) {}
        
        return post;
    }
    
    public void skipTo(long docId)
    {    
        for (int i = curIndex; i < plist.size(); i++)
        {
            if (plist.get(i).docid < docId)
            {
               curIndex++;
               continue;
            } 
            else
                break;
        }
    }
    
    public boolean hasMore()
    {
       return (curIndex >=0 && curIndex < plist.size());
    }
    
    public long getCurDoc()
    {
        long curDoc = -1;
        try
        {
            curDoc = plist.get(curIndex).getDocId();
        } catch(IndexOutOfBoundsException e) {}
         
        return curDoc;
    }
    
    void print()
    {
        for(int j=0; j<plist.size(); j++)
        {
            plist.get(j).print();
            System.out.println();
        }
    }
    
    
    
    public int getCtf()
    {
      calcCtf(); 
      //System.out.println(" CTF = "+ctf);
      return ctf;
    }
    
    void calcCtf()
    {
        //System.out.println("size of pl is "+plist.size());
        for(int j=0; j<plist.size(); j++)
        {
            ctf+=plist.get(j).count;
        }
        
    }
    
    
    
    public int getDtf()
    {
        calcDtf();
        return dtf;
    }
    
    void calcDtf()
    {
        dtf=plist.size();
    }

    
    
}
