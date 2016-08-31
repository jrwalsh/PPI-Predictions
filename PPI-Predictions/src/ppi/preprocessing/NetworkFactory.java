package ppi.preprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import ppi.custom.DomainDomainInteraction;
import ppi.custom.PredictedDomain;

public class NetworkFactory {

	public static UndirectedGraph<String, DefaultEdge> generateNetwork(ArrayList<PredictedDomain> predictedDomains, ArrayList<DomainDomainInteraction> DDIs) {
		UndirectedGraph<String, DefaultEdge> graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		HashMap<String, HashSet<String>> domainMap = new HashMap<String, HashSet<String>>();
		HashSet<String> domainsNotFound = new HashSet<String>();
		HashSet<String> selfLoops = new HashSet<String>();

		// Prep node list so we can convert to a gene-gene interaction network on the fly
		for (PredictedDomain domain : predictedDomains) {
			if (domainMap.containsKey(domain.getDomainName())) {
				domainMap.get(domain.getDomainName()).add(domain.getGeneName());
			} else {
				HashSet<String> temp = new HashSet<String>();
				temp.add(domain.getGeneName());
				domainMap.put(domain.getDomainName(), temp);
			}
		}
		
		for (DomainDomainInteraction ddi : DDIs) {
			if (domainMap.containsKey(ddi.getSourceDomain())) {
				if (domainMap.containsKey(ddi.getTargetDomain())) {
					HashSet<String> sourceGenes = domainMap.get(ddi.getSourceDomain());
					HashSet<String> targetGenes = domainMap.get(ddi.getTargetDomain());
					
					for (String geneA : sourceGenes) {
						for (String geneB : targetGenes) {
							graph.addVertex(geneA); // Must add vertex, but if duplicate it will be ignored
							graph.addVertex(geneB);
							if (!geneA.equalsIgnoreCase(geneB)) graph.addEdge(geneA, geneB); // Loops not allowed in simple graphs, duplicates automatically removed
							//TODO mark self loops on a custom vertex
							else selfLoops.add(geneA);
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
		return graph;
	}

}
