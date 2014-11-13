/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package routing;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import movement.MovementModel;

import core.Connection;
import core.Coord;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;
import core.SimError;
import core.SimScenario;

/** SprayAndWait router's settings name space ({@value} ) */
/**
 * Epidemic message router with drop-oldest buffer and only single transferring
 * connections at a time.
 * 
 * 
 */
public class EpidemicJ48Router extends ActiveRouter {
	private boolean isCollecting;
	private String zonesGrids[][] = null;
	private int nroZonesX = 0;
	private int nroZonesY = 0;
	private HashMap<String, MessageRecievedInformation> receivedMessageData = null;
	protected Random rng = new Random(535622341);
	private int squareSise = 1000;
	public static final String COLLECTING_FASE = "isCollecting";
	public static final String EPIDEMICJ48_NS = "EpidemicJ48Router";
	private double[] classProbability = { 1, 1, 0.8, 0.5, 0.2, 0.1, 0 };

	/**
	 * Constructor. Creates a new message router based on the settings in the
	 * given Settings object.
	 * 
	 * @param s
	 *            The settings object
	 */
	public EpidemicJ48Router(Settings s) {
		super(s);
		Settings epidemicSettings = new Settings(EPIDEMICJ48_NS);
		this.setCollecting(epidemicSettings.getBoolean(COLLECTING_FASE));
		// TODO: read&use epidemic router specific settings (if any)
	}

	/**
	 * Copy constructor.
	 * 
	 * @param r
	 *            The router prototype where setting values are copied from
	 */
	protected EpidemicJ48Router(EpidemicJ48Router r) {
		super(r);
		this.setCollecting(r.isCollecting());
		this.receivedMessageData = new HashMap<String, MessageRecievedInformation>();

		initWorldsize();
		// TODO: copy epidemic settings here (if any)
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

	@Override
	public void update() {
		super.update();
		if (!canStartTransfer()) {
			return; // nothing to transfer or is currently transferring
		}

		if (isTransferring()) {
			return; // nothing to transfer or is currently transferring
		}

		// Try first the messages that can be delivered to final recipient
		if (exchangeDeliverableMessages() != null) {
			return; // started a transfer, don't try others (yet)
		}

		// then try any/all message to any/all connection
		this.tryAllMessagesToAllConnections();
	}

	@Override
	protected Connection tryMessagesToConnections(List<Message> messages,
			List<Connection> connections) {
		if (this.getHost().getFase().equals(DTNHost.collect)) {
			// especifaco no arquivo de configuracao
			// valor false significa que estamos na fase de treinamento
			// ou seja coletando
			for (int i = 0, n = connections.size(); i < n; i++) {
				Connection con = connections.get(i);
				Message started = tryAllMessages(con, messages);
				if (started != null) {
					return con;
				}
			}
		} else if (this.getHost().getFase().equals(DTNHost.test)) {

			for (int i = 0, n = connections.size(); i < n; i++) {
				Connection con = connections.get(i);
				EpidemicJ48Router otherRouter = (EpidemicJ48Router) (con
						.getOtherNode(this.getHost())).getRouter();
				int classeThisHost = otherRouter.evaluate();
				double prob = 0;
				if (classeThisHost > this.classProbability.length) {
					throw new SimError("unreconized class number");
				} else {
					prob= this.classProbability[classeThisHost-1];
					double random = rng.nextDouble();
					if (random <= prob) {
						Message started = tryAllMessages(con, messages);
						if (started != null) {
							return con;
						}
					}
				}

			}

		}

		return null;
	}

	private int evaluate() {
		// TODO Auto-generated method stub
		// double time= SimClock.getTime()-this.TimeEnterInPreviusZone;
		// String
		// tmp=time+" "+this.codePreviusZone+" "+(this.sumLobbyInPreviusZone/this.contLobbyInPreviusZone)+" "+this.nroCopiesInPreviusZone+"\n";
		int interval = (int) Math.round(SimClock.getTime() / 600D);
		int li = this.getHost().getLobby();
		String zone = getZone(this.getHost().getLocation());

		String id = this.getHost().toString();

		String classified = this.getHost().classifier((double) interval, li,
				zone, id);
		int classe = Integer.parseInt(classified.substring(1));
		return classe;

	}

	@Override
	public EpidemicJ48Router replicate() {
		return new EpidemicJ48Router(this);
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

	public void reinitiateReceivedMessageInformation() {
		if (this.receivedMessageData != null)
			this.receivedMessageData.clear();
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

	public boolean isCollecting() {
		return isCollecting;
	}

	public void setCollecting(boolean isCollecting) {
		this.isCollecting = isCollecting;
	}

	private class MessageRecievedInformation {
		private int timeInterval = 0;
		private int li = 0;
		private String zone = "";
		private Coord recievedLocation = null;
		private double recievedTime = 0;
	}
}