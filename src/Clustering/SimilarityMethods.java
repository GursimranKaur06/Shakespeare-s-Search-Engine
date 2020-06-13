package Clustering;


import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mini
 */
public abstract class SimilarityMethods {
    
    abstract double score(Map<String, Double> Dv1, Map<String, Double> Dv2);
    
}
