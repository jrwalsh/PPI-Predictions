package ppi.preprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import ppi.custom.DomainDomainInteraction;
import ppi.custom.GeneGeneInteraction;
import ppi.custom.MixedGraph;
import ppi.custom.PredictedDomain;

public class NetworkFactory {

	public static UndirectedGraph<String, DefaultEdge> generateNetwork(ArrayList<PredictedDomain> predictedDomains, ArrayList<DomainDomainInteraction> DDIs) {
		System.out.print("Creating network..."); //TODO only on verbose flag
		UndirectedGraph<String, DefaultEdge> graph = new MixedGraph<String, DefaultEdge>(DefaultEdge.class);
		HashMap<String, HashSet<String>> domainMap = new HashMap<String, HashSet<String>>();
		HashSet<String> domainsNotFound = new HashSet<String>();
		HashSet<String> selfLoops = new HashSet<String>();

		// Prep node list so we can convert to a gene-gene interaction network on the fly
		System.out.print("Prepping domain map..."); //TODO only on verbose flag
		for (PredictedDomain domain : predictedDomains) {
			if (domainMap.containsKey(domain.getDomainName())) {
				domainMap.get(domain.getDomainName()).add(domain.getGeneName());
			} else {
				HashSet<String> temp = new HashSet<String>();
				temp.add(domain.getGeneName());
				domainMap.put(domain.getDomainName(), temp);
			}
		}
		
		System.out.println("Building network..."); //TODO only on verbose flag
		for (DomainDomainInteraction ddi : DDIs) {
			if (domainMap.containsKey(ddi.getSourceDomain())) {
				if (domainMap.containsKey(ddi.getTargetDomain())) {
					HashSet<String> sourceGenes = domainMap.get(ddi.getSourceDomain());
					HashSet<String> targetGenes = domainMap.get(ddi.getTargetDomain());
					
					for (String geneA : sourceGenes) {
						for (String geneB : targetGenes) {
							graph.addVertex(geneA); // Must add vertex, but if duplicate it will be ignored
							graph.addVertex(geneB);
							graph.addEdge(geneA, geneB);
							if (geneA.equalsIgnoreCase(geneB))
								selfLoops.add(geneA);
						}
					}
				} else {
//					System.err.println("Target domain " + ddi.getTargetDomain() + " not found in prediction list.");
					domainsNotFound.add(ddi.getTargetDomain());
				}
			} else {
//				System.err.println("Source domain " + ddi.getSourceDomain() + " not found in prediction list.");
				domainsNotFound.add(ddi.getSourceDomain());
			}
		}
		
		//TODO no reason to print this out all the time
		System.err.println("Some domains involved in interactions were not present in the predicted domain assignments list: " + domainsNotFound.size() + " domains");
		System.err.println("Some genes are involved in self interactions: " + selfLoops.size() + " genes");
		System.out.println("Done creating network!"); //TODO only on verbose flag
		return graph;
	}

	//TODO this method needs to be written
	public static UndirectedGraph<String, DefaultEdge> generateNetworkGGI(ArrayList<PredictedDomain> predictedDomains, ArrayList<GeneGeneInteraction> GGIs) {
		UndirectedGraph<String, DefaultEdge> graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		HashMap<String, HashSet<String>> domainMap = new HashMap<String, HashSet<String>>();
		HashSet<String> domainsNotFound = new HashSet<String>();
		HashSet<String> selfLoops = new HashSet<String>();

		// Prep node list so we can convert to a gene-gene interaction network on the fly
//		GeneGeneInteraction
//		for (PredictedDomain domain : predictedDomains) {
//			if (domainMap.containsKey(domain.getDomainName())) {
//				domainMap.get(domain.getDomainName()).add(domain.getGeneName());
//			} else {
//				HashSet<String> temp = new HashSet<String>();
//				temp.add(domain.getGeneName());
//				domainMap.put(domain.getDomainName(), temp);
//			}
//		}
		
//		for (GeneGeneInteraction ddi : GGIs) {
//			if (domainMap.containsKey(ddi.getSourceDomain())) {
//				if (domainMap.containsKey(ddi.getTargetDomain())) {
//					HashSet<String> sourceGenes = domainMap.get(ddi.getSourceDomain());
//					HashSet<String> targetGenes = domainMap.get(ddi.getTargetDomain());
//					
//					for (String geneA : sourceGenes) {
//						for (String geneB : targetGenes) {
//							graph.addVertex(geneA); // Must add vertex, but if duplicate it will be ignored
//							graph.addVertex(geneB);
//							if (!geneA.equalsIgnoreCase(geneB)) graph.addEdge(geneA, geneB); // Loops not allowed in simple graphs, duplicates automatically removed
//							//TODO mark self loops on a custom vertex
//							else selfLoops.add(geneA);
//						}
//					}
//				} else {
////					System.err.println("Target domain " + ddi.getTargetDomain() + " not found in prediction list.");
//					domainsNotFound.add(ddi.getTargetDomain());
//				}
//			} else {
////				System.err.println("Source domain " + ddi.getSourceDomain() + " not found in prediction list.");
//				domainsNotFound.add(ddi.getSourceDomain());
//			}
//		}
		
		//TODO no reason to print this out all the time
		System.err.println("Some domains involved in interactions were not present in the predicted domain assignments list: " + domainsNotFound.size() + " domains");
		System.err.println("Some genes are involved in self interactions: " + selfLoops.size() + " genes");
		return graph;
	}
	
	public static HashSet<DomainDomainInteraction> convertGGItoDDI(ArrayList<PredictedDomain> predictedDomains, ArrayList<GeneGeneInteraction> GGIs) {
		System.out.println("Converting GGI list to DDI list..."); //TODO only on verbose flag
		HashSet<DomainDomainInteraction> DDIs = new HashSet<DomainDomainInteraction>();
		HashMap<String, HashSet<String>> geneMap = new HashMap<String, HashSet<String>>();
		HashSet<String> genesNotFound = new HashSet<String>();
		
		// Prep map of gene to domains
		for (PredictedDomain domain : predictedDomains) {
			if (geneMap.containsKey(domain.getGeneName())) {
				geneMap.get(domain.getGeneName()).add(domain.getDomainName());
			} else {
				HashSet<String> temp = new HashSet<String>();
				temp.add(domain.getDomainName());
				geneMap.put(domain.getGeneName(), temp);
			}
		}
		
		for (GeneGeneInteraction GGI : GGIs) {
			HashSet<String> domainsA = geneMap.get(GGI.getSourceGene());
			HashSet<String> domainsB = geneMap.get(GGI.getTargetGene());
			try {
				for (String domainA : domainsA) {
					for (String domainB : domainsB) {
						DDIs.add(new DomainDomainInteraction(domainA, domainB, null)); //TODO going to have a lot of duplicates if I can't make this a set, poor space management but otherwise correct answer if I don't
					}
				}
			} catch (NullPointerException e) {
				//TODO might want to count how often one or the other gene has no predicted domains
			}
		}
		System.out.println("DDI list has " + DDIs.size() + " items."); //TODO only on verbose flag
		System.out.println("Done converting GGI list to DDI list!"); //TODO only on verbose flag
		return DDIs;
	}

	public static HashSet<GeneGeneInteraction> convertEdgesToGGIs(UndirectedGraph<String, DefaultEdge> graph) {
		System.out.println("Converting edges to GGI objects..."); //TODO only on verbose flag
		HashSet<GeneGeneInteraction> GGIs = new HashSet<GeneGeneInteraction>();
		for (DefaultEdge edge : graph.edgeSet()) {
			GGIs.add(new GeneGeneInteraction(graph.getEdgeSource(edge), graph.getEdgeTarget(edge)));
		}
		System.out.println("Done converting edges to GGI objects!"); //TODO only on verbose flag
		return GGIs;
	}
}
