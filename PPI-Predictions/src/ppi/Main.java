package ppi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import ppi.custom.DomainDomainInteraction;
import ppi.custom.GeneGeneInteraction;
import ppi.custom.MixedGraph;
import ppi.custom.PredictedDomain;
import ppi.preprocessing.EdgeFileReader;
import ppi.preprocessing.NetworkFactory;
import ppi.preprocessing.NodeFileReader;


public class Main {
	
	public static void main(String args[]) {
		Long start = System.currentTimeMillis();
		
		crossValidate();
		
		System.out.println("done!");
		
		Long stop = System.currentTimeMillis();
		Long runtime = (stop - start) / 1000;
		System.out.println("Runtime is " + runtime + " seconds.");
	}
	
	/**
	 * Try generating a network based on iPfam DDIs
	 */
	private static void testNetworkPrediction() {
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
		UndirectedGraph<String, DefaultEdge> graph = NetworkFactory.generateNetwork(predictedDomains, DDIs);
		System.out.println(graph.edgeSet().size() + " edges in this network");
	}
	
	/**
	 * Try generating a network based on all possible DDIs within the HC set
	 */
	private static void testAssumedDDIs() {
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		UndirectedGraph<String, DefaultEdge> graph = NetworkFactory.generateNetworkGGI(predictedDomains, GGIs);
	}
	
	private static void generateConfusionMatrix() {
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		
		UndirectedGraph<String, DefaultEdge> predictedGraph = NetworkFactory.generateNetwork(predictedDomains, DDIs);
		UndirectedGraph<String, DefaultEdge> hcGraph = new MixedGraph<String, DefaultEdge>(DefaultEdge.class);
		
		System.out.println(predictedGraph.edgeSet().size() + " edges in the predicted network");
		
		ArrayList<GeneGeneInteraction> predictedGGIs = new ArrayList<GeneGeneInteraction>();
		ArrayList<GeneGeneInteraction> tpGGIs = new ArrayList<GeneGeneInteraction>();
		predictedGGIs.addAll(NetworkFactory.convertEdgesToGGIs(predictedGraph));
		tpGGIs.addAll(predictedGGIs);
		tpGGIs.retainAll(GGIs); // Intersection gives all TP results
		
		System.out.println(GGIs.size() + "\t" + predictedGGIs.size() + "\t" + tpGGIs.size());
	}
	

	private static void crossValidate() {
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
//		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		
		for (int i=0; i<10; i++) {
			ArrayList<GeneGeneInteraction> tmp = new ArrayList<GeneGeneInteraction>();
			ArrayList<GeneGeneInteraction> GGI_Test = new ArrayList<GeneGeneInteraction>();
			ArrayList<GeneGeneInteraction> GGI_Train = new ArrayList<GeneGeneInteraction>();
			
			// Perform randomization and split into test/train groups
			tmp.addAll(GGIs);
			Collections.shuffle(tmp);
			int cutoff =  (int) Math.floor((tmp.size()/10.0));
			GGI_Test.addAll(tmp.subList(0,cutoff));
			GGI_Train.addAll(tmp.subList(cutoff+1, tmp.size()));
			
			// Run prediction based on the DDIs from the training set
			HashSet<DomainDomainInteraction> DDIs = NetworkFactory.convertGGItoDDI(predictedDomains, GGI_Train);
			ArrayList<DomainDomainInteraction> DDI_Train = new ArrayList<DomainDomainInteraction>();
			DDI_Train.addAll(DDIs);
			UndirectedGraph<String, DefaultEdge> predictedGraph = NetworkFactory.generateNetwork(predictedDomains, DDI_Train);
			System.out.println(predictedGraph.edgeSet().size() + " edges in the predicted network");
			
			ArrayList<GeneGeneInteraction> predictedGGIs = new ArrayList<GeneGeneInteraction>();
			ArrayList<GeneGeneInteraction> tpGGIs = new ArrayList<GeneGeneInteraction>();
			tpGGIs.addAll(GGI_Test);
			predictedGGIs.addAll(NetworkFactory.convertEdgesToGGIs(predictedGraph));
			tpGGIs.retainAll(predictedGGIs); // Intersection gives all TP results
			
			System.out.println("TruthSet\tPredictions\tTPs");
			System.out.println(GGIs.size() + "\t" + predictedGGIs.size() + "\t" + tpGGIs.size());
		}
	}
}