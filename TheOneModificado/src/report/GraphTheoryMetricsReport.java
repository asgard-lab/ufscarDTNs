package report;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

import core.DTNHost;
import core.UpdateListener;

import util.Graph;
import util.MutableInt;


public class GraphTheoryMetricsReport extends Report implements UpdateListener {

	// public GraphMetricsListener(String time,int total)
	private double metricInterval;
	private List<DTNHost> activeHost = null;
	private double incMeasureInterval = 100D;
	private List<Graph> snaps;
	
	
	public GraphTheoryMetricsReport() {
		init();

	}

	protected void init() {
		super.init();
		this.metricInterval = incMeasureInterval;
		// this.degressCounter = new HashMap<Integer, MutableInt>();
		// this.ccCounter = new HashMap<String, MutableInt>();
		this.snaps = new ArrayList<Graph>();

	}

	private void takeSampleHosts(int time, List<DTNHost> hosts) {

		
		Graph g = new Graph(hosts.size(), time); // enviamos nro nós total,lista de
												// nos ativos, nro ativos, tempo
		g.initAdjacencyMatrix(hosts);
		g.getLobbies(this.activeHost);
		this.snaps.add(g);
	}

	@Override
	public void done() {

		String tmp = "";
		for (int i = 0; i < this.snaps.size(); i++) {
			Graph g = this.snaps.get(i);
			tmp = String.valueOf(g.getTime());
			write(tmp);
			write("\n---------------------matrix of connectivity");
			write(g.printToCyGraph());
			write("\n------------------Probability distribution lobbies------------------");

			int nroConnectedNodes = g.getNroConnectedNodes();
			HashMap<Integer, MutableInt> lobbyCounter = g.getLobbiesCounter();
			List<Integer> lobbies = g.getListLobbies();
			double p = 0D;
			String tmp1 = "";
			for (int k = 0; k < lobbies.size(); k++) {
				int d = lobbies.get(k);
				if (lobbyCounter.containsKey(d)) {
					int cases = lobbyCounter.get(d).get();
					p = (double) (cases) / nroConnectedNodes;
					// p = (double) Math.round(p * 100) / 100;
				}
				tmp1 = d + "\t" + p;
				write(tmp1);

			}

			
		}

		super.done();
	}

	@Override
	public void updated(List<DTNHost> hosts) {
		// TODO Auto-generated method stub
		double actualTime = this.getSimTime();
		if (actualTime >= this.metricInterval) {
			updateActiveNodesList(hosts);
			int n = (int) Math.round(actualTime);
			takeSampleHosts(n, hosts);
			this.metricInterval = this.metricInterval + this.incMeasureInterval;

		}
	}

	private void updateActiveNodesList(List<DTNHost> hosts) {
		// TODO Auto-generated method stub
		this.activeHost = new ArrayList<DTNHost>();
		for (int i = 0; i < hosts.size(); i++) {
			if (hosts.get(i).isRadioActive()) {
				this.activeHost.add(hosts.get(i));
				
			}
		}

	}
}
