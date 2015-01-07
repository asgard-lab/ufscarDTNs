/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package routing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

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
public class EpidemicJ48Router extends ClassifierRouter {

	public static final String EPIDEMICJ48_NS = "EpidemicJ48Router";
	private double[] classProbability = { 1, 0.90, 0.80, 0.50, 0.25 };
	private int[][] distanceBetweenClasses = new int[5][];
	private Map<Integer, double[]> evolvingProbability = new HashMap<Integer, double[]>();

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

		// TODO: read&use epidemic router specific settings (if any)
	}

	/**
	 * Copy constructor.
	 * 
	 * @param r
	 *            The router prototype where setting values are copied from
	 * @throws IOException
	 */
	protected EpidemicJ48Router(EpidemicJ48Router r) throws IOException {
		super(r);
		initProbabilities();
		initDistancesBetweenClasses();
		// System.out.println("fefefe");

	}

	private void initDistancesBetweenClasses() throws IOException {
		// TODO Auto-generated method stub
		FileReader fr = new FileReader("D:\\one_1.5.1-RC2\\distances.txt");
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		int nroRow = 0;
		while (((line = br.readLine()) != null)) {
			StringTokenizer tokens = new StringTokenizer(line);
			int dlineWith1 = Integer.parseInt(tokens.nextToken());
			int dlineWith2 = Integer.parseInt(tokens.nextToken());
			int dlineWith3 = Integer.parseInt(tokens.nextToken());
			int dlineWith4 = Integer.parseInt(tokens.nextToken());
			int dlineWith5 = Integer.parseInt(tokens.nextToken());
			int[] tmp = { dlineWith1, dlineWith2, dlineWith3, dlineWith4,
					dlineWith5 };
			this.distanceBetweenClasses[nroRow] = tmp;
			nroRow++;

		}

	}

	private void initProbabilities() throws IOException {
		// TODO Auto-generated method stub
		FileReader fr = new FileReader("D:\\one_1.5.1-RC2\\probabilities.txt");
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
		if (this.getHost().getClassifierWeka().getFase()
				.equals(DTNHost.collect)) {
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
		} else if (this.getHost().getClassifierWeka().getFase()
				.equals(DTNHost.test)) {

			/*
			 * for (int i = 0, n = connections.size(); i < n; i++) { Connection
			 * con = connections.get(i); EpidemicJ48Router otherRouter =
			 * (EpidemicJ48Router) (con
			 * .getOtherNode(this.getHost())).getRouter(); int classeThisHost =
			 * otherRouter.evaluate();
			 * 
			 * double prob = 0; int interval = (int)
			 * Math.round(SimClock.getTime() / 600D); this.classProbability =
			 * this.evolvingProbability.get(interval); if (classProbability ==
			 * null) System.out.println("ERROR"); if (classeThisHost >
			 * this.classProbability.length) { throw new
			 * SimError("unreconized class number"); } else { prob =
			 * this.classProbability[classeThisHost - 1]; double random =
			 * rng.nextDouble(); if (random <= prob) { Message started =
			 * tryAllMessages(con, messages); if (started != null) { return con;
			 * } } }
			 * 
			 * }
			 */
			int bestGroup = -1;
			int GroupOfHostWithBestGroup = -1;
			Connection con = null;
			int classThisRouter = this.evaluate();
			for (int i = 0, n = connections.size(); i < n; i++) {

				Connection tmpCon = connections.get(i);
				EpidemicJ48Router otherRouter = (EpidemicJ48Router) (tmpCon
						.getOtherNode(this.getHost())).getRouter();
				int classeThisHost = otherRouter.evaluate();
				int distance = getDistanceFromClasses(classThisRouter,
						classeThisHost);
				if (bestGroup < distance) {
					bestGroup = distance;
					GroupOfHostWithBestGroup = classeThisHost;
					con = tmpCon;
				}

			}
			if (bestGroup != -1 && con != null) {

				//double prob = 0;
				//int interval = (int) Math.round(SimClock.getTime() / 600D);
				//this.classProbability = this.evolvingProbability.get(interval);
				//if (classProbability == null)
					//System.out.println("ERROR");
			//	if (GroupOfHostWithBestGroup > this.classProbability.length || GroupOfHostWithBestGroup<0) {
				//	throw new SimError("unreconized class number");
			//	} else {
				//	prob = this.classProbability[GroupOfHostWithBestGroup - 1];
				//	double random = rng.nextDouble();
					//if (random <= prob) {
						Message started = tryAllMessages(con, messages);
						if (started != null) {
							return con;
						}
					//}
				//}

			}
/*DISTANCE AND PROB 50
			int bestGroup = -1;
			int GroupOfHostWithBestGroup = -1;
			Connection con = null;
			int classThisRouter = this.evaluate();
			for (int i = 0, n = connections.size(); i < n; i++) {

				Connection tmpCon = connections.get(i);
				EpidemicJ48Router otherRouter = (EpidemicJ48Router) (tmpCon
						.getOtherNode(this.getHost())).getRouter();
				int classeThisHost = otherRouter.evaluate();
				int distance = getDistanceFromClasses(classThisRouter,
						classeThisHost);
				if (bestGroup < distance) {
					bestGroup = distance;
					GroupOfHostWithBestGroup = classeThisHost;
					con = tmpCon;
				}

			}
			if (bestGroup != -1 && con != null) {

				double prob = 0;
				int interval = (int) Math.round(SimClock.getTime() / 600D);
				this.classProbability = this.evolvingProbability.get(interval);
				if (classProbability == null)
					System.out.println("ERROR");
				if (GroupOfHostWithBestGroup > this.classProbability.length || GroupOfHostWithBestGroup<0) {
					throw new SimError("unreconized class number");
				} else {
					prob = this.classProbability[GroupOfHostWithBestGroup - 1];
					double random = rng.nextDouble();
					if (random <= prob) {
						Message started = tryAllMessages(con, messages);
						if (started != null) {
							return con;
						}
					}
				}

			}*/

			/*
			 * PRIORIZACION
			 * 
			 * int bestGroup = -1; String idFromHostWithBestGroup = "";
			 * Connection con = null; for (int i = 0, n = connections.size(); i
			 * < n; i++) {
			 * 
			 * Connection tmpCon = connections.get(i); EpidemicJ48Router
			 * otherRouter = (EpidemicJ48Router) (tmpCon
			 * .getOtherNode(this.getHost())).getRouter(); int classeThisHost =
			 * otherRouter.evaluate(); if (bestGroup < classeThisHost) {
			 * bestGroup = classeThisHost; idFromHostWithBestGroup =
			 * otherRouter.getHost().getName(); con = tmpCon; }
			 * 
			 * } if (bestGroup != -1 && con != null) {
			 * 
			 * double prob = 0; int interval = (int)
			 * Math.round(SimClock.getTime() / 600D); this.classProbability =
			 * this.evolvingProbability.get(interval); if (classProbability ==
			 * null) System.out.println("ERROR"); if (bestGroup >
			 * this.classProbability.length) { throw new
			 * SimError("unreconized class number"); } else { prob =
			 * this.classProbability[bestGroup - 1]; double random =
			 * rng.nextDouble(); if (random <= prob) { Message started =
			 * tryAllMessages(con, messages); if (started != null) { return con;
			 * } } }
			 * 
			 * }
			 */

		}

		return null;
	}

	private int getDistanceFromClasses(int classThisRouter, int classeThisHost) {
		// TODO Auto-generated method stub

		return this.distanceBetweenClasses[(classThisRouter - 1)][(classeThisHost - 1)];
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

		String classified = this.getHost().getClassifierWeka()
				.classifier((double) interval, li, zone, id);
		int classe = Integer.parseInt(classified.substring(1));
		return classe;

	}

	@Override
	public EpidemicJ48Router replicate() {
		try {
			return new EpidemicJ48Router(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}