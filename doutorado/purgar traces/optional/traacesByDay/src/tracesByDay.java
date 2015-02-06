package traacesByDay;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;

public class tracesByDay {

	public void read(List<String> file) throws IOException {
		FileReader fr = null;
		FileWriter wr = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		StringTokenizer tokens = null;
		String linea = "";
		String lastDay = "";
		String lastMonth = "";
		String day = "";
		String month = "";
		try {
			fr = new FileReader(file.get(0));
			br = new BufferedReader(fr);
			while (((linea = br.readLine()) != null) /* && (contador <= 100000) */) {
				tokens = new StringTokenizer(linea, "-");
				day = tokens.nextToken();
				String rest = tokens.nextToken();
				tokens = new StringTokenizer(rest, ":");
				month = tokens.nextToken();
				if (!(lastDay.equals(day)) || !(lastMonth.equals(month))) {
					if (!lastDay.equals("") || !lastMonth.equals("")) {
						bw.close();
						wr.close();
					}
					wr = new FileWriter(file.get(1) + "2001-" + month +"-"+ day);
					bw = new BufferedWriter(wr);
					lastDay = day;
					lastMonth = month;
				}

				bw.append(linea);
				bw.newLine();

			}
			bw.close();
			wr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
			fileInfo.add(tmp);
			int index = tmp.indexOf(fileName);
			fileInfo.add(tmp.substring(0, index));

		}
		// System.out.println(fileName);
		return fileInfo;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		tracesByDay traces = new tracesByDay();

		traces.read(traces.chooseFile());

	}

}
