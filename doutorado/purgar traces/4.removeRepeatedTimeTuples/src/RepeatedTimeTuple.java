package removeRepeatedTimeTuples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;

public class RepeatedTimeTuple {
	private List<String> pathAndFileInfo = null;

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

	private void makeItToPaths() throws IOException {

		// TODO Auto-generated method stub

		FileReader fr = new FileReader(this.pathAndFileInfo.get(2));
		FileWriter wr = new FileWriter(this.pathAndFileInfo.get(1)
				+ this.pathAndFileInfo.get(0) + "Final.txt");

		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(wr);
		String line = "";
		StringTokenizer tokens = null;

		int contLine = 0;
		double previousTime = -1;
		while (((line = br.readLine()) != null)) {
			if (contLine == 0) {
				bw.append(line);
				bw.newLine();
				contLine++;
				continue;
			}
			previousTime = -1;
			tokens = new StringTokenizer(line);
			int id = Integer.parseInt(tokens.nextToken());
			String output = "" + id;

			while (tokens.hasMoreTokens()) {
				String tuplaTXY = tokens.nextToken();
				StringTokenizer tokensTXY = new StringTokenizer(tuplaTXY, ",");
				double time = Double.parseDouble(tokensTXY.nextToken());
				double x = Double.parseDouble(tokensTXY.nextToken());
				double y = Double.parseDouble(tokensTXY.nextToken());
				if (previousTime == -1) {
					output = output + " " + tuplaTXY;
					previousTime = time;
				} else {
					if (time - previousTime > 3) {
						output = output + " " + tuplaTXY;
						previousTime = time;
					}

				}

			}
			bw.append(output);
			bw.newLine();

		}
		br.close();
		bw.close();
		fr.close();
		wr.close();

	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		RepeatedTimeTuple r = new RepeatedTimeTuple();
		r.chooseFile();
		r.makeItToPaths();
	}

}
