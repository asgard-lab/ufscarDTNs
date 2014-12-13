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
public class EpidemicJ48Router extends ClassifierRouter {
	
	
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
		if (this.getHost().getClassifierWeka().getFase().equals(DTNHost.collect)) {
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
		} else if (this.getHost().getClassifierWeka().equals(DTNHost.test)) {

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

		String classified = this.getHost().getClassifierWeka().classifier((double) interval, li,
				zone, id);
		int classe = Integer.parseInt(classified.substring(1));
		return classe;

	}

	@Override
	public EpidemicJ48Router replicate() {
		return new EpidemicJ48Router(this);
	}

	

	

	

	

	
}