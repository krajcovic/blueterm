package cz.monetplus.blueterm;

public enum TerminalPorts {
	MASTER(33333), BANK(33334), FLEET(33335), MAINTENANCE(33336);

	private int port;

	TerminalPorts(int port) {
		this.setPort(port);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public static TerminalPorts valueOf(int i) {
		for (TerminalPorts element : TerminalPorts.values()) {
			if (element.getPort() == i) {
				return element;
			}
		}

		return null;
	}
}
