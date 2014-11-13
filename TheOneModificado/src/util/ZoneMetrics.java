package util;

import java.util.ArrayList;
import java.util.List;

public class ZoneMetrics {
	private double initTime=-1;
	private double endTime=-1;
	
	private double avgInterContactTime=-1;
	private double avgDurationContactTime=-1;
	private double avgNeighbors=-1;

	
	private int MaxNeighbors=-1; //de down to up
	private int minNeighbors=-1; //de down to up
	public double getInitTime() {
		return initTime;
	}
	public void setInitTime(double initTime) {
		this.initTime = initTime;
	}
	public double getTimInZone() {
		return this.endTime-this.initTime;
	}
	public void setMetrics(double inittime, double endTime, double avgInter, double avgDuration, double avgNeigh, int maxNeigh, int minNeigh)
	{
		this.setAvgInterContactTime(avgInter);
		this.setAvgDurationContactTime(avgDuration);
		this.setAvgNeighbors(avgNeigh);
		this.MaxNeighbors=maxNeigh;
		this.initTime=inittime;
		this.endTime=endTime;
		this.minNeighbors=minNeigh;
	}
	public double getAvgInterContactTime() {
		return avgInterContactTime;
	}
	public void setAvgInterContactTime(double avgInterContactTime) {
		this.avgInterContactTime = avgInterContactTime;
	}
	public double getAvgDurationContactTime() {
		return avgDurationContactTime;
	}
	public void setAvgDurationContactTime(double avgDurationContactTime) {
		this.avgDurationContactTime = avgDurationContactTime;
	}
	public double getAvgNeighbors() {
		return avgNeighbors;
	}
	public void setAvgNeighbors(double avgNeighbors) {
		this.avgNeighbors = avgNeighbors;
	}
	

}
