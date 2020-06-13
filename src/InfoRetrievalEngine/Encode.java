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
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;



public class Encode
{
    
    
    Integer[] deltaEncode(PostingsList post)
    {
        ArrayList<Integer> record = new ArrayList();                // Delta encoded array list
        int prevdoc = 0, prevpost;
        for(int i=0; i<post.plist.size(); i++)                      // For each posting in posting list
        {
            record.add((int)post.plist.get(i).docid-prevdoc);       // Add current docid-prev docid to array
            record.add(post.plist.get(i).count);                    // Add count to array list
            
            prevpost=0;
            for(int j=0; j<post.plist.get(i).positions.size(); j++)
            {
                record.add(post.plist.get(i).positions.get(j)-prevpost);    // Add current pos-prev pos to array list
                prevpost=post.plist.get(i).positions.get(j);
            }
            
            prevdoc=(int)post.plist.get(i).docid;
        }
        Integer[] RecordArray = new Integer[record.size()];

        RecordArray = record.toArray(RecordArray);
        //System.out.print("Integer array is : ");
        /*for(int k=0; k<record.size(); k++)
        {
            System.out.print(RecordArray[k]+" ");
        }
        System.out.println(" CTF is : "+ctf);*/
        

        return RecordArray;
    }
    
    
    
    PostingsList deltaDecode(ArrayList<Integer> arraylist)
    {
        PostingsList NewPlObj = new PostingsList();
        int prevdoc=0, prevpos=0, i=0, docid=0, count=0, currentpos=0;
        
        while(i<arraylist.size())                      
        {
            prevpos = 0;
            docid = arraylist.get(i) + prevdoc;             // Delta decode docids
            prevdoc = docid;
            
            i++;
            count = arraylist.get(i);
            
            i++;
            for(int j=0; j<count; j++)
            {
                currentpos = arraylist.get(i) + prevpos;    // Delta decode positions
                i++;
                NewPlObj.add(docid, currentpos);            // Add data to new postings list
                prevpos += currentpos;
            }
            
        }
        
        //NewPlObj.print();
        return NewPlObj;
    }
    
    
    
    byte[] vbyteEncode(Integer[] wordarray)
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream(); 
		for(int i:wordarray)
		{
                    while(i>=128)
                    {
                            output.write((byte)i & 0x7F);
                            i>>>=7;

                    }
                    output.write((byte)i|0x80);
		}
		byte [] bytearray = output.toByteArray();
        
        return bytearray;
    }
    
    
    
    ArrayList<Integer> vbyteDecode(byte[] bytearray)
    {
	ArrayList<Integer> output = new ArrayList<Integer>();
	for(int i =0; i<bytearray.length; i++)
	{
            int position = 0;
            int result = ((int)bytearray[i] & 0x7F);
            while ((bytearray[i] & 0x80) == 0)
            {
		i++;
		position++;
		int unsignedByte = ((int)bytearray[i] & 0x7F);
		result |= (unsignedByte <<(7*position));
       	    }
		output.add(result);
	}
	return output;
    }
    
    
    PostingsList readUncompressedArray(byte[] bytearray, int numbytes) throws Exception
    {
        PostingsList NewPlObj = new PostingsList();
        int off = 0;

        while(off < numbytes)
        {
            Posting posting = new Posting();
            posting.docid = fromByteArray(Arrays.copyOfRange(bytearray, off, off+4));
            off += 4;
            int tf = fromByteArray(Arrays.copyOfRange(bytearray, off, off+4));
            posting.count = tf;
            off += 4;
            
            for(int i=0; i<tf; i++)
            {
                posting.positions.add(fromByteArray(Arrays.copyOfRange(bytearray, off, off+4)));
                off += 4;
            }
            
            NewPlObj.plist.add(posting);

        }
        
           //NewPlObj.print();
           
        return NewPlObj;
    }
    
    int fromByteArray(byte[] bytes)
    {
    return ByteBuffer.wrap(bytes).getInt();
    }

    
    
    
}
