package InferenceNetwork;

import InferenceNetwork.AndNode;
import BM25Ranking.Retrieval;
import InfoRetrievalEngine.CreateIndex;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.List;
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
public class ImplementIN {
    
    public static void main(String args[]) throws java.io.IOException, java.io.FileNotFoundException
    {
        int k = 10;
        String query;
        ArrayList <Map.Entry<Integer,Double>> results;                                  // To store the top k documents for any query
        
        
        InferenceNetwork network = new InferenceNetwork();
        Retrieval model = new Retrieval();                                              // Scoring model to use
        CreateIndex index = new CreateIndex();
        
        QueryNode QNode;
        ArrayList<ProximityNode> children;                                              
        
        List<String> queries = new ArrayList<String>();                                 // ArrayList to store queries
        BufferedReader reader = new BufferedReader(new FileReader("Queries.txt"));      // Read queries from a file
        
        while((query = reader.readLine()) != null)
        {
            queries.add(query);
        }
        
        reader.close();
        
        int QueryNumber = 0;
        String RunTag = "gursimrankau-and-DIR-1500";
        String FileName;

        // AND
        for(String Query : queries)
        {
            children = genTermNodes(Query);
            QueryNumber++;
            
            QNode = new AndNode(children);
            FileName = "and.trecrun";
            results = network.runQuery(QNode, k);                                       // Get the top k results for the query
            model.trec(QueryNumber, results, RunTag, FileName, index);                  // Print results for a query in trecrun format
        }
        
        // MAX
        QueryNumber = 0;
        for(String Query : queries)
        {
            children = genTermNodes(Query);
            QueryNumber++;
            
            QNode = new MaxNode(children);
            FileName = "max.trecrun";
            results = network.runQuery(QNode, k);                                       // Get the top k results for the query
            model.trec(QueryNumber, results, RunTag, FileName, index);                  // Print results for a query in trecrun format
        }
        
        // OR
        QueryNumber = 0;
        for(String Query : queries)
        {
            children = genTermNodes(Query);
            QueryNumber++;
            
            QNode = new OrNode(children);
            FileName = "or.trecrun";
            results = network.runQuery(QNode, k);                                       // Get the top k results for the query
            model.trec(QueryNumber, results, RunTag, FileName, index);                  // Print results for a query in trecrun format
        }
        
        // SUM
        QueryNumber = 0;
        for(String Query : queries)
        {
            children = genTermNodes(Query);
            QueryNumber++;
            
            QNode = new SumNode(children);
            FileName = "sum.trecrun";
            results = network.runQuery(QNode, k);                                       // Get the top k results for the query
            model.trec(QueryNumber, results, RunTag, FileName, index);                  // Print results for a query in trecrun format
        }

        // UNORDERED WINDOW
        QueryNumber = 0;
        for(String Query : queries)
        {
            children = genTermNodes(Query);
            QueryNumber++;
            
            QNode = new UnorderedWindow(3*children.size(), children);
            FileName = "uw.trecrun";
            results = network.runQuery(QNode, k);                                       // Get the top k results for the query
            model.trec(QueryNumber, results, RunTag, FileName, index);                  // Print results for a query in trecrun format
        }
        
        // ORDERED WINDOW
        QueryNumber = 0;
        for(String Query : queries)
        {
            children = genTermNodes(Query);
            QueryNumber++;
            
            QNode = new OrderedWindow(1, children);
            FileName = "od1.trecrun";
            results = network.runQuery(QNode, k);                                       // Get the top k results for the query
            model.trec(QueryNumber, results, RunTag, FileName, index);                  // Print results for a query in trecrun format
        }
        
    }
    
    static ArrayList<ProximityNode> genTermNodes(String query) throws java.io.IOException       // Generate term nodes for each term in the query
    {
        String[] terms = query.split("\\s+");                                                   // Split query on whitespaces
        ArrayList<ProximityNode> children = new ArrayList<ProximityNode>();
        for (String term : terms)
        {
            ProximityNode node = new TermNode(term);
            children.add(node);
        }
        return children;
    }
    
}
