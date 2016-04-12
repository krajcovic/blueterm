package cz.monetplus.blueterm.worker;

import java.nio.ByteBuffer;

import cz.monetplus.blueterm.requests.Requests;

/**
 * Identificators for Handle message thread.
 * 
 * @author "Dusan Krajcovic"
 * 
 */
public class HandleMessage {

    /**
     * Handle operation.
     */
    private HandleOperations operation;
    
    /**
     * Who call it.
     */
    private Requests request;
    
    /**
     * Buffer with data.
     */
    private ByteBuffer buffer;

    /**
     * @param operation
     */
    public HandleMessage(HandleOperations operation) {
        super();
        this.operation = operation;
        this.buffer = ByteBuffer.allocate(0);
    }

    public HandleMessage(HandleOperations operation, ByteBuffer buffer) {
        super();
        this.operation = operation;
        this.buffer = buffer;
    }

    public HandleMessage(HandleOperations operation, byte[] data) {
        super();
        this.operation = operation;
        if (data != null) {
            this.buffer = ByteBuffer.allocate(data.length);
            this.buffer.put(data, 0, data.length);
        } else {
            this.buffer = ByteBuffer.allocate(0);// new ByteArrayBuffer(0);
        }
    }

    public HandleMessage(HandleOperations operation, byte data) {
        super();
        this.operation = operation;
        this.buffer = ByteBuffer.allocate(1);//new ByteArrayBuffer(1);
        this.buffer.put(data);

    }

    public HandleMessage(HandleOperations operation, String string) {
        this.operation = operation;
        this.buffer = ByteBuffer.allocate(string.getBytes().length);//new ByteArrayBuffer(string.getBytes().length);
        this.buffer.put(string.getBytes(), 0, string.getBytes().length);
    }

    public HandleOperations getOperation() {
        return operation;
    }

    public void setOperation(HandleOperations operation) {
        this.operation = operation;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public Requests getRequest() {
        return request;
    }

    public void setRequest(Requests request) {
        this.request = request;
    }
}
