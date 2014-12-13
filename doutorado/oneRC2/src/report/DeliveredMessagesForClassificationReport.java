/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import movement.MovementModel;
import routing.ClassifierRouter;
import core.Coord;
import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.NetworkInterface;
import core.Settings;
import core.SimClock;
import core.SimScenario;
import core.UpdateListener;
import core.World;

/**
 * Report information about all delivered messages. Messages created during the
 * warm up period are ignored. For output syntax, see {@link #HEADER}.
 */
public class DeliveredMessagesForClassificationReport extends Report implements
		MessageListener, UpdateListener {
	public static String HEADER = "# NodeID  TimeInterval LIndex Zone distance Time  ";
			
	public String SCENARIO_NS = "Scenario";
	public static final String NROF_GROUPS_S = "nrofHostGroups";
	public static final String GROUP_NS = "Group";
	public static final String GROUP_ID_S = "groupID";
	/** number of hosts in the group -setting id ({@value} ) */
	public static final String NROF_HOSTS_S = "nrofHosts";
	private double timeInterval = 600;
	//private List<DTNHost> hosts = null;
	int nroNodes=0;
	private String classificationDistanceGlobalFileName = "F:\\Experimentos\\oneSaWC4\\train\\"
			+ "distanceClassification.arff";

	private String auxiliarFileName = "F:\\Experimentos\\oneSaWC4\\auxiliars\\"
			+ "auxiliar.txt";
	private String zonesNodesArff = "";
	private String nodesArff = "";
	private int nroZonesX = -1;
	private int nroZonesY = -1;
	public static final String CLASSIFIER_ROUTER_NS = "ClassifierRouter";
	protected static final String PATH_S = "ClassifierFilesPath";
	private String generalPath = "";
	private List<String> NodesIds=null;
	

	/**
	 * Constructor.
	 */
	public DeliveredMessagesForClassificationReport() {

		init();

	}

	@Override
	public void init() {
		super.init();
		Settings snwSettings = new Settings(CLASSIFIER_ROUTER_NS);

		this.generalPath = snwSettings.getSetting(this.PATH_S);
		if (!this.generalPath.endsWith("/")) {
			this.generalPath += "/"; // make sure dir ends with directory
										// delimiter
		}
		this.generalPath = this.generalPath + getScenarioName();
		this.classificationDistanceGlobalFileName = this.generalPath
				+ "TRAINDED.arff";
		this.auxiliarFileName = this.generalPath + "auxiliar.txt";
		initWorldsize();
		getIdsNodes();
		write(HEADER);
		initFile(this.auxiliarFileName);

	}

	private void getIdsNodes() {
		// TODO Auto-generated method stub
		List<DTNHost> lnodes = SimScenario.getInstance().getWorld().getHosts();
		int nroNodes= lnodes.size();
		this.NodesIds= new ArrayList<String>();
		for(int i=0;i<nroNodes;i++)
		{
			this.NodesIds.add(lnodes.get(i).getName());
		}
	}

	/**
	 * Returns the given messages hop path as a string
	 * 
	 * @param m
	 *            The message
	 * @return hop path as a string
	 */

	public void messageTransferred(Message m, DTNHost from, DTNHost to,
			boolean finalTarget) {
		if (isWarmupID(m.getId())) {
			return;
		}
		if ((!(from.getRouter() instanceof ClassifierRouter) || !(to
				.getRouter() instanceof ClassifierRouter)))
			return;

		if (m.getFrom().getAddress() != from.getAddress()) {
			if (from.getRouter() instanceof ClassifierRouter) {
				ClassifierRouter router = (ClassifierRouter) from.getRouter();
				String messageInformation = router
						.getReceivedMessageInformation(m.getId());
			//	this.nroZonesX = router.getNroZonesX();
				//this.nroZonesY = router.getNroZonesY();
				if (!messageInformation.equals("")) {
					writeInFile(this.auxiliarFileName, messageInformation);

				}

			}

		}
		if (!finalTarget) {
			if (to.getRouter() instanceof ClassifierRouter) {
				ClassifierRouter router = (ClassifierRouter) to.getRouter();
				int interval = m.intervalReception;

				int li = m.liReception;
				String zone = m.zoneReception;
				Coord c = new Coord(m.recievedLocation.getX(),
						m.recievedLocation.getY());
				double time = m.timeReception;
				router.registerMessageRecieved(m.getId(), interval, li, zone,
						c, time);

			}

		}

	}

	public void newMessage(Message m) {
		if (isWarmup()) {
			addWarmupID(m.getId());
		}
	}

	// nothing to implement for the rest
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {
	}

	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
	}

	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
		if (m.getTo().getAddress() != to.getAddress()) {
			if (to.getRouter() instanceof ClassifierRouter) {
				ClassifierRouter router = (ClassifierRouter) to.getRouter();
				int interval = (int) Math.round(SimClock.getTime()
						/ this.timeInterval);
				int li = to.getLobby();
				String zone = router.getZone(to.getLocation());
				m.intervalReception = interval;
				m.liReception = li;
				m.zoneReception = zone;
				m.recievedLocation = new Coord(to.getLocation().getX(), to
						.getLocation().getY());
				m.timeReception = SimClock.getTime();
				// router.registerMessageRecieved(m.getId(), interval, li,
				// zone);

			}
		}
	}

	@Override
	public void done() {

		int cont = 0;

		for (int i = 0; i < this.nroZonesX; i++) {
			for (int j = 0; j < this.nroZonesY; j++) {
				if (cont == 0)
					zonesNodesArff = "{Z" + cont;
				else
					zonesNodesArff = zonesNodesArff + ",Z" + cont;
				cont++;
			}
		}

		zonesNodesArff = zonesNodesArff + "}";

		for (int i = 0; i < this.NodesIds.size(); i++) {
			if (i == 0)
				nodesArff = "{" + this.NodesIds.get(i);
			else
				nodesArff = nodesArff + "," + this.NodesIds.get(i);

		}
		nodesArff = nodesArff + "}";

		writeGlobalClassificationFiles();
		super.done();
	}

	private void writeGlobalClassificationFiles() {
		// TODO Auto-generated method stub
		String headerForDistanceClassification = "@RELATION dtn_node_copies\n"+"@attribute Node "+
				this.nodesArff+"\n@attribute Interval NUMERIC\n@attribute lobbyIndex NUMERIC\n"
				+ "@attribute Zone "
				+ this.zonesNodesArff
				+ "\n@attribute distance NUMERIC\n@attribute Time NUMERIC\n";

		initFile(this.classificationDistanceGlobalFileName,
				headerForDistanceClassification);

	}

	private void initFile(String file) {

		File log = new File(file);

		if (log.exists() == false) {

			try {
				log.createNewFile();
				// writeHeaderArff();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// escribir separador
			// writeSeparator();
			return;
		}

	}

	private void initFile(String file, String content) {

		File log = new File(file);

		if (log.exists() == false) {

			try {
				log.createNewFile();
				FileWriter fileWriter = new FileWriter(log, true);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(content);
				bufferedWriter.write("@data");
				bufferedWriter.newLine();

				bufferedWriter.close();
				// writeHeaderArff();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// escribir separador
			// writeSeparator();
			return;
		}

	}

	public void writeInFile(String file, String write) {

		File log = new File(file);

		try {
			FileWriter fileWriter = new FileWriter(log, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write(write);
			bufferedWriter.newLine();
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void updated(List<DTNHost> hosts) {
		// TODO Auto-generated method stub
		
	}

	private void initWorldsize() {
		// TODO Auto-generated method stub
		Settings s = new Settings(SimScenario.SCENARIO_NS);
		// nrofGroups = s.getInt(NROF_GROUPS_S);

		s.setNameSpace(MovementModel.MOVEMENT_MODEL_NS);
		int[] worldSize = s.getCsvInts(MovementModel.WORLD_SIZE, 2);
		
		int x_size = worldSize[0];
		int y_size = worldSize[1];

		if (x_size % ClassifierRouter.squareSise == 0)
			nroZonesX = (x_size / ClassifierRouter.squareSise);
		else
			nroZonesX = ((int) (x_size / ClassifierRouter.squareSise) + 1);
		// para y, numero de cuadrados
		if (y_size % ClassifierRouter.squareSise == 0)
			nroZonesY = ((y_size / ClassifierRouter.squareSise));
		else
			nroZonesY = ((int) (y_size / ClassifierRouter.squareSise) + 1);

		
		
	}

	

}
