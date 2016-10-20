package ppi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JFileChooser;

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
	private final String mapGeneNamesToIDs = "/Data/eutils_map_with_hc.tab";
	
	public static void main(String args[]) {
		Long start = System.currentTimeMillis();
		
		test();
//		basicStatistics();
//		countDDIFrequency();
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
		HashMap<String, String> map = readMap();
		UndirectedGraph<String, DefaultEdge> graph = NetworkFactory.generateNetwork(predictedDomains, DDIs);
		System.out.println(graph.edgeSet().size() + " edges in this network");
		
		HashSet<String> set = new HashSet<String>();
		for (DefaultEdge edge : graph.edgeSet()) {
			set.add(graph.getEdgeSource(edge));
			set.add(graph.getEdgeTarget(edge));
//			System.out.println(map.get(graph.getEdgeSource(edge)) + "\t" + map.get(graph.getEdgeTarget(edge)));
		}
		int hit = 0;
		int miss = 0;
		String out = "";
		for (String item : set) {
			if (map.containsKey(item)) hit++;
			else miss++;
			out += item + "\n";
		}
		System.out.println(hit + "\t" + miss);
		printString(new File("/home/jesse/Desktop/test.out"), out);
		
//		DomainDomainInteraction ddia = new DomainDomainInteraction("PF00319", "PF01486", null);
//		DomainDomainInteraction ddib = new DomainDomainInteraction("PF01486", "PF00319", null);
//		System.out.println(ddia.equals(ddib));
//		
//		HashSet<DomainDomainInteraction> ddis = new HashSet<DomainDomainInteraction>();
//		ddis.add(ddia);
//		ddis.add(ddib);
//		System.out.println(ddis.size());
//		System.out.println(ddia.hashCode());
//		System.out.println(ddib.hashCode());
//		
//		final int prime = 31;
//		int result = 1;
//		System.out.println(result);
//		result = prime * result + (("PF01486" == null) ? 0 : "PF00319".hashCode());
//		System.out.println(result);
//		result = prime * result + (("PF00319" == null) ? 0 : "PF01486".hashCode());
//		System.out.println(result);
		
//		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
//		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
//		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
//		
//		HashSet<DomainDomainInteraction> assumed_DDI_set = NetworkFactory.convertGGItoDDI(predictedDomains, GGIs);
//		HashSet<DomainDomainInteraction> DDI_intersection = new HashSet<DomainDomainInteraction>();
//		DDI_intersection.addAll(DDIs);
//		DDI_intersection.retainAll(assumed_DDI_set);
//		ArrayList<DomainDomainInteraction> DDI_set = new ArrayList<DomainDomainInteraction>();
//		DDI_set.addAll(DDI_intersection);
//		
//		UndirectedGraph<String, DefaultEdge> predictedGraph = NetworkFactory.generateNetwork(predictedDomains, DDI_set);
//		ArrayList<GeneGeneInteraction> predictedGGIs = new ArrayList<GeneGeneInteraction>();
//		ArrayList<GeneGeneInteraction> tpGGIs = new ArrayList<GeneGeneInteraction>();
//		predictedGGIs.addAll(NetworkFactory.convertEdgesToGGIs(predictedGraph));
//		tpGGIs.addAll(predictedGGIs);
//		tpGGIs.retainAll(GGIs); // Intersection gives all TP results
//		
//		System.out.println("HCSet\tPredictions\tTP");
//		System.out.println(GGIs.size() + "\t" + predictedGGIs.size() + "\t" + tpGGIs.size());
//		
//		System.out.println("Size of DDI set");
//		System.out.println(DDI_set.size());
	}
	
	public static HashMap<String, String> readMap() {
		BufferedReader reader = null;
		HashMap<String, String> map = new HashMap<String, String>(); 
		
		try {
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); //TODO use this LookAndFeel when on windows
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
					String entrezID = data[1].replaceAll(";", ""); //TODO handle when many-to-many happens
					map.put(geneName, entrezID);
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
	 * Count how often each DDI occurs in the high confidence set, assuming every domain on each genes interacts with each domain on its partner gene. 
	 */
	private static void countDDIFrequency() {
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		HashSet<DomainDomainInteraction> DDIset = NetworkFactory.convertGGItoDDI(predictedDomains, GGIs);
		
		HashMap<String, HashSet<String>> geneToDomainMap = new HashMap<String, HashSet<String>>();
		for (PredictedDomain predictedDomain : predictedDomains) {
			if (geneToDomainMap.containsKey(predictedDomain.getGeneName())) {
				geneToDomainMap.get(predictedDomain.getGeneName()).add(predictedDomain.getDomainName());
			} else {
				HashSet<String> temp = new HashSet<String>();
				temp.add(predictedDomain.getDomainName());
				geneToDomainMap.put(predictedDomain.getGeneName(), temp);
			}
		}
		
		String outString = "DDI_Source\tDDI_Target\tFrequency\n";
		for (DomainDomainInteraction DDI : DDIset) {
			outString += DDI.getSourceDomain() + "\t" + DDI.getTargetDomain();
			int frequency = 0;
			for (GeneGeneInteraction GGI : GGIs) {
				if (!geneToDomainMap.containsKey(GGI.getSourceGene()) || !geneToDomainMap.containsKey(GGI.getTargetGene())) continue;
				if (geneToDomainMap.get(GGI.getSourceGene()).contains(DDI.getSourceDomain()) && geneToDomainMap.get(GGI.getTargetGene()).contains(DDI.getTargetDomain())) {
					frequency++;
				} else if (geneToDomainMap.get(GGI.getSourceGene()).contains(DDI.getTargetDomain()) && geneToDomainMap.get(GGI.getTargetGene()).contains(DDI.getSourceDomain())) {
					frequency++;
				}
			}
			outString += "\t" + frequency + "\n";
		}
		
		try {
			FileWriter fileWriter = new FileWriter("DDI_frequency.tab", false);
			fileWriter.write(outString + "\n");
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		
		HashSet<DomainDomainInteraction> DDIset = NetworkFactory.convertGGItoDDI(predictedDomains, GGIs);
		ArrayList<DomainDomainInteraction> DDIs = new ArrayList<DomainDomainInteraction>();
		DDIs.addAll(DDIset);
		UndirectedGraph<String, DefaultEdge> graph = NetworkFactory.generateNetwork(predictedDomains, DDIs);
		
		System.out.println(graph.edgeSet().size() + " edges in this network");
		
		
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
		int numRuns = 3;
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		ArrayList<GeneGeneInteraction> GGIs = EdgeFileReader.readGGIList();
		
		// Write header
		try {
			FileWriter fileWriter = new FileWriter("outfile.txt", false);
			fileWriter.write("TruthSet\tPredictions\tTPs\tFPs_filtered\tUniqueGenesInTestSet\n");
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
			predictedGGIs.addAll(NetworkFactory.convertEdgesToGGIs(predictedGraph));
			
			ArrayList<GeneGeneInteraction> tpGGIs = new ArrayList<GeneGeneInteraction>();
			tpGGIs.addAll(GGI_Test);
			tpGGIs.retainAll(predictedGGIs); // Intersection gives all TP results
			
			ArrayList<GeneGeneInteraction> fpGGIs = new ArrayList<GeneGeneInteraction>();
			fpGGIs.addAll(predictedGGIs);
			fpGGIs.removeAll(tpGGIs);
			
			// Filter FP set so that only FPs where both source and target are included in the test set are considered FPs
			HashSet<String> genesInTestSet = new HashSet<String>();
			for (GeneGeneInteraction GGI : GGI_Test) {
				genesInTestSet.add(GGI.getSourceGene());
				genesInTestSet.add(GGI.getTargetGene());
			}
			ArrayList<GeneGeneInteraction> fpGGIs_filtered = new ArrayList<GeneGeneInteraction>();
			for (GeneGeneInteraction GGI : fpGGIs) {
				if (genesInTestSet.contains(GGI.getSourceGene()) && genesInTestSet.contains(GGI.getTargetGene())) {
					fpGGIs_filtered.add(GGI);
				}
			}
			
			String line = GGIs.size() + "\t" + predictedGGIs.size() + "\t" + tpGGIs.size() + "\t" + fpGGIs_filtered.size() + "\t" + genesInTestSet.size();
			System.out.println("TruthSet\tPredictions\tTPs\tFPs_filtered\tUniqueGenesInTestSet");
			System.out.println(line);
			
			try {
				FileWriter fileWriter = new FileWriter("outfile.txt", true);
				fileWriter.write(line + "\n");
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
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