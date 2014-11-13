package report;



import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.TransmissionListener;

public class TransfersReport extends Report implements MessageListener,
		TransmissionListener {

	private int nrofStarted;
	private int nrofAborted;
	private int nrofRelayed;
	private int nrofCreated;
	private int nrofRejected;
	private int nrofRejectedResource;
	private int nrofRejectedByDestinyTransmiting;
	private int nrofRejectedAlreadyHaved;
	private int nrofRejectedOtherReason;

	public TransfersReport() {
		init();
	}

	@Override
	protected void init() {
		super.init();

		this.nrofStarted = 0;
		this.nrofAborted = 0;
		this.nrofRelayed = 0;
		this.nrofRejected = 0;
		this.nrofRejectedResource = 0;
		this.nrofRejectedByDestinyTransmiting = 0;
		this.nrofRejectedAlreadyHaved = 0;
		this.nrofRejectedOtherReason = 0;

	}

	@Override
	public void messageRejectedDestinyTransmiting(Message m, DTNHost from,
			DTNHost to) {
		if (isWarmupID(m.getId())) {
			return;
		}

		this.nrofRejectedByDestinyTransmiting++;
		this.nrofRejected++;
		// TODO Auto-generated method stub

	}

	@Override
	public void messageRejectedAlreadyHaved(Message m, DTNHost from, DTNHost to) {
		// TODO Auto-generated method stub
		if (isWarmupID(m.getId())) {
			return;
		}

		this.nrofRejectedAlreadyHaved++;
		this.nrofRejected++;

	}

	@Override
	public void messageRejectedByResources(Message m, DTNHost from, DTNHost to) {
		// TODO Auto-generated method stub
		if (isWarmupID(m.getId())) {
			return;
		}

		this.nrofRejectedResource++;
		this.nrofRejected++;
	}

	@Override
	public void messageRejectedOtherReasons(Message m, DTNHost from, DTNHost to) {
		// TODO Auto-generated method stub
		if (isWarmupID(m.getId())) {
			return;
		}

		this.nrofRejectedOtherReason++;
		this.nrofRejected++;
	}

	@Override
	public void newMessage(Message m) {
		// TODO Auto-generated method stub

	}

	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
		if (isWarmupID(m.getId())) {
			return;
		}

		this.nrofStarted++;
	}

	@Override
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {
		// TODO Auto-generated method stub

	}

	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
		if (isWarmupID(m.getId())) {
			return;
		}

		this.nrofAborted++;
	}

	public void messageTransferred(Message m, DTNHost from, DTNHost to,
			boolean finalTarget) {
		if (isWarmupID(m.getId())) {
			return;
		}

		this.nrofRelayed++;

	}

	@Override
	public void done() {
		write("Message stats for scenario " + getScenarioName()
				+ "\nsim_time: " + format(getSimTime()));

		String statsText = "created: " + this.nrofCreated + "\nstarted: "
				+ this.nrofStarted + "\nrelayed: " + this.nrofRelayed
				+ "\naborted: " + this.nrofAborted + "\nRejected: "
				+ this.nrofRejected + "\nRejected by Resources: "
				+ this.nrofRejectedResource
				+ "\nRejected by receptor occuped: "
				+ this.nrofRejectedByDestinyTransmiting
				+ "\nRejected by existing: " + this.nrofRejectedAlreadyHaved
				+ "\nRejected by other reasons: "
				+ this.nrofRejectedOtherReason

		;

		write(statsText);
		super.done();
	}

	
}
