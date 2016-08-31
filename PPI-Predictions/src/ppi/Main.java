package ppi;

import java.util.ArrayList;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import ppi.custom.DomainDomainInteraction;
import ppi.custom.PredictedDomain;
import ppi.preprocessing.EdgeFileReader;
import ppi.preprocessing.NetworkFactory;
import ppi.preprocessing.NodeFileReader;


public class Main {
	
	public static void main(String args[]) {
		Long start = System.currentTimeMillis();
		
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
//		for (PredictedDomain predDomain : predictedDomains) {
//			System.out.println(predDomain);
//		}
		
		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
//		for (DomainDomainInteraction ddi : DDIs) {
//			System.out.println(ddi);
//		}
		
		UndirectedGraph<String, DefaultEdge> graph = NetworkFactory.generateNetwork(predictedDomains, DDIs);
		
		System.out.println(graph.edgeSet().size() + " edges in this network");
		
		System.out.println("done!");
		
		Long stop = System.currentTimeMillis();
		Long runtime = (stop - start) / 1000;
		System.out.println("Runtime is " + runtime + " seconds.");
	}
	
}