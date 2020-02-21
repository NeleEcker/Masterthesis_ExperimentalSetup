package MasterThesis.Thesis;

import java.io.FileOutputStream;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

public class NegSamplesDBpedia {
	public static void main(String[] args) {
		String oldVersionLocation = "/home/necker/Documents/Datasets/mappingbased_objects_en_2016_04.ttl";
		String newVersionLocation = "/home/necker/Documents/Datasets/mappingbased_objects_en_2016_10.ttl";
		NegSamplesDBpedia diff = new NegSamplesDBpedia();
		Model difference = diff.getDifference(oldVersionLocation, newVersionLocation);
		diff.storeDifference(difference);
	}
	
	public Model getDifference(String oldVersionLocation, String newVersionLocation) {
		Model oldVersion = this.loadModel(oldVersionLocation);
		Model newVersion = this.loadModel(newVersionLocation);
		
		Model difference = this.findDifference(oldVersion, newVersion);
		return difference;
	}
	
	private Model loadModel(String modelLocation) {
		Dataset ds = this.createDataset(modelLocation);
		Model model = ds.getDefaultModel();
		FileManager.get().readModel(model, modelLocation);
		return model;
	}
	
	private Dataset createDataset(String modelLocation) {
		String datasetLocation = modelLocation + "_db";
		Dataset ds = TDBFactory.createDataset(datasetLocation);
		return ds;
	}
	
	private Model findDifference(Model oldVersion, Model newVersion) {
		Model difference  = oldVersion.difference(newVersion);
		return difference;
	}
	
	private void storeDifference(Model difference) {
		String fileLocation = "/home/necker/Documents/Datasets/Difference.ttl";
		FileOutputStream fo = null;
		try {
			fo = new FileOutputStream(fileLocation);
			difference.write(fo, "TURTLE");
			fo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
