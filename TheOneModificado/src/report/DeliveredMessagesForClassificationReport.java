/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import java.util.List;

import routing.EpidemicJ48Router;
import routing.SaWClassifiedRouter;

import core.Coord;
import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.SimClock;
import core.UpdateListener;

/**
 * Report information about all delivered messages. Messages created during the
 * warm up period are ignored. For output syntax, see {@link #HEADER}.
 */
public class DeliveredMessagesForClassificationReport extends Report implements
		MessageListener, UpdateListener {
	public static String HEADER = "# time  ID  size  hopcount  deliveryTime  "
			+ "fromHost  toHost  remainingTtl  isResponse  path";
	
	private double timeInterval = 600;
	private List<DTNHost> hosts = null;
	private String classificationDistanceGlobalFileName = "F:\\Experimentos\\oneSaWC4\\train\\"
			+ "distanceClassification.arff";

	private String auxiliarFileName = "F:\\Experimentos\\oneSaWC4\\auxiliars\\"
			+ "auxiliar.txt";
	private String zonesNodesArff = "";
	private String nodesArff = "";
	private int nroZonesX = -1;
	private int nroZonesY = -1;

	/**
	 * Constructor.
	 */
	public DeliveredMessagesForClassificationReport() {
		
		init();

	}

	@Override
	public void init() {
		super.init();
		write(HEADER);
		initFile(this.auxiliarFileName);
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
		if((!(from.getRouter() instanceof SaWClassifiedRouter) || !(to.getRouter() instanceof SaWClassifiedRouter))&&
				(!(from.getRouter() instanceof EpidemicJ48Router) || !(to.getRouter() instanceof EpidemicJ48Router)))
			return;
		
		
		
		if (m.getFrom().getAddress() != from.getAddress()) {
			if (from.getRouter() instanceof SaWClassifiedRouter) {
				SaWClassifiedRouter router = (SaWClassifiedRouter) from
						.getRouter();
				String messageInformation = router
						.getReceivedMessageInformation(m.getId());
				this.nroZonesX=router.getNroZonesX();
				this.nroZonesY=router.getNroZonesY();
				if (!messageInformation.equals("")) {
					writeInFile(this.auxiliarFileName, messageInformation);

				}

			}
			else if (from.getRouter() instanceof EpidemicJ48Router) {
				EpidemicJ48Router router = (EpidemicJ48Router) from
						.getRouter();
				String messageInformation = router
						.getReceivedMessageInformation(m.getId());
				this.nroZonesX=router.getNroZonesX();
				this.nroZonesY=router.getNroZonesY();
				if (!messageInformation.equals("")) {
					writeInFile(this.auxiliarFileName, messageInformation);

				}

			}

		}
		if (!finalTarget) {
			if (to.getRouter() instanceof SaWClassifiedRouter) {
				SaWClassifiedRouter router = (SaWClassifiedRouter) to
						.getRouter();
				int interval = m.intervalReception;
					
				int li = m.liReception;
				String zone = m.zoneReception;
				Coord c=new Coord(m.recievedLocation.getX(),m.recievedLocation.getY());
				double time=m.timeReception;
				router.registerMessageRecieved(m.getId(), interval, li, zone, c,time);
				
				

			}
			else if (to.getRouter() instanceof EpidemicJ48Router) {
				EpidemicJ48Router router = (EpidemicJ48Router) to
						.getRouter();
				int interval = m.intervalReception;
				
				int li = m.liReception;
				String zone = m.zoneReception;
				Coord c=new Coord(m.recievedLocation.getX(),m.recievedLocation.getY());
				double time=m.timeReception;
				router.registerMessageRecieved(m.getId(), interval, li, zone, c,  time);
				

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
		if (m.getTo().getAddress()!=to.getAddress()) {
			if (to.getRouter() instanceof SaWClassifiedRouter) {
				SaWClassifiedRouter router = (SaWClassifiedRouter) to
						.getRouter();
				int interval = (int) Math.round(SimClock.getTime()
						/ this.timeInterval);
				int li = to.getLobby();
				String zone = router.getZone(to.getLocation());
				m.intervalReception=interval;
				m.liReception=li;
				m.zoneReception=zone;
				m.recievedLocation= new Coord(to.getLocation().getX(),to.getLocation().getY());
				m.timeReception= SimClock.getTime();
		//		router.registerMessageRecieved(m.getId(), interval, li, zone);
				
				

			}
			else if (to.getRouter() instanceof EpidemicJ48Router) {
				EpidemicJ48Router router = (EpidemicJ48Router) to
						.getRouter();
				int interval = (int) Math.round(SimClock.getTime()
						/ this.timeInterval);
				int li = to.getLobby();
				String zone = router.getZone(to.getLocation());
				m.intervalReception=interval;
				m.liReception=li;
				m.zoneReception=zone;
				m.recievedLocation= new Coord(to.getLocation().getX(),to.getLocation().getY());
				m.timeReception= SimClock.getTime();
			//	router.registerMessageRecieved(m.getId(), interval, li, zone);
				

			}
		}
	}

	@Override
	public void done() {
		
		int cont=0;
	
		
		for (int i = 0; i < this.nroZonesX; i++) {
			for (int j = 0; j < this.nroZonesY; j++) {
				if(cont==0)
					zonesNodesArff="{Z" + cont;
				else
					zonesNodesArff=zonesNodesArff+",Z"+cont;
				cont++;
			}
		}
		
		zonesNodesArff=zonesNodesArff+"}";
		
		for(int i=0;i<829;i++)
		{
			if(i==0)
				nodesArff="{i"+i;
			else
				nodesArff=nodesArff+",i"+i;
			
			
		}
		nodesArff=nodesArff+"}";
		
		writeGlobalClassificationFiles();
		super.done();
	}

	private void writeGlobalClassificationFiles() {
		// TODO Auto-generated method stub
		String headerForDistanceClassification = "@RELATION dtn_node_copies\n@attribute Interval NUMERIC\n@attribute lobbyIndex NUMERIC\n"
				+ "@attribute Zone "
				+ this.zonesNodesArff
				+ "\n@attribute Node "
				+ this.nodesArff
				+ "\n@attribute distance NUMERIC\n";

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
		this.hosts = hosts;
	}

}
