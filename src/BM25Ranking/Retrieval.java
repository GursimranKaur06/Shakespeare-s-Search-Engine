package BM25Ranking;
import InfoRetrievalEngine.ReadFromDisk;
import InfoRetrievalEngine.PostingsList;


import InfoRetrievalEngine.CreateIndex;
import InfoRetrievalEngine.Posting;
import InfoRetrievalEngine.PostingsList;
import InfoRetrievalEngine.ReadFromDisk;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


/**
 *
 * @author mini
 */
public class Retrieval {
    
    ReadFromDisk obj = new ReadFromDisk();
    Map<String, Integer[]> lookup = obj.loadLookupTable();
    
    
    void process(CreateIndex ob1) throws IOException      // To call BM25
    {
       ArrayList<String> query0 = new ArrayList<String>()
       {{
            add("the");
            add("king");
            add("queen");
            add("royalty");
        }};
       
       ArrayList<String> query1 = new ArrayList<String>()
       {{
            add("servant");
            add("guard");
            add("soldier");
        }};
       
       ArrayList<String> query2 = new ArrayList<String>()
       {{
            add("hope");
            add("dream");
            add("sleep");
        }};
       
       ArrayList<String> query3 = new ArrayList<String>()
       {{
            add("ghost");
            add("spirit");
        }};
       
       ArrayList<String> query4 = new ArrayList<String>()
       {{
            add("fool");
            add("jester");
            add("player");
        }};
       
       ArrayList<String> query5 = new ArrayList<String>()
       {{
            add("to");
            add("be");
            add("or");
            add("not");
            add("to");
            add("be");
        }};
       
       ArrayList<String> query6 = new ArrayList<String>()
       {{
            add("alas");
        }};
       
       ArrayList<String> query7 = new ArrayList<String>()
       {{
            add("alas");
            add("poor");
        }};
       
       ArrayList<String> query8 = new ArrayList<String>()
       {{
            add("alas");
            add("poor");
            add("yorick");
        }};
       
       ArrayList<String> query9 = new ArrayList<String>()
       {{
            add("antony");
            add("strumpet");
        }};
       
       ArrayList<ArrayList<String>> queries = new ArrayList<ArrayList<String>>();
       queries.add(query0);
       queries.add(query1);
       queries.add(query2);
       queries.add(query3);
       queries.add(query4);
       queries.add(query5);
       queries.add(query6);
       queries.add(query7);
       queries.add(query8);
       queries.add(query9);
       
       
       for(int i=0; i<queries.size(); i++)
       {
           retrievalBM25(queries.get(i), ob1, 10, i+1);

           jelinekSmoothing(queries.get(i), ob1, 10, i+1);

           dirichletSmoothing(queries.get(i), ob1, 10, i+1);
       }
    }
    
    double BM25(int tf, int tdf, int qtf, double k1, double k2, double K, int N)     // Calculate BM25 score for 1 query term for a document
    {
        double idf = Math.log((N - tdf + 0.5) / (tdf + 0.5));
        
        double termf = ((k1 + 1) * tf) / (tf + K);
        
        double queryf = ((k2 + 1) * qtf) / (k2 + qtf);
        
        return idf * termf * queryf;
    }
    
    double jelinekMercer(int ctf, int tf, long dl, long CollSize, double lambda)
    {
       
       double foreground = ((1.0*tf)/dl);
       
       double background = (1.0*ctf)/CollSize;
       
       double score = Math.log(((1 - lambda) * foreground) + (lambda * background));
       
       return score;
       
    }
    
    
    public double dirichlet(int ctf, int tf, long dl, long CollSize, double mu)     // Calculate Dirichlet score for 1 query term for a document
    {
        double background = (1.0*ctf)/CollSize;
        
        double score = Math.log((tf+(mu * background))/ (dl + mu));
        
        return score;
    }
    
    
    
    ArrayList<Map.Entry<Integer, Double>> retrievalBM25(ArrayList<String> words, CreateIndex ob1, int k, int QueryNumber) throws IOException
    {
        PriorityQueue<Map.Entry<Integer, Double>> result = new PriorityQueue<Map.Entry<Integer, Double>>(new Comparator<Map.Entry<Integer, Double>>()
        {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2)
            {
                if (o1.getValue() < o2.getValue()) 
                   return -1;
                else if (o1.getValue() > o2.getValue())
                   return 1;

                   return 0;
            }
        });
        
        HashMap<String, Integer> QueryWords = new HashMap<>();      // Map for Query words and their frequency
        
        double k1 = 1.2;
	double k2 = 100;
	double b = 0.75;
        int N = ob1.getTotalDocs();
        
        double avdl = ob1.getAvdl();
        
        

        for(int i=0; i<words.size(); i++)
        {
            if (!QueryWords.containsKey(words.get(i)))
            {  
                QueryWords.put(words.get(i), 1);            // Word seen for the first time
            }
            
            else
            {
                QueryWords.put(words.get(i), QueryWords.get(words.get(i)) + 1);   // Update count of word in hashmap
            }

        }
        
        /*for (String key : QueryWords.keySet()) {
			System.out.println(key);
                        System.out.println(QueryWords.get(key));
		}*/
        
        PostingsList plist = new PostingsList();
        boolean ScoreFound = false;
        for(int doc=0; doc<N; doc++)                    // Loop over all documents
        {
            ScoreFound = false;
            ob1.loadDocLengths();
            long dl = ob1.getDocLength(doc);
            long CollSize = ob1.getCollSize();
            double K = k1 * ((1 - b) + ((b * dl) / avdl));

            Double score = 0.0;
            for(String key : QueryWords.keySet())
            {
                Integer[] ex = lookup.get(key);
                plist = obj.ReadFile(ex[2], ex[3]);
                
                if(plist.check(doc) == true)
                {
                    Posting post = plist.getPosting(doc);
                    int tf = post.getCount();
                    //int ctf = plist.GetCtf();
                    score += BM25(tf, plist.getDtf(), QueryWords.get(key), k1, k2, K, N);
                    //score += jelinekMercer(plist.GetCtf(), tf, dl, CollSize);
                    ScoreFound = true;
                }
                
                else
                    continue;
            }
            
            if(ScoreFound == true)
            {
                result.add(new AbstractMap.SimpleEntry<Integer, Double>(doc, score));
            }

            if (result.size() > k)
                result.poll();
            
        }
        ArrayList<Map.Entry<Integer, Double>> scores = new ArrayList<Map.Entry<Integer, Double>>();

        for(Map.Entry<Integer, Double> pair: result)
                scores.add(pair);
        
        
        
        scores.sort(Comparator.comparing(Map.Entry::getValue));     // Sort the docs in descending order of scores
        Collections.reverse(scores);
        
        String runtag = "gursimrankau-BM25-<"+k1+">-<"+k2+">";      // Set runtag
        String FileName = "bm25.trecrun";
        trec(QueryNumber, scores, runtag, FileName, ob1);                  // Print in trec format
        
        
        return scores;
    }
    
    
    
    ArrayList<Map.Entry<Integer, Double>> jelinekSmoothing(ArrayList<String> words, CreateIndex ob1, int k, int QueryNumber) throws IOException
    {
        PriorityQueue<Map.Entry<Integer, Double>> result = new PriorityQueue<Map.Entry<Integer, Double>>(new Comparator<Map.Entry<Integer, Double>>()
        {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2)
            {
                if (o1.getValue() < o2.getValue()) 
                   return -1;
                else if (o1.getValue() > o2.getValue())
                   return 1;

                   return 0;
            }
        });
        
        int N = ob1.getTotalDocs();                     // Total number of documents
        ob1.loadDocLengths();
        //double avdl = ob1.getAvdl();                    // Average Document Length
        double lambda = 0.2;
        
        PostingsList plist = new PostingsList();
        for(int doc=0; doc<N; doc++)                    // Loop over all documents
        {
            long dl = ob1.getDocLength(doc);
            long CollSize = ob1.getCollSize();          // Collection Size

            Double score = 0.0;
            for(int i=0; i<words.size(); i++)
            {
                
                Integer[] ex = lookup.get(words.get(i));
                plist = obj.ReadFile(ex[2], ex[3]);
                
                if(plist.check(doc) == true)
                {
                    Posting post = plist.getPosting(doc);
                    int tf = post.getCount();
                    score += jelinekMercer(plist.getCtf(), tf, dl, CollSize, lambda);
                }
                
                else
                    score += jelinekMercer(plist.getCtf(), 0, dl, CollSize, lambda);
            }
            
            result.add(new AbstractMap.SimpleEntry<Integer, Double>(doc, score));

            if (result.size() > k)
                result.poll();
            
        }
        ArrayList<Map.Entry<Integer, Double>> scores = new ArrayList<Map.Entry<Integer, Double>>();

        for(Map.Entry<Integer, Double> pair: result)
                scores.add(pair);
        
        scores.sort(Comparator.comparing(Map.Entry::getValue));     // Sort the docs in descending order of scores
        Collections.reverse(scores);
        
        String runtag = "gursimrankau-ql-JM-<"+lambda+">";          // Set runtag
        String FileName = "ql-jm.trecrun";
        trec(QueryNumber, scores, runtag, FileName, ob1);           // Print in trec format
        
        return scores;
    }
    
    
    
    ArrayList<Map.Entry<Integer, Double>> dirichletSmoothing(ArrayList<String> words, CreateIndex ob1, int k, int QueryNumber) throws IOException
    {
        PriorityQueue<Map.Entry<Integer, Double>> result = new PriorityQueue<Map.Entry<Integer, Double>>(new Comparator<Map.Entry<Integer, Double>>()
        {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2)
            {
                if (o1.getValue() < o2.getValue()) 
                   return -1;
                else if (o1.getValue() > o2.getValue())
                   return 1;

                   return 0;
            }
        });
        
    
        int N = ob1.getTotalDocs();                     // Total number of documents
        //ob1.loadDocLengths();
        //double avdl = ob1.getAvdl();                    // Average Document Length
        double mu = 1500.0;

        
        PostingsList plist = new PostingsList();
        boolean ScoreFound = false;
        for(int doc=0; doc<N; doc++)                    // Loop over all documents
        {
            long dl = ob1.getDocLength(doc);
            long CollSize = ob1.getCollSize();          // Collection Size

            Double score = 0.0;
            for(int i=0; i<words.size(); i++)
            {
                Integer[] ex = lookup.get(words.get(i));
                plist = obj.ReadFile(ex[2], ex[3]);
                
                if(plist.check(doc) == true)
                {
                    Posting post = plist.getPosting(doc);
                    int tf = post.getCount();
                    score += dirichlet(plist.getCtf(), tf, dl, CollSize, mu);
                }
                
                else
                    score += dirichlet(plist.getCtf(), 0, dl, CollSize, mu);
            }

            result.add(new AbstractMap.SimpleEntry<Integer, Double>(doc, score));

            if (result.size() > k)
                result.poll();
            
        }
        ArrayList<Map.Entry<Integer, Double>> scores = new ArrayList<Map.Entry<Integer, Double>>();

        for(Map.Entry<Integer, Double> pair: result)
                scores.add(pair);
        
        scores.sort(Comparator.comparing(Map.Entry::getValue));     // Sort the docs in descending order of scores
        Collections.reverse(scores);
        
        String runtag = "gursimrankau-ql-DIR-<"+mu+">";             // Set runtag
        String FileName = "ql-dir.trecrun";
        trec(QueryNumber, scores, runtag, FileName, ob1);           // Print in trec format
        
        return scores;
    }
        
    
    public void trec(int QueryNumber, ArrayList<Map.Entry<Integer, Double>> scores, String runtag, String FileName, CreateIndex ob1) throws java.io.IOException
    {
        Map<Long, String> SceneIds = ob1.getSceneIds();
        
        int i=1;
        
        FileWriter writer = new FileWriter(FileName, true);
        
        for (Map.Entry<Integer, Double> entry : scores)
        {
           Long docid = new Long(entry.getKey());
           writer.write("Q"+QueryNumber+"   "+ "skip" + "   " + SceneIds.get(docid) + "   " + i + "   " + entry.getValue() + "   " + runtag + "\n");
           i++;
        }
        writer.flush();
        writer.close();
    }
}
    
    
    
    
    
    
    
    
    

