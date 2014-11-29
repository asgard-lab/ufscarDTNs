package prueba;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;

public class Translator {
	Date maxDate = null;
	Date minDate = null;
	double dminDate = 0;
	double dmaxDate = 0;
	double minX = Double.MAX_VALUE;
	double maxX = 0;
	double minY = Double.MAX_VALUE;
	double maxY = 0;
	HashMap<Integer, ArrayList<Coord>> routes = null;
	int maxBusId = 0;
	int minBusId = 0;
	List<ArrayList<Object>> data = null;
	Map<Integer, Integer> busesIds = null;
	List<String> pathAndFileInfo = null;// will contain the filename of traces,
										// the path and the AbsPath

	public Translator() throws Exception {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
		try {
			minDate = format.parse("2020-31-12 23:59:59");
			maxDate = format.parse("1900-01-01 23:59:59");
			this.createTable();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* create table in memory for sorting according to the suitable fields */
	public void createTable() throws Exception {

		data = read();
		customCompareTime();
		System.out.println("Range of times:" + this.minDate + " to "
				+ this.maxDate);
		System.out.println("Coverage of X:" + this.minX + " to " + this.maxX);
		System.out.println("Coverage of Y:" + this.minY + " to " + this.maxY);

	}

	/* sorting by time the traces tuples */
	private void customCompareTime() {
		// TODO Auto-generated method stub
		Collections.sort(data, new Comparator<ArrayList<Object>>() {
			@Override
			public int compare(ArrayList<Object> obj1, ArrayList<Object> obj2) {
				Object o1 = obj1.get(0);
				Object o2 = obj2.get(0);
				if (o1.getClass().equals(o2.getClass())) {
					return ((Comparable) o1).compareTo((Comparable) o2);
				} else {
					if (o1.getClass().getCanonicalName()
							.equals("java.lang.String")) {
						return 1;
					} else {
						return -1;
					}
				}

			}

		});
	}

	/* converting the times in Date format to double format */
	private void convertTimeToSeconds() {
		// TODO Auto-generated method stub
		double minDate = this.minDate.getTime() / (double) (1000);
		this.dminDate = minDate;
		this.dmaxDate = this.maxDate.getTime() / (double) (1000);
		List<ArrayList<Object>> tmpData = new ArrayList<ArrayList<Object>>();
		for (int k = 0; k < this.data.size(); k++) {
			ArrayList<Object> e = data.get(k);
			Date date = (Date) e.get(0);
			double ddate = (date.getTime() / (double) (1000)) - minDate;
			ArrayList<Object> f = new ArrayList<Object>();
			f.add(ddate);
			f.add(e.get(1));
			f.add(e.get(2));
			f.add(e.get(3));
			f.add(e.get(4));
			f.add(e.get(5));
			tmpData.add(f);
		}
		this.data = tmpData;
		this.dmaxDate = this.dmaxDate - this.dminDate;
		this.dminDate = 0;

		System.out.println("Time of simulation:"
				+ (this.dmaxDate - this.dminDate));
	}

	/* bound the traces by time or desire area */
	private void cut() throws Exception {
		// TODO Auto-generated method stub
		Scanner reader = new Scanner(System.in);
		int opc = 0;
		while (opc != 3) {
			System.out.println("OPCIONES DE CORTADO:");
			System.out.println("\t\t\t1.POR TIEMPO");
			System.out.println("\t\t\t2.POR AREA");
			System.out.println("\t\t\t3.FINALIZAR");
			System.out.println("Ingrese la opcion:");
			opc = reader.nextInt();
			if (opc == 1) {
				System.out
						.println("\t\t\tIngrese fecha inicial YYYY-dd-MM HH:mm:ss:");
				reader.nextLine();
				String dIni = reader.nextLine();
				System.out
						.println("\t\t\tIngrese fecha final YYYY-dd-MM HH:mm:ss:");

				String dFin = reader.nextLine();
				Date dateIni = null;
				Date dateFin = null;
				try {
					dateIni = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss")
							.parse(dIni);
					dateFin = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss")
							.parse(dFin);

				} catch (ParseException ex) {
					ex.printStackTrace();
				}
				if ((dateIni != null) && (dateFin != null)) {
					removeByTime(dateIni, dateFin);
					arrangeArea();
				}

			} else if (opc == 2) {
				this.showInformation();
				System.out.println("\t\t\tIngrese limite inferior de X:");
				double infX = reader.nextDouble();
				System.out.println("\t\t\tIngrese limite superior de X:");
				double supX = reader.nextDouble();
				System.out.println("\t\t\tIngrese limite inferior de Y:");
				double infY = reader.nextDouble();
				System.out.println("\t\t\tIngrese limite superior de Y:");
				double supY = reader.nextDouble();
				removeByArea(infX, supX, infY, supY);
				arrangeArea();
			}
			if (opc != 1 && opc != 2 && opc != 3) {
				System.out.println("Opcion desconocida");
			}
		}
	}

	/* once made a cut, offset the coordenades to init from 0 in x and 0 in y */
	private void arrangeArea() {
		// TODO Auto-generated method stub
		List<ArrayList<Object>> tmpData = new ArrayList<ArrayList<Object>>();

		for (int k = 0; k < this.data.size(); k++) {
			ArrayList<Object> e = data.get(k);
			double x = (double) e.get(4) - this.minX;
			double y = (double) e.get(5) - this.minY;
			ArrayList<Object> f = new ArrayList<Object>();
			f.add(e.get(0));
			f.add(e.get(1));
			f.add(e.get(2));
			f.add(e.get(3));
			f.add(x);
			f.add(y);
			tmpData.add(f);
		}
		this.data = tmpData;
		this.maxX = this.maxX - this.minX;
		this.maxY = this.maxY - this.minY;
		this.minY = 0;
		this.minX = 0;
		System.out.println("Area of simulation:" + (this.maxX - this.minX)
				+ " por " + (this.maxY - this.minY));
	}

	private void removeByArea(double infX, double supX, double infY, double supY) {
		// TODO Auto-generated method stub
		List<ArrayList<Object>> tmpdata = new ArrayList<ArrayList<Object>>();
		double tmpMaxX = 0.0, tmpMinX = Double.MAX_VALUE;
		double tmpMaxY = 0.0, tmpMinY = Double.MAX_VALUE;
		int contMinimo = 0;
		Date tmpDateIni = null;
		Date tmpDateFin = null;
		for (int k = 0; k < this.data.size(); k++) {
			ArrayList<Object> e = data.get(k);
			double tmpX = (double) e.get(4);
			double tmpY = (double) e.get(5);
			Date eDate = (Date) e.get(0);
			if (((tmpX >= infX) && (tmpX <= supX) && (tmpY >= infY) && (tmpY <= supY))) {
				if (contMinimo == 0)
					tmpDateIni = eDate;
				tmpdata.add(e);
				if (tmpX > tmpMaxX)
					tmpMaxX = tmpX;
				if (tmpY > tmpMaxY)
					tmpMaxY = tmpY;
				if (tmpX < tmpMinX)
					tmpMinX = tmpX;
				if (tmpY < tmpMinY)
					tmpMinY = tmpY;
				contMinimo++;
				tmpDateFin = eDate;
			}

		}
		this.data = tmpdata;
		if (this.minDate.compareTo(tmpDateIni) < 0)
			this.minDate = tmpDateIni;
		this.maxDate = tmpDateFin;
		this.maxX = tmpMaxX;
		this.maxY = tmpMaxY;
		this.minX = tmpMinX;
		this.minY = tmpMinY;

	}

	private void removeByTime(Date ini, Date fin) throws Exception {
		System.out.println("Range of time in the dataSet:"
				+ this.minDate.toString() + " to " + this.maxDate.toString());
		int indexIni = -1;
		int indexExit = -1;
		for (int k = 0; k < this.data.size(); k++) {
			ArrayList<Object> e = data.get(k);
			Date tmp = (Date) e.get(0);
			int i = tmp.compareTo(ini);
			int f = tmp.compareTo(fin);

			if (indexIni == -1 && i >= 0 && f <= 0)
				indexIni = k;
			if (indexIni != -1 && indexExit == -1 && i >= 0 && f > 0)
				indexExit = k;
			if (f > 0)
				break;

		}
		List<ArrayList<Object>> tmpList = null;
		if (indexExit == -1) // end of the file
		{
			tmpList = new ArrayList<ArrayList<Object>>(this.data.subList(
					indexIni, this.data.size()));

		} else {
			tmpList = new ArrayList<ArrayList<Object>>(this.data.subList(
					indexIni, indexExit));
		}
		this.data = tmpList;
		ArrayList<Object> obj = this.data.get(0);
		this.minDate = (Date) obj.get(0);
		obj = this.data.get(this.data.size() - 1);
		this.maxDate = (Date) obj.get(0);

		getMinMaxXY();

	}

	private void getMinMaxXY() {
		// TODO Auto-generated method stub
		List<ArrayList<Object>> tmpList = new ArrayList<ArrayList<Object>>();
		tmpList.addAll(this.data);
		ArrayList<Object> tmpObj = null;
		Collections.sort(tmpList, new Comparator<ArrayList<Object>>() {
			@Override
			public int compare(ArrayList<Object> obj1, ArrayList<Object> obj2) {
				Object o1 = obj1.get(4);
				Object o2 = obj2.get(4);
				if (o1.getClass().equals(o2.getClass())) {
					return ((Comparable) o1).compareTo((Comparable) o2);
				} else {
					if (o1.getClass().getCanonicalName()
							.equals("java.lang.String")) {
						return 1;
					} else {
						return -1;
					}
				}

			}

		});
		int size = tmpList.size() - 1;
		tmpObj = tmpList.get(0);
		this.minX = (double) tmpObj.get(4);
		tmpObj = tmpList.get(size);
		this.maxX = (double) tmpObj.get(4);

		Collections.sort(tmpList, new Comparator<ArrayList<Object>>() {
			@Override
			public int compare(ArrayList<Object> obj1, ArrayList<Object> obj2) {
				Object o1 = obj1.get(5);
				Object o2 = obj2.get(5);
				if (o1.getClass().equals(o2.getClass())) {
					return ((Comparable) o1).compareTo((Comparable) o2);
				} else {
					if (o1.getClass().getCanonicalName()
							.equals("java.lang.String")) {
						return 1;
					} else {
						return -1;
					}
				}

			}

		});
		tmpObj = tmpList.get(0);
		this.minY = (double) tmpObj.get(5);
		tmpObj = tmpList.get(size);
		this.maxY = (double) tmpObj.get(5);
	}

	private List<ArrayList<Object>> read() throws Exception {
		// TODO Auto-generated method stub

		this.chooseFile();
		FileReader fr = new FileReader(this.pathAndFileInfo.get(2));
		BufferedReader br = new BufferedReader(fr);
		ArrayList<Object> tmp = null;
		String linea = "";
		List<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();

		while (((linea = br.readLine()) != null)) {

			StringTokenizer tokens;
			StringTokenizer tokensDate;

			tokens = new StringTokenizer(linea);
			tmp = new ArrayList<Object>();
			String dateToFormat = tokens.nextToken();
			tokensDate = new StringTokenizer(dateToFormat, ":");

			String formatedDate = "2001-" + tokensDate.nextToken() + " "
					+ tokensDate.nextToken() + ":" + tokensDate.nextToken()
					+ ":" + tokensDate.nextToken();

			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-dd-MM HH:mm:ss");
			Date date = format.parse(formatedDate);

			int bus = Integer.parseInt(tokens.nextToken());
			int route = Integer.parseInt(tokens.nextToken());
			String unknow = tokens.nextToken();
			double x = Double.parseDouble(tokens.nextToken()) * 0.3048;
			double y = Double.parseDouble(tokens.nextToken()) * 0.3048;
			tmp.add(date);
			tmp.add(bus);
			tmp.add(route);
			tmp.add(unknow);
			tmp.add(x);
			tmp.add(y);
			data.add(tmp);

			// comparations:
			if (date.compareTo(this.maxDate) > 0)
				this.maxDate = date;
			if (date.compareTo(this.minDate) < 0)
				this.minDate = date;
			if (x > this.maxX)
				this.maxX = x;
			if (x < this.minX)
				this.minX = x;
			if (y > this.maxY)
				this.maxY = y;
			if (y < this.minY)
				this.minY = y;
		}
		br.close();
		fr.close();
		return data;

	}

	private void showInformation() {

		System.out.println("Coverage of X:" + this.minX + " to " + this.maxX);
		System.out.println("Coverage of Y:" + this.minY + " to " + this.maxY);
	}

	/*
	 * created file for configuration with information based in the paths and
	 * routes
	 */
	private void createPath() throws IOException {
		routesPlotFile();
		getGreaterBusId();
		getPathFile();

	}

	private void getPathFile() throws IOException {
		// TODO Auto-generated method stub
		FileWriter wr = null;
		BufferedWriter bw = null;
		this.busesIds = new HashMap<Integer, Integer>();
		HashMap<Integer, ArrayList<ArrayList<Object>>> buses = new HashMap<Integer, ArrayList<ArrayList<Object>>>();
		List<Integer> listBusesIds = new ArrayList<Integer>();
		wr = new FileWriter(this.pathAndFileInfo.get(1) + "paths\\"
				+ this.pathAndFileInfo.get(0) + "pathFile.txt");
		bw = new BufferedWriter(wr);
		String toInputInFile = "";
		int contBuses = 0;
		for (int k = 0; k < this.data.size(); k++) {
			ArrayList<Object> tmp = this.data.get(k);
			int busid = ((int) tmp.get(1)) - this.minBusId;

			double time = (double) tmp.get(0);
			double x = (double) tmp.get(4);
			double y = (double) tmp.get(5);
			
			if (!this.busesIds.containsKey(busid)) {
				buses.put(contBuses, new ArrayList<ArrayList<Object>>());
				this.busesIds.put(busid, contBuses);
				listBusesIds.add(contBuses);
				contBuses++;
			}
			int tmpId = this.busesIds.get(busid);
			List<ArrayList<Object>> tmpList = buses.get(tmpId);
			ArrayList<Object> tmpObj = new ArrayList<Object>();
			tmpObj.add(time);
			tmpObj.add(x);
			tmpObj.add(y);
			tmpList.add(tmpObj);

		}
		Collections.sort(listBusesIds);
		
		this.maxBusId = contBuses - 1;
		this.minBusId = 0;
		toInputInFile = (maxBusId + " " + this.dminDate + " " + this.dmaxDate
				+ " " + this.minX + " " + this.maxX + " " + this.minY + " " + this.maxY);

		try {

			bw.append(toInputInFile);
			bw.newLine();

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int j = 0; j < listBusesIds.size(); j++) {

			int busIdInList = listBusesIds.get(j);
			toInputInFile = Integer.toString(busIdInList);
			ArrayList<ArrayList<Object>> objTimesXY = buses.get(busIdInList);
			for (int i = 0; i < objTimesXY.size(); i++) {

				ArrayList<Object> objTXY = objTimesXY.get(i);
				toInputInFile = toInputInFile + " " + objTXY.get(0) + ","
						+ objTXY.get(1) + "," + objTXY.get(2);

			}
			try {

				bw.append(toInputInFile);
				bw.newLine();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		bw.close();
		wr.close();

	}

	private void getGreaterBusId() {
		// TODO Auto-generated method stub
		List<ArrayList<Object>> tmpList = new ArrayList<ArrayList<Object>>();
		tmpList.addAll(this.data);
		Collections.sort(tmpList, new Comparator<ArrayList<Object>>() {
			@Override
			public int compare(ArrayList<Object> obj1, ArrayList<Object> obj2) {
				Object o1 = obj1.get(1);
				Object o2 = obj2.get(1);
				if (o1.getClass().equals(o2.getClass())) {
					return ((Comparable) o1).compareTo((Comparable) o2);
				} else {
					if (o1.getClass().getCanonicalName()
							.equals("java.lang.String")) {
						return 1;
					} else {
						return -1;
					}
				}

			}

		});
		int size = tmpList.size();
		ArrayList<Object> tmpObj = tmpList.get(size - 1);
		this.maxBusId = (int) tmpObj.get(1);
		tmpObj = tmpList.get(0);
		this.minBusId = (int) tmpObj.get(1);
	}

	private void routesPlotFile() throws IOException {
		// TODO Auto-generated method stub
		FileWriter wr = null;
		BufferedWriter bw = null;
		this.routes = new HashMap<Integer, ArrayList<Coord>>();
		Set<Point> coordenades = new HashSet<Point>();
		for (int k = 0; k < this.data.size(); k++) {
			ArrayList<Object> tmp = this.data.get(k);
			int route = (int) tmp.get(2);
			double x = (double) tmp.get(4);
			int ix = (int) x;
			double y = (double) tmp.get(5);
			int iy = (int) y;
			coordenades.add(new Point(ix, iy));
			if (!this.routes.containsKey(route))
				this.routes.put(route, new ArrayList<Coord>());
			this.routes.get(route).add(new Coord(x, y));
		}
		for (Map.Entry<Integer, ArrayList<Coord>> entry : this.routes
				.entrySet()) {

			wr = new FileWriter(this.pathAndFileInfo.get(1) + "routes\\route"
					+ entry.getKey() + ".txt");
			bw = new BufferedWriter(wr);
			bw.write("X Y");
			bw.newLine();
			ArrayList<Coord> tmpList = entry.getValue();
			int size = tmpList.size();
			for (int k = 0; k < size; k++) {
				bw.write(tmpList.get(k).getX() + " " + tmpList.get(k).getY());
				bw.newLine();
			}
			bw.close();
			wr.close();

		}
		createCoordenadesFile(coordenades);
	}

	private void createCoordenadesFile(Set<Point> coordenades)
			throws IOException {
		// TODO Auto-generated method stub
		FileWriter wr = new FileWriter(this.pathAndFileInfo.get(1)
				+ "mapCoord\\" + this.pathAndFileInfo.get(0) + "Coord.txt");

		BufferedWriter bw = new BufferedWriter(wr);
		int nroGroups = coordenades.size();
		String cadena = "";
		try {
			cadena = "Scenario.name = "
					+ this.pathAndFileInfo.get(0).toUpperCase() + "Coord";
			bw.append(cadena);
			bw.newLine();
			cadena = "Scenario.simulateConnections = false";
			bw.append(cadena);
			bw.newLine();
			cadena = "Scenario.updateInterval = 0.1";
			bw.append(cadena);
			bw.newLine();
			cadena = "Scenario.nrofHostGroups = " + nroGroups;
			bw.append(cadena);
			bw.newLine();
			cadena = "Scenario.endTime = 1000";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.movementModel =  StationaryMovement";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.nrofHosts = 1";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.router = EpidemicRouter";
			bw.append(cadena);
			bw.newLine();
			cadena = "highspeedInterface.type = SimpleBroadcastInterface";
			bw.append(cadena);
			bw.newLine();
			cadena = "highspeedInterface.transmitSpeed = 10M";
			bw.append(cadena);
			bw.newLine();
			cadena = "highspeedInterface.transmitRange = 250";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.bufferSize = 1G";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.waitTime = 0, 120";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.nrofInterfaces = 1";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.interface1 = highspeedInterface";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.msgTtl = 300";
			bw.append(cadena);
			bw.newLine();
			cadena = "Events.nrof = 1";
			bw.append(cadena);
			bw.newLine();
			cadena = "Events1.class = MessageEventGenerator";
			bw.append(cadena);
			bw.newLine();
			cadena = "Events1.interval = 25,60";
			bw.append(cadena);
			bw.newLine();
			cadena = "Events1.size = 500k,1M";
			bw.append(cadena);
			bw.newLine();
			cadena = "Events1.hosts = 0, 5";
			bw.append(cadena);
			bw.newLine();

			int wide = (int) (this.maxX - this.minX + 1000);
			int height = (int) (this.maxY - this.minY + 1000);
			cadena = "Events1.prefix = M";
			bw.append(cadena);
			bw.newLine();
			cadena = "MovementModel.rngSeed = 1";
			bw.append(cadena);
			bw.newLine();
			cadena = "MovementModel.worldSize = " + wide + "," + height;
			bw.append(cadena);
			bw.newLine();
			cadena = "MovementModel.warmup = 1000";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.nrofReports = 1";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.warmup = 0";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.reportDir = reports/";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report1 = MessageStatsReport";
			bw.append(cadena);
			bw.newLine();

			cadena = "Optimization.cellSizeMult = 5";
			bw.append(cadena);
			bw.newLine();
			cadena = "Optimization.randomizeUpdateOrder = true";
			bw.append(cadena);
			bw.newLine();
			cadena = "GUI.UnderlayImage.fileName = data/mapa_BAW.png";
			bw.append(cadena);
			bw.newLine();
			cadena = "GUI.UnderlayImage.offset = -10, -15";
			bw.append(cadena);
			bw.newLine();
			cadena = "GUI.UnderlayImage.scale = 24.8";
			bw.append(cadena);
			bw.newLine();
			cadena = "GUI.UnderlayImage.rotate = 0";
			bw.append(cadena);
			bw.newLine();
			cadena = "GUI.EventLogPanel.nrofEvents = 100";
			bw.append(cadena);
			bw.newLine();

		} catch (Exception e) {
			e.printStackTrace();
		}
		int contGroups = 1;
		for (Point s : coordenades) {
			try {

				cadena = "Group" + contGroups + ".nodeLocation ="
						+ (int) s.getX() + "," + (int) s.getY();
				bw.append(cadena);
				bw.newLine();
				cadena = "Group" + contGroups + ".groupID = n" + contGroups;
				bw.append(cadena);
				bw.newLine();
				contGroups++;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		bw.close();
		wr.close();

	}

	public void getActiveFile() throws IOException {

		StringTokenizer tokens = null;
		FileReader fr = new FileReader(this.pathAndFileInfo.get(1) + "paths\\"
				+ this.pathAndFileInfo.get(0) + "pathFile.txt");
		FileWriter wr = new FileWriter(this.pathAndFileInfo.get(1) + "active\\"
				+ this.pathAndFileInfo.get(0) + "activeFile.txt");
		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(wr);
		String linea = "";
		int count = 0;
		try {
			while (((linea = br.readLine()) != null) /* && (contador <= 100000) */) {
				if (count > 0) {
					String lastToken = linea
							.substring(linea.lastIndexOf(" ") + 1);
					tokens = new StringTokenizer(linea);
					int id = Integer.parseInt(tokens.nextToken());
					if (id == 416)
						System.out.println("aoucgh");
					String initTXY = tokens.nextToken();

					StringTokenizer tokenstxy = new StringTokenizer(initTXY,
							",");
					String initTime = tokenstxy.nextToken();
					tokenstxy = new StringTokenizer(lastToken, ",");
					String endTime = tokenstxy.nextToken();
					String output = Integer.toString(id) + " " + initTime + " "
							+ endTime;
					bw.append(output);
					bw.newLine();

				}
				count++;

			}
			bw.close();
			wr.close();
			br.close();
			fr.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
	
	private void createConfigurationFile() throws IOException {
		// TODO Auto-generated method stub
		FileWriter wr = new FileWriter(this.pathAndFileInfo.get(1)
				+ "configurations\\" + this.pathAndFileInfo.get(0) + "Conf.txt");
		BufferedWriter bw = new BufferedWriter(wr);
		String cadena = "";
		try {
			cadena = "Scenario.name = "
					+ this.pathAndFileInfo.get(0).toUpperCase()
					+ "_%%Events1.size%%_interface_%%Group.interface1%%_nos_%%Group.nrofHosts%%_buffer_%%Group.bufferSize%%_tempo_%%Scenario.endTime%%";
			bw.append(cadena);
			bw.newLine();
			cadena = "Scenario.simulateConnections = true";
			bw.append(cadena);
			bw.newLine();
			cadena = "Scenario.updateInterval = 0.1";
			bw.append(cadena);
			bw.newLine();
			cadena = "Scenario.nrofHostGroups = 1";
			bw.append(cadena);
			bw.newLine();
			cadena = "Scenario.endTime =" + this.dmaxDate;
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.movementModel =  ExternalPathMovement";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.traceFile = " + this.pathAndFileInfo.get(0)
					+ "pathFile.txt";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.activeFile = " + this.pathAndFileInfo.get(0)
					+ "activeFile.txt";
			bw.append(cadena);
			bw.newLine();
			int numero = this.maxBusId;
			numero++;
			cadena = "Group.nrofHosts =" + numero;
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.router = EpidemicRouter";
			bw.append(cadena);
			bw.newLine();
			cadena = "highspeedInterface.type = SimpleBroadcastInterface";
			bw.append(cadena);
			bw.newLine();
			cadena = "highspeedInterface.transmitSpeed = 10M";
			bw.append(cadena);
			bw.newLine();
			cadena = "highspeedInterface.transmitRange = 250";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.bufferSize = 1G";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.waitTime = 0, 120";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.nrofInterfaces = 1";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.interface1 = highspeedInterface";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.msgTtl = 300";
			bw.append(cadena);
			bw.newLine();
			cadena = "Group.groupID = i";
			bw.append(cadena);
			bw.newLine();
			cadena = "Events.nrof = 1";
			bw.append(cadena);
			bw.newLine();
			cadena = "Events1.class = MessageEventGenerator";
			bw.append(cadena);
			bw.newLine();
			cadena = "Events1.interval = 25,60";
			bw.append(cadena);
			bw.newLine();
			cadena = "Events1.size = 500k,1M";
			bw.append(cadena);
			bw.newLine();
			cadena = "Events1.hosts = 0," + this.maxBusId;
			bw.append(cadena);
			bw.newLine();

			int wide = (int) (this.maxX - this.minX + 1000);
			int height = (int) (this.maxY - this.minY + 1000);
			cadena = "Events1.prefix = M";
			bw.append(cadena);
			bw.newLine();
			cadena = "MovementModel.rngSeed = 1";
			bw.append(cadena);
			bw.newLine();
			cadena = "MovementModel.worldSize = " + wide + "," + height;
			bw.append(cadena);
			bw.newLine();
			cadena = "MovementModel.warmup = 1000";
			bw.append(cadena);
			bw.newLine();
			cadena = "MapBasedMovement.nrofMapFiles = 0";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.nrofReports = 18";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.warmup = 0";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.reportDir = reports/";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.granularity = 60";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report1 = MessageStatsReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report2 = AdjacencyGraphvizReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report3 = ConnectivityONEReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report4 = ContactsDuringAnICTReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report5 = ContactsPerHourReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report6 = ContactTimesReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report7 = DeliveredMessagesReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report8 = DistanceDelayReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report9 = EncountersVSUniqueEncountersReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report10 = InterContactTimesReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report11 = MessageAvailabilityReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report12 = MessageCopyCountReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report13 = MessageDelayReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report14 = MessageDeliveryReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report15 = MessageLocationReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report16 = TotalContactTimeReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report17 =  TotalEncountersReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Report.report18 = UniqueEncountersReport";
			bw.append(cadena);
			bw.newLine();
			cadena = "Optimization.cellSizeMult = 5";
			bw.append(cadena);
			bw.newLine();
			cadena = "Optimization.randomizeUpdateOrder = true";
			bw.append(cadena);
			bw.newLine();
			cadena = "GUI.UnderlayImage.fileName = data/mapa_BAW.png";
			bw.append(cadena);
			bw.newLine();
			cadena = "GUI.UnderlayImage.offset = -10, -15";
			bw.append(cadena);
			bw.newLine();
			cadena = "GUI.UnderlayImage.scale = 24.8";
			bw.append(cadena);
			bw.newLine();
			cadena = "GUI.UnderlayImage.rotate = 0";
			bw.append(cadena);
			bw.newLine();
			cadena = "GUI.EventLogPanel.nrofEvents = 100";
			bw.append(cadena);
			bw.newLine();

			bw.close();
			wr.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Translator test = new Translator();
		test.cut();
		test.convertTimeToSeconds();
		test.createPath();
		test.getActiveFile();
		test.createConfigurationFile();

	}

}
