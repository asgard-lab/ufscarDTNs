package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.org.apache.bcel.internal.generic.NEW;

import core.Coord;
import core.DTNHost;

public class Graph {
	private int[][] adjacencyMatrix;
	private int[][] pathlengthMatrix;
	private int[] lobbiesMatrix = null;
	// private int[] betweennessMatrix = null;
	private int[] stressMatrix = null;
	private Coord[] LocationMatrix = null;
	int nroNodes = 0;
	private int nroConnectedNodes = 0;
	int nroUnActiveNodes = 0;
	int nroUnConnectedNodes = 0;
	int nroLobbiesNodes = 0;
	double pathTotal = 0;
	double pathNode = 0;
	private int time = 0;
	private HashMap<Integer, Integer> nodesLobbies = new HashMap<Integer, Integer>();
	private List<Integer> loobies = new ArrayList<Integer>();
	private HashMap<Integer, MutableInt> lobbiesCounter = new HashMap<Integer, MutableInt>();

	public Graph(int elements, int t) {
		this.nroNodes = elements;
		this.adjacencyMatrix = new int[elements][elements];
		this.LocationMatrix = new Coord[elements];
		this.pathlengthMatrix = new int[elements][elements];
		for (int i = 0; i < elements; i++) {
			for (int j = 0; j < elements; j++) {
				this.adjacencyMatrix[i][j] = -1;
				this.pathlengthMatrix[i][j] = Integer.MAX_VALUE;
			}
			this.LocationMatrix[i] = null;
		}
		this.setTime(t);
	}

	public void setLobbiesMatrix(List<DTNHost> nodes) {
		if (this.adjacencyMatrix != null) {
			for (int i = 0; i < nodes.size(); i++) {
				DTNHost host = nodes.get(i);
				int id = host.getAddress();
				int lobby = getLobbyIndex(id);
				this.lobbiesMatrix[id] = lobby;
			}
		}

	}

	/*
	 * public void setBetweenessMatrix(List<DTNHost> nodes) { if
	 * (this.adjacencyMatrix != null) { for (int i = 0; i < nodes.size(); i++) {
	 * DTNHost host = nodes.get(i); int id = host.getAddress();
	 * 
	 * } } }
	 */

	public void setStressMatrix(List<DTNHost> nodes) {
		if (this.adjacencyMatrix != null) {
			for (int i = 0; i < nodes.size(); i++) {
				DTNHost host = nodes.get(i);
				int id = host.getAddress();
				int stress = (int) stressCentrality(id);
				this.lobbiesMatrix[id] = stress;
			}
		}
	}

	public void getLobbies(List<DTNHost> nodes) {
		for (DTNHost v : nodes) {
			if (v.isRadioActive() && v.getConnections().size() > 0) {

				int lobby = getLobbyIndex(v.getAddress());
				this.getNodesLobbies().put(v.getAddress(), lobby);
				MutableInt lobbyCounter = this.getLobbiesCounter().get(lobby);

				if (lobbyCounter == null) {
					this.getLobbiesCounter().put(lobby, new MutableInt());

				} else {

					lobbyCounter.increment();

				}
				if (!this.getListLobbies().contains(lobby))
					this.getListLobbies().add(lobby);

			}
		}

	}

	public void initAdjacencyMatrix(List<DTNHost> nodes) {
		// this.nroNodes = nodes.size();
		for (DTNHost v : nodes) {
			if (!v.isRadioActive())
				initVertexConnections(v, false);
			else
				initVertexConnections(v, true);
		}
		initPathsLengths();

	}

	private void initPathsLengths() {
		// TODO Auto-generated method stub
		   for(int k=0;k<this.nroNodes;k++){
			      for(int i=0;i<this.nroNodes;i++){
			        for(int j=0;j<this.nroNodes;j++){
			          this.pathlengthMatrix[i][j]=Math.min(this.pathlengthMatrix[i][j],this.pathlengthMatrix[i][k]+this.pathlengthMatrix[k][j]);
			        }
			      }
			    }
	}

	public void initVertexConnections(DTNHost v, boolean active) {
		DTNHost other = null;
		HashSet<Integer> others = new HashSet<Integer>();
		if (active == true) {
			this.LocationMatrix[v.getAddress()] = new Coord(v.getLocation()
					.getX(), v.getLocation().getY());
			if (v.getConnections().size() > 0) {
				for (int i = 0; i < v.getConnections().size(); i++) {

					other = v.getConnections().get(i).getOtherNode(v);
					others.add(other.getAddress());

				}
				this.nroConnectedNodes++;
				for (int i = 0; i < this.nroNodes; i++) {
					if (others.contains(i))
						this.adjacencyMatrix[v.getAddress()][i] = 1;
					else
						this.adjacencyMatrix[v.getAddress()][i] = 0;
				}
			} else if (v.getConnections().size() == 0) {
				for (int i = 0; i < this.nroNodes; i++) {
					this.adjacencyMatrix[v.getAddress()][i] = 0;
					this.adjacencyMatrix[i][v.getAddress()] = 0;
				}
				this.nroUnConnectedNodes++;

			}

		} else {

			this.nroUnActiveNodes++;
		}
	}

	public int getDegree(int id) {
		if (this.adjacencyMatrix[id][id] == -1)
			return 0;
		int sum = 0;
		for (int i = 0; i < this.nroNodes; i++) {
			sum = sum + this.adjacencyMatrix[id][i];

		}
		return sum;
	}

	public int getLobbyIndex(int id) {
		if (this.adjacencyMatrix[id][id] == -1)
			return 0;

		Map<Integer, Integer> lobbies = new HashMap<Integer, Integer>();
		for (int i = 0; i < this.nroNodes; i++) {
			int sum = 0;
			if (this.adjacencyMatrix[id][i] == 1) {
				for (int j = 0; j < this.nroNodes; j++) {
					sum = sum + this.adjacencyMatrix[i][j];
				}
				for (int k = 1; k <= sum; k++) {
					if (lobbies.containsKey(k)) {
						int tmp = lobbies.get(k);
						lobbies.put(k, tmp + 1);
					} else {
						lobbies.put(k, 1);

					}
				}

			}

		}
		int max = 0;
		for (Entry<Integer, Integer> e : lobbies.entrySet()) {
			if (e.getValue() >= e.getKey()) {
				if (e.getKey() > max) {
					max = e.getKey();

				}
			}
		}
		return max;
	}

	public void print() {
		for (int i = 0; i < this.nroNodes; i++) {
			for (int j = 0; j < this.nroNodes; j++) {
				System.out.print(this.adjacencyMatrix[i][j] + "\t");
			}
			System.out.print("\n");
		}
	}

	public void print(int[][] p) {
		for (int i = 0; i < this.nroNodes; i++) {
			for (int j = 0; j < this.nroNodes; j++) {
				System.out.print(p[i][j] + "\t");
			}
			System.out.print("\n");
		}
	}

	public int distance(int source, int destiny) {
		int[][] d = new int[this.nroNodes][this.nroNodes];
		for (int i = 0; i < this.nroNodes; i++)
			for (int j = 0; j < this.nroNodes; j++)
				d[i][j] = 0;

		List<NodeAndFather> tmpNodes = new ArrayList<NodeAndFather>();
		int antFather = source;
		tmpNodes.add(new NodeAndFather(source, source));
		boolean flagFinded = false;
		while (tmpNodes.size() > 0 && flagFinded == false) {
			int node = tmpNodes.get(0).value;
			antFather = tmpNodes.get(0).f;
			for (int i = 0; i < this.nroNodes; i++) {

				if (this.adjacencyMatrix[node][i] == 1) {
					if ((i == antFather))
						continue;
					if (d[i][antFather] > 0)
						continue;
					// d[node][i] = d[antFather][node] + 1;
					d[i][node] = d[node][antFather] + 1;
					if (i != destiny)
						tmpNodes.add(new NodeAndFather(node, i));
					else
						flagFinded = true;
				}
			}

			tmpNodes.remove(tmpNodes
					.indexOf(new NodeAndFather(antFather, node)));

		}
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < this.nroNodes; i++) {
			if (d[destiny][i] > 0 && d[destiny][i] < min)
				min = d[destiny][i];
		}
		if (min == Integer.MAX_VALUE)
			min = 0;
		return min;

	}

	public int[][] shortestPath(int source, int destiny) {
		int[][] d = new int[this.nroNodes][this.nroNodes];
		for (int i = 0; i < this.nroNodes; i++)
			for (int j = 0; j < this.nroNodes; j++)
				d[i][j] = 0;

		List<NodeAndFather> tmpNodes = new ArrayList<NodeAndFather>();
		int antFather = source;
		tmpNodes.add(new NodeAndFather(source, source));

		while (tmpNodes.size() > 0 /* && flagFinded == false */) {
			int node = tmpNodes.get(0).value;
			antFather = tmpNodes.get(0).f;
			for (int i = 0; i < this.nroNodes; i++) {

				if (this.adjacencyMatrix[node][i] == 1) {
					if ((i == antFather))
						continue;
					if (d[i][antFather] > 0)
						continue;
					// d[node][i] = d[antFather][node] + 1;
					if (d[i][node] > 0) {
						break;
					}
					d[i][node] = d[node][antFather] + 1;
					if (i != destiny)
						tmpNodes.add(new NodeAndFather(node, i));
					else {

						break;
					}
				}
			}

			tmpNodes.remove(tmpNodes
					.indexOf(new NodeAndFather(antFather, node)));

		}
		/*
		 * if (flagFinded == false) return null; else return d;
		 */
		// print(d);
		return d;

	}

	public double betweennessCentrality(int node) {

		this.pathNode = 0;
		this.pathTotal = 0;

		int[][] d = new int[this.nroNodes][this.nroNodes];
		double sum = 0;
		for (int i = 0; i < this.nroNodes; i++) {
			if (i == node)
				continue;
			for (int k = i + 1; k < this.nroNodes; k++) {

				if (k == node)
					continue;
				d = shortestPath(i, k);
				if (d != null) {
					int min = minPath(d, k);
					sum = sum + pathCount(d, k, i, min, node);
				}
			}
		}
		double n = (this.nroNodes - 1) * (this.nroNodes - 2) / 2;
		return sum / n;
	}

	public double stressCentrality(int node) {

		this.pathNode = 0;
		this.pathTotal = 0;

		int[][] d = new int[this.nroNodes][this.nroNodes];
		double sum = 0;
		for (int i = 0; i < this.nroNodes; i++) {
			if (i == node)
				continue;
			for (int k = i + 1; k < this.nroNodes; k++) {

				if (k == node)
					continue;
				d = shortestPath(i, k);
				if (d != null) {
					int min = minPath(d, k);
					sum = sum + pathCount(d, k, i, min, node);
				}
			}
		}
		// double n = (this.nroNodes - 1) * (this.nroNodes - 2) / 2;
		return sum;
	}

	private int minPath(int[][] d, int target) {

		int min = Integer.MAX_VALUE;
		for (int i = 0; i < this.nroNodes; i++) {
			if (d[target][i] > 0 && d[target][i] < min)
				min = d[target][i];

		}
		return min;
	}

	private double pathCount(int[][] d, int target, int source, int min,
			int node) {

		int pathsNumber = 0;
		int inPathNumNode = 0, outPathNumNode = 0, pathNumNode = 0;
		List<Integer> l = new ArrayList<Integer>();
		List<Integer> lf = new ArrayList<Integer>();
		int maxCont = 0, cont = 0;
		int index = target;
		l.add(target);
		while (min > 0) {
			cont = 0;
			while (l.size() > 0) {

				index = l.get(0);
				for (int i = 0; i < this.nroNodes; i++) {

					if (d[index][i] == min) {
						cont++;
						if (i == (node)) {
							outPathNumNode++;
						}
						if (!lf.contains(i))
							lf.add(i);
					}

				}
				l.remove(l.indexOf(index));
				if (index == node)
					inPathNumNode = lf.size();
			}

			l.addAll(lf);
			lf.clear();
			if (cont > maxCont)
				maxCont = cont;
			min = min - 1;
		}
		pathsNumber = maxCont;

		if (outPathNumNode >= inPathNumNode)
			pathNumNode = outPathNumNode;
		else
			pathNumNode = inPathNumNode;
		this.pathTotal = this.pathTotal + pathsNumber;
		// return pathNumNode;
		return ((double) pathNumNode) / ((double) pathsNumber);
	}

	public double closenessCnetrality(int node) {
		double sum = 0;
		int cont = 0;
		for (int i = 0; i < this.nroNodes; i++) {
			if (i != (node)) {

				sum = sum + distance(node, i);
				cont++;

			}
		}
		return cont / ((double) (sum));

	}

	private int indexMinDistance(int d[]) {
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < d.length; i++) {
			if (d[i] < min) {
				min = d[i];
			}
		}
		return min;
	}

	public int getNroConnectedNodes() {
		return nroConnectedNodes;
	}

	public void setNroConnectedNodes(int nroConnectedNodes) {
		this.nroConnectedNodes = nroConnectedNodes;
	}

	public HashMap<Integer, Integer> getNodesLobbies() {
		return nodesLobbies;
	}

	public void setNodesLobbies(HashMap<Integer, Integer> nodesLobbies) {
		this.nodesLobbies = nodesLobbies;
	}

	public HashMap<Integer, MutableInt> getLobbiesCounter() {
		return lobbiesCounter;
	}

	public void setLobbiesCounter(HashMap<Integer, MutableInt> lobbiesCounter) {
		this.lobbiesCounter = lobbiesCounter;
	}

	public String printToCyGraph() {
		String tmp = "";
		for (int i = 0; i < this.nroNodes; i++) {
			int suma = 0;
			HashSet<Integer> s = new HashSet<Integer>();
			for (int j = 0; j < this.nroNodes; j++) {
				suma = suma + this.adjacencyMatrix[i][j];
				if (this.adjacencyMatrix[i][j] == 1) {
					s.add(j);
				}

			}
			if (suma == 0 && s.size() == 0) {
				tmp = tmp + i + "\n";
			} else if (suma > 0) {
				Iterator<Integer> it = s.iterator();

				while (it.hasNext()) {
					int id = (int) it.next();
					tmp = tmp + i + "\t-\t" + id + "\n";

				}

			}

		}
		return tmp;

	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public List<Integer> getListLobbies() {
		return loobies;
	}

	public void setListLobbies(List<Integer> loobies) {
		this.loobies = loobies;
	}

	private class NodeAndFather {
		public int f;
		public int value;

		public NodeAndFather(int f, int v) {
			this.f = f;
			this.value = v;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof NodeAndFather) {
				// id comparison
				NodeAndFather mo = (NodeAndFather) o;
				if (mo.f == this.f || mo.value == this.value)
					return true;
				else
					return false;
			}
			return false;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "p:" + this.f + ", n:" + this.value;
		}

	}

	public void setCentralityMatrics(List<DTNHost> hosts) {
		// TODO Auto-generated method stub
		initCentralityMatrix();
		this.setLobbiesMatrix(hosts);
		// this.setBetweenessMatrix(hosts);
	//	this.setStressMatrix(hosts);
	}

	private void initCentralityMatrix() {
		// TODO Auto-generated method stub
		// this.betweennessMatrix = new int[this.nroNodes];
		this.lobbiesMatrix = new int[this.nroNodes];
		//this.stressMatrix = new int[this.nroNodes];

		for (int i = 0; i < this.nroNodes; i++) {
			//this.stressMatrix[i] = -1;
			// this.betweennessMatrix[i] = -1;
			this.lobbiesMatrix[i] = -1;

		}

	}

	public String printLobbiesLocation() {
		// TODO Auto-generated method stub
		String tmp = "";
		for (int i = 0; i < this.nroNodes; i++) {
			if(this.LocationMatrix[i]!=null)
			{
			Coord c = this.LocationMatrix[i];
			double x = (double) Math.round(c.getX() * 10) / 10;
			double y = (double) Math.round(c.getY() * 10) / 10;
			int lobby = this.lobbiesMatrix[i];
			tmp = tmp + x + "\t" + y + "\t" + lobby + "\n";
			}

		}
		return tmp;
	}

	public String printStressLocation() {
		// TODO Auto-generated method stub
		String tmp = "";
		for (int i = 0; i < this.nroNodes; i++) {
			if(this.LocationMatrix[i]==null)
				continue;
			
			Coord c = this.LocationMatrix[i];
			double x = (double) Math.round(c.getX() * 10) / 10;
			double y = (double) Math.round(c.getY() * 10) / 10;
			int stress = this.stressMatrix[i];
			tmp = tmp + x + "\t" + y + "\t" + stress + "\n";

		}
		return tmp;
	}

}
