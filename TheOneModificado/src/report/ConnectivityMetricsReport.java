package report;

import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;

import core.ConnectionListener;

import core.DTNHost;
import core.SimClock;

import util.ConnectionMetric;
import util.MutableInt;

public class ConnectivityMetricsReport extends Report implements
		ConnectionListener {

	// public GraphMetricsListener(String time,int total)

	private List<ConnectionMetric> cons;
	private HashMap<Integer, MutableInt> connectivityChangesCounter = new HashMap<Integer, MutableInt>();
	private HashMap<Integer, MutableInt> linkDurationCounter = new HashMap<Integer, MutableInt>();

	private List<Integer> connectivityChanges = new ArrayList<Integer>();
	private List<Integer> linksDuration = new ArrayList<Integer>();

	public ConnectivityMetricsReport() {
		init();

	}

	protected void init() {
		super.init();
		this.cons = new ArrayList<ConnectionMetric>();

	}

	@Override
	public void done() {

		// write("-- LC distribution ---");
		int nropares = this.cons.size();
		int nropareConDuration = 0;
		for (int k = 0; k < nropares; k++) {
			int nroChanges = this.cons.get(k).getChanges();
			double sumaDuration = this.cons.get(k).getSumDuration();
			MutableInt changesCounter = this.connectivityChangesCounter
					.get(nroChanges);

			if (changesCounter == null) {
				this.connectivityChangesCounter.put(nroChanges,
						new MutableInt());

			} else {

				changesCounter.increment();

			}
			if (!this.connectivityChanges.contains(nroChanges))
				this.connectivityChanges.add(nroChanges);
			if (sumaDuration > 0) {
				double value = sumaDuration / ((double) nroChanges);
				int intValue = (int) Math.round(value);

				MutableInt durationCounter = this.linkDurationCounter
						.get(intValue);

				if (durationCounter == null) {
					this.linkDurationCounter.put(intValue, new MutableInt());

				} else {

					durationCounter.increment();

				}
				if (!this.linksDuration.contains(intValue))
					this.linksDuration.add(intValue);
				nropareConDuration++;
			}

		}
		// visualizar
		write("------------------LC-----------------");
		String tmp1;

		for (int k = 0; k < this.connectivityChanges.size(); k++) {
			int v = connectivityChanges.get(k);

			tmp1 = String.valueOf(v);

			double p = 0D;
			if (this.connectivityChangesCounter.containsKey(v)) {
				int cases = this.connectivityChangesCounter.get(v).get();
				p = (double) (cases) / nropares;
				// p = (double) Math.round(p * 100) / 100;
			}
			tmp1 = tmp1 + "\t" + p;

			write(tmp1);

		}
		tmp1 = "";
		write("------------------links duration----------------");
		for (int k = 0; k < this.linksDuration.size(); k++) {
			int v = linksDuration.get(k);

			tmp1 = String.valueOf(v);

			double p = 0D;
			if (this.linkDurationCounter.containsKey(v)) {
				int cases = this.linkDurationCounter.get(v).get();
				p = (double) (cases) / nropareConDuration;
				// p = (double) Math.round(p * 100) / 100;
			}
			tmp1 = tmp1 + "\t" + p;

			write(tmp1);

		}
		tmp1 = "";

		super.done();
	}

	@Override
	public void hostsConnected(DTNHost host1, DTNHost host2) {
		// TODO Auto-generated method stub

		if (!host1.isRadioActive() || !host2.isRadioActive())
			return;
		double t = (double) Math.round(SimClock.getTime() * 10) / 10;
		ConnectionMetric c = new ConnectionMetric(host1.getAddress(),
				host2.getAddress());

		if (!this.cons.contains(c)) {
			c.connect(t);
			this.cons.add(c);

		} else {
			if (this.cons.get(this.cons.indexOf(c)).isState() == false)
				this.cons.get(this.cons.indexOf(c)).connect(t);
		}
	}

	@Override
	public void hostsDisconnected(DTNHost host1, DTNHost host2) {
		// TODO Auto-generated method stub

		ConnectionMetric c = new ConnectionMetric(host1.getAddress(),
				host2.getAddress());
		double t = (double) Math.round(SimClock.getTime() * 10) / 10;
		if (this.cons.contains(c)) {
			if (this.cons.get(this.cons.indexOf(c)).isState() == true)
				this.cons.get(this.cons.indexOf(c)).disConnect(t);
		} else
			System.out.println("ERROR");
	}

}
