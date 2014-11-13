package util;

import java.util.HashMap;


public class Snapshot {
	private int nodes;
	private int connectedNodes;
	private int enlaces;
	
	private HashMap<Integer, MutableInt> degressCounter;
	private HashMap<Double, MutableInt> ccCounter;
	private double avgSpeed;
	private double desvioSpeed;
	private HashMap<Double, MutableInt> speedCounter;
	private double time;
	private double avgDegree;
	private double desvioPadrao;
	private double avgCc;
	private double desvioPadraoCc;
	private int nroNodesInMovement=0;
	public Snapshot()
	{
		setTime(0D);
		setNodes(0);
		setEnlaces(0);
		setDegressCounter(new HashMap<Integer, MutableInt>());
		setCcCounter(new HashMap<Double, MutableInt>());
		setAvgDegree(0.0);
		desvioPadrao=0.0D;
		setNoConnectedNodes(0);

	}
	public Snapshot(int n,int nConnected, int e,double t)
	{
		setTime(t);
		setNodes(n);
		this.setNoConnectedNodes(nConnected);
		setEnlaces(e);
		setDegressCounter(new HashMap<Integer, MutableInt>());
		setCcCounter(new HashMap<Double, MutableInt>());
		this.speedCounter=new HashMap<Double, MutableInt>();

	}
	public HashMap<Integer, MutableInt> getDegressCounter() {
		return degressCounter;
	}
	public void setDegressCounter(HashMap<Integer, MutableInt> degressCounter) {
		this.degressCounter = degressCounter;
	}
	public HashMap<Double, MutableInt> getCcCounter() {
		return ccCounter;
	}
	public void setCcCounter(HashMap<Double, MutableInt> ccCounter) {
		this.ccCounter = ccCounter;
	}
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public int getEnlaces() {
		return enlaces;
	}
	public void setEnlaces(int enlaces) {
		this.enlaces = enlaces;
	}
	public int getNodes() {
		return nodes;
	}
	public void setNodes(int nodes) {
		this.nodes = nodes;
	}
	public double getAvgDegree() {
		return avgDegree;
	}
	public void setAvgDegree(double avgDegree) {
		this.avgDegree = avgDegree;
	}
	public double getDesvioPadrao() {
		return desvioPadrao;
	}
	public void setDesvioPadrao(double desvioPadrao) {
		this.desvioPadrao = desvioPadrao;
	}
	public double getAvgCc() {
		return avgCc;
	}
	public void setAvgCc(double avgCc) {
		this.avgCc = avgCc;
	}
	public double getDesvioPadraoCc() {
		return desvioPadraoCc;
	}
	public void setDesvioPadraoCc(double desvioPadraoCc) {
		this.desvioPadraoCc = desvioPadraoCc;
	}
	public int getNoConnectedNodes() {
		return connectedNodes;
	}
	public void setNoConnectedNodes(int connectedNodes) {
		this.connectedNodes = connectedNodes;
	}
	public double getAvgSpeed() {
		return avgSpeed;
	}
	public void setAvgSpeed(double avgSpeed) {
		this.avgSpeed = avgSpeed;
	}
	public double getDesvioSpeed() {
		return desvioSpeed;
	}
	public void setDesvioSpeed(double desvioSpeed) {
		this.desvioSpeed = desvioSpeed;
	}
	public HashMap<Double, MutableInt> getSpeedCounter() {
		return speedCounter;
	}
	public void setSpeedCounter(HashMap<Double, MutableInt> speedCounter) {
		this.speedCounter = speedCounter;
	}
	public int getNroNodesInMovement() {
		return nroNodesInMovement;
	}
	public void setNroNodesInMovement(int nroNodesInMovement) {
		this.nroNodesInMovement = nroNodesInMovement;
	}

}
