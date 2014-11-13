package util;

import java.util.ArrayList;
import java.util.List;

public class ZoneStatReport {
	private double avgEnlaces = 0;
	//falta dividr entre 2 a los enlaces, se hara en la impresion
	private int denominator=0;
	private double avgNodes=0;
	private double avgTotalnodes=0;
	private List<Integer> Nodes=new ArrayList<Integer>();
	private List<Integer> enlaces=new ArrayList<Integer>();
	
	private int nrofStarted=0;
	private int nrofAborted=0;
	private int nrofRelayed=0;
	
	public double getAvgContatos() {
		return avgEnlaces;
	}

	public void setAvgContatos(double contatos) {
		double avgParcial = 0;
		this.enlaces.add((int)contatos);
		try {
			avgParcial = contatos / this.getDenominator();

			if (this.getDenominator() == 0)
				throw new Exception("Division by zero!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}
		this.avgEnlaces = avgEnlaces+avgParcial;
	}

	public int getDenominator() {
		return denominator;
	}

	public void setDenominator(int denominator) {
		this.denominator = denominator;
	}

	public int getNrofStarted() {
		return nrofStarted;
	}

	public void incNrofStarted() {
		this.nrofStarted++;
	}

	public double getAvgNodes() {
		return avgNodes;
	}

	public void setAvgNodes(int nodes) {
		this.Nodes.add(nodes);
		double avgParcial = 0;
		try {
			avgParcial = nodes / this.getDenominator();

			if (this.getDenominator() == 0)
				throw new Exception("Division by zero!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}
		this.avgNodes= avgNodes+avgParcial;
	}

	public int getNrofAborted() {
		return nrofAborted;
	}

	public void incNrofAborted() {
		this.nrofAborted++;
	}

	public int getNrofRelayed() {
		return nrofRelayed;
	}

	public void incNrofRelayed() {
		this.nrofRelayed++;
	}

	public double getAvgTotalnodes() {
		return avgTotalnodes;
	}

	public void incAvgTotalnodes() {
		this.avgTotalnodes++;
	}
	@Override 
	public String toString() {
		//LINKS\t NODES\t TXS\t TXA\t TXR
		String tmp= String.valueOf(this.avgEnlaces)+"\t "+String.valueOf(this.avgNodes)+"\t "+this.nrofStarted
				+"\t "+this.nrofAborted+"\t "+this.nrofRelayed;
		return tmp;
	}

	public List<Integer> getNodes() {
		return Nodes;
	}

	public void setNodes(List<Integer> nodes) {
		Nodes = nodes;
	}

	public List<Integer> getEnlaces() {
		return enlaces;
	}

	public void setEnlaces(List<Integer> enlaces) {
		this.enlaces = enlaces;
	}

}
