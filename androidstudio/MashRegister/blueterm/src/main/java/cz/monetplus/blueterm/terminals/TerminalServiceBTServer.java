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

package cz.monetplus.blueterm.terminals;

import java.io.IOException;
import java.util.UUID;

import cz.monetplus.blueterm.util.MonetUtils;
import cz.monetplus.blueterm.worker.MessageThread;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for incoming
 * connections, a thread for connecting with a device, and a thread for
 * performing data transmissions when connected.
 */
public class TerminalServiceBTServer {
    // Debugging
    private static final String TAG = "TerminalService";
    // private static final boolean D = true;

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
    // UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID_INSECURE =
    // UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter bluetoothAdapter;

    // private MessageThread messageThread;

    private AcceptThread mAcceptThread;

    private BluetoothServerSocket mmServerSocket;

    private BluetoothSocket mmSocket;

    /**
     * Constructor. Prepares a new BluetoothChat session.
     * 
     * @param context
     *            The UI Activity Context
     * @param handler
     *            A Handler to send messages back to the UI Activity
     */
    /**
     * @param context
     *            Application context.
     * @param messageThread
     *            Message thread with queue.
     * @param adapter
     *            Bluetooth adapter (only one for application).
     */
    public TerminalServiceBTServer(Context context,
            MessageThread messageThread, BluetoothAdapter adapter) {
        bluetoothAdapter = adapter;
        // currentTerminalState = TerminalState.STATE_NONE;
        // this.messageThread = messageThread;
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * 
     * @param device
     *            The BluetoothDevice to connect
     * @param secure
     *            Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "connect to: " + device);

        // Start the thread to connect with the given device
        // mConnectThread = new ConnectThread(device, secure);
        // mConnectThread.start();
        mAcceptThread = new AcceptThread(device, secure);
        mAcceptThread.start();

    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection.
     * 
     * @param socket
     *            The BluetoothSocket on which the connection was made.
     * @param device
     *            The BluetoothDevice that has been connected.
     */
    public synchronized void connected() {
        Log.d(TAG, "connected");
    }

    /**
     * Stop all threads.
     */
    public synchronized void stop() {
        Log.d(TAG, "stop");

        try {
            if (mmSocket != null) {
                mmSocket.close();
            }
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner.
     * 
     * @param out
     *            The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public synchronized void write(byte[] out) {
        mAcceptThread.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    // private void connectionFailed() {
    // if (messageThread != null) {
    // messageThread.setOutputMessage("Terminal connection failed.");
    // messageThread.addMessage(HandleOperations.Exit);
    // }
    // }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    // private void connectionLost() {
    //
    // if (messageThread != null) {
    // messageThread.addMessage(HandleOperations.Exit);
    // }
    // }

    public BluetoothAdapter getAdapter() {
        return bluetoothAdapter;
    }

    /**
     * This thread runs while attempting to make an outgoing connection with a
     * device. It runs straight through; the connection either succeeds or
     * fails.
     */
    private class AcceptThread extends Thread {

        public AcceptThread(BluetoothDevice device, boolean secure) {
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            // try {
            // if (secure) {
            // mmSocket = device
            // .createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            // } else {
            // mmSocket = device
            // .createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            // }

            try {
                mmServerSocket = bluetoothAdapter
                        .listenUsingRfcommWithServiceRecord(NAME_INSECURE,
                                MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            // } catch (IOException e) {
            // Log.e(TAG, "create() failed", e);
            // }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Thread#run()
         */
        public void run() {
            Log.i(TAG, "BEGIN mAcceptThread:");
            setName("AcceptThread");

            // Always cancel discovery because it will slow down a connection
            getAdapter().cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                mmSocket = mmServerSocket.accept();
            } catch (IOException e) {
                // break;
            }

            // Reset the ConnectThread because we're done
            // synchronized (TerminalServiceBT.this) {
            // mConnectThread = null;
            // }

            // if (mmSocket.isConnected()) {
            // messageThread.addMessage(new HandleMessage(
            // HandleOperations.TerminalConnected));
            // } else {
            // messageThread.setOutputMessage("Socket is closed");
            // messageThread.addMessage(new HandleMessage(
            // HandleOperations.Exit));
            // }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + " socket failed", e);
            }
        }

        /**
         * Write to the connected OutStream.
         * 
         * @param buffer
         *            The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                if (mmSocket.isConnected()) {
                    mmSocket.getOutputStream().write(buffer);
                    // mmOutStream.flush();
                }

                // Log.d(">>>term", new String(buffer, "UTF-8"));
                Log.d(">>>term", MonetUtils.bytesToHex(buffer));
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }
    }

    // /**
    // * This thread runs while attempting to make an outgoing connection with a
    // * device. It runs straight through; the connection either succeeds or
    // * fails.
    // */
    // private class ConnectThread extends Thread {
    //
    // public ConnectThread(BluetoothDevice device, boolean secure) {
    // // Get a BluetoothSocket for a connection with the
    // // given BluetoothDevice
    // try {
    // if (secure) {
    // mmSocket = device
    // .createRfcommSocketToServiceRecord(MY_UUID_SECURE);
    // } else {
    // mmSocket = device
    // .createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
    // }
    // } catch (IOException e) {
    // Log.e(TAG, "create() failed", e);
    // }
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see java.lang.Thread#run()
    // */
    // public void run() {
    // Log.i(TAG, "BEGIN mConnectThread:");
    // setName("ConnectThread");
    //
    // // Always cancel discovery because it will slow down a connection
    // getAdapter().cancelDiscovery();
    //
    // // Make a connection to the BluetoothSocket
    // try {
    // // This is a blocking call and will only return on a
    // // successful connection or an exception
    // mmSocket.connect();
    // } catch (IOException e) {
    // // Close the socket
    // try {
    // mmSocket.close();
    // } catch (IOException e2) {
    // Log.e(TAG, "unable to close() "
    // + " socket during connection failure", e2);
    // }
    // connectionFailed();
    // return;
    // }
    //
    // // Reset the ConnectThread because we're done
    // synchronized (TerminalServiceBTServer.this) {
    // mConnectThread = null;
    // }
    //
    // if (mmSocket.isConnected()) {
    // messageThread.addMessage(new HandleMessage(
    // HandleOperations.TerminalConnected));
    // } else {
    // messageThread.setOutputMessage("Socket is closed");
    // messageThread.addMessage(new HandleMessage(
    // HandleOperations.Exit));
    // }
    // }
    //
    // public void cancel() {
    // try {
    // mmSocket.close();
    // } catch (IOException e) {
    // Log.e(TAG, "close() of connect " + " socket failed", e);
    // }
    // }
    // }
    //
    // /**
    // * This thread runs during a connection with a remote device. It handles
    // all
    // * incoming and outgoing transmissions.
    // */
    // private class ConnectedThread extends Thread {
    // // private final BluetoothSocket mmSocket;
    //
    // private InputStream mmInStream;
    // private OutputStream mmOutStream;
    //
    // public ConnectedThread(/* BluetoothSocket socket */) {
    // Log.d(TAG, "create ConnectedThread: ");
    //
    // // Get the BluetoothSocket input and output streams
    // try {
    // mmInStream = mmSocket.getInputStream();
    // } catch (IOException e) {
    // Log.e(TAG, "Socket InputStream is connected", e);
    // messageThread
    // .setOutputMessage("Socket InputStream is connected");
    // messageThread.addMessage(HandleOperations.Exit);
    // }
    //
    // try {
    // mmOutStream = mmSocket.getOutputStream();
    // } catch (IOException e) {
    // Log.e(TAG, "Socket OutputStream is connected", e);
    // messageThread
    // .setOutputMessage("Socket InputStream is connected");
    // messageThread.addMessage(HandleOperations.Exit);
    // }
    // }
    //
    // public void run() {
    // Log.i(TAG, "BEGIN mConnectedThread");
    //
    // messageThread.addMessage(HandleOperations.TerminalReady);
    //
    // // Keep listening to the InputStream while connected
    // while (messageThread != null) {
    // try {
    // byte[] buffer = SlipInputReader.read(mmInStream);
    //
    // // Send the obtained bytes to the UI Activity
    // if (messageThread != null) {
    // messageThread.addMessage(new HandleMessage(
    // HandleOperations.TerminalRead, buffer));
    // }
    // } catch (IOException e) {
    // // Ukoncene cteni, to neni problem.
    // Log.d(TAG, e.getMessage());
    // connectionLost();
    // break;
    // }
    // }
    // }

    // }
}
