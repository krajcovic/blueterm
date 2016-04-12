package cz.monetplus.blueterm.terminal;

import java.io.IOException;

import cz.monetplus.blueterm.MonetBTAPIError;

public interface ITerminalsThread {

    void connectionLost(MonetBTAPIError error);

    void write(byte[] buffer) throws IOException;

    void interrupt();

    void setState(TerminalState newState);
}
