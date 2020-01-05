package MasterThesis.Thesis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class ChangeNameToKey {
	public static void main(String[]args) {
		ChangeNameToKey change = new ChangeNameToKey();
		change.change();
	}
	
	private HashMap<String, String> entities;
	private HashMap<String, String> relations;
	
	private void change() {
		String path = "./Datasets/DBPediaSample/";
		entities = new HashMap<String, String>();
		relations = new HashMap<String, String>();
		this.fillHashMaps(path + "entity2idNew.txt", entities);
		this.fillHashMaps(path + "relation2idNew.txt", relations);
		this.transformNameToKey(path + "train.txt", path + "train2id.txt");
		this.transformNameToKey(path + "test.txt", path + "test2id.txt");
		this.transformNameToKey(path + "valid.txt", path + "valid2id.txt");
	}
	
	private void fillHashMaps(String pathSample, HashMap<String, String> sample) {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(pathSample);
			br = new BufferedReader(fr);
			String line = br.readLine();
			while(line != null) {
				String[] split = line.split("\\s+");
				System.out.println(split[0] + " " +split[1]);
				sample.put(split[0], split[1]);
				line = br.readLine();
			}
			fr.close();
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void transformNameToKey(String pathOld, String pathNew) {
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fr = new FileReader(pathOld);
			br = new BufferedReader(fr);
			fw = new FileWriter(pathNew);
			bw = new BufferedWriter(fw);
			
			String readLine = br.readLine();
			String writeLine;
			while(readLine != null) {
				System.out.println(readLine);
				String[] split = readLine.split("\\s+");
				String keySubject = entities.get(split[0]);
				String keyObject = entities.get(split[1]);
				String keyRelation = relations.get(split[2]);
				
				writeLine = keySubject + " " + keyObject + " " + keyRelation;
				System.out.println(writeLine);
				bw.write(writeLine);
				bw.newLine();
				readLine = br.readLine();
			}
			
			br.close();
			fr.close();
			bw.close();
			fw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
