package InfoRetrievalEngine;


import java.io.BufferedWriter;
import java.io.RandomAccessFile;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.nio.*;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mini
 */

public class WriteToDisk
{
    Map<String, Integer[]> CompLookup = new HashMap();          // Hashmap for compressed data lookup table - maps word(key) to array(value)
                                                                // Value array - ctf, dtf, offset, no. of bytes
    Map<String, Integer[]> UncompLookup = new HashMap();        // Hashmap for uncompressed data lookup table
    
    WriteToDisk(){};
    
    WriteToDisk(Map<String, PostingsList> InvertedIndex)
    {
        try
        {
            System.out.println("Do you want to encode the file? True/False");      // Take user's input for encoding choice
            Scanner sc = new Scanner(System.in);
            boolean choice = sc.nextBoolean();
            write(InvertedIndex, choice);                                          // Write to file with choice
            
        }catch (InputMismatchException e) { System.out.println("Invalid input!"); }
    
    
    } 

    
    void write(Map<String, PostingsList> InvertedIndex, boolean choice)
    {
        try
        {    
            
            Encode EncodeObj = new Encode();
                      

            if(choice==true)      // Data to be encoded - Write compressed data to a file
            {
              RandomAccessFile InvListWriter = new RandomAccessFile("CompressedIndexFile","rw");      // File to store compressed inverted index
              
              
              for(Map.Entry<String, PostingsList> row : InvertedIndex.entrySet())
              {
                  Integer[] LookList = new Integer[4];                                    // List container for value field of lookup map
                  Integer[] RecordArray = EncodeObj.deltaEncode(row.getValue());          // Delta encode postings list
                  byte[] ByteArray = EncodeObj.vbyteEncode(RecordArray);                  // Vbyte encode and get byte array

                  LookList[0] = row.getValue().getCtf();                                  // Store ctf
                  
                  LookList[1] = row.getValue().getDtf();                                  // Store dtf
                  
                  LookList[2] = (int)InvListWriter.getFilePointer();                      // Store offset
                  
                  LookList[3] = ByteArray.length;                                         // Store number of bytes
                  
                  
                  InvListWriter.write(ByteArray);                                         // Write bytes to file

                  CompLookup.put(row.getKey(), LookList);                                 // Add lookup data for word to CompLookup table  
                  
              }
              
              File CompLook = new File("CompressedLookUpTable");                // File to store Compressed Lookup Table
              FileOutputStream fos = new FileOutputStream(CompLook);
              ObjectOutputStream oos=new ObjectOutputStream(fos);

              oos.writeObject(CompLookup);
              oos.flush();
              oos.close();
              fos.close();
              
              InvListWriter.close();
              
            }
            
            
            else            // Write uncompressed data to a file
            {
                
                RandomAccessFile InvListWriter = new RandomAccessFile("UncompressedIndexFile","rw");      // File to store uncompressed inverted index
                BufferedWriter writer = new BufferedWriter(new FileWriter("unlookcheck"));
                
                for(Map.Entry<String, PostingsList> row : InvertedIndex.entrySet())
                {
                    writer.write(row.getKey()+" ");

                    PostingsList pl = row.getValue();

                    ArrayList<Integer> intList = new ArrayList<>();

                    for(int i=0; i<row.getValue().getDtf(); i++) {

                    Posting posting = pl.plist.get(i);

                    intList.add((int)posting.docid);
                    intList.add(posting.count);

                    for(int pos = 0; pos<posting.count; pos++)
                    intList.add(posting.positions.get(pos));

                    }

                    long startOffset = InvListWriter.getFilePointer();

                    int totalBytes = intList.size()*4;
                    
                    Integer[] LookList = new Integer[4];          // List container for value field of lookup map
                    
                    LookList[0] = row.getValue().getCtf();
                    writer.write(LookList[0]+" ");
                    LookList[1] = row.getValue().getDtf();
                    writer.write(LookList[1]+" ");
                    LookList[2] = (int)startOffset;
                    writer.write(LookList[2]+" ");
                    LookList[3] = totalBytes;
                    writer.write(LookList[3]+"\n");

                    ByteBuffer byteBuffer = ByteBuffer.allocate(totalBytes);

                    for(int i=0; i<intList.size(); i++)
                    byteBuffer.putInt(intList.get(i));

                    InvListWriter.write(byteBuffer.array());

                    UncompLookup.put(row.getKey(), LookList);                     // Add lookup data for word to CompLookup table  

                }

                File UncompLook = new File("UncompressedLookUpTable");            // File to store Unompressed Lookup Table
                FileOutputStream fos = new FileOutputStream(UncompLook);
                ObjectOutputStream oos=new ObjectOutputStream(fos);

                oos.writeObject(UncompLookup);
                oos.flush();
                oos.close();
                fos.close(); 
                
                InvListWriter.close();

              }
        } catch (Exception e) { e.printStackTrace();}
        
    }
    
    void writeDocVectors(Map<Long, Map<String, Double>> DocVectors) throws java.io.FileNotFoundException, java.io.IOException
    {
        File DocVecFile = new File("Document Vectors");                // File to store map of document vectors
        FileOutputStream fos = new FileOutputStream(DocVecFile);
        ObjectOutputStream oos=new ObjectOutputStream(fos);

        oos.writeObject(DocVectors);
        oos.flush();
        oos.close();
        fos.close();
    }
    
}
