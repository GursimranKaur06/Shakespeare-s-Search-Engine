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
import java.util.Map;



// Main class for whole project

public class IndexMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            CreateIndex ob1 = new CreateIndex();
            
            ob1.parseFile();
            //ob1.loadMaps();

            //ob1.calcAvdl();
            
            
            //WriteToDisk ob2 = new WriteToDisk(ob1.InvertedIndex);       // To write the index to disk
            
            ReadFromDisk ob3 = new ReadFromDisk();
            //ob3.searchLookupTable("alas").print();
            //ob3.choice=1;
           // PostingsList pl = ob3.searchLookupTable("stove");
            //System.out.println("For Stove : ");
            //pl.print();
            //PostingsList pl2 = ob3.SearchLookupTable("them");
            //pl2.print();
            //ob3.choice=2;
            //ob3.SearchLookupTable();
            
            //QueryProcessing ob4 = new QueryProcessing();
            //ob4.process2(ob1);
            //ob4.process(ob1);
            
            //Retrieval ob5 = new Retrieval();
            //ob5.process(ob1);
            
        } catch(Exception e){e.printStackTrace();}
    
    }
}
