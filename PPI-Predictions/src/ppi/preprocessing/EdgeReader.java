package ppi.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import ppi.custom.DomainNode;

public class EdgeReader {
	public static void readNodeList() {
		BufferedReader reader = null;
//		HashMap<String, DomainNode> domainMap = new HashMap<String, DomainNode>();
		UndirectedGraph<DomainNode, DefaultEdge> predictions = new SimpleGraph<DomainNode, DefaultEdge>(DefaultEdge.class);
		
		try {
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("Data/Arabidopsis"));
			
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File uniprotFile = fc.getSelectedFile();
				
				reader = new BufferedReader(new FileReader(uniprotFile));
				String line = reader.readLine();//skip header
				while ((line = reader.readLine()) != null) {
					String[] data = line.split("\t");
					predictions.addVertex(new ppi.custom.DomainNode(data[0], data[1], data[2], data[3], data[4], data[5], data[6]));
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
