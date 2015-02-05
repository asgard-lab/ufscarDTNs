package removeBuses;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class removeBuses {
	private List<String> pathAndFileInfo = null;

	public void choossingFiles(List<Integer> lstBusesIds) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Choose the Active File");
		this.chooseFile();
		makeItToActive(lstBusesIds);
		System.out.println("Choose the Path File");
		this.chooseFile();
		makeItToPaths(lstBusesIds);
	}

	private void makeItToPaths(List<Integer> lstBusesIds) throws IOException {

		// TODO Auto-generated method stub
		double maxTime = 0;
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = 0;
		double maxY = 0;

		FileReader fr = new FileReader(this.pathAndFileInfo.get(2));
		FileWriter wr = new FileWriter(this.pathAndFileInfo.get(1)
				+ this.pathAndFileInfo.get(0) + "tmp.txt");
		File file = new File(this.pathAndFileInfo.get(1)
				+ this.pathAndFileInfo.get(0) + "tmp.txt");
		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(wr);
		String line = "";
		StringTokenizer tokens = null;
		int cont = 0;
		int contLine = 0;
		while (((line = br.readLine()) != null)) {
			if (contLine == 0) {
				contLine++;
				continue;
			}
			tokens = new StringTokenizer(line);
			int id = Integer.parseInt(tokens.nextToken());
			String output = "";
			if (!lstBusesIds.contains(id)) {
				output = "" + cont;
				while (tokens.hasMoreTokens()) {
					String tuplaTXY = tokens.nextToken();
					output = output + " " + tuplaTXY;
					StringTokenizer tokensTXY = new StringTokenizer(tuplaTXY,
							",");
					double time = Double.parseDouble(tokensTXY.nextToken());
					double x = Double.parseDouble(tokensTXY.nextToken());
					double y = Double.parseDouble(tokensTXY.nextToken());
					if (time > maxTime) {
						maxTime = time;
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
				bw.append(output);
				bw.newLine();
				cont++;

			}

		}
		br.close();
		bw.close();
		fr.close();
		wr.close();
		System.out.println("SEE IF THESE FILES NEED MODIFICATIONS:");
		System.out.println("maximum Time:" + maxTime);
		System.out.println("minimum X:" + minX);
		System.out.println("maximum X:" + maxX);
		System.out.println("minimum Y:" + minY);
		System.out.println("maximum Y:" + maxY);
		String newHeader = (cont - 1) + " 0.0 " + maxTime + " " + minX + " "
				+ maxX + " " + minY + " " + maxY;
		fr = new FileReader(this.pathAndFileInfo.get(1)
				+ this.pathAndFileInfo.get(0) + "tmp.txt");
		wr = new FileWriter(this.pathAndFileInfo.get(1)
				+ this.pathAndFileInfo.get(0) + "Final.txt");
		br = new BufferedReader(fr);
		bw = new BufferedWriter(wr);
		line = "";

		bw.append(newHeader);
		bw.newLine();

		while (((line = br.readLine()) != null)) {
			bw.append(line);
			bw.newLine();

		}
		bw.close();
		wr.close();
		br.close();
		fr.close();
		try{
			 
    		
 
    		if(file.delete()){
    			System.out.println(file.getName() + " is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}
 
    	}catch(Exception e){
 
    		e.printStackTrace();
 
    	}

	}

	private void makeItToActive(List<Integer> lstBusesIds) throws IOException {
		// TODO Auto-generated method stub
		FileReader fr = new FileReader(this.pathAndFileInfo.get(2));
		FileWriter wr = new FileWriter(this.pathAndFileInfo.get(1)
				+ this.pathAndFileInfo.get(0) + "Final.txt");
		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(wr);
		String line = "";
		StringTokenizer tokens = null;
		int cont = 0;
		while (((line = br.readLine()) != null)) {
			tokens = new StringTokenizer(line);
			int id = Integer.parseInt(tokens.nextToken());
			String output = "";
			if (!lstBusesIds.contains(id)) {
				output = "" + cont;
				while (tokens.hasMoreTokens()) {
					output = output + " " + tokens.nextToken();
				}
				bw.append(output);
				bw.newLine();
				cont++;

			}

		}
		br.close();
		bw.close();
		fr.close();
		wr.close();

	}

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

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		removeBuses rmv = new removeBuses();

		System.out.println("Insert the list of buses. Ex: 1,2,3,4");
		Scanner reader = new Scanner(System.in);

		String strListBuses = reader.nextLine();

		List<Integer> lstBusesIds = new ArrayList<Integer>();

		StringTokenizer tokens = new StringTokenizer(strListBuses, ",");
		while (tokens.hasMoreTokens()) {
			lstBusesIds.add(Integer.parseInt(tokens.nextToken()));
		}
		rmv.choossingFiles(lstBusesIds);

	}
}
