package ppi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

import org.apache.commons.io.IOUtils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import ppi.custom.DomainDomainInteraction;
import ppi.custom.GeneGeneInteraction;
import ppi.custom.MixedGraph;
import ppi.custom.PredictedDomain;
import ppi.preprocessing.EdgeFileReader;
import ppi.preprocessing.NetworkFactory;
import ppi.preprocessing.NodeFileReader;


public class Test {
	private final String predictedDomains = "/Data/Arabidopsis/pfam_predictions.tab";
	private final String iPfamDDIs = "/Data/iPfam/iPfam_domain_interactions.tab";
	private final String highConfidenceGGIs = "/Data/Arabidopsis/high_confidence_interactions.tab";
	
	public static void main(String args[]) {
		Long start = System.currentTimeMillis();
		
		test();
		
		System.out.println("done!");
		
		Long stop = System.currentTimeMillis();
		Long runtime = (stop - start) / 1000;
		System.out.println("Runtime is " + runtime + " seconds.");
	}
	
	/**
	 * Test run ideas
	 * @throws IOException 
	 */
	private static void test() {
//		mappableCoexp();
//		percentMappableGenes();
		printDegree();
//		getCoexpressionOfPredictedEdges();
//		getCoexpressionOfHCEdges();
		
//		createMap();
//		graphable();
//		removeBasedOnPredictedOrder();
//		removeBasedOnRandomRemoval();
	}
	
	private static void printDegree() {
		// For HC set
//		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		
		// For predicted set
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
		UndirectedGraph<String, DefaultEdge> graph = NetworkFactory.generateNetwork(predictedDomains, DDIs);
		System.out.println(graph.edgeSet().size() + " edges in this network");
		
		ArrayList<GeneGeneInteraction> GGIs = new ArrayList<GeneGeneInteraction>();
		GGIs.addAll(NetworkFactory.convertEdgesToGGIs(graph));
		
		// For mapped set
//		ArrayList<GeneGeneInteraction> GGIs = readEdgeListFile();
		
		
		DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		for (GeneGeneInteraction GGI : GGIs) {
			directedGraph.addVertex(GGI.getSourceGene());
			directedGraph.addVertex(GGI.getTargetGene());
			directedGraph.addEdge(GGI.getSourceGene(), GGI.getTargetGene());
		}
		
//		System.out.println(directedGraph.vertexSet().size() + " : " + directedGraph.edgeSet().size());
		
//		String out = "";
//		for (String vertex : directedGraph.vertexSet()) {
//			int degree = directedGraph.inDegreeOf(vertex) + directedGraph.outDegreeOf(vertex);
////			System.out.println(vertex + "\t" + degree);
//			out += vertex + "\t" + degree + "\n";
//		}
		
		String out = "";
		BufferedReader reader = null;
		try {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Please select the");
			fc.setCurrentDirectory(new File("")); //TODO don't hardcode the file locations
			
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				
				reader = new BufferedReader(new FileReader(file));
				String line = "";//reader.readLine();//skip header
//					System.out.println("Skipping header line : " + line); //TODO make this optional?
				while ((line = reader.readLine()) != null) {
					String[] data = line.split("\t");
					String vertex = data[0].toUpperCase();
					int degree = 0;
					if (directedGraph.containsVertex(vertex)) degree = directedGraph.inDegreeOf(vertex) + directedGraph.outDegreeOf(vertex);
					out += line + "\t" + degree + "\n";
				}
				
			} else {
				System.err.println("User Canceled");
				return;
			}
			
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		printString(new File("/home/jesse/Desktop/AtGenExpress_Development_averaged_with_degrees_hc.tsv"), out);
	}
	
	// Want to graph genes vs. the number of edges they explain as a cumulative total.  This method does the counting and outputs a file
	// we can work with in R to do the actual graph.
	private static void graphable() {
		// For HC set
//		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		
		// For predicted set
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
		UndirectedGraph<String, DefaultEdge> graph = NetworkFactory.generateNetwork(predictedDomains, DDIs);
		System.out.println(graph.edgeSet().size() + " edges in this network");
		
		ArrayList<GeneGeneInteraction> GGIs = new ArrayList<GeneGeneInteraction>();
		GGIs.addAll(NetworkFactory.convertEdgesToGGIs(graph));
		
		// For mapped set
//		ArrayList<GeneGeneInteraction> GGIs = readEdgeListFile();
		
		
		DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		for (GeneGeneInteraction GGI : GGIs) {
			directedGraph.addVertex(GGI.getSourceGene());
			directedGraph.addVertex(GGI.getTargetGene());
			directedGraph.addEdge(GGI.getSourceGene(), GGI.getTargetGene());
		}
		
		System.out.println(directedGraph.vertexSet().size() + " : " + directedGraph.edgeSet().size());
		
		String out = "";
		while (directedGraph.vertexSet().size() > 0 && directedGraph.edgeSet().size() > 0) {
			int maxDegree = 0;
			String maxVertex = "";
			for (String vertex : directedGraph.vertexSet()) {
				int degree = directedGraph.inDegreeOf(vertex) + directedGraph.outDegreeOf(vertex);
				if (degree > maxDegree) {
					maxDegree = degree;
					maxVertex = vertex;
				}
			}
//				System.out.println(maxVertex + " : " + maxDegree);
			out += maxVertex + "\t" + maxDegree + "\n";
			directedGraph.removeVertex(maxVertex);
//				System.out.println(directedGraph.vertexSet().size() + " -- " + directedGraph.edgeSet().size());
		}
		printString(new File("/home/jesse/Desktop/out.out"), out);
	}
	
	// like graphable but using different remove order
	private static void removeBasedOnPredictedOrder() {
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
		ArrayList<GeneGeneInteraction> hcGGIs = EdgeFileReader.readGGIList();
		UndirectedGraph<String, DefaultEdge> graph = NetworkFactory.generateNetwork(predictedDomains, DDIs);
		System.out.println(graph.edgeSet().size() + " edges in this network");
		
		ArrayList<GeneGeneInteraction> GGIs = new ArrayList<GeneGeneInteraction>();
		GGIs.addAll(NetworkFactory.convertEdgesToGGIs(graph));
		
		DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		for (GeneGeneInteraction GGI : GGIs) {
			directedGraph.addVertex(GGI.getSourceGene());
			directedGraph.addVertex(GGI.getTargetGene());
			directedGraph.addEdge(GGI.getSourceGene(), GGI.getTargetGene());
		}
		
		DirectedGraph<String, DefaultEdge> hcDirectedGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		for (GeneGeneInteraction GGI : hcGGIs) {
			hcDirectedGraph.addVertex(GGI.getSourceGene());
			hcDirectedGraph.addVertex(GGI.getTargetGene());
			hcDirectedGraph.addEdge(GGI.getSourceGene(), GGI.getTargetGene());
		}
		
		String out = "";
		while (!directedGraph.vertexSet().isEmpty()) {
			int maxDegree = 0;
			String maxVertex = "";
			for (String vertex : directedGraph.vertexSet()) {
				int degree = directedGraph.inDegreeOf(vertex) + directedGraph.outDegreeOf(vertex);
				if (degree > maxDegree) {
					maxDegree = degree;
					maxVertex = vertex;
				}
			}
			directedGraph.removeVertex(maxVertex);
			if (hcDirectedGraph.containsVertex(maxVertex)) {
				int degree = hcDirectedGraph.inDegreeOf(maxVertex) + hcDirectedGraph.outDegreeOf(maxVertex);
				if (degree != 0) {
					out += maxVertex + "\t" + degree + "\n";
				}
				hcDirectedGraph.removeVertex(maxVertex);
			}
		}
		
		// Not all the HC nodes and edges are in predicted set, so after we are done running through the ones in the predicted set, 
		// go through the remaining nodes in the HC set in sorted order 
		while (hcDirectedGraph.vertexSet().size() > 0 && hcDirectedGraph.edgeSet().size() > 0) {
			int maxDegree = 0;
			String maxVertex = "";
			for (String vertex : hcDirectedGraph.vertexSet()) {
				int degree = hcDirectedGraph.inDegreeOf(vertex) + hcDirectedGraph.outDegreeOf(vertex);
				if (degree > maxDegree) {
					maxDegree = degree;
					maxVertex = vertex;
				}
			}
			if (maxDegree != 0) {
				out += maxVertex + "\t" + maxDegree + "\n";
			}
			hcDirectedGraph.removeVertex(maxVertex);
		}
		printString(new File("/home/jesse/Desktop/cumulative_edges_hc_based_on_predicted_networks_order.out"), out);
	}
	
	// like graphable but using different remove order
	private static void removeBasedOnRandomRemoval() {
		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		for (GeneGeneInteraction GGI : GGIs) {
			directedGraph.addVertex(GGI.getSourceGene());
			directedGraph.addVertex(GGI.getTargetGene());
			directedGraph.addEdge(GGI.getSourceGene(), GGI.getTargetGene());
		}
		
//		// Purge edges to self  //TODO not sure if this is necessary or not, but seems to double count self edges (i.e. count as both inbound and outbound edges) if I leave them 
//		System.out.println(directedGraph.edgeSet().size());
//		Set<DefaultEdge> edges = new HashSet<DefaultEdge>();
//		edges.addAll(directedGraph.edgeSet());
//		for (DefaultEdge edge : edges) {
//			if (directedGraph.getEdgeSource(edge).equalsIgnoreCase(directedGraph.getEdgeTarget(edge))) {
//				directedGraph.removeEdge(edge);
//			}
//		}
//		System.out.println(directedGraph.edgeSet().size());
		
		String out = "";
		ArrayList<String> nodes = new ArrayList<String>();
		nodes.addAll(directedGraph.vertexSet());
		int nodeSize = nodes.size();
		for (int i = 0; i < nodeSize; i++) {
			Collections.shuffle(nodes);
			String vertex = nodes.get(0);
			int degree = directedGraph.inDegreeOf(vertex) + directedGraph.outDegreeOf(vertex);
			if (degree != 0) out += vertex + "\t" + degree + "\n";
			directedGraph.removeVertex(vertex);
			nodes.remove(vertex);
		}
		printString(new File("/home/jesse/Desktop/cumulative_edges_hc_random_order.out"), out);
	}
	
	public static ArrayList<GeneGeneInteraction> readEdgeListFile() {
		BufferedReader reader = null;
		ArrayList<GeneGeneInteraction> GGIs = new ArrayList<GeneGeneInteraction>(); 
		
		try {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Please select the");
			fc.setCurrentDirectory(new File("Data/Arabidopsis")); //TODO don't hardcode the file locations
			
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File uniprotFile = fc.getSelectedFile();
				
				reader = new BufferedReader(new FileReader(uniprotFile));
				String line = "";//reader.readLine();//skip header
//					System.out.println("Skipping header line : " + line); //TODO make this optional?
				while ((line = reader.readLine()) != null) {
					String[] data = line.split("\t");
					String[] edge = data[0].split(":");
					GGIs.add(new GeneGeneInteraction(edge[0], edge[1]));
				}
				
			} else {
				System.err.println("User Canceled");
				return null;
			}
			
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		finally {
			try {
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		return GGIs;
	}
	
	private static void createMap() {
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		
		// Get all unique genes in the predicted set
		HashSet<String> geneNames = new HashSet<String>();
		for (PredictedDomain predictedDomain : predictedDomains) {
			geneNames.add(predictedDomain.getGeneName());
		}
		
		// Get all unique genes in the high confidence set.
		for (GeneGeneInteraction ggi : GGIs) {
			geneNames.add(ggi.getSourceGene());
			geneNames.add(ggi.getTargetGene());
		}
		System.out.println(geneNames.size());
		String out = "";
		for (String geneName : geneNames) {
			try {
//				System.out.println(geneName + "\t" + EutilsGetGene(geneName));
				out += geneName + "\t" + EutilsGetGene(geneName) + "\n";
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		printString(new File("/home/jesse/Desktop/eutils_map_with_hc.tab"), out);
	}
	
	private static String EutilsGetGene(String gene) throws IOException {
		URL url = new URL("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&term="+gene+"[gene]");
		URLConnection con = url.openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		String body;
		body = IOUtils.toString(in, encoding);
		
		String pattern = "<Id>(\\d+)</Id>";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(body);
		ArrayList<String> matches = new ArrayList<String>();
		while (m.find()) {
			matches.add(m.group(1));
		}
		
		if (matches.isEmpty()) return "";
		else return matches.get(0);
	}
	
	
	private static ArrayList<String> mappableCoexp() {
		HashMap<String, String> map = readMap();
		HashMap<String, String> reverseMap = new HashMap<String, String>();
		for (String key : map.keySet()) {
			reverseMap.put(map.get(key), key);
		}
		
		File folder = new File("/home/jesse/Downloads/Ath-m.v15-08.G20836-S15275.rma.mrgeo.d/");
		File[] listOfFiles = folder.listFiles();
		
		int notFound = 0;
		ArrayList<String> mappedCoexp = new ArrayList<String>();
		ArrayList<String> unmappedCoexp = new ArrayList<String>();
		for (File file : listOfFiles) {
			if (reverseMap.containsKey(file.getName())) {
//				System.out.println(reverseMap.get(file.getName()));
				mappedCoexp.add(reverseMap.get(file.getName()));
			} else {
				notFound++;
				unmappedCoexp.add(file.getName());
				System.out.println(file.getName());
			}
		}
		
		System.out.println("For the coexp data, we have " + listOfFiles.length + " unique genes. We fail to map " + notFound);
		return mappedCoexp;
	}
	
	
	/**
	 * Simple test to see how many of the genes we are working with can be converted between gene name and gene ID
	 * > I found that a dump of all uniprot ara gene/proteins with the names and ids provided the best results so far. 
	 */
	private static void percentMappableGenes() {
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		HashMap<String, String> map = readMap();
		
		int found;
		int notFound;
		HashSet<String> genesWithPredictedDomains = new HashSet<String>();
		HashSet<String> genesInHCSet = new HashSet<String>();
		for (PredictedDomain predictedDomain : predictedDomains) {
			genesWithPredictedDomains.add(predictedDomain.getGeneName());
		}
		for (GeneGeneInteraction edge : GGIs) {
			genesInHCSet.add(edge.getSourceGene());
			genesInHCSet.add(edge.getTargetGene());
		}
		
		found = 0;
		notFound = 0;
		HashSet<String> unmappedPred = new HashSet<String>();
		for (String gene : genesWithPredictedDomains) {
			if (map.containsKey(gene)) found++;
			else {
				notFound++;
				unmappedPred.add(gene);
			}
		}
		System.out.println("For the predicted set, we have " + genesWithPredictedDomains.size() + " unique genes. We map " + found + " and fail to map " + notFound);
		
		found = 0;
		notFound = 0;
		HashSet<String> unmappedHC = new HashSet<String>();
		for (String gene : genesInHCSet) {
			if (map.containsKey(gene)) found++;
			else {
				notFound++;
				unmappedHC.add(gene);
			}
		}
		System.out.println("For the hc set, we have " + genesInHCSet.size() + " unique genes. We map " + found + " and fail to map " + notFound);
		
		// Give more details about this un-mapped set
		String unmappedOut = "";
		for (String unmappedGene : unmappedPred) {
			unmappedOut += unmappedGene + "\n";
		}
		printString(new File("/home/jesse/Desktop/unmappedPred.out"), unmappedOut);
		unmappedOut = "";
		for (String unmappedGene : unmappedHC) {
			unmappedOut += unmappedGene + "\n";
		}
		printString(new File("/home/jesse/Desktop/unmappedHC.out"), unmappedOut);
		
		
		// Look for genes with no coexpression data
		String noDataPred = "";
		int noCoexp = 0;
		for (String gene : genesWithPredictedDomains) {
			File temp = new File("/home/jesse/Downloads/Ath-m.v15-08.G20836-S15275.rma.mrgeo.d/" + map.get(gene));
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(temp));
			} catch (FileNotFoundException e) {
				noDataPred += gene + "\n";
				noCoexp++;
			}
		}
		System.out.println("For the predicted set, we have " + genesWithPredictedDomains.size() + " unique genes. We have no data for " + noCoexp);
		printString(new File("/home/jesse/Desktop/noDataPred.out"), noDataPred);
		
		String noDataHC = "";
		noCoexp = 0;
		for (String gene : genesInHCSet) {
			File temp = new File("/home/jesse/Downloads/Ath-m.v15-08.G20836-S15275.rma.mrgeo.d/" + map.get(gene));
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(temp));
			} catch (FileNotFoundException e) {
				noDataHC += gene + "\n";
				noCoexp++;
			}
		}
		System.out.println("For the hc set, we have " + genesInHCSet.size() + " unique genes. We have no data for " + noCoexp);
		printString(new File("/home/jesse/Desktop/noDataHC.out"), noDataHC);
		
		
//		ArrayList<String> unmappedCoexp = mappableCoexp();
		
		
	}
	
	private static void getCoexpressionOfPredictedEdges() {
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		HashMap<String, String> map = readMap();
		UndirectedGraph<String, DefaultEdge> graph = NetworkFactory.generateNetwork(predictedDomains, DDIs);
		System.out.println(graph.edgeSet().size() + " edges in this network");
		
		ArrayList<GeneGeneInteraction> predictedGGIs = new ArrayList<GeneGeneInteraction>();
		predictedGGIs.addAll(NetworkFactory.convertEdgesToGGIs(graph));
		
		String out = "";
		int failToMap = 0;
		int failToFindData = 0;
		HashSet<String> unmapped = new HashSet<String>();
		HashSet<String> noData = new HashSet<String>();
//		for (DefaultEdge edge : graph.edgeSet()) {
//			String source = map.get(graph.getEdgeSource(edge));
//			String target = map.get(graph.getEdgeTarget(edge));
		for (GeneGeneInteraction ggi : predictedGGIs) {
			String source = map.get(ggi.getSourceGene());
			String target = map.get(ggi.getTargetGene());
			if (source == null || target == null || source.isEmpty() || target.isEmpty()) {
//				System.err.println("Edge " + graph.getEdgeSource(edge) + " : " + graph.getEdgeTarget(edge) + " not mapped");
				failToMap++;
				if (source == null || source.isEmpty()) unmapped.add(ggi.getSourceGene());
				if (target == null || target.isEmpty()) unmapped.add(ggi.getTargetGene());
				continue;
			}
			File temp = new File("/home/jesse/Downloads/Ath-m.v15-08.G20836-S15275.rma.mrgeo.d/" + source);
			
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(temp));
				String line;
				try {
					line = reader.readLine();
					while ((line = reader.readLine()) != null) {
						String[] data = line.split("\t");
						if (data[0].equalsIgnoreCase(target)) {
							if (GGIs.contains(ggi)) {
								out += source + ":" + target + "\t" + data[2] + "\tTP\n";
//								System.out.println(source + ":" + target + "\t" + data[2] + "\tTP");
							} else {
								out += source + ":" + target + "\t" + data[2] + "\tFP\n";
//								System.out.println(source + ":" + target + "\t" + data[2] + "\tFP");
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.err.println("File " + source + "is empty");
				}
			} catch (FileNotFoundException e) {
//				System.err.println("Missing file : " + "/home/jesse/Downloads/Ath-m.v15-08.G20836-S15275.rma.mrgeo.d/" + source);
				failToFindData++;
				noData.add(ggi.getSourceGene() + "\t" + ggi.getTargetGene());
			}
		}
		System.out.println("Failed to map at least one of the genes from name to entrez id for " + failToMap + " edges"); 
		System.out.println("Failed to find coexpression data for " + failToFindData + " edges");
		printString(new File("/home/jesse/Desktop/testCoexp.out"), out);
//		System.out.println(map.get(graph.getEdgeSource(edge)) + "\t" + map.get(graph.getEdgeTarget(edge)));
		
		// Give more details about this un-mapped set
		String unmappedOut = "";
//		for (String unmappedGene : unmapped) {
////			System.out.println(unmappedGene);
//			unmappedOut += unmappedGene + "\n";
//			for (GeneGeneInteraction edge : GGIs) {
//				if (edge.getSourceGene().equalsIgnoreCase(unmappedGene) || edge.getTargetGene().equalsIgnoreCase(unmappedGene)) {
////					System.out.println("\t" + edge.getSourceGene() + ":" + edge.getTargetGene());
//					unmappedOut += "\t" + edge.getSourceGene() + ":" + edge.getTargetGene() + "\n";
//				}
//			}
//		}
		
		// Give more details about this no-data set
		String noDataOut = "";
		for (String noDataEdge : noData) {
			noDataOut += noDataEdge + "\n";
		}
		
		printString(new File("/home/jesse/Desktop/unmapped.out"), unmappedOut);
		printString(new File("/home/jesse/Desktop/noData.out"), noDataOut);
	}
	
	private static void getCoexpressionOfHCEdges() {
		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		HashMap<String, String> map = readMap();
		System.out.println(GGIs.size() + " edges in the hc network");
		
		String out = "";
		int failToMap = 0;
		int failToFindData = 0;
		HashSet<String> unmapped = new HashSet<String>();
		HashSet<String> noData = new HashSet<String>();
		for (GeneGeneInteraction edge : GGIs) {
			String source = map.get(edge.getSourceGene());
			String target = map.get(edge.getTargetGene());
			if (source == null || target == null || source.isEmpty() || target.isEmpty()) {
				failToMap++;
				if (source == null || source.isEmpty()) unmapped.add(edge.getSourceGene());
				if (target == null || target.isEmpty()) unmapped.add(edge.getTargetGene());
				continue;
			}
			File temp = new File("/home/jesse/Downloads/Ath-m.v15-08.G20836-S15275.rma.mrgeo.d/" + source);
			
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(temp));
				String line;
				try {
					boolean found = false;
					line = reader.readLine();
					while ((line = reader.readLine()) != null && !found) {
						String[] data = line.split("\t");
						if (data[0].equalsIgnoreCase(target)) {
							out += source + ":" + target + "\t" + data[2] + "\n";
							found = true;
						}
					}
					if (!found) failToFindData++;
				} catch (IOException e) {
					System.err.println("File " + source + "is empty");
				}
			} catch (FileNotFoundException e) {
				failToFindData++;
				noData.add(source);
			}
		}
//		for (String data : noData) System.out.println(data);
		System.out.println("Failed to map at least one of the genes from name to entrez id for " + failToMap + " edges due to " + unmapped.size() + " unmapped genes"); 
		System.out.println("Failed to find coexpression data for " + failToFindData + " edges");
		printString(new File("/home/jesse/Desktop/testCoexp_TP.out"), out);
		
		// Give more details about this un-mapped set
		for (String unmappedGene : unmapped) {
			System.out.println(unmappedGene);
			for (GeneGeneInteraction edge : GGIs) {
				if (edge.getSourceGene().equalsIgnoreCase(unmappedGene) || edge.getTargetGene().equalsIgnoreCase(unmappedGene)) {
					System.out.println("\t" + edge.getSourceGene() + ":" + edge.getTargetGene());
				}
			}
		}
	}
	
	public static HashMap<String, String> readMap() {
		BufferedReader reader = null;
		HashMap<String, String> map = new HashMap<String, String>(); 
		
		try {
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("Data")); //TODO don't hardcode the file locations
			
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				System.out.println("Reading map file..."); //TODO only on verbose flag
				File mapFile = fc.getSelectedFile();
				
				reader = new BufferedReader(new FileReader(mapFile));
				String line = reader.readLine();//skip header
				System.out.println("Skipping header line : " + line); //TODO only on verbose flag
				while ((line = reader.readLine()) != null) {
					String[] data = line.split("\t");
					String geneName = data[0].toUpperCase();
					String entrezID = "";
					if (data.length > 1 && !data[1].isEmpty()) {
						entrezID = data[1].replaceAll(";", ""); //TODO handle when many-to-many happens
						map.put(geneName, entrezID);
					}
				}
				
			} else {
				System.err.println("User Canceled");
				return null;
			}
			
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		finally {
			try {
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		System.out.println("Done reading map file!"); //TODO only on verbose flag
		return map;
	}
	
	/**
	 * Simple function to print a string to the specified file location.
	 * 
	 * @param fileName
	 * @param printString
	 */
	protected static void printString(File file, String printString) {
		PrintStream o = null;
		try {
			o = new PrintStream(file);
			o.println(printString);
			o.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}