package core;

public interface TransmissionListener {
	

		
		
		/**
		 * Method is called when a message's transfer is started
		 * @param m The message that is going to be transferred
		 * @param from Node where the message is transferred from 
		 * @param to Node where the message is transferred to
		 */
	//	public void messageTransferStarted(Message m, DTNHost from, DTNHost to);
		
		public void messageRejectedDestinyTransmiting(Message m, DTNHost from, DTNHost to);
		public void messageRejectedAlreadyHaved(Message m, DTNHost from, DTNHost to);
		
		public void messageRejectedByResources(Message m, DTNHost from, DTNHost to);
		public void messageRejectedOtherReasons(Message m, DTNHost from, DTNHost to);
		
		
		/**
		 * Method is called when a message's transfer was aborted before 
		 * it finished
		 * @param m The message that was being transferred
		 * @param from Node where the message was being transferred from 
		 * @param to Node where the message was being transferred to
		 */
	//	public void messageTransferAborted(Message m, DTNHost from, DTNHost to);
		
		/**
		 * Method is called when a message is successfully transferred from
		 * a node to another.
		 * @param m The message that was transferred
		 * @param from Node where the message was transferred from
		 * @param to Node where the message was transferred to
		 * @param firstDelivery Was the target node final destination of the message
		 * and received this message for the first time.
		 */
//		public void messageTransferred(Message m, DTNHost from, DTNHost to,
	//			boolean firstDelivery);
	}



