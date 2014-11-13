package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class ZoneToClassify {
	private double avgDurationContact = 0;
	private double avgInterContact = 0;
	//falta dividr entre 2 a los enlaces, se hara en la impresion
	private HashSet<Integer> Nodes=new HashSet<Integer>();
	public double getAvgDurationContact() {
		return avgDurationContact;
	}
	public void setAvgDurationContact(double avgDurationContact) {
		this.avgDurationContact = avgDurationContact;
	}
	public double getAvgInterContact() {
		return avgInterContact;
	}
	public void setAvgInterContact(double avgInterContact) {
		this.avgInterContact = avgInterContact;
	}
	public void insertNode(int id)
	{
		this.getNodes().add(id);
		
	}
	public HashSet<Integer> getNodes() {
		return Nodes;
	}
	public void setNodes(HashSet<Integer> nodes) {
		Nodes = nodes;
	}
	
	
	
	

	

}
