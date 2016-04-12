/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.monetplus.blueterm.terminal;

import java.io.IOException;

import android.app.Activity;
import android.util.Log;
import cz.monetplus.blueterm.HandleMessages;
import cz.monetplus.blueterm.MessageThread;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for incoming
 * connections, a thread for connecting with a device, and a thread for
 * performing data transmissions when connected.
 */
public class TerminalServiceBT {
    // Debugging
    private static final String TAG = "TerminalService";

    // Name for the SDP record when creating server socket
    // private static final String NAME_SECURE = "BluetoothChatSecure";
    // private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // private static final UUID MY_UUID_SECURE = UUID
    // .fromString("00001101-0000-1000-8000-00805F9B34FB");
    // private static final UUID MY_UUID_INSECURE = UUID
    // .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final Activity activity;

    private MessageThread messageThread;

    private ConnectThread mConnectThread;

    private ConnectedThread mConnectedThread;

    /**
     * @param activity
     *            Application context.
     * @param messageThread
     *            Message thread with queue.
     * @param adapter
     *            Bluetooth adapter (only one for application).
     */
    public TerminalServiceBT(Activity activity, MessageThread messageThread) {
        setState(TerminalState.STATE_NONE);
        this.messageThread = messageThread;
        this.activity = activity;
    }

    /**
     * Set the current state of the chat connection.
     * 
     * @param state
     *            An integer defining the current connection state.
     */
    private synchronized void setState(TerminalState state) {
        // Log.d(TAG, "setState() " + currentTerminalState + " -> " + state);
        // currentTerminalState = state;

        // Give the new state to the Handler so the UI Activity can update
        if (messageThread != null) {
            messageThread
                    .addMessage(HandleMessages.MESSAGE_STATE_CHANGE, state);
        }
    }

    /**
     * @return Return the current connection state.
     */
    public final synchronized TerminalState getState() {
        if (messageThread != null) {
            return messageThread.getCurrentTerminalState();
        }

        return TerminalState.STATE_NONE;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public final synchronized void start() {
        Log.d(TAG, "start");

        interrupt();

        setState(TerminalState.STATE_NONE);
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * 
     */
    public final synchronized void connect() {
        // Cancel any thread attempting to make a connection
        if (messageThread.getCurrentTerminalState() == TerminalState.STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.interrupt();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.interrupt();
            mConnectedThread = null;
        }

        setState(TerminalState.STATE_CONNECTING);

        while (getState() != TerminalState.STATE_CONNECTING) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(/* device, secure */);
        // mConnectThread = new AcceptThread(bluetoothAdapter, secure);
        mConnectThread.start();
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection.
     * 
     * @throws Exception
     *             Exception by creating new thread.
     */
    public final synchronized void connected() throws Exception {
        Log.d(TAG, "connected");

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(messageThread, activity);
        mConnectedThread.start();
    }

    /**
     * Stop all threads.
     */
    public final synchronized void stop() {
        Log.d(TAG, "stop");

        // Kdyz zastavuju, tak uz nic nikam neposilej.
        setState(TerminalState.STATE_NONE);
        messageThread = null;

        interrupt();
    }

    private synchronized void interrupt() {
        if (mConnectThread != null) {

            // do {
            // try {
            mConnectThread.interrupt();
            // mConnectThread.join(1000);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
            // } while (mConnectThread.isAlive());

            mConnectThread = null;
        }

        if (mConnectedThread != null) {

            // do {
            // try {
            mConnectedThread.interrupt();
            // mConnectedThread.join(1000);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
            // } while (mConnectedThread.isAlive());
            mConnectedThread = null;
        }
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner.
     * 
     * @param out
     *            The bytes to write
     * @throws IOException
     *             Input output exception by write to thread.
     * @see ConnectedThread#write(byte[])
     */
    public final void write(byte[] out) throws IOException {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (messageThread.getCurrentTerminalState() != TerminalState.STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }

        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * This thread runs while attempting to make an outgoing connection with a
     * device. It runs straight through; the connection either succeeds or
     * fails.
     */
    public class ConnectThread extends Thread {
        public ConnectThread(/* BluetoothDevice device, boolean secure */) {
            interrupt();
        }

        @Override
        public final void run() {
            Log.i(TAG, "BEGIN mConnectThread:");
            setName("ConnectThread");

            // Reset the ConnectThread because we're done
            synchronized (TerminalServiceBT.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            try {
                connected();
            } catch (Exception e) {
                // Log.e(TAG, "Connection error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public final void interrupt() {
            super.interrupt();
        }
    }
}
