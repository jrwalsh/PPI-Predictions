package ppi.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import ppi.custom.PredictedDomain;

public class NodeFileReader {
	public static ArrayList<PredictedDomain> readNodeList() {
		BufferedReader reader = null;
		ArrayList<PredictedDomain> predictedDomains = new ArrayList<PredictedDomain>(); 
		
		try {
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); //TODO use this LookAndFeel when on windows
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("Data/Arabidopsis")); //TODO don't hardcode the file locations
			
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File uniprotFile = fc.getSelectedFile();
				
				reader = new BufferedReader(new FileReader(uniprotFile));
				String line = reader.readLine();//skip header
				while ((line = reader.readLine()) != null) {
					String[] data = line.split("\t");
					String geneName = data[1].substring(0, data[1].indexOf(".")); //TODO use different rules for ara vs maize
					predictedDomains.add(new PredictedDomain(data[0], data[1], geneName, data[2], data[3], data[4], data[5], data[6]));
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
		return predictedDomains;
	}
}
