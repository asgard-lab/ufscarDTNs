package util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.DTNHost;
import core.SimClock;


import routing.maxprop.MeetingProbabilitySet;

public class TimingMeetingProbabilitySet extends MeetingProbabilitySet {
	private Map<Integer, TimingConnetion> timings;
	
	public TimingMeetingProbabilitySet(int probSetMaxSize, double alpha) {
		// TODO Auto-generated constructor stub
		super(probSetMaxSize, alpha);
		this.setTimings(new HashMap<Integer, TimingConnetion>());
		 if (maxSetSize == INFINITE_SET_SIZE || maxSetSize < 1) {
	        	this.setTimings(new HashMap<Integer,TimingConnetion>());
	        	this.maxSetSize = INFINITE_SET_SIZE;
	        } else {
	        	this.setTimings(new HashMap<Integer, TimingConnetion>(maxSetSize));
	            this.maxSetSize = maxSetSize;
	        }
	}

	public TimingMeetingProbabilitySet() {
		// TODO Auto-generated constructor stub
		super();
	}

	public TimingMeetingProbabilitySet(double alpha,
			List<Integer> initiallyKnownNodes) {
		super(alpha,initiallyKnownNodes);

	}
    @Override
	public void updateMeetingProbFor(Integer index) {
        Map.Entry<Integer, Double> smallestEntry = null;
        double smallestValue = Double.MAX_VALUE;

		this.lastUpdateTime = SimClock.getTime();
		
		if (probs.size() == 0) { // first entry
			probs.put(index, 1.0);
			this.getTimings().put(index, new TimingConnetion());
			
			return;
		}
		
		double newValue = getProbFor(index) + alpha;
		if(this.getTimings().containsKey(index))
		{
			TimingConnetion tcon= this.getTimings().get(index);
			double duration=tcon.durationTime;
			
			double lastcontact=tcon.endTime;
			double inicio=tcon.startTime;
		//	this.getTimings().put(index, new TimingConnetion(inicio,duration,lastcontact));
		}
		else 
			this.getTimings().put(index, new TimingConnetion());
		probs.put(index, newValue);
		

		/* now the sum of all entries is 1+alpha;
		 * normalize to one by dividing all the entries by 1+alpha */ 
		for (Map.Entry<Integer, Double> entry : probs.entrySet()) {
			entry.setValue(entry.getValue() / (1+alpha));
            if (entry.getValue() < smallestValue) {
                smallestEntry = entry;
                smallestValue = entry.getValue();
            }

		}

        if (probs.size() >= maxSetSize) {
            core.Debug.p("Probsize: " + probs.size() + " dropping " + 
                    probs.remove(smallestEntry.getKey()));
        }
	}

	public void updateTiming(int address) {
		// TODO Auto-generated method stub
		if(this.timings.containsKey(address)){
			this.getTimings().get(address).connectionEnd();
			this.getTimings().get(address).setProbSuccessSends();
			
			
		}
				
		
	}
	public Map<Integer, TimingConnetion> getTimings() {
		return timings;
	}

	public void setTimings(Map<Integer, TimingConnetion> timings) {
		this.timings = timings;
	}
	protected class TimingConnetion {
		private double startTime;
		private double endTime;
		private double lastContactTime=-1D;
	//	private int otherHost=-1;
		private double interContactTime=-1D;
		private double durationTime=-1D;
		private int success=0;
		private int attempts=0;
		private double probSuccessSends=0D;
		private boolean firsConnection;
		
		public TimingConnetion( ){
		//	this.otherHost=id;
			this.firsConnection=true;
			this.startTime = SimClock.getTime();
			this.endTime = -1;
			interContactTime=-1;
		}
		public TimingConnetion (double initbefore, double durationtime, double lastcontactime, double pro ){
			//	this.otherHost=id;
				this.startTime = SimClock.getTime();
				this.endTime = -1;
				interContactTime=startTime-initbefore;
				this.durationTime=durationtime;
				this.lastContactTime=lastcontactime;
				this.firsConnection=false;
			
			}
		
		
		/**
		 * Should be called when the connection ended to record the time.
		 * Otherwise {@link #getConnectionTime()} will use end time as
		 * the time of the request.
		 */
		public void connectionEnd() {
			this.endTime = SimClock.getTime();
			this.durationTime=this.startTime-endTime;
		}
		
		/**
		 * Returns the time that passed between creation of this info 
		 * and call to {@link #connectionEnd()}. Unless connectionEnd() is 
		 * called, the difference between start time and current sim time
		 * is returned.
		 * @return The amount of simulated seconds passed between creation of
		 * this info and calling connectionEnd()
		 */
		public double getConnectionTime() {
			if (this.endTime == -1) {
				return SimClock.getTime() - this.startTime;
			}			
			else {
				return this.endTime - this.startTime;
			}
		}
		public void setProbSuccessSends()
		{
			this.probSuccessSends=this.success/this.attempts;
		}
		
	
		
		
	}
}
