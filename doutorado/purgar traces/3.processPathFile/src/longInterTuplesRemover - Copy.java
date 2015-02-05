package processPathFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;

public class longInterTuplesRemover {

	private List<String> activeFileInfo = null;
	private List<String> pathFileInfo = null;
	private List<String> observationsFileInfo = null;
	private Map<Integer, List<Double>> obsMap = new HashMap<Integer,List<Double>>();
	private Map<Integer, List<Double>> activeMap = new HashMap<Integer, List<Double>>();
	private Map<Integer, List<String>> newPathContent = new HashMap<Integer, List<String>>();
	double maxTime = 0;
	double minX = Double.MAX_VALUE;
	double minY = Double.MAX_VALUE;
	double maxX = 0;
	double maxY = 0;
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
		String newHeader = (busesCount ) + " 0.0 " + maxTime + " " + minX
				+ " " + maxX + " " + minY + " " + maxY;

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
			int indexLast= tmp.size()-1;
			line = "" + i;
			StringTokenizer tokenTXY= new StringTokenizer(tmp.get(0), ",");
			lineActive = "" + i+" "+tokenTXY.nextToken();
			tokenTXY= new StringTokenizer(tmp.get(indexLast), ",");
			lineActive = lineActive +" "+tokenTXY.nextToken();
			
			
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
		FileReader frObservations = new FileReader(
				this.observationsFileInfo.get(2));
		FileReader frPath = new FileReader(this.pathFileInfo.get(2));
		FileWriter wr = new FileWriter(this.pathFileInfo.get(1)
				+ this.pathFileInfo.get(0) + "tmp.txt");

		BufferedReader brObservations = new BufferedReader(frObservations);
		BufferedReader brPath = new BufferedReader(frPath);
		BufferedWriter bw = new BufferedWriter(wr);

		String lineObservations = "";
		StringTokenizer tokens = null;
		StringTokenizer tokensPath = null;

		int contLine = 0;
		String linePath = "";
		int pathId = -1;
		int id = -1;

		while (((lineObservations = brObservations.readLine()) != null)) {

			tokens = new StringTokenizer(lineObservations);
			id = Integer.parseInt(tokens.nextToken());
			double time = Double.parseDouble(tokens.nextToken());
			while (pathId < id) {
				if ((linePath = brPath.readLine()) != null) {
					if (contLine == 0) {
						contLine++;
						continue;
					}
					tokensPath = new StringTokenizer(linePath);
					pathId = Integer.parseInt(tokensPath.nextToken());
					/*if (pathId == 0 || pathId == 7 || pathId == 28
							|| pathId == 44 || pathId == 62 || pathId == 90
							|| pathId == 139 || pathId == 140 || pathId == 197
							|| pathId == 213 || pathId == 230 || pathId == 247
							|| pathId == 256 || pathId == 310 || pathId == 323
							|| pathId == 385 || pathId == 388 || pathId == 413
							|| pathId == 419 || pathId == 425 || pathId == 433
							|| pathId == 435 || pathId == 436 || pathId == 437
							|| pathId == 439 || pathId == 440 || pathId == 441
							|| pathId == 442 || pathId == 443) {
						System.out.println("nao modificados");
					}
					if (pathId == 424 || pathId == 320 || pathId == 294
							|| pathId == 288 || pathId == 231 || pathId == 225
							|| pathId == 55) {
						System.out.println("casos especiais");
					}*/
					boolean flag = false;
					if (pathId == id) {

						busesCount = idBuses.size();

						while (tokensPath.hasMoreTokens() && flag == false) {
							String tuplaTXY = tokensPath.nextToken();
							StringTokenizer tokensTXY = new StringTokenizer(
									tuplaTXY, ",");

							double timePath = Double.parseDouble(tokensTXY
									.nextToken());
							double x = Double
									.parseDouble(tokensTXY.nextToken());
							double y = Double
									.parseDouble(tokensTXY.nextToken());

							if (time == this.activeMap.get(pathId)) {
								flag = true;
								break;
							}

							if (timePath <= time) {

								if (!this.newPathContent
										.containsKey(busesCount)) {

									this.newPathContent.put(busesCount,
											new ArrayList<String>());
									this.idBuses.add(busesCount);

								}

								this.newPathContent.get(busesCount).add(
										tuplaTXY);
								compare(timePath, x, y);
							} else {
								flag = true;
								break;
							}

						}
					}

					else {
						busesCount = this.idBuses.size();
						while (tokensPath.hasMoreTokens()) {
							String tuplaTXY = tokensPath.nextToken();
							StringTokenizer tokensTXY = new StringTokenizer(
									tuplaTXY, ",");
							double timePath = Double.parseDouble(tokensTXY
									.nextToken());
							double x = Double
									.parseDouble(tokensTXY.nextToken());
							double y = Double
									.parseDouble(tokensTXY.nextToken());
							compare(timePath, x, y);

							if (!this.newPathContent.containsKey(busesCount)) {
								this.newPathContent.put(busesCount,
										new ArrayList<String>());
								this.idBuses.add(busesCount);

							}

							this.newPathContent.get(busesCount).add(tuplaTXY);
						}
					}
				}
			}

		}
		while ((linePath = brPath.readLine()) != null) {
			tokensPath = new StringTokenizer(linePath);
			pathId = Integer.parseInt(tokensPath.nextToken());
			busesCount = this.idBuses.size();
			while (tokensPath.hasMoreTokens()) {
				String tuplaTXY = tokensPath.nextToken();
				StringTokenizer tokensTXY = new StringTokenizer(tuplaTXY, ",");
				double timePath = Double.parseDouble(tokensTXY.nextToken());
				double x = Double.parseDouble(tokensTXY.nextToken());
				double y = Double.parseDouble(tokensTXY.nextToken());
				compare(timePath, x, y);

				if (!this.newPathContent.containsKey(busesCount)) {
					this.newPathContent
							.put(busesCount, new ArrayList<String>());
					this.idBuses.add(busesCount);

				}

				this.newPathContent.get(busesCount).add(tuplaTXY);
			}
		}
		brObservations.close();
		frObservations.close();
	}

	private void compare(double t, double x, double y) {
		if (t > maxTime) {
			maxTime = t;
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
			List<Double> lstTimes= new ArrayList<Double>();
			while(tokens.hasMoreTokens())
			{double time = Double.parseDouble(tokens.nextToken());
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
