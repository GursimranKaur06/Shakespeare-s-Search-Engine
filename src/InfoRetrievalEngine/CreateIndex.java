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
//import java.util.zip.GZIPInputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.JSONTokener;
import java.io.*;
import java.util.*;




// Class to create the inverted index

public class CreateIndex
{
    public Map<String, PostingsList> InvertedIndex = new HashMap();        // Map from words to posting list for inverted index
    Map<Long, Integer> DocLengths = new HashMap<Long, Integer>();          // Map from Doc id to doc lengths
    Map<Long, Map<String, Double>> DocVectors = new HashMap<>();           // Map from Doc ids to doc vectors
    int TotalDocs;
    long avdl;
    long CollSize;
    
    
    
    public int getTotalDocs()
    {
        return TotalDocs;
    }
    
    public long getCollSize()
    {
        return CollSize;
    }
    
    
    
    public void loadMaps() throws java.io.IOException
    {
        loadDocLengths();
        calcAvdl();
        loadDocVectors();
    }
    
    
    
    void calcAvdl() throws java.io.FileNotFoundException, java.io.IOException          // Calculate average doc length for the corpus
    {
        long sum = 0;
        String line;
        
        BufferedReader reader = new BufferedReader(new FileReader("Doc Lengths.txt"));
        while ((line = reader.readLine()) != null)
        {
            String[] data = line.split(" ");
            sum += Long.parseLong(data[1]);
        }
        CollSize = sum;
        avdl = sum/TotalDocs;
        reader.close();
    }
    
    public long getAvdl()          // Get average doc length
    {
        return avdl;
    }
    
    public void loadDocLengths() throws java.io.FileNotFoundException, java.io.IOException
    {
        String line;
        
        BufferedReader reader = new BufferedReader(new FileReader("Doc Lengths.txt"));
        
        while ((line = reader.readLine()) != null)
        {
            TotalDocs++;
            String[] data = line.split(" ");
            DocLengths.put(Long.parseLong(data[0]), Integer.parseInt(data[1]));
        }
        reader.close();
        
    }
    
    public long getDocLength(long DocId) throws java.io.FileNotFoundException, java.io.IOException   // Get the length of a particular doc
    {
        
        if(DocLengths.containsKey(DocId))
        {
            return DocLengths.get(DocId);
        }
        return -1;                                           // If that document is not found in the file
    }
    
    public Map<Long, String> getSceneIds() throws java.io.FileNotFoundException, java.io.IOException
    {
        Map<Long, String> SceneIds = new HashMap<Long, String>();
        String line;
        BufferedReader reader = new BufferedReader(new FileReader("Scene Ids.txt"));
        while ((line = reader.readLine()) != null)
        {
            String[] data = line.split(" ");
            SceneIds.put(Long.parseLong(data[0]), data[1]);
        }
        reader.close();
        
         /*for(Map.Entry<Long, String> pair: SceneIds.entrySet())
        {
            System.out.println("id = "+pair.getKey()+" scene = "+pair.getValue());
        }*/
        return SceneIds;
        
    }
    
    
    void loadDocVectors()           // Load document vectors into map from file
    {
        try
        {
            File ReadDocVecs=new File("Document Vectors");                   
            FileInputStream fis=new FileInputStream(ReadDocVecs);
            ObjectInputStream ois=new ObjectInputStream(fis);

            DocVectors = (HashMap<Long, Map<String, Double>>)ois.readObject();

            ois.close();
            fis.close();
            
       } catch(Exception e) { e.printStackTrace(); }
        
    }
    
    public Map<String, Double> getDocVec(long DocId)
    {
        return DocVectors.get(DocId);
    }
    
    
    
    public Double getPrior(String type, long docId)
    {
        Double returnValue = null;
        
        if (docId < 1 || docId > getTotalDocs())
        {
            return returnValue;
        }
        
        try
        {
            RandomAccessFile reader = new RandomAccessFile(type + ".prior", "r");
            reader.seek((docId - 1) * 8);
            returnValue = reader.readDouble();
            reader.close();
            
        } catch (IOException e) {e.printStackTrace();}
        
        return returnValue;
    }
    
    
    
    public void parseFile() throws java.io.IOException
    {
        Map<Long, Map<String, Double>> DocVectors = new HashMap<>();   // Map of doc ids to respective doc vectors

        try
        {   String path="/Users/mini/NetBeansProjects/InfoRetrievalEngine/shakespeare-scenes.json";
            JSONParser jp = new JSONParser();
            JSONObject jo = (JSONObject) jp.parse(new FileReader(path));
            JSONArray jarray = (JSONArray) jo.get("corpus");
            JSONObject jobject=new JSONObject();
            
            BufferedWriter writer = new BufferedWriter(new FileWriter("Doc Lengths.txt"));
            BufferedWriter writescene = new BufferedWriter(new FileWriter("Scene Ids.txt"));


            for(int i=0;i<jarray.size();i++)
            {
                jobject = (JSONObject)jarray.get(i);
                String PlayId = (String)jobject.get("playId");
                String SceneId = (String)jobject.get("sceneId");
                long DocId = (long)jobject.get("sceneNum");
                String text = (String)jobject.get("text");
                String[] tokens = text.split("\\s+");
                
                PostingsList ExistingPl;                                        // Object to read an existing posting list
                Map<String, Double> DocVec = new HashMap<>();                   // Doc vector for current doc
                
                writer.write(Long.toString(DocId) + " " + tokens.length+ "\n"); // Write doc id and lengths to file
                writescene.write(Long.toString(DocId) + " " + SceneId + "\n");

                for (int pos=0; pos<tokens.length; pos++) 
                {
                    
                    if(!InvertedIndex.containsKey(tokens[pos]))           // Check if the word is not present in the map
                    {
                        PostingsList pl=new PostingsList(DocId, pos);     // Create a new postings list for a new word
                        InvertedIndex.put(tokens[pos], pl);               // Add the new word and it's list to the Inverted Index
                    }
                    
                    else
                    {
                        ExistingPl=InvertedIndex.get(tokens[pos]);        // Get the Postings List for the existing word
                        ExistingPl.add(DocId, pos);                       // Add the new posting to the postings list of the word
                    }
                    
                    DocVec.put(tokens[pos], DocVec.getOrDefault(tokens[pos], 0.0) + 1);   // Add the term and it's count to doc vector
                }
                
                DocVectors.put(DocId, DocVec);
            }
            writer.close();
            writescene.close();
            
            WriteToDisk ob2 = new WriteToDisk();
            ob2.writeDocVectors(DocVectors);                // Write doc vectors to index
            
         } catch(Exception e) {e.printStackTrace();}
    } 
}
