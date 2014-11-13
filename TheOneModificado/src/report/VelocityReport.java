package report;

import java.util.ArrayList;
import java.util.Map;

import java.util.HashMap;

import java.util.LinkedList;

import java.util.Queue;

import java.util.List;

import movement.ExternalPathMovement;

import core.Coord;
import core.DTNHost;
import core.MovementListener;
import core.SimClock;

import core.UpdateListener;

import util.MutableInt;

import util.Velocity;

public class VelocityReport extends Report implements MovementListener,
		UpdateListener {

	// public GraphMetricsListener(String time,int total)

	private boolean initFlag = false;
	private HashMap<Integer, Queue<Double>> initTimes = new HashMap<Integer, Queue<Double>>();
	private HashMap<Integer, Queue<Double>> endTimes = new HashMap<Integer, Queue<Double>>();

	private HashMap<Integer, infoVelocity> infoVelocityByNode = new HashMap<Integer, infoVelocity>();
	private HashMap<Integer, List<infoVelocity>> infoNode = new HashMap<Integer, List<infoVelocity>>();
	private HashMap<Integer, MutableInt> timeCounter = new HashMap<Integer, MutableInt>();
	private HashMap<Integer, MutableInt> velocityCounter = new HashMap<Integer, MutableInt>();
	private HashMap<Double, MutableInt> temporalDependecyCounter = new HashMap<Double, MutableInt>();
	private HashMap<Double, MutableInt> spatialDependencyCounter = new HashMap<Double, MutableInt>();
	private HashMap<Integer, MutableInt> relativeVelocityCounter = new HashMap<Integer, MutableInt>();
	private HashMap<Integer, MutableInt> vmaxSpatialDependencyCounter = new HashMap<Integer, MutableInt>();
	private HashMap<Integer, MutableInt> vmaxRelativeVelocityCounter = new HashMap<Integer, MutableInt>();
	private int contTuplasSD = 0;
	private int contTuplasRS = 0;
	private List<Integer> velocities = new ArrayList<Integer>();
	private List<Integer> times = new ArrayList<Integer>();
	private List<Double> temporalDependecy = new ArrayList<Double>();
	private List<Double> spatialDependency = new ArrayList<Double>();
	private List<Integer> relativeVelocity = new ArrayList<Integer>();
	private List<Integer> vmaxSpatialDependency = new ArrayList<Integer>();
	private List<Integer> vmaxRelativeVelocity = new ArrayList<Integer>();
	private List<DTNHost> hosts = null;
	private int nroVelocities = 0;
	private int nroTimes = 0;
	private int nroDependecies = 0;

	public VelocityReport() {
		init();

	}

	protected void init() {
		super.init();

	}

	public void calculeDistributions() {
		for (Map.Entry<Integer, List<infoVelocity>> entry : this.infoNode
				.entrySet()) {
			int t = 0;
			int velocity = 0;
			List<infoVelocity> list = entry.getValue();
			double temporalD = 0;
			for (int i = 0; i < list.size(); i++) {
				t = (int) Math.round(list.get(i).getDurationTime() ) ;
				velocity = (int) Math
						.round(list.get(i).getV().getSpeed() );
				
				if (i < list.size() - 1) {
					double srtmp = getDspatial(list.get(i).getV(),
							list.get(i + 1).getV());
					temporalD = (double) Math.round(srtmp * 10) / 10;

				}
				// distri de velocidad
				MutableInt vCounter = this.velocityCounter.get(velocity);

				if (vCounter == null) {
					this.velocityCounter.put(velocity, new MutableInt());

				} else {

					vCounter.increment();

				}
				this.nroVelocities++;
				if (!this.velocities.contains(velocity))
					this.velocities.add(velocity);
				// distri tempo
				MutableInt tCounter = this.timeCounter.get(t);

				if (tCounter == null) {
					this.timeCounter.put(t, new MutableInt());

				} else {

					tCounter.increment();

				}
				if (!this.times.contains(t))
					this.times.add(t);
				this.nroTimes++;
				// distri temporaldependency
				MutableInt srCounter = this.temporalDependecyCounter
						.get(temporalD);

				if (srCounter == null) {
					this.temporalDependecyCounter.put(temporalD,
							new MutableInt());

				} else {

					srCounter.increment();

				}
				if (!this.temporalDependecy.contains(temporalD))
					this.temporalDependecy.add(temporalD);
				this.nroDependecies++;
			}

		}
	}

	private double getRelativeSpeed(Velocity v1, Velocity v2) {
		// TODO Auto-generated method stub
		double x = v2.getX() - v1.getX();
		double y = v2.getY() - v1.getY();
		double mod = Math.pow(x, 2) + Math.pow(y, 2);
		return Math.pow(mod, 0.5);
	}

	private double getDspatial(Velocity vi, Velocity vj) {
				return (RD(vi, vj) * SR(vi, vj));
	}

	private double SR(Velocity vi, Velocity vj) {
		// TODO Auto-generated method stub
		double min = Double.MAX_VALUE, max = 0;
		if (vi.getSpeed() <= vj.getSpeed())
			min = vi.getSpeed();
		else
			min = vj.getSpeed();
		if (vi.getSpeed() >= vj.getSpeed())
			max = vi.getSpeed();
		else
			max = vj.getSpeed();
		return min / max;
	}

	private double RD(Velocity vi, Velocity vj) {
		// TODO Auto-generated method stub
		double num = 0, den = 1;

		num = vi.getX() * vj.getX() + vi.getY() * vj.getY();
		den = vi.getSpeed() * vj.getSpeed();

		return num / den;
	}

	private double getEuclideandDistance(Coord a, Coord b) {
		double x = Math.pow((b.getX() - a.getX()), 2);
		double y = Math.pow((b.getY() - a.getY()), 2);
		return Math.pow((x + y), 0.5);
	}

	@Override
	public void done() {
		this.calculeDistributions();

		write("------------------velocity distribution------------------");
		String tmp1;

		for (int k = 0; k < this.velocities.size(); k++) {
			int v = velocities.get(k);

			tmp1 = String.valueOf(v);

			double p = 0D;
			if (this.velocityCounter.containsKey(v)) {
				int cases = this.velocityCounter.get(v).get();
				p = (double) (cases) / this.nroVelocities;
				// p = (double) Math.round(p * 100) / 100;
			}
			tmp1 = tmp1 + "\t" + p;

			write(tmp1);

		}
		write("------------------TEMPORAL distribution------------------");

		for (int k = 0; k < this.times.size(); k++) {
			int t = times.get(k);

			tmp1 = String.valueOf(t);

			double p = 0D;
			if (this.timeCounter.containsKey(t)) {
				int cases = this.timeCounter.get(t).get();
				p = (double) (cases) / this.nroTimes;
				// p = (double) Math.round(p * 100) / 100;
			}
			tmp1 = tmp1 + "\t" + p;

			write(tmp1);
		}
		write("------------------TEMPORAL DEPENDENCY distribution------------------");
		for (int k = 0; k < this.temporalDependecy.size(); k++) {
			double td = temporalDependecy.get(k);

			tmp1 = String.valueOf(td);

			double p = 0D;
			if (this.temporalDependecyCounter.containsKey(td)) {
				int cases = this.temporalDependecyCounter.get(td).get();
				p = (double) (cases) / this.nroDependecies;
				// p = (double) Math.round(p * 100) / 100;
			}
			tmp1 = tmp1 + "\t" + p;

			write(tmp1);
		}
		write("------------------spatial DEPENDENCY distribution------------------");
		for (int k = 0; k < this.spatialDependency.size(); k++) {
			double sd = spatialDependency.get(k);

			tmp1 = String.valueOf(sd);

			double p = 0D;
			if (this.spatialDependencyCounter.containsKey(sd)) {
				int cases = this.spatialDependencyCounter.get(sd).get();
				p = (double) (cases) / this.contTuplasSD;
				// p = (double) Math.round(p * 100) / 100;
			}
			tmp1 = tmp1 + "\t" + p;

			write(tmp1);
		}
		write("------------------relative velocity distribution------------------");
		for (int k = 0; k < this.relativeVelocity.size(); k++) {
			int rs = relativeVelocity.get(k);

			tmp1 = String.valueOf(rs);

			double p = 0D;
			if (this.relativeVelocityCounter.containsKey(rs)) {
				int cases = this.relativeVelocityCounter.get(rs).get();
				p = (double) (cases) / this.contTuplasRS;
				// p = (double) Math.round(p * 100) / 100;
			}
			tmp1 = tmp1 + "\t" + p;

			write(tmp1);
		}
		
		write("------------------vamx avg spatial DEPENDENCY ------------------");
		for (int k = 0; k < this.vmaxSpatialDependency.size(); k++) {
			int vmaxsd = vmaxSpatialDependency.get(k);

			tmp1 = String.valueOf(vmaxsd);

			double avg = 0D;
			if (this.vmaxSpatialDependencyCounter.containsKey(vmaxsd)) {
				int cases = this.vmaxSpatialDependencyCounter.get(vmaxsd).get();
				double sumaSD=this.vmaxSpatialDependencyCounter.get(vmaxsd).getCc();
				avg = sumaSD/(double) (cases);
				// p = (double) Math.round(p * 100) / 100;
			}
			tmp1 = tmp1 + "\t" + avg;

			write(tmp1);
		}
		write("------------------vamx avg relative velocity ------------------");
		for (int k = 0; k < this.vmaxRelativeVelocity.size(); k++) {
			int vmaxrs = vmaxRelativeVelocity.get(k);

			tmp1 = String.valueOf(vmaxrs);

			double avg = 0D;
			if (this.vmaxRelativeVelocityCounter.containsKey(vmaxrs)) {
				int cases = this.vmaxRelativeVelocityCounter.get(vmaxrs).get();
				double sumaRD=this.vmaxRelativeVelocityCounter.get(vmaxrs).getCc();
				avg = sumaRD/(double) (cases);
			}
			tmp1 = tmp1 + "\t" + avg;

			write(tmp1);
		}
		
		super.done();
	}

	@Override
	public void updated(List<DTNHost> hosts) { 
		// TODO Auto-generated method stub
		this.hosts = new ArrayList<DTNHost>();
		if (this.initFlag == false) {
			initActivityTimes(hosts); //inicializar tiempos de actividade para mov externo
		} else {//atualizaar lista de host activos en radio, en movimiento y con velocidad maior a cero
			for (int i = 0; i < hosts.size(); i++) {
				if (hosts.get(i).isMovementActive()
						&& hosts.get(i).isRadioActive() &&hosts.get(i).getV().getSpeed()>0 && hosts.get(i).getV().getSpeed()<=22)
					this.hosts.add(hosts.get(i));
			}

		}
	}

	private void initActivityTimes(List<DTNHost> hosts) {
		// TODO Auto-generated method stub
		for (int i = 0; i < hosts.size(); i++) {
			DTNHost h = hosts.get(i);
			if (h.getMovementModel() instanceof ExternalPathMovement) {
				this.initTimes.put(h.getAddress(),
						new LinkedList<Double>(h.getInitActivityTimes(1)));
				this.endTimes.put(h.getAddress(),
						new LinkedList<Double>(h.getEndActivityTimes(1)));
			}
		}
		this.initFlag = true;
	}

	@Override
	public void newDestination(DTNHost host, Coord destination, double speed) {
		// TODO Auto-generated method stub
		if (initFlag == false)
			return;
	//	if (host.getAddress() != 56)
		//	return;
		if (host.getMovementModel() instanceof ExternalPathMovement) {
			newDestinationExternalMovement(host, destination, speed);
		} else {
			newDestinationInternalMovement(host, destination, speed);
		}
	}

	private void newDestinationInternalMovement(DTNHost host,
			Coord destination, double speed) {
		// TODO Auto-generated method stub
		int id = host.getAddress();
		this.initTimes = null;
		this.endTimes = null;

		if (!this.infoVelocityByNode.containsKey(id)) {
			// this.infoVelocityByNode.put(id,new
			// infoVelocity(host.getLocation()));
			this.infoVelocityByNode.put(
					id,
					new infoVelocity(host.getLocation(), setVelocity(
							host.getLocation(), destination, speed)));
			calculeMetrics(this.infoVelocityByNode.get(id), id);// agregado para
																// otras
																// metricas
		} else {
			infoVelocity iv = this.infoVelocityByNode.get(id);
			if (iv.P1.getX() != host.getLocation().getX()
					&& iv.P1.getY() != host.getLocation().getY()) {
				iv.completeInformation(host.getLocation());
				if (!this.infoNode.containsKey(id)) {
					this.infoNode.put(id, new ArrayList<infoVelocity>());
					this.infoNode.get(id).add(iv);

				} else
					this.infoNode.get(id).add(iv);
				this.infoVelocityByNode.put(id, new infoVelocity(iv.P2,
						setVelocity(iv.P2, destination, speed)));
				calculeMetrics(this.infoVelocityByNode.get(id), id);// agregado
																	// para
																	// otras
																	// metricas
			}
		}

	}

	private void calculeMetrics(infoVelocity velocidade, int idHost) {
		// TODO Auto-generated method stub
		//calcular metricas sobre dependencia espacial y relative velocity com sus vmax
		double R = 250;
		int c1 = 1;
		Velocity vi = velocidade.getV();
		Coord pi = velocidade.P1;

		for (int i = 0; i < this.hosts.size(); i++) {
			DTNHost other = this.hosts.get(i);
			if (other.getAddress() == idHost)
				continue;
			else {
				Velocity vj = other.getV();
				double distance = getEuclideandDistance(pi, other.getLocation());

				// ///////////////////////////////////////
				if (distance <= (R * c1)) {
					// *************para spatial
					// dependency****************************
					double dSpatial = getDspatial(vi, other.getV());
			//		if (dSpatial != 0) {
						double maxVSD = 0;
						if (vj.getSpeed() > vi.getSpeed())
							maxVSD = vj.getSpeed();
						else
							maxVSD = vi.getSpeed();
						double SD = (double) Math.round(dSpatial * 10) / 10;
						MutableInt dsMutable = this.spatialDependencyCounter
								.get(SD);
						if (dsMutable == null) {
							this.spatialDependencyCounter.put(SD,
									new MutableInt());

						} else {

							dsMutable.increment();

						}
						if (!this.spatialDependency.contains(SD))
							this.spatialDependency.add(SD);

						this.contTuplasSD++;
						int vmaxSD = (int) Math.round(maxVSD) ;
						MutableInt vamxDSMutable = this.vmaxSpatialDependencyCounter
								.get(vmaxSD);
						if (vamxDSMutable == null) {
							this.vmaxSpatialDependencyCounter.put(vmaxSD,
									new MutableInt(dSpatial));

						} else {

							vamxDSMutable.increment(dSpatial);

						}
						if (!this.vmaxSpatialDependency.contains(vmaxSD))
							this.vmaxSpatialDependency.add(vmaxSD);

				//	}
					// ***********+ para relative velocity
					double relativeSpeed = getRelativeSpeed(vi, other.getV());
				//	if (relativeSpeed != 0) {
						double maxVRS = 0;
						if (vj.getSpeed() > vi.getSpeed())
							maxVRS = vj.getSpeed();
						else
							maxVRS = vi.getSpeed();
						int RS = (int) Math.round(relativeSpeed ) ;
						MutableInt rsMutable = this.relativeVelocityCounter
								.get(RS);
						if (rsMutable == null) {
							this.relativeVelocityCounter.put(RS,
									new MutableInt());

						} else {

							rsMutable.increment();

						}
						if (!this.relativeVelocity.contains(RS))
							this.relativeVelocity.add(RS);

						this.contTuplasRS++;
						int vmaxRS = (int) Math.round(maxVRS) ;
						MutableInt vmaxRSMutable = this.vmaxRelativeVelocityCounter
								.get(vmaxRS);
						if (vmaxRSMutable == null) {
							this.vmaxRelativeVelocityCounter.put(vmaxRS,
									new MutableInt(relativeSpeed));

						} else {

							vmaxRSMutable.increment(relativeSpeed);

						}
						if (!this.vmaxRelativeVelocity.contains(vmaxRS))
							this.vmaxRelativeVelocity.add(vmaxRS);

					}
				}
			//}
		}

	}

	private Velocity setVelocity(Coord a, Coord b, double speed) {
		// TODO Auto-generated method stub
		double x1, x2, y1, y2, x, y;
		x1 = a.getX();
		y1 = a.getY();
		x2 = b.getX();
		y2 = b.getY();
		x = x2 - x1;
		y = y2 - y1;

		return new Velocity(speed, Math.atan2(y, x));

	}

	private void newDestinationExternalMovement(DTNHost host,
			Coord destination, double speed) {
		// TODO Auto-generated method stub
		if(speed==0 || speed > 22) // se obtem maiores velocidades, mas, nao estudam'se
			return;
		
		int id = host.getAddress();
	
		if (this.initTimes.get(host.getAddress()).size() <= 0
				|| this.endTimes.get(host.getAddress()).size() <= 0)
			return;
		double time = SimClock.getIntTime();

		double initT = this.initTimes.get(host.getAddress()).peek();
		double endT = this.endTimes.get(host.getAddress()).peek();
		if (time < initT)
			return;
		if (time < endT) {
			// cadastrar;

			if (!this.infoVelocityByNode.containsKey(id)) { // si lista temporal de informaciones no continene de ese nodo
				// this.infoVelocityByNode.put(id,new
				// infoVelocity(host.getLocation()));
				this.infoVelocityByNode.put( //agregamos nodo y primer punto de localizacion
						id,
						new infoVelocity(host.getLocation(), setVelocity(
								host.getLocation(), destination, speed)));
				
				calculeMetrics(this.infoVelocityByNode.get(id), id);

			} else {
				infoVelocity iv = this.infoVelocityByNode.get(id);
				if (iv.P1.getX() != host.getLocation().getX()
						&& iv.P1.getY() != host.getLocation().getY()) {
					iv.completeInformation(host.getLocation());
					if (!this.infoNode.containsKey(id)) {
						this.infoNode.put(id, new ArrayList<infoVelocity>());
						this.infoNode.get(id).add(iv);

					} else
						this.infoNode.get(id).add(iv);
					// this.infoVelocityByNode.put(id, new infoVelocity(iv.P2));
					this.infoVelocityByNode.put(id, new infoVelocity(iv.P2,
							setVelocity(iv.P2, destination, speed)));
					if(infoVelocityByNode.get(id).getV()==null)
						System.out.print("ddd");
					calculeMetrics(this.infoVelocityByNode.get(id), id);
				}
			}

		} else {
			this.initTimes.get(host.getAddress()).remove();
			this.endTimes.get(host.getAddress()).remove();
			if (this.infoVelocityByNode.containsKey(id)) {
				//this.infoVelocityByNode.put(id,
					//	new infoVelocity(host.getLocation()));
				this.infoVelocityByNode.put(id,new infoVelocity(host.getLocation(), setVelocity(
								host.getLocation(), destination, speed)));
			}
		}

	}

	@Override
	public void initialLocation(DTNHost host, Coord location) {
		// TODO Auto-generated method stub

	}

	private class infoVelocity {
		private Coord P1 = null;
		private double iTime = 0;
		private double durationTime = 0;
		private Coord P2 = null;
		private Velocity v = null;
		

		public infoVelocity(Coord p1) {
			this.P1 = new Coord(p1.getX(), p1.getY());
			this.iTime = SimClock.getTime();

		}

		public infoVelocity(Coord p1, Velocity v) {
			this.P1 = new Coord(p1.getX(), p1.getY());
			this.iTime = SimClock.getTime();
			this.v = v;

		}

		public void completeInformation(Coord location) {
			// TODO Auto-generated method stub
			this.P2 = new Coord(location.getX(), location.getY());
			this.setDurationTime(SimClock.getTime() - this.iTime);
			// this.setV(new Velocity(
			// getDistance(P1, P2) / this.getDurationTime(), getAngle(P1,
			// P2)));

		}

		private double getDistance(Coord a, Coord b) {
			return Math.pow((Math.pow((b.getX() - a.getX()), 2) + Math.pow(
					(b.getY() - a.getY()), 2)), 0.5);
		}

		private double getAngle(Coord a, Coord b) {
			double x1, x2, y1, y2, x, y;
			x1 = a.getX();
			y1 = a.getY();
			x2 = b.getX();
			y2 = b.getY();
			x = x2 - x1;
			y = y2 - y1;

			return Math.atan2(y, x);
		}

		public double getDurationTime() {
			return durationTime;
		}

		public void setDurationTime(double durationTime) {
			this.durationTime = durationTime;
		}

		public Velocity getV() {
			return v;
		}

		public void setV(Velocity v) {
			this.v = v;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return this.P1.toString() + "->" + this.P2.toString() + " V:"
					+ this.v + " t:" + this.durationTime;
		}
	}
}
