package routing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;
import core.SimError;

/**
 * Implementation of Spray and wait router as depicted in <I>Spray and Wait: An
 * Efficient Routing Scheme for Intermittently Connected Mobile Networks</I> by
 * Thrasyvoulos Spyropoulus et al.
 * 
 */
public class SaWClassifiedRouter extends ClassifierRouter {
	/** identifier for the initial number of copies setting ({@value} ) */
	public static final String NROF_COPIES = "nrofCopies";
	/** identifier for the binary-mode setting ({@value} ) */
	public static final String BINARY_MODE = "binaryMode";

	public static final String SPRAYANDWAIT_NS = "SaWClassifiedRouter";
	/** Message property key */
	public static final String MSG_COUNT_PROPERTY = SPRAYANDWAIT_NS + "."
			+ "copies";

	protected int initialNrofCopies;
	protected boolean isBinary;
	private Map<Integer, double[]> evolvingProbability = new HashMap<Integer, double[]>();

	// private int anteriorNroCopiesInPreviusZone = 0;

	public SaWClassifiedRouter(Settings s) {
		super(s);
		Settings snwSettings = new Settings(SPRAYANDWAIT_NS);

		initialNrofCopies = snwSettings.getInt(NROF_COPIES);
		isBinary = snwSettings.getBoolean(BINARY_MODE);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param r
	 *            The router prototype where setting values are copied from
	 * @throws IOException
	 */
	protected SaWClassifiedRouter(SaWClassifiedRouter r) throws IOException {
		super(r);
		this.initialNrofCopies = r.initialNrofCopies;
		this.isBinary = r.isBinary;
		initProbabilities();

	}

	private void initProbabilities() throws IOException {
		// TODO Auto-generated method stub
		FileReader fr = new FileReader(
				"D:\\one_1.5.1-RC2\\probabilitiesSaW50.txt");
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		while (((line = br.readLine()) != null)) {
			StringTokenizer tokens = new StringTokenizer(line);
			int interval = Integer.parseInt(tokens.nextToken());
			double p1 = Double.parseDouble(tokens.nextToken());
			double p2 = Double.parseDouble(tokens.nextToken());
			double p3 = Double.parseDouble(tokens.nextToken());
			double p4 = Double.parseDouble(tokens.nextToken());
			double p5 = Double.parseDouble(tokens.nextToken());
			if (!this.evolvingProbability.containsKey(interval)) {
				double[] tmp = { p1, p2, p3, p4, p5 };
				this.evolvingProbability.put(interval, tmp);

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

		String classified = this.getHost().getClassifierWeka()
				.classifier((double) interval, li, zone, id);
		int classe = Integer.parseInt(classified.substring(1));
		return classe;

	}

	@Override
	protected Connection tryMessagesToConnections(List<Message> messages,
			List<Connection> connections) {
		if (this.getHost().getClassifierWeka().getFase()
				.equals(DTNHost.collect)) {
			for (int i = 0, n = connections.size(); i < n; i++) {
				Connection con = connections.get(i);
				Message started = tryAllMessages(con, messages);
				if (started != null) {
					return con;
				}
			}
		} else if (this.getHost().getClassifierWeka().getFase()
				.equals(DTNHost.test)) {

			/*
			 * anterior SaW, excluyendo classe baja for (int i = 0, n =
			 * connections.size(); i < n; i++) {
			 * 
			 * Connection con = connections.get(i); DTNHost other =
			 * con.getOtherNode(this.getHost()); Message started = null;
			 * SaWClassifiedRouter tmprouter = (SaWClassifiedRouter) other
			 * .getRouter(); if (tmprouter.evaluate() >= 2) { started =
			 * tryAllMessages(con, messages); } if (started != null) { return
			 * con; } }
			 */
			/*
			 * PROBABILITY VETOR 50 double[] classProbability = new double[5];
			 * for (int i = 0, n = connections.size(); i < n; i++) { Connection
			 * con = connections.get(i); SaWClassifiedRouter otherRouter =
			 * (SaWClassifiedRouter) (con
			 * .getOtherNode(this.getHost())).getRouter(); int classeThisHost =
			 * otherRouter.evaluate(); double prob = 0; int interval = (int)
			 * Math.round(SimClock.getTime() / 600D);
			 * 
			 * classProbability = this.evolvingProbability.get(interval); if
			 * (classProbability == null) System.out.println("ERROR"); if
			 * (classeThisHost > classProbability.length) { throw new
			 * SimError("unreconized class number"); } else { prob =
			 * classProbability[classeThisHost - 1]; double random =
			 * rng.nextDouble(); if (random <= prob) { Message started =
			 * tryAllMessages(con, messages); if (started != null) { return con;
			 * } } }
			 * 
			 * }
			 */
			/*
			 * deliverying to lowest nodes witha fixed probability for (int i =
			 * 0, n = connections.size(); i < n; i++) { Connection con =
			 * connections.get(i); SaWClassifiedRouter otherRouter =
			 * (SaWClassifiedRouter) (con
			 * .getOtherNode(this.getHost())).getRouter(); int classeThisHost =
			 * otherRouter.evaluate(); if (classeThisHost >= 2) { Message
			 * started = tryAllMessages(con, messages); if (started != null) {
			 * return con; }
			 * 
			 * } else { double prob = 0.10;
			 * 
			 * double random = rng.nextDouble(); if (random <= prob) { Message
			 * started = tryAllMessages(con, messages); if (started != null) {
			 * return con; } } }
			 * 
			 * }
			 */
			/*
			 * O menor so pode entregar a os maiores que ele. int
			 * classThisRouter = this.evaluate(); for (int i = 0, n =
			 * connections.size(); i < n; i++) { Connection con =
			 * connections.get(i); SaWClassifiedRouter otherRouter =
			 * (SaWClassifiedRouter) (con
			 * .getOtherNode(this.getHost())).getRouter(); int classeThisHost =
			 * otherRouter.evaluate(); if (classeThisHost >= 2) { Message
			 * started = tryAllMessages(con, messages); if (started != null) {
			 * return con; }
			 * 
			 * } else {
			 * 
			 * 
			 * 
			 * if (classeThisHost != classThisRouter) { Message started =
			 * tryAllMessages(con, messages); if (started != null) { return con;
			 * } } }
			 * 
			 * }
			 */
			int classThisRouter = this.evaluate();
			for (int i = 0, n = connections.size(); i < n; i++) {
				Connection con = connections.get(i);
				SaWClassifiedRouter otherRouter = (SaWClassifiedRouter) (con
						.getOtherNode(this.getHost())).getRouter();
				int classeThisHost = otherRouter.evaluate();
				
					

				if ((classeThisHost > classThisRouter)||(classThisRouter==5 && classThisRouter==classeThisHost) ){
					Message started = tryAllMessages(con, messages);
					if (started != null) {
						return con;
					}
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
		try {
			return new SaWClassifiedRouter(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
