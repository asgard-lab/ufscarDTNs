package classification;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.classifiers.trees.J48;
import weka.core.Instance;
//import weka.core.Instances;
//import weka.classifiers.trees.J48; //trocar a NaiveBayes para comparar
import weka.core.Instances;

public class WekaTheOne {

	private Instances dataset;
	private J48 Algorithm = null;
	private FileReader fileReader = null;

	public void readFile(String filename) {
		try {
			fileReader = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println(String.format("ARFF file not found: %s",
					filename));
			return;
		}

	}

	public boolean initTraining(String file) {
		this.readFile(file);

		this.setClassifier();

		return true;

	}

	public String classifier(double interval, double lobby, String zone,
			String node) {
		double[] values = new double[dataset.numAttributes()];
		values[0] = getDataset().attribute(0).indexOfValue(node);
		values[1] = interval;
		values[2] = lobby;
		values[3] = getDataset().attribute(3).indexOfValue(zone);
		
		values[4] = dataset.attribute(4).indexOfValue("?");
		Instance unlabeled = new Instance(1.0, values);
		unlabeled.setDataset(dataset);
		double classAtribute = 0D;
		try {
			classAtribute = Algorithm.classifyInstance(unlabeled);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String tmp = dataset.attribute(4).value((int) classAtribute);
		return tmp;
	}

	public void setClassifier() {
		this.Algorithm = new J48();
		this.Algorithm.setUnpruned(true); 
		try {
			BufferedReader reader = new BufferedReader(fileReader);
			dataset = new Instances(reader);
			reader.close();
			dataset.setClassIndex(dataset.numAttributes() - 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Algorithm.buildClassifier(this.dataset);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // build classifier

	}

	public Instances getDataset() {
		return dataset;
	}

	public void setDataset(Instances dataset) {
		this.dataset = dataset;
	}

}
