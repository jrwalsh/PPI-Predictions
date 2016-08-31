package ppi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import ppi.predictions.DomainNode;

public class Main {
	
	public static void main(String args[]) {
		readNodeList();
	}

	public static void readNodeList() {
		BufferedReader reader = null;
		HashMap<String, DomainNode> domainMap = new HashMap<String, DomainNode>();
		UndirectedGraph<DomainNode, DefaultEdge> predictions = new SimpleGraph<DomainNode, DefaultEdge>(DefaultEdge.class);
		
		try {
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			JFileChooser fc = new JFileChooser();
			
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File uniprotFile = fc.getSelectedFile();
				
				reader = new BufferedReader(new FileReader(uniprotFile));
				String line = reader.readLine();//skip header
				while ((line = reader.readLine()) != null) {
					String[] data = line.split("\t");
					System.out.println(line);
//					predictions.addVertex(data[0]);
				}
			} else {
				System.err.println("User Canceled");
				return;
			}
			
			for (Object v : predictions.vertexSet()) {
				System.out.println(v.toString());
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
	}
}