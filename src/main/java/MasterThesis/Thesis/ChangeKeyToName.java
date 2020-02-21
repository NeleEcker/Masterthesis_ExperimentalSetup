package MasterThesis.Thesis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class ChangeKeyToName {

	public static void main(String[] args) {
		ChangeKeyToName ana = new ChangeKeyToName();
		ana.convertAnalysisResults();
	}
	
	private void convertAnalysisResults() {
		String locationEntities = "/data/Analyse/entity2idnell.txt";
		String locationResults = "/data/Analyse/NellPredictTail.txt";
		String locationTransformedResults = "/data/Analyse/NellPredictTailTransformed_CorrectedV3.txt";
		HashMap<String, String> entities = this.readFileToMap(locationEntities);
		ArrayList<String> results = this.transformResults(locationResults, entities);
		this.writeResults(locationTransformedResults, results);
	}
	
	private HashMap<String, String> readFileToMap(String location) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(location));
			String line = br.readLine();
			while((line = br.readLine()) != null) {
				String[]elements = line.split("\\s+");
				map.put(elements[1], elements[0]);
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	private ArrayList<String> transformResults(String location, HashMap<String, String> entities) {
		ArrayList<String> results = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(location));
			String line;
			while((line = br.readLine()) != null) {
				System.out.println(line);
				String[]elements = line.split("\\s+");
				String newLine = "";
				for(int i=0; i<elements.length; i++) {
					String name = entities.get(elements[i]);
					newLine += name;
					newLine += " ";
				}
				System.out.println(newLine);
				results.add(newLine);
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return results;
	}
	
	private void writeResults(String location, ArrayList<String> results) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(location));
			for(int i=0; i<results.size(); i++) {
				String result = results.get(i);
				bw.write(result);
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
