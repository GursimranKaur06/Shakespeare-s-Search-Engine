/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryIndFeatures;

import QueryIndFeatures.PriorNode;
import InferenceNetwork.TermNode;
import InferenceNetwork.QueryNode;
import InferenceNetwork.ProximityNode;
import InferenceNetwork.InferenceNetwork;
import InferenceNetwork.AndNode;
import InferenceNetwork.AndNode;
import InferenceNetwork.InferenceNetwork;
import InferenceNetwork.ProximityNode;
import InferenceNetwork.QueryNode;
import InferenceNetwork.TermNode;
import InfoRetrievalEngine.CreateIndex;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author mini
 */
public class TestPriorNode
{
    
    public static void main(String args[]) throws java.io.IOException
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter value for k");
        int k = sc.nextInt();
        
        CreateIndex index = new CreateIndex();
        index.parseFile();
        index.loadMaps();
        
        List <Map.Entry<Integer,Double>> results;
        InferenceNetwork network = new InferenceNetwork();
        QueryNode queryNode;
        ArrayList<QueryNode> children;
        String query = "the king queen royalty";
        
        String OutFile, runtag, qId;
        
        OutFile = "uniform.trecrun";
        runtag = "gursimrankau-uniform-dir-1500";
        
        children = genTermNodes(query, index, "uniform");
        queryNode = new AndNode(children);
        results = network.runQuery(queryNode, k);
        qId = "Q1";
        
        try
        {
            PrintWriter writer = new PrintWriter(new FileWriter(OutFile));
            printResults(results, index, writer, runtag, qId);
            writer.close();
            
        } catch (IOException e) { e.printStackTrace(); }
            
        
        OutFile = "random.trecrun";
        runtag = "gursimrankau-random-dir-1500";
        children = genTermNodes(query, index, "random");
        queryNode = new AndNode(children);
        results = network.runQuery(queryNode, k);
        qId = "Q1";
        
        try
        {
            PrintWriter writer = new PrintWriter(new FileWriter(OutFile));
            printResults(results, index, writer, runtag, qId);
            writer.close();
        } catch (IOException e) { e.printStackTrace(); }
            
    }
    
    
    private static void printResults(List<Map.Entry<Integer, Double>> results, CreateIndex index, PrintWriter writer, String runtag, String qId) throws java.io.FileNotFoundException, java.io.IOException
    {
        int rank = 1;
        Map<Long, String> SceneIds = index.getSceneIds();
        
        for (Map.Entry<Integer, Double> entry: results)
        {
            String SceneId = SceneIds.get(entry.getKey());
            String OutLine = qId + " skip " + String.format("%-35s", SceneId) + " " + rank + " " + String.format("%.7f", entry.getValue()) + " " + runtag;
            System.out.println(OutLine);
            writer.println(OutLine);
            rank++;
        }
    }
    
    static ArrayList<QueryNode> genTermNodes(String query, CreateIndex index, String type) throws java.io.IOException       // Generate term nodes for each term in the query
    {
        String[] terms = query.split("\\s+");                                                   // Split query on whitespaces
        ArrayList<QueryNode> children = new ArrayList<QueryNode>();
        PriorNode prior = new PriorNode(type, index);
        children.add(prior);
        for (String term : terms)
        {
            ProximityNode node = new TermNode(term);
            children.add(node);
        }
        return children;
    }
}
