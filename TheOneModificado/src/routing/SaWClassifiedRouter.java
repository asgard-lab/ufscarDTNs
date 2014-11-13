/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package routing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import movement.MovementModel;

import util.Velocity;

import core.Connection;
import core.Coord;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;
import core.SimScenario;

/**
 * Implementation of Spray and wait router as depicted in <I>Spray and Wait: An
 * Efficient Routing Scheme for Intermittently Connected Mobile Networks</I> by
 * Thrasyvoulos Spyropoulus et al.
 * 
 */
public class SaWClassifiedRouter extends ActiveRouter {
	/** identifier for the initial number of copies setting ({@value} ) */
	public static final String NROF_COPIES = "nrofCopies";
	/** identifier for the binary-mode setting ({@value} ) */
	public static final String BINARY_MODE = "binaryMode";
	public static final String COLLECTING_FASE = "isCollecting";
	/** SprayAndWait router's settings name space ({@value} ) */
	public static final String SPRAYANDWAIT_NS = "SaWClassifiedRouter";
	/** Message property key */
	public static final String MSG_COUNT_PROPERTY = SPRAYANDWAIT_NS + "."
			+ "copies";

	protected int initialNrofCopies;
	protected boolean isBinary;
	private boolean isCollecting;
	private String zonesGrids[][] = null;
	private int nroZonesX = 0;
	private int nroZonesY = 0;
	private HashMap<String, MessageRecievedInformation> receivedMessageData = null;
	protected Random rng = new Random(535622341);
	private int squareSise = 1000;

	// private int anteriorNroCopiesInPreviusZone = 0;

	public SaWClassifiedRouter(Settings s) {
		super(s);
		Settings snwSettings = new Settings(SPRAYANDWAIT_NS);
		setCollecting(snwSettings.getBoolean(COLLECTING_FASE));
		initialNrofCopies = snwSettings.getInt(NROF_COPIES);
		isBinary = snwSettings.getBoolean(BINARY_MODE);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param r
	 *            The router prototype where setting values are copied from
	 */
	protected SaWClassifiedRouter(SaWClassifiedRouter r) {
		super(r);
		this.initialNrofCopies = r.initialNrofCopies;
		this.isBinary = r.isBinary;
		this.isCollecting = r.isCollecting;
		this.receivedMessageData = new HashMap<String, MessageRecievedInformation>();

		initWorldsize();
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
	public int receiveMessage(Message m, DTNHost from) {
		return super.receiveMessage(m, from);
	}

	@Override
	public Message messageTransferred(String id, DTNHost from) {
		Message msg = super.messageTransferred(id, from);
		Integer nrofCopies = (Integer) msg.getProperty(MSG_COUNT_PROPERTY);

		assert nrofCopies != null : "Not a SnW message: " + msg;

		if (isBinary) {
			/* in binary S'n'W the receiving node gets ceil(n/2) copies */
			nrofCopies = (int) Math.ceil(nrofCopies / 2.0);
		} else {
			/* in standard S'n'W the receiving node gets only single copy */
			nrofCopies = 1;
		}

		msg.updateProperty(MSG_COUNT_PROPERTY, nrofCopies);
		return msg;
	}

	@Override
	public boolean createNewMessage(Message msg) {
		makeRoomForNewMessage(msg.getSize());

		msg.setTtl(this.msgTtl);
		msg.addProperty(MSG_COUNT_PROPERTY, new Integer(initialNrofCopies));
		addToMessages(msg, true);
		return true;
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

		/* try messages that could be delivered to final recipient */
		if (exchangeDeliverableMessages() != null) {
			return;
		}
		// String zone = this.getZone(this.getHost().getLocation());

		/* create a list of SAWMessages that have copies left to distribute */
		@SuppressWarnings(value = "unchecked")
		List<Message> copiesLeft = sortByQueueMode(getMessagesWithCopiesLeft());

		if (copiesLeft.size() > 0) {
			/* try to send those messages */
			this.tryMessagesToConnections(copiesLeft, getConnections());
		}
	}

	private int evaluate() {
		// TODO Auto-generated method stub
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
	protected Connection tryMessagesToConnections(List<Message> messages,
			List<Connection> connections) {
		if (this.getHost().getFase().equals(DTNHost.collect)) {
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
				DTNHost other = con.getOtherNode(this.getHost());
				Message started = null;
				SaWClassifiedRouter tmprouter = (SaWClassifiedRouter) other
						.getRouter();
				if (tmprouter.evaluate() >= 2) {
					started = tryAllMessages(con, messages);
				}
				if (started != null) {
					return con;
				}
			}

		}

		return null;
	}

	/**
	 * Creates and returns a list of messages this router is currently carrying
	 * and still has copies left to distribute (nrof copies > 1).
	 * 
	 * @return A list of messages that have copies left
	 */
	protected List<Message> getMessagesWithCopiesLeft() {
		List<Message> list = new ArrayList<Message>();

		for (Message m : getMessageCollection()) {
			Integer nrofCopies = (Integer) m.getProperty(MSG_COUNT_PROPERTY);
			assert nrofCopies != null : "SnW message " + m + " didn't have "
					+ "nrof copies property!";
			if (nrofCopies > 1) {
				list.add(m);
			}
		}

		return list;
	}

	/**
	 * Called just before a transfer is finalized (by
	 * {@link ActiveRouter#update()}). Reduces the number of copies we have left
	 * for a message. In binary Spray and Wait, sending host is left with
	 * floor(n/2) copies, but in standard mode, nrof copies left is reduced by
	 * one.
	 */
	@Override
	protected void transferDone(Connection con) {
		Integer nrofCopies;
		String msgId = con.getMessage().getId();
		/* get this router's copy of the message */
		Message msg = getMessage(msgId);

		if (msg == null) { // message has been dropped from the buffer after..
			return; // ..start of transfer -> no need to reduce amount of copies
		}

		/* reduce the amount of copies left */
		nrofCopies = (Integer) msg.getProperty(MSG_COUNT_PROPERTY);
		if (isBinary) {
			nrofCopies /= 2;
		} else {
			nrofCopies--;
		}
		msg.updateProperty(MSG_COUNT_PROPERTY, nrofCopies);
		

	}

	@Override
	public SaWClassifiedRouter replicate() {
		return new SaWClassifiedRouter(this);
	}

	private double getDspatial(Velocity vi, Velocity vj) {
		return (RD(vi, vj) * SR(vi, vj));
	}

	private double SR(Velocity vi, Velocity vj) {
		// TODO Auto-generated method stub
		double min = Double.MAX_VALUE, max = 0;
		if (vi.getSpeed() <= vj.getSpeed())
			min = vi.getSpeed();
		else
			min = vj.getSpeed();
		if (vi.getSpeed() >= vj.getSpeed())
			max = vi.getSpeed();
		else
			max = vj.getSpeed();
		return min / max;
	}

	private double RD(Velocity vi, Velocity vj) {
		// TODO Auto-generated method stub
		double num = 0, den = 1;

		num = vi.getX() * vj.getX() + vi.getY() * vj.getY();
		den = vi.getSpeed() * vj.getSpeed();

		return num / den;
	}

	public boolean isCollecting() {
		return isCollecting;
	}

	public void setCollecting(boolean isCollecting) {
		this.isCollecting = isCollecting;
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

	private class MessageRecievedInformation {
		private int timeInterval = 0;
		private int li = 0;
		private String zone = "";
		private Coord recievedLocation = null;
		private double recievedTime = 0;
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

}
