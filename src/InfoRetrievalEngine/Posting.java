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
import java.util.*;

// Class to create a single posting in the index

public class Posting
{
    long docid;
    int count=0;            // Count of 1 word in a particular document
    int curPos = 0;         // Pointer for current position
    public ArrayList<Integer> positions = new ArrayList<Integer>();
    
    Posting()
    {}
   
    Posting(long docid, int position)
    {
        this.docid=docid;
        count++;
        positions.add(position);
    }
    
    public long getDocId()
    {
        return docid;
    }
    
    public int getCount()
    {
        return count;
    }
    
    
    
    public boolean hasPosition(int pos)
    {
       for(int i=0; i<positions.size(); i++)
       {
           if(positions.get(i) == pos)
               return true;
       }
       
       return false;
    }
    
    
    public int getCurPosition()                         // Return the current posting
    {
        int position = -1;
        try
        {
            position = positions.get(curPos);
        } catch(Exception e) {}
        
        return position;
    }
    
    public void skipTo(int pos)
    {    
        for (int i = curPos; i < positions.size(); i++)
        {
            if (positions.get(i) < pos)
            {
               curPos++;
               continue;
            } 
            else
                break;
        }
    }
    
    public boolean hasMore()
    {
       return (curPos >=0 && curPos < positions.size());
    }
    
    void print()
    {
        System.out.print("Doc ID = "+ docid +" Positions = ");
        for(int x=0; x<positions.size(); x++)
        {
            System.out.print(positions.get(x)+" ");
        }
        System.out.print("Count = "+count);
        
    }
}
