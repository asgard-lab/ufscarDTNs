package util;

public class MutableInt implements Comparable {
	int value = 1; // note that we start at 1 since we're counting
	private double cc = 0D;

	public void increment() {
		++value;
	}
	public void increment(double vcc) {
		value++;
		cc = cc + vcc;
	}
	public void increment(int casos, double suma) {
		value = value + casos;
		cc = cc + suma;
	}

	public int get() {
		return value;
	}

	public MutableInt() {
		cc = 0D;
		value = 1;

	}

	public MutableInt(double c) {
		cc = c;
		value = 1;
	}

	public MutableInt(int cases, double c) {
		cc = c;
		value = cases;
	}

	@Override
	public int compareTo(Object o1) {
		if (this.value == ((MutableInt) o1).value)
			return 0;
		else if ((this.value) > ((MutableInt) o1).value)
			return 1;
		else
			return -1;
	}

	public double getCc() {
		return cc;
	}

	public void setCc(double cc) {
		this.cc = cc;
	}

	public void setAcumulative(int cases, double cc) {
		this.cc = cc;
		this.value = cases;
	}
}