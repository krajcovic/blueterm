package cz.monetplus.blueterm.worker;

import org.apache.http.util.ByteArrayBuffer;

/**
 * Identificators for Handle message thread.
 * 
 * @author "Dusan Krajcovic"
 * 
 */
public class HandleMessage {

    private HandleOperations operation;

    private ByteArrayBuffer buffer;

    public HandleMessage(HandleOperations operation) {
        super();
        this.operation = operation;
        this.buffer = new ByteArrayBuffer(0);
    }

    public HandleMessage(HandleOperations operation, ByteArrayBuffer buffer) {
        super();
        this.operation = operation;
        this.buffer = buffer;
    }

    public HandleMessage(HandleOperations operation, byte[] data) {
        super();
        this.operation = operation;
        if (data != null) {
            this.buffer = new ByteArrayBuffer(data.length);
            this.buffer.append(data, 0, data.length);
        } else {
            this.buffer = new ByteArrayBuffer(0);
        }
    }

    public HandleMessage(HandleOperations operation, byte data) {
        super();
        this.operation = operation;
        this.buffer = new ByteArrayBuffer(1);
        this.buffer.append(data);

    }

    public HandleMessage(HandleOperations operation, String string) {
        this.operation = operation;
        this.buffer = new ByteArrayBuffer(string.getBytes().length);
        this.buffer.append(string.getBytes(), 0, string.getBytes().length);
    }

    public HandleOperations getOperation() {
        return operation;
    }

    public void setOperation(HandleOperations operation) {
        this.operation = operation;
    }

    public ByteArrayBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteArrayBuffer buffer) {
        this.buffer = buffer;
    }

}
