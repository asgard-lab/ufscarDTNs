package spaceTimeDistanceDistributions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;

public class spaceTimeDistanceDistributions {

	private List<String> pathAndFileInfo = null;
	Map<Integer, MutableInt> mapTimes = new HashMap<Integer, MutableInt>();
	List<Integer> lstObsTimes = new ArrayList<Integer>();
	// private int theshould = 129;
	int registerCount = 0;
	double aveTimes = 0;
	Map<Integer, List<Double>> mapObsTimes = new HashMap<Integer, List<Double>>();

	public void chooseFile() {
		String fileName = "";
		List<String> fileInfo = new ArrayList<String>();
		JFileChooser chooser = new JFileChooser();
		int status = chooser.showOpenDialog(null);

		if (status == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			fileName = file.getName();
			if (file == null) {
				System.out.println("Error by choosing a file");
				return;
			}
			String tmp = chooser.getSelectedFile().getAbsolutePath();

			int index = tmp.indexOf(fileName);
			String pathWithoutFilename = tmp.substring(0, index);
			StringTokenizer tokens = new StringTokenizer(fileName, ".");
			fileName = tokens.nextToken();
			fileInfo.add(fileName);
			fileInfo.add(pathWithoutFilename);
			fileInfo.add(tmp);

			this.pathAndFileInfo = fileInfo;
		}

		// System.out.println(fileName);

	}

	public static void main(String[] args) throws NumberFormatException,
			IOException {
		// TODO Auto-generated method stub
		spaceTimeDistanceDistributions obj = new spaceTimeDistanceDistributions();
		int opc = 0;
		Scanner reader = new Scanner(System.in);
		while (opc != 3) {
			System.out.println("OPCIONES DE CORTADO:");
			System.out.println("\t\t\t1.OBTER DISTRIBUCOES");
			System.out.println("\t\t\t2.OBTER OBSERVACOES");
			System.out.println("\t\t\t3.FINALIZAR");
			System.out.println("Ingrese la opcion:");
			opc = reader.nextInt();
			if (opc == 1) {
				obj.getDistributions();
			}
			else if (opc == 2) {
				System.out.println("\t\t\tIngrese o limiar para as observacoes:");
				int lim = reader.nextInt();
				obj.getObservationsV2(lim);
			}
			if (opc != 1 && opc != 2 && opc != 3) {
				System.out.println("Opcion desconocida");
			}
			
			
		} 

	}

	private void getObservations(int threshould) throws IOException {
		// TODO Auto-generated method stub
		chooseFile();
		FileReader fr = new FileReader(this.pathAndFileInfo.get(2));
		mapObsTimes = new HashMap<Integer, List<Double>>();
		lstObsTimes = new ArrayList<Integer>();
		BufferedReader br = new BufferedReader(fr);

		String line = "";
		StringTokenizer tokens = null;

		int contLine = 0;

		while (((line = br.readLine()) != null)) {
			if (contLine == 0) {
				contLine++;
				continue;
			}
			tokens = new StringTokenizer(line);
			int id = Integer.parseInt(tokens.nextToken());
			String output = "";

			double antTime = -1;
		//	double antX = -1;
	//		double antY = -1;
			boolean flag = false;
			while (tokens.hasMoreTokens()) {
				String tuplaTXY = tokens.nextToken();
				output = output + " " + tuplaTXY;
				StringTokenizer tokensTXY = new StringTokenizer(tuplaTXY, ",");
				double time = Double.parseDouble(tokensTXY.nextToken());
			//	double x = Double.parseDouble(tokensTXY.nextToken());
			//	double y = Double.parseDouble(tokensTXY.nextToken());
				if (flag == true) {

					int timeDistance = (int) (time - antTime);

					if (timeDistance > threshould) {
						if (!this.mapObsTimes.containsKey(id)) {
							this.lstObsTimes.add(id);
							this.mapObsTimes.put(id, new ArrayList<Double>());
						}
						this.mapObsTimes.get(id).add(antTime);
					}

				} else {

					flag = true;
				}
				antTime = time;
				//antX = x;
				//antY = y;

			}

		}
		br.close();

		fr.close();
		FileWriter wr = new FileWriter(this.pathAndFileInfo.get(1)
				+ this.pathAndFileInfo.get(0) + "Observations.txt");
		BufferedWriter bw = new BufferedWriter(wr);
		Collections.sort(this.lstObsTimes);

		for (int i = 0; i < this.lstObsTimes.size(); i++) {

			String output = "" + lstObsTimes.get(i);
			List<Double> tmpTimes = this.mapObsTimes.get(lstObsTimes.get(i));
			for (int j = 0; j < tmpTimes.size(); j++) {
				output = output + " " + tmpTimes.get(j);
			}

			bw.append(output);
			bw.newLine();

		}

		bw.close();
		wr.close();
	}
	
	private void getObservationsV2(int threshould) throws IOException {
		// TODO Auto-generated method stub
		chooseFile();
		FileReader fr = new FileReader(this.pathAndFileInfo.get(2));
		mapObsTimes = new HashMap<Integer, List<Double>>();
		lstObsTimes = new ArrayList<Integer>();
		BufferedReader br = new BufferedReader(fr);

		String line = "";
		StringTokenizer tokens = null;

		int contLine = 0;

		while (((line = br.readLine()) != null)) {
			if (contLine == 0) {
				contLine++;
				continue;
			}
			tokens = new StringTokenizer(line);
			int id = Integer.parseInt(tokens.nextToken());
			String output = "";

			double antTime = -1;
		//	double antX = -1;
	//		double antY = -1;
			boolean flag = false;
			while (tokens.hasMoreTokens()) {
				String tuplaTXY = tokens.nextToken();
				output = output + " " + tuplaTXY;
				StringTokenizer tokensTXY = new StringTokenizer(tuplaTXY, ",");
				double time = Double.parseDouble(tokensTXY.nextToken());
			//	double x = Double.parseDouble(tokensTXY.nextToken());
			//	double y = Double.parseDouble(tokensTXY.nextToken());
				if (flag == true) {

					int timeDistance = (int) (time - antTime);

					if (timeDistance > threshould) {
						if (!this.mapObsTimes.containsKey(id)) {
							this.lstObsTimes.add(id);
							this.mapObsTimes.put(id, new ArrayList<Double>());
						}
						this.mapObsTimes.get(id).add(antTime); //guardar ultimo bom
						this.mapObsTimes.get(id).add(time); //o siguiente al ultimo bom
					}

				} else {

					flag = true;
				}
				antTime = time;
				//antX = x;
				//antY = y;

			}

		}
		br.close();

		fr.close();
		FileWriter wr = new FileWriter(this.pathAndFileInfo.get(1)
				+ this.pathAndFileInfo.get(0) + "ObservationsV2.txt");
		BufferedWriter bw = new BufferedWriter(wr);
		Collections.sort(this.lstObsTimes);

		for (int i = 0; i < this.lstObsTimes.size(); i++) {

			String output = "" + lstObsTimes.get(i);
			List<Double> tmpTimes = this.mapObsTimes.get(lstObsTimes.get(i));
			for (int j = 0; j < tmpTimes.size(); j++) {
				output = output + " " + tmpTimes.get(j);
			}

			bw.append(output);
			bw.newLine();

		}

		bw.close();
		wr.close();
	}

	private void getDistributions() throws NumberFormatException, IOException {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		this.chooseFile();
		FileReader fr = new FileReader(this.pathAndFileInfo.get(2));

		BufferedReader br = new BufferedReader(fr);

		String line = "";
		StringTokenizer tokens = null;

		int contLine = 0;
		registerCount = 0;
		while (((line = br.readLine()) != null)) {
			if (contLine == 0) {
				contLine++;
				continue;
			}
			tokens = new StringTokenizer(line);
			int id = Integer.parseInt(tokens.nextToken());
			String output = "";

			double antTime = -1;
			//double antX = -1;
			//double antY = -1;
			boolean flag = false;
			while (tokens.hasMoreTokens()) {
				String tuplaTXY = tokens.nextToken();
				output = output + " " + tuplaTXY;
				StringTokenizer tokensTXY = new StringTokenizer(tuplaTXY, ",");
				double time = Double.parseDouble(tokensTXY.nextToken());
			//	double x = Double.parseDouble(tokensTXY.nextToken());
			//	double y = Double.parseDouble(tokensTXY.nextToken());
				if (flag == true) {

					int timeDistance = (int) (time - antTime);
					if(timeDistance <0)
						System.out.println("ouch");

					if (!this.mapTimes.containsKey(timeDistance)) {
						this.mapTimes.put(timeDistance, new MutableInt());
					} else
						this.mapTimes.get(timeDistance).increment();
					registerCount++;
				} else {

					flag = true;
				}
				antTime = time;
				//antX = x;
				//antY = y;

			}

		}
		br.close();

		fr.close();
		// getting distributions
		FileWriter wr = new FileWriter(this.pathAndFileInfo.get(1)
				+ this.pathAndFileInfo.get(0) + "Distributions.txt");
		BufferedWriter bw = new BufferedWriter(wr);
		double sum = 0;

		bw.append("Value\tFrequency");
		bw.newLine();
		for (Map.Entry<Integer, MutableInt> entry : this.mapTimes.entrySet()) {

			sum = sum
					+ ((double) (entry.getKey()) * entry.getValue()
							.getCounter());
			bw.append(entry.getKey() + "\t" + entry.getValue().getCounter());
			bw.newLine();

		}
		bw.newLine();
		this.aveTimes = sum / ((double) registerCount);

		bw.close();
		wr.close();

	}

	/*
	 * private double getDistance(double antX, double x, double antY, double y)
	 * { // TODO Auto-generated method stub
	 * 
	 * double tmpX = Math.pow((antX - x), 2); double tmpY = Math.pow((antY - y),
	 * 2); double rpta = tmpX + tmpY; return Math.pow(rpta, 0.5); }
	 */

}
