package util;

public class ConnectionMetric {
	private int i;
	private int j;
	private double sumDuration=0;
	private double tduration = 0;
	private double tInicio = 0;
	private int changes = 0;
	private boolean state=false;
	

	public ConnectionMetric(int i, int j) {
		if (i < j) {
			this.i = i;
			this.j = j;
		} else {
			this.i = j;
			this.j = i;
		}
		
		this.tduration = 0;
		this.setChanges(0);
	}
	public void connect(double t)
	{
		this.tduration = 0;
		this.tInicio=t;
		setState(true);
		incChanges();
	}
	public void disConnect(double t)
	{
		this.tduration=t-tInicio;
		this.setSumDuration(this.getSumDuration()+tduration);
		setState(false);
	}
public void incChanges()
{
	this.setChanges(this.getChanges() + 1);
}
	@Override
	public String toString() {
		return i + "-" + j+ "LCs:"+this.changes;
	}

	@Override
	public int hashCode() {
		String s=this.i+"-"+this.j;
		return s.hashCode();
		
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConnectionMetric) {
			ConnectionMetric other = (ConnectionMetric) obj;
			int tmpi = 0, tmpj = 0;
			if (other.i < other.j) {
				tmpi = other.i;
				tmpj = other.j;
			} else {

				tmpj = other.i;
				tmpi = other.j;

			}

			if ((i == tmpi && j == tmpj))
				return true;
		}
		// TODO Auto-generated method stub
		return false;
	}
	public int getChanges() {
		return changes;
	}
	public void setChanges(int changes) {
		this.changes = changes;
	}
	public double getSumDuration() {
		return sumDuration;
	}
	public void setSumDuration(double sumDuration) {
		this.sumDuration = sumDuration;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
}