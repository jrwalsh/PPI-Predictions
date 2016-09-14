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
	private final String predictedDomains = "/Data/Arabidopsis/pfam_predictions.tab";
	private final String iPfamDDIs = "/Data/iPfam/iPfam_domain_interactions.tab";
	private final String highConfidenceGGIs = "/Data/Arabidopsis/high_confidence_interactions.tab";
	
	public static void main(String args[]) {
		Long start = System.currentTimeMillis();
		
		test();
//		basicStatistics();
//		generateNetworkFromiPfam();
//		generateNetworkFromAssumedDDIs();
//		generateConfusionMatrix();
//		crossValidate();
		
		System.out.println("done!");
		
		Long stop = System.currentTimeMillis();
		Long runtime = (stop - start) / 1000;
		System.out.println("Runtime is " + runtime + " seconds.");
	}
	
	/**
	 * Test run ideas
	 */
	private static void test() {
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		
		HashSet<DomainDomainInteraction> assumed_DDI_set = NetworkFactory.convertGGItoDDI(predictedDomains, GGIs);
		HashSet<DomainDomainInteraction> DDI_intersection = new HashSet<DomainDomainInteraction>();
		DDI_intersection.addAll(DDIs);
		DDI_intersection.retainAll(assumed_DDI_set);
		ArrayList<DomainDomainInteraction> DDI_set = new ArrayList<DomainDomainInteraction>();
		DDI_set.addAll(DDI_intersection);
		
		UndirectedGraph<String, DefaultEdge> predictedGraph = NetworkFactory.generateNetwork(predictedDomains, DDI_set);
		ArrayList<GeneGeneInteraction> predictedGGIs = new ArrayList<GeneGeneInteraction>();
		ArrayList<GeneGeneInteraction> tpGGIs = new ArrayList<GeneGeneInteraction>();
		predictedGGIs.addAll(NetworkFactory.convertEdgesToGGIs(predictedGraph));
		tpGGIs.addAll(predictedGGIs);
		tpGGIs.retainAll(GGIs); // Intersection gives all TP results
		
		System.out.println("HCSet\tPredictions\tTP");
		System.out.println(GGIs.size() + "\t" + predictedGGIs.size() + "\t" + tpGGIs.size());
		
		System.out.println("Size of DDI set");
		System.out.println(DDI_set.size());
	}
	
	private static void basicStatistics() {
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		HashSet<DomainDomainInteraction> assumed_DDI_set = NetworkFactory.convertGGItoDDI(predictedDomains, GGIs);
		
		// Stats for the predictedDomains
		for (PredictedDomain predictedDomain : predictedDomains) {
			
		}
		System.out.println("Predicted Domains (pfam_scan)");
		System.out.println("Total predictions " + predictedDomains.size());
		
		// Which DDI's in the iPfam set are actually predicting known GGIs from the HC set?  How many of them are there?
		HashSet<DomainDomainInteraction> DDI_intersection = new HashSet<DomainDomainInteraction>();
		DDI_intersection.addAll(DDIs);
		DDI_intersection.retainAll(assumed_DDI_set);
		System.out.println("The overlap between the iPfam DDIs and the assumed DDIs based on the high confidence set is: " + DDI_intersection.size());
		
		
		
	}
	
	/**
	 * Try generating a network based on iPfam DDIs
	 */
	private static void generateNetworkFromiPfam() {
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
		UndirectedGraph<String, DefaultEdge> graph = NetworkFactory.generateNetwork(predictedDomains, DDIs);
		System.out.println(graph.edgeSet().size() + " edges in this network");
	}
	
	/**
	 * Try generating a network based on all possible DDIs within the HC set
	 */
	private static void generateNetworkFromAssumedDDIs() {
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		UndirectedGraph<String, DefaultEdge> graph = NetworkFactory.generateNetworkGGI(predictedDomains, GGIs);
	}
	
	/**
	 * Generate a network based on iPfam DDIs and then compare to the high confidence GGI set.
	 */
	private static void generateConfusionMatrix() {
		//TODO this isn't right, look it over and fix it.  What is the unused hcGraph for?
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
		
		
		
//		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
//		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
//		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
//		
//		HashSet<DomainDomainInteraction> assumed_DDI_set = NetworkFactory.convertGGItoDDI(predictedDomains, GGIs);
//		HashSet<DomainDomainInteraction> DDI_intersection = new HashSet<DomainDomainInteraction>();
//		DDI_intersection.addAll(DDIs);
//		DDI_intersection.retainAll(assumed_DDI_set);
//		
//		UndirectedGraph<String, DefaultEdge> predictedGraph = NetworkFactory.generateNetwork(predictedDomains, DDIs); //DDI_intersection
//		ArrayList<GeneGeneInteraction> predictedGGIs = new ArrayList<GeneGeneInteraction>();
//		ArrayList<GeneGeneInteraction> tpGGIs = new ArrayList<GeneGeneInteraction>();
//		predictedGGIs.addAll(NetworkFactory.convertEdgesToGGIs(predictedGraph));
//		tpGGIs.addAll(predictedGGIs);
//		tpGGIs.retainAll(GGIs); // Intersection gives all TP results
//		
//		System.out.println("HCSet\tPredictions\tTP");
//		System.out.println(GGIs.size() + "\t" + predictedGGIs.size() + "\t" + tpGGIs.size());
	}
	
	/**
	 * Run at 10% test, 90% train cross validation using assumed DDIs generated from the high confidence GGI set.
	 */
	private static void crossValidate() {
		int numRuns = 10;
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
//		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		
		for (int i=0; i<numRuns; i++) {
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