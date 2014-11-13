package util;

import core.Message;

public class HostMessage {
	private String node;
	private Message m;

	public HostMessage(String h, Message m) {
		this.node = h;
		this.setM(m);

	}

	@Override
	public boolean equals(Object other) {
		HostMessage x = (HostMessage) other;
		if (x.node.equals(this.node))
			return true;
		else
			return false;

	}

	@Override
	public String toString() {
		return node;
	}

	

	public Message getM() {
		return m;
	}

	public void setM(Message m) {
		this.m = m;
	}
}
