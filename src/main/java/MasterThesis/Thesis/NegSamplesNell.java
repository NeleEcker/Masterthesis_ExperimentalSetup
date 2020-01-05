package MasterThesis.Thesis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class NegSamplesNell {
	
	public final String directory = "/home/necker/eclipse-workspace/Thesis/Datasets/FeedbackFiles";
	public final String fileEnding = ".csv";
	public static ArrayList<String> negSamples;

	public static void main(String[] args) {
		NegSamplesNell sample = new NegSamplesNell();
		sample.getNegSamples();
	}
	
	private void getNegSamples() {
		negSamples = new ArrayList<String>();
		ArrayList<String> fileNames = this.getFileNames(this.directory);
		for(String fileName : fileNames) {
			this.readCsvFile(fileName);
		}
		System.out.println(negSamples.size());
		this.writeNegSampleFile();
	}
	
	private ArrayList<String> getFileNames (String directory) {
		ArrayList<String> fileNames = new ArrayList<String>();
		File folder = new File(directory);
		for(File fileEntry : folder.listFiles()) {
			fileNames.add(fileEntry.getName());
		}
		return fileNames;
	}
	
	private void readCsvFile(String fileName) {
		final String fileDirectory = directory + "/" + fileName;
		try {
			BufferedReader csvReader = new BufferedReader(new FileReader(fileDirectory));
			String row;
			int entity = 100;
			int relation = 100;
			int value = 100;
			int action = 100;
			int index = 0;
			while((row = csvReader.readLine()) != null) {
				if(index == 0) {
					System.out.println(row);
					index = 1;
				}
				String[]data = row.split("\"");
				ArrayList<String> elements = new ArrayList<String>(Arrays.asList(data));
				if(entity == 100 || relation == 100 || action == 100 || value == 100) {
					entity = elements.indexOf("Entity");
					relation = elements.indexOf("Relation");
					action = elements.indexOf("Action");
					value = elements.indexOf("Value");
					if(entity == -1) {
						entity = elements.indexOf("\"Entity\"");
						relation = elements.indexOf("\"Relation\"");
						value = elements.indexOf("\"Value\"");
						action = elements.indexOf("\"Action\"");
					}
				}
				if(action == -1) break;
				String firstCharacter = elements.get(action).substring(0, 1);
				if(firstCharacter.equals("n")) {
					String head = elements.get(entity);
					String label = elements.get(relation);
					String tail = elements.get(value);
					String negSample = head + " " + label + " " + tail;
					negSamples.add(negSample);
				}
			}
			csvReader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeNegSampleFile() {
		String writeDirectory = directory + "/" + "NellNegSample.txt";
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(writeDirectory));
			for(String line : negSamples) {
				bw.write(line);
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
