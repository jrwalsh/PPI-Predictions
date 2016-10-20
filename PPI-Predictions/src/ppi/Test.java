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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

import org.apache.commons.io.IOUtils;
import org.jgrapht.UndirectedGraph;
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
//		percentMappableGenes();
		getCoexpressionOfPredictedEdges();
//		getCoexpressionOfHCEdges();
		
//		createMap();
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
		for (String gene : genesWithPredictedDomains) {
			if (map.containsKey(gene)) found++;
			else notFound++;
		}
		System.out.println("For the predicted set, we have " + genesWithPredictedDomains.size() + " unique genes. We map " + found + " and fail to map " + notFound);
		
		found = 0;
		notFound = 0;
		for (String gene : genesInHCSet) {
			if (map.containsKey(gene)) found++;
			else notFound++;
		}
		System.out.println("For the hc set, we have " + genesInHCSet.size() + " unique genes. We map " + found + " and fail to map " + notFound);
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
//		for (DefaultEdge edge : graph.edgeSet()) {
//			String source = map.get(graph.getEdgeSource(edge));
//			String target = map.get(graph.getEdgeTarget(edge));
		for (GeneGeneInteraction ggi : predictedGGIs) {
			String source = map.get(ggi.getSourceGene());
			String target = map.get(ggi.getTargetGene());
			if (source == null || target == null || source.isEmpty() || target.isEmpty()) {
//				System.err.println("Edge " + graph.getEdgeSource(edge) + " : " + graph.getEdgeTarget(edge) + " not mapped");
				failToMap++;
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
			}
		}
		System.out.println("Failed to map at least one of the genes from name to entrez id for " + failToMap + " edges"); 
		System.out.println("Failed to find coexpression data for " + failToFindData + " edges");
		printString(new File("/home/jesse/Desktop/testCoexp.out"), out);
//		System.out.println(map.get(graph.getEdgeSource(edge)) + "\t" + map.get(graph.getEdgeTarget(edge)));
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
					line = reader.readLine();
					while ((line = reader.readLine()) != null) {
						String[] data = line.split("\t");
						if (data[0].equalsIgnoreCase(target)) {
							out += source + ":" + target + "\t" + data[2] + "\n";
						}
					}
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