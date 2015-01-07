package classification;

import routing.ClassifierRouter;
import core.SimScenario;
import core.World;


public class Classifier {
	public static final String test = "TESTING";
	public static final String train = "TRAINING";
	public static final String collect = "COLLECTING";

	public static final String distanceClassification = "DC";
	public static final String timeClassification = "TC";
	public static final String distanceRegression = "DR";
	public static final String timeRegression = "TR";
	public static final int uninitiated_value = -1;
	private String fase;

	public double maxMetric = 0;

	private boolean classifierMode = false; // false, usa naserian, true, usa
											// classificadores
	
	public String getFase() {
		return fase;
	}

	public void setFase(String fase) {
		this.fase = fase;
	}

	public void setFaseToCollect() {
		// TODO Auto-generated method stub
		this.fase = this.collect;
		
	}

	public void setFaseToTrain() {
		// TODO Auto-generated method stub
		this.fase = this.train;
	}

	public boolean isTraining() {
		// TODO Auto-generated method stub
		if(this.fase.equals(this.train))
			return true;
		
		else
			return false;
	}

	public void setFaseToTest() {
		// TODO Auto-generated method stub
		this.fase = this.test;
	}
	
	private void initTraining() {

		// TODO Auto-generated method stub
		// leer arquivos, ver se tem o número necesário de registros, si no
		// manda a collect on the fly
		// se tiver sucesso passa a la fase de classifing
		World w = SimScenario.getInstance().getWorld();

		if (w.flagClassificatorTrained == false) {
			w.datasetClassifier = new WekaTheOne();
			if (w.datasetClassifier
					.initTraining(ClassifierRouter.classificationFile) == true) {
				w.flagClassificatorTrained = true;
				this.classifierMode = true;
				this.setFase(this.test);

			} else
				this.fase = this.collect;
		}
	}
	public String classifier(double interval, double lobby, String zone,
			String node) {
		this.initTraining();
		World w = SimScenario.getInstance().getWorld();
		if (w.flagClassificatorTrained == true)
			return w.datasetClassifier.classifier(interval, lobby, zone, node);
		else
			return null;
	}

}
