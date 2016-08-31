package ppi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import ppi.custom.DomainDomainInteraction;
import ppi.custom.DomainNode;
import ppi.custom.PredictedDomain;
import ppi.preprocessing.EdgeFileReader;
import ppi.preprocessing.NodeFileReader;


public class Main {
	
	public static void main(String args[]) {
		ArrayList<PredictedDomain> predictedDomains = NodeFileReader.readNodeList();
		for (PredictedDomain predDomain : predictedDomains) {
			System.out.println(predDomain);
		}
		
		ArrayList<DomainDomainInteraction> DDIs = EdgeFileReader.readDDIList();
		for (DomainDomainInteraction ddi : DDIs) {
			System.out.println(ddi);
		}
	}
	
}