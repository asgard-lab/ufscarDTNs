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

public class Coverage {
	List<Coord> mapCoords = new ArrayList<Coord>();
	int countMapPoints = 0;

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

	public void removeRepeated(List<String> pathAndFileInfo) throws IOException {
		StringTokenizer tokens = null;
		FileReader fr = new FileReader(pathAndFileInfo.get(2));
		FileWriter wr = new FileWriter(pathAndFileInfo.get(1)
				+ pathAndFileInfo.get(0) + "nonRepeated.txt");
		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(wr);
		String linea = "";
		List<Coord> tmpCoords = new ArrayList<Coord>();
		int c = 0;
		while (((linea = br.readLine()) != null) /* && (contador <= 100000) */) {
		//	if (c == 0) {
			//	c++;
				//continue;

			//}

			tokens = new StringTokenizer(linea);
			int x = Integer.parseInt(tokens.nextToken());
			int y = Integer.parseInt(tokens.nextToken());
			if (!tmpCoords.contains(new Coord(x, y))) {
				bw.append(x + "\t" + y);
				bw.newLine();
				tmpCoords.add(new Coord(x, y));

			}

		}
		bw.close();
		wr.close();
		br.close();
		fr.close();
	}

	private void readMap(List<String> mapa) throws IOException {
		// TODO Auto-generated method stub
		FileReader fr = new FileReader(mapa.get(1) + mapa.get(0)
				+ "nonRepeated.txt");
		BufferedReader br = new BufferedReader(fr);
		String linea = "";
		StringTokenizer tokens = null;

		while (((linea = br.readLine()) != null) /* && (contador <= 100000) */) {
			tokens = new StringTokenizer(linea);
			int x = Integer.parseInt(tokens.nextToken());
			int y = Integer.parseInt(tokens.nextToken());
			if (!this.mapCoords.contains(new Coord(x, y))) {
				mapCoords.add(new Coord(x, y));
				this.countMapPoints++;
			}
		}
	}

	private void calculateCoverage(List<String> pathAndFileInfoArea)
			throws Exception, IOException {
		// TODO Auto-generated method stub
		FileReader fr = new FileReader(pathAndFileInfoArea.get(1)
				+ pathAndFileInfoArea.get(0) + "nonRepeated.txt");
		BufferedReader br = new BufferedReader(fr);
		String linea = "";
		StringTokenizer tokens = null;
		int count = 0;
		while (((linea = br.readLine()) != null) /* && (contador <= 100000) */) {
			tokens = new StringTokenizer(linea);
			int x = Integer.parseInt(tokens.nextToken());
			int y = Integer.parseInt(tokens.nextToken());
			if (this.mapCoords.contains(new Coord(x, y))) {
				count++;

			}
		}
		System.out.println("COVERAGE:" + ((double)count / (double)this.countMapPoints));

	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Coverage area = new Coverage();
		List<String> pathAndFileInfo1 = null;// mapa
		List<String> pathAndFileInfo2 = null;// will contain the filename of
												// traces,
	//List<String> pathAndFileInfo3 = null;// mapa
		//List<String> pathAndFileInfo4 = null;// will contain the filename of
		//List<String> pathAndFileInfo5 = null;// mapa
		//List<String> pathAndFileInfo6 = null;// mapa
		
		pathAndFileInfo1 = area.chooseFile();
		pathAndFileInfo2 = area.chooseFile();
	//	pathAndFileInfo3 = area.chooseFile();
	//	pathAndFileInfo4 = area.chooseFile();
	//	pathAndFileInfo5 = area.chooseFile();
	//	pathAndFileInfo6 = area.chooseFile();

		area.removeRepeated(pathAndFileInfo1);
		area.removeRepeated(pathAndFileInfo2);
		
	//	area.removeRepeated(pathAndFileInfo3);
	//	area.removeRepeated(pathAndFileInfo4);
	//	area.removeRepeated(pathAndFileInfo5);
	//	area.removeRepeated(pathAndFileInfo6);

		area.readMap(pathAndFileInfo1);
		area.calculateCoverage(pathAndFileInfo2);

	}

}
