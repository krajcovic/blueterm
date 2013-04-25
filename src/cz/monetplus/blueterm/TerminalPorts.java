package cz.monetplus.blueterm;

public enum TerminalPorts {
	UNDEFINED(-1), SERVER(0), MASTER(33333), BANK(33334), FLEET(33335), MAINTENANCE(
			33336);

	private int portNumber;

	TerminalPorts(int port) {
		this.setPort(port);
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPort(int port) {
		this.portNumber = port;
	}

	public static TerminalPorts valueOf(int i) {
		for (TerminalPorts element : TerminalPorts.values()) {
			if (element.getPortNumber() == i) {
				return element;
			}
		}

		return UNDEFINED;
	}
}
