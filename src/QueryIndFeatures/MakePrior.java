/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryIndFeatures;

import InfoRetrievalEngine.CreateIndex;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author mini
 */
public class MakePrior {
    
    public static void main(String args[]) throws java.io.IOException
    {
        CreateIndex index = new CreateIndex();
        index.loadMaps();
        Map<Long, String> SceneIds = index.getSceneIds();
        
   try {
            String type = "uniform";
            RandomAccessFile writer = new RandomAccessFile(type + ".prior", "rw");
            double uniform = Math.log(1.0/index.getTotalDocs());
            
            for (int i = 1; i <= index.getTotalDocs(); i++)
            {
                writer.writeDouble(uniform);
                System.out.println(SceneIds.get(new Long(i)) + "\t" + uniform);
            }
            writer.close();
            
            type = "random";
            Random rand = new Random(1024);
            writer = new RandomAccessFile(type + ".prior", "rw");
            for (int i = 1; i <= index.getTotalDocs(); i++) {
                double prior = Math.log(rand.nextDouble());
                writer.writeDouble(prior);
                System.out.println(SceneIds.get(new Long(i)) + "\t" + prior);
            }
            writer.close();
            
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
