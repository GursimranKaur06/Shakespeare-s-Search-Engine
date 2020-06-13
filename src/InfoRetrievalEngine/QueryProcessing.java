package InfoRetrievalEngine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Random;
import java.lang.Math;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mini
 */
public class QueryProcessing
{
   ReadFromDisk obj = new ReadFromDisk();
   Map<String, Integer[]> lookup = obj.loadLookupTable();
   
   void process(CreateIndex ob1) throws IOException
   {
       
       
        FileReader  fr = new FileReader("Setsof7.txt");
        BufferedReader reader = new BufferedReader(fr);
        
        PrintWriter writer = new PrintWriter("SetsOf14.txt", "UTF-8");
        
        String line;
       
        while((line = reader.readLine()) != null) {
        String toWrite = "";
        for(String word : line.split("\\s+")) {
        String secondTerm = GetDice(word, ob1);
        toWrite += word + " " + secondTerm + " ";
        }
        writer.println(toWrite.trim());
        }
        reader.close();
        writer.close();
        
        process7(ob1);

    }
   
   
   // Processing 7 words' 100 sets
   void process7(CreateIndex ob1) throws IOException
   {
       // COMMENT ONCE THE FILE OF 7 WORDS IS WRITTEN
       /*Random rand = new Random();
       FileWriter writer = new FileWriter("SetsOf7.txt"); 
       
       for(int i=0; i<100; i++)
       {     
           for(int j=0; j<7; j++)
             {
                 ArrayList<String> words = new ArrayList();
                 Object randomword = lookup.keySet().toArray()[new Random().nextInt(lookup.keySet().toArray().length)];      // Get a random key word from lookup table
                 words.add(randomword.toString());
                 writer.write(randomword.toString()+" ");
                 //System.out.print(randomword.toString()+" ");
                 //if(i==0 && j==0)
                 //{
                     //ArrayList<String> words1 = new ArrayList();
                     //words1.add("stove");
                     //words1.add("cancel");
                     //words1.add("can");
                     //rankdocs(words, ob1, 3);
                     //GetDice(words, ob1);
                 //}
             }
        
        writer.write(System.lineSeparator());
        //System.out.println();

       }
       writer.close();*/
       
       FileReader  fr = new FileReader("Setsof14.txt");
       BufferedReader reader = new BufferedReader(fr);
       
       String line;
       ArrayList<String> queryline = new ArrayList();
       long start = System.currentTimeMillis();

       while((line = reader.readLine()) != null)
       {
            for(String ql : line.split("\\s+"))
            {
                queryline.add(ql);

            }
            
            rankdocs(queryline, ob1, 2);
       }  
       long finish = System.currentTimeMillis();
       long timeElapsed = finish - start;
       System.out.println("TOTAL TIME ELAPSED = "+timeElapsed);
   }
   
   
   
   ArrayList<Map.Entry<Integer, Double>> rankdocs(ArrayList<String> words, CreateIndex ob1, int k) throws IOException
   {
       //ReadFromDisk obj = new ReadFromDisk();
   
	PriorityQueue<Map.Entry<Integer, Double>> result = new PriorityQueue<Map.Entry<Integer, Double>>(new Comparator<Map.Entry<Integer, Double>>()
        {
            @Override
            public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2)
            {
                if (o1.getValue() < o2.getValue()) 
                   return -1;
                else if (o1.getValue() > o2.getValue())
                   return 1;

                   return 0;
            }
        });

        PostingsList[] plist = new PostingsList[words.size()];

        for(int i=0; i<words.size(); i++)
        {
            Integer[] ex = lookup.get(words.get(i));
            plist[i] = obj.ReadFile(ex[2], ex[3]);
            //System.out.println(words.get(i) + "--");
            //plist[i].print();			
        }

        for(int doc=0; doc<ob1.getTotalDocs(); doc++)
        {

            Double score = 0.0;

            for (PostingsList p: plist)
            {
                for(Posting posting: p.plist)
                {
                    if (posting.getDocId() == doc)
                       score += posting.count;
                }
            }

            result.add(new AbstractMap.SimpleEntry<Integer, Double>(doc, score));

            if (result.size() > k)
                result.poll();
            
        }

        ArrayList<Map.Entry<Integer, Double>> scores = new ArrayList<Map.Entry<Integer, Double>>();

        for(Map.Entry<Integer, Double> pair: result)
                scores.add(pair);
        
        for (Map.Entry<Integer, Double> score : result)
        System.out.println(score.getKey() + "   " + score.getValue());

        return scores;

   }
   
   
   
   String GetDice(String word, CreateIndex ob1) throws IOException
   {
        PostingsList randomword = ob1.InvertedIndex.get(word);
        int nA = randomword.getCtf();
        int nB;
        double dice;
        double maxDice = 0.000000;
        String term2 = "";

        HashMap<String, Integer> nABs = new HashMap<String,Integer>();

        for(String word1: ob1.InvertedIndex.keySet()) {
                nABs.put(word1,0);
        }
        
		for(Posting post1: randomword.plist) {
			
			//System.out.println("Checking "+ post1.positions.toString());
			for(String secondTerm : ob1.InvertedIndex.keySet()) {
				
				//System.out.println(secondTerm);
                            //Integer[] obj2 = lookup.get(secondTerm);
                            PostingsList pL2 = ob1.InvertedIndex.get(secondTerm);
				
				//PostingsList pL2 = getPostings(secondTerm, readLookupObj);		
				
				for(Posting post2: pL2.plist) {
					
					
					if (post2.docid == post1.docid) {
						
						for(int pos1 : post1.positions) {							
							
							for(int pos2: post2.positions) {
								
								if (pos1 + 1 == pos2) {
									
									nABs.put(secondTerm, nABs.get(secondTerm)+1);
								}
							}
						}
					}
				}
			}		
				//System.out.println(nAB);
		}
		
		for(String word2: ob1.InvertedIndex.keySet()) {
			//Integer[] obj3 = ob1.InvertedIndex.get(word);
                        PostingsList word3 = ob1.InvertedIndex.get(word2);
			nB = word3.getCtf();
			
			int nAB = nABs.get(word2);
			
			dice = (double) 2*nAB/(nA + nB);
			
			if (nAB > 0)
				//System.out.println(nAB + "  "+ word + " " + dice + " " + maxDice);
			
			if (dice >= maxDice) {
				
				maxDice = dice;
				term2 = word2;
			}
		}
		
		//return term2;
                //System.out.println("Vocab Word Is : "+word+" Match is "+term2);
                //writer.write(words.get(i)+" "+term2);
                return term2;

    } 
   
   
  }

