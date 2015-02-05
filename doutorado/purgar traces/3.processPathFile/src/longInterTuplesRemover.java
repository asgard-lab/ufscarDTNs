package processPathFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;

public class longInterTuplesRemover {

	private List<String> activeFileInfo = null;
	private List<String> pathFileInfo = null;
	private List<String> observationsFileInfo = null;
	private Map<Integer, List<Double>> obsMap = new HashMap<Integer, List<Double>>();
	private Map<Integer, List<Double>> activeMap = new HashMap<Integer, List<Double>>();
	private Map<Integer, List<Double>> validActive = new HashMap<Integer, List<Double>>();
	private Map<Integer, List<String>> newPathContent = new HashMap<Integer, List<String>>();
	double maxTime = 0;
	double minX = Double.MAX_VALUE;
	double minY = Double.MAX_VALUE;
	double maxX = 0;
	double maxY = 0;
	double minTime = Double.MAX_VALUE;
	int busesCount = -1;
	List<Integer> idBuses = new ArrayList<Integer>();

	public longInterTuplesRemover() {
		// TODO Auto-generated constructor stub
		System.out.println("Select the active file");
		this.activeFileInfo = chooseFile();
		System.out.println("Select the path file");
		this.pathFileInfo = chooseFile();
		System.out.println("Select the observation file");
		this.observationsFileInfo = chooseFile();
	}

	private void removeObservedTuples() throws IOException {
		// TODO Auto-generated method stub
		readActiveFile();
		readObservationFile();
		modifyPathFile();
		writeNewPathFile();

	}

	private void readActiveFile() throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		FileReader fr = new FileReader(this.activeFileInfo.get(2));

		BufferedReader br = new BufferedReader(fr);

		String line = "";
		StringTokenizer tokens = null;

		while (((line = br.readLine()) != null)) {
			tokens = new StringTokenizer(line);
			int id = Integer.parseInt(tokens.nextToken());
			double timeIni = Double.parseDouble(tokens.nextToken());
			double timeFin = Double.parseDouble(tokens.nextToken());
			if (!this.activeMap.containsKey(id)) {
				this.activeMap.put(id, new ArrayList<Double>());
				this.activeMap.get(id).add(timeIni);
				this.activeMap.get(id).add(timeFin);
			}

		}
		br.close();
		fr.close();
	}

	private void writeNewPathFile() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("SEE IF THESE FILES NEED MODIFICATIONS:");
		System.out.println("maximum Time:" + maxTime);
		System.out.println("minimum X:" + minX);
		System.out.println("maximum X:" + maxX);
		System.out.println("minimum Y:" + minY);
		System.out.println("maximum Y:" + maxY);
		String newHeader = (busesCount) + " " + minTime + " " + maxTime + " "
				+ minX + " " + maxX + " " + minY + " " + maxY;

		FileWriter wr = new FileWriter(this.pathFileInfo.get(1)
				+ this.pathFileInfo.get(0) + "New.txt");
		FileWriter wrActive = new FileWriter(this.activeFileInfo.get(1)
				+ this.activeFileInfo.get(0) + "New.txt");

		BufferedWriter bw = new BufferedWriter(wr);
		BufferedWriter bwActive = new BufferedWriter(wrActive);
		String line = "";
		String lineActive = "";

		bw.append(newHeader);
		bw.newLine();
		for (int i = 0; i < this.idBuses.size(); i++) {
			int id = this.idBuses.get(i);
			List<String> tmp = this.newPathContent.get(id);

			line = "" + i;

			lineActive = "" + i + " " + this.validActive.get(i).get(0);

			lineActive = lineActive + " " + +this.validActive.get(i).get(1);

			for (int j = 0; j < tmp.size(); j++) {
				line = line + " " + tmp.get(j);
			}
			bw.append(line);
			bw.newLine();
			bwActive.append(lineActive);
			bwActive.newLine();

		}

		bw.close();
		wr.close();
		bwActive.close();
		wrActive.close();

	}

	private void modifyPathFile() throws NumberFormatException, IOException {
		// TODO Auto-generated method stub

		FileReader frPath = new FileReader(this.pathFileInfo.get(2));

		validActive = new HashMap<Integer, List<Double>>();
		BufferedReader brPath = new BufferedReader(frPath);

		String linePath = "";
		StringTokenizer tokens = null;

		int contLine = 0;
		String paths = "";
		int pathId = -1;
		int id = -1;
		int contBusesInFile = 0;
		this.idBuses = new ArrayList<Integer>();
		while (((linePath = brPath.readLine()) != null)) {
			if (contLine == 0) {
				contLine++;
				continue;
			}

			tokens = new StringTokenizer(linePath);
			id = Integer.parseInt(tokens.nextToken());

			List<Double> actTimes = this.activeMap.get(id);
			double tini = actTimes.get(0);
			double tfin = actTimes.get(1);
			List<Double> observationTimes = this.obsMap.get(id);
			double greaterPeriod = -1;
			double validIni = -1;
			double validFin = -1;
			if (observationTimes != null) {
				for (int i = 0; i < observationTimes.size(); i = i + 2) {
					try {
						double ti = observationTimes.get(i);
						double tiPlus1 = observationTimes.get(i + 1);

						if (i > 0)
							tini = observationTimes.get(i - 1);
						else
							tini = actTimes.get(0);
						if ((i + 2) == observationTimes.size())
							tfin = actTimes.get(1);
						else
							tfin = observationTimes.get(i + 2);
						if ((ti - tini) > greaterPeriod) {
							greaterPeriod = ti - tini;
							validIni = tini;
							validFin = ti;
						}
						if ((tfin - tiPlus1) > greaterPeriod) {
							greaterPeriod = tfin - tiPlus1;
							validIni = tiPlus1;
							validFin = tfin;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			else
			{
				validIni = tini;
				validFin = tfin;
			}
			

			List<String> tmpTuplas = new ArrayList<String>();
			while (tokens.hasMoreTokens()) {

				String tuplaTXY = tokens.nextToken();
				StringTokenizer tokensTXY = new StringTokenizer(tuplaTXY, ",");
				double timePath = Double.parseDouble(tokensTXY.nextToken());
				double x = Double.parseDouble(tokensTXY.nextToken());
				double y = Double.parseDouble(tokensTXY.nextToken());

				if (timePath >= validIni && timePath <= validFin) {
					tmpTuplas.add(tuplaTXY);
					compare(timePath, x, y);

				}

			}
			if (tmpTuplas.size() > 0) {
				if (!this.newPathContent.containsKey(contBusesInFile)) {
					this.newPathContent.put(contBusesInFile,
							new ArrayList<String>());
					this.newPathContent.get(contBusesInFile).addAll(tmpTuplas);
				}

				if (!this.idBuses.contains(contBusesInFile)) {
					this.idBuses.add(contBusesInFile);
				}
				if (!this.validActive.containsKey(contBusesInFile)) {
					List<Double> tmplist = new ArrayList<Double>();
					tmplist.add(validIni);
					tmplist.add(validFin);
					this.validActive.put(contBusesInFile, tmplist);
				}
				contBusesInFile++;
			}

		}
		this.busesCount=contBusesInFile;
		Collections.sort(this.idBuses);

		brPath.close();

		frPath.close();

	}

	private void compare(double t, double x, double y) {
		if (t > maxTime) {
			maxTime = t;
		}
		if (t < minTime) {
			minTime = t;
		}
		if (x > maxX) {
			maxX = x;
		}
		if (y > maxY) {
			maxY = y;
		}
		if (x < minX) {
			minX = x;
		}
		if (y < minY) {
			minY = y;
		}
	}

	private void readObservationFile() throws IOException {
		// TODO Auto-generated method stub
		FileReader fr = new FileReader(this.observationsFileInfo.get(2));

		BufferedReader br = new BufferedReader(fr);

		String line = "";
		StringTokenizer tokens = null;

		while (((line = br.readLine()) != null)) {
			tokens = new StringTokenizer(line);
			int id = Integer.parseInt(tokens.nextToken());
			List<Double> lstTimes = new ArrayList<Double>();
			while (tokens.hasMoreTokens()) {
				double time = Double.parseDouble(tokens.nextToken());
				lstTimes.add(time);

			}

			if (!this.obsMap.containsKey(id)) {
				this.obsMap.put(id, new ArrayList<Double>());
				this.obsMap.get(id).addAll(lstTimes);

			}

		}
		br.close();
		fr.close();
	}

	public List<String> chooseFile() {
		String fileName = "";
		List<String> fileInfo = new ArrayList<String>();
		JFileChooser chooser = new JFileChooser();
		int status = chooser.showOpenDialog(null);

		if (status == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			fileName = file.getName();
			if (file == null) {
				System.out.println("Error by choosing a file");
				return null;
			}
			String tmp = chooser.getSelectedFile().getAbsolutePath();

			int index = tmp.indexOf(fileName);
			String pathWithoutFilename = tmp.substring(0, index);
			StringTokenizer tokens = new StringTokenizer(fileName, ".");
			fileName = tokens.nextToken();
			fileInfo.add(fileName);
			fileInfo.add(pathWithoutFilename);
			fileInfo.add(tmp);

			return fileInfo;
		}

		return null;

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		longInterTuplesRemover remover = new longInterTuplesRemover();
		remover.removeObservedTuples();

	}

}
