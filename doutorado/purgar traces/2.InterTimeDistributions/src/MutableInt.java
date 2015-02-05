package spaceTimeDistanceDistributions;

public class MutableInt {


	int counter=0;
	
	MutableInt(){
		counter=1;
	}
	public void increment()
	{
		counter++;
	}
	public int getCounter()
	{
		return counter;
	}
}
