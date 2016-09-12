package ppi.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import ppi.custom.DomainDomainInteraction;
import ppi.custom.DomainNode;
import ppi.custom.GeneGeneInteraction;
import ppi.custom.PredictedDomain;

/**
 * Read in a list of edges.  Currently support Domain-Domain-Interaction (DDI) edge lists and gene-gene interaction (GGI) lists.
 * 
 * @author jesse
 *
 */
public class EdgeFileReader {
	
	public static ArrayList<DomainDomainInteraction> readDDIList() {
		BufferedReader reader = null;
		ArrayList<DomainDomainInteraction> DDIs = new ArrayList<DomainDomainInteraction>(); 
		
		try {
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); //TODO use this LookAndFeel when on windows
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("Data/iPfam")); //TODO don't hardcode the file locations
			
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				System.out.println("Reading edge file..."); //TODO only on verbose flag
				File uniprotFile = fc.getSelectedFile();
				
				reader = new BufferedReader(new FileReader(uniprotFile));
				String line = reader.readLine();//skip header
				System.out.println("Skipping header line : " + line); //TODO make this optional?
				while ((line = reader.readLine()) != null) {
					String[] data = line.split("\t");
					DDIs.add(new DomainDomainInteraction(data[0], data[1], data[2]));
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
		System.out.println("Done reading edge file!"); //TODO only on verbose flag
		return DDIs;
	}
	
	public static ArrayList<GeneGeneInteraction> readGGIList() {
		BufferedReader reader = null;
		ArrayList<GeneGeneInteraction> DDIs = new ArrayList<GeneGeneInteraction>(); 
		
		try {
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); //TODO use this LookAndFeel when on windows
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Please select the");
			fc.setCurrentDirectory(new File("Data/Arabidopsis")); //TODO don't hardcode the file locations
			
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File uniprotFile = fc.getSelectedFile();
				
				reader = new BufferedReader(new FileReader(uniprotFile));
				String line = reader.readLine();//skip header
				System.out.println("Skipping header line : " + line); //TODO make this optional?
				while ((line = reader.readLine()) != null) {
					String[] data = line.split("\t");
					DDIs.add(new GeneGeneInteraction(data[0], data[1]));
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
		return DDIs;
	}
}
