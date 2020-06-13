package InfoRetrievalEngine;


import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static jdk.nashorn.internal.objects.ArrayBufferView.buffer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mini
 */
public class ReadFromDisk
{
    int choice;
    
    public ReadFromDisk()
    {
        choice=1; // To read data as uncompressed since checking of both indices has been carried out
    }
    
    public PostingsList ReadFile(int offset, int numbytes)
    {
        Encode EncodeObject = new Encode();
        PostingsList obj = new PostingsList();
        try
        {    
            byte[] bytearray = new byte[numbytes];                                              // numbytes = number of bytes to read for that word
            if(choice==1)
            {
                int off=0;
                RandomAccessFile reader = new RandomAccessFile("UncompressedIndexFile","rw");       // Open Uncompressed Index File to read
                reader.seek(offset);                                                                // Read from where the postingslist begins
                reader.read(bytearray,0,numbytes);                                                  // Read bytes
              
                obj = EncodeObject.readUncompressedArray(bytearray, numbytes);
                reader.close();

            }
            
            else
            {
                RandomAccessFile reader = new RandomAccessFile("CompressedIndexFile","rw");         // Open Compressed Index File to read
                reader.seek(offset);                                                                // Read from where the postingslist begins
                reader.read(bytearray,0,numbytes);                                                  // Read bytes
                ArrayList<Integer> arraylist = EncodeObject.vbyteDecode(bytearray);
                obj = EncodeObject.deltaDecode(arraylist);
                //obj.print();
                reader.close();
                
            }
            
        } catch (Exception e) {  }
        
        return obj;
    }
    
    
    public PostingsList searchLookupTable(String word)
    {
        if(choice==1)
        {
           Map<String,Integer[]> unlookup = loadLookupTable(); 
           Integer[] unoffset = unlookup.get(word);
           return ReadFile(unoffset[2], unoffset[3]);
        }
        
        else
        {
           Map<String,Integer[]> colookup = loadLookupTable(); 
           Integer[] cooffset = colookup.get(word);
           return ReadFile(cooffset[2], cooffset[3]);
        }
        
    }        
         
    
    
    public Map<String, Integer[]> loadLookupTable()
    {
        String lookupfile="";
        Map<String,Integer[]> Lookup = new HashMap();
    
       if (choice==1)                               // Uncompressed data's lookup table
           lookupfile = "UncompressedLookUpTable";
       
       if (choice==2)                               // Compressed data's lookup table
           lookupfile = "CompressedLookUpTable";
       
        try
        {
            File toRead=new File(lookupfile);                   
            FileInputStream fis=new FileInputStream(toRead);
            ObjectInputStream ois=new ObjectInputStream(fis);

            Lookup = (HashMap<String,Integer[]>)ois.readObject();

            ois.close();
            fis.close();
            
       } catch(Exception e) { e.printStackTrace(); }
        
        return Lookup;
    }
    
}