package routing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import movement.MovementModel;
import core.Coord;
import core.DTNHost;
import core.Settings;
import core.SettingsError;
import core.SimClock;
import core.SimScenario;

public class ClassifierRouter extends ActiveRouter {
	protected boolean isCollecting;
	protected String generalPath;
	protected String zonesGrids[][] = null;
	protected int nroZonesX = 0;
	protected int nroZonesY = 0;
	protected HashMap<String, MessageRecievedInformation> receivedMessageData = null;
	protected Random rng = new Random(535622341);
	public static int squareSise = 1000;
	public static final String CLASSIFIER_ROUTER_NS = "ClassifierRouter";
	protected static final String COLLECTING_FASE = "isCollecting";
	protected static final String PATH_S = "ClassifierFilesPath";
	/** SprayAndWait router's settings name space ({@value} ) */

	protected ClassifierRouter(ClassifierRouter r) {
		super(r);
		this.isCollecting = r.isCollecting;
		this.receivedMessageData = new HashMap<String, MessageRecievedInformation>();
		this.generalPath=r.generalPath;
		initWorldsize();
		// TODO Auto-generated constructor stub
	}
	
	public ClassifierRouter(Settings s) {
		super(s);
		Settings snwSettings = new Settings(CLASSIFIER_ROUTER_NS);
		setCollecting(snwSettings.getBoolean(COLLECTING_FASE));
		this.generalPath= snwSettings.getSetting(this.PATH_S);
		
		File f = new File(this.generalPath);
		if (!f.exists() || !f.isDirectory()) {
			throw new SettingsError("The classifier folder does not exists or is not a valid folder");
		}
		
		
		
	}
	private void initWorldsize() {
		// TODO Auto-generated method stub
		Settings s = new Settings(SimScenario.SCENARIO_NS);
		// nrofGroups = s.getInt(NROF_GROUPS_S);

		s.setNameSpace(MovementModel.MOVEMENT_MODEL_NS);
		int[] worldSize = s.getCsvInts(MovementModel.WORLD_SIZE, 2);

		int x_size = worldSize[0];
		int y_size = worldSize[1];

		if (x_size % this.squareSise == 0)
			setNroZonesX((x_size / this.squareSise));
		else
			setNroZonesX((int) (x_size / this.squareSise) + 1);
		// para y, numero de cuadrados
		if (y_size % this.squareSise == 0)
			setNroZonesY((y_size / this.squareSise));
		else
			setNroZonesY((int) (y_size / this.squareSise) + 1);

		this.zonesGrids = new String[getNroZonesX()][getNroZonesY()];
		iniciarGrids();
	}
	
	
	
	public String getZone(Coord c) {
		int x = 0, y = 0;
		double dx = c.getX();
		double dy = c.getY();
		if (dx % this.squareSise == 0)
			x = (int) (dx / this.squareSise);
		else
			x = (int) (dx / this.squareSise) + 1;
		// para y, numero de cuadrados
		if (dy % this.squareSise == 0)
			y = (int) (dy / this.squareSise);
		else
			y = (int) (dy / this.squareSise) + 1;
		return this.zonesGrids[x][y];

	}

	private void iniciarGrids() {
		// TODO Auto-generated method stub
		int cont = 0;
		for (int i = 0; i < this.getNroZonesX(); i++) {
			for (int j = 0; j < this.getNroZonesY(); j++) {
				this.zonesGrids[i][j] = "Z" + cont;
				cont++;
			}
		}

	}

	public boolean isCollecting() {
		return isCollecting;
	}

	protected void setCollecting(boolean isCollecting) {
		this.isCollecting = isCollecting;
	}
	
	public int getNroZonesX() {
		return nroZonesX;
	}

	public void setNroZonesX(int nroZonesX) {
		this.nroZonesX = nroZonesX;
	}

	public int getNroZonesY() {
		return nroZonesY;
	}

	public void setNroZonesY(int nroZonesY) {
		this.nroZonesY = nroZonesY;
	}

	
	@Override
	public MessageRouter replicate() {
		// TODO Auto-generated method stub
		return new ClassifierRouter(this);
	}
	
	private class MessageRecievedInformation {
		private int timeInterval = 0;
		private int li = 0;
		private String zone = "";
		private Coord recievedLocation = null;
		private double recievedTime = 0;
	}
	public void registerMessageRecieved(String id, int interval, int li,
			String zone, Coord c, double time) {
		// TODO Auto-generated method stub
		// String tmp= interval+" "+li+" "+zone;
		MessageRecievedInformation info = new MessageRecievedInformation();
		info.timeInterval = interval;
		info.li = li;
		info.zone = zone;
		info.recievedLocation = new Coord(c.getX(), c.getY());
		info.recievedTime = time;
		this.receivedMessageData.put(id, info);
	}
	public String getReceivedMessageInformation(String m) {
		if (this.receivedMessageData.containsKey(m)) {
			DTNHost host = this.getHost();
			MessageRecievedInformation info = this.receivedMessageData.get(m);
			double distancia = host.getLocation().distance(
					info.recievedLocation);
			double time = SimClock.getTime() - info.recievedTime;
			String tmp = host.toString() + " " + info.timeInterval + " "
					+ info.li + " " + info.zone + " " + distancia + " " + time;
			this.receivedMessageData.remove(m);
			return tmp;

		} else
			return "";
	}
	private void writeHeaderArff(String fileName, String tmp) {
		// TODO Auto-generated method stub
		File log = new File(fileName);

		try {
			FileWriter fileWriter = new FileWriter(log, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write(tmp);
			bufferedWriter.newLine();
			bufferedWriter.write("@data");
			bufferedWriter.newLine();

			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void reinitiateReceivedMessageInformation() {
		if (this.receivedMessageData != null)
			this.receivedMessageData.clear();
	}


}
