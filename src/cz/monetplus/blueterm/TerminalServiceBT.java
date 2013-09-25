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

package cz.monetplus.blueterm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import cz.monetplus.blueterm.util.MonetUtils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for incoming
 * connections, a thread for connecting with a device, and a thread for
 * performing data transmissions when connected.
 */
public class TerminalServiceBT {
    // Debugging
    private static final String TAG = "TerminalService";
    private static final boolean D = true;

    // Name for the SDP record when creating server socket
    // private static final String NAME_SECURE = "BluetoothChatSecure";
    // private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
    // UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID_INSECURE =
    // UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;

    private Handler mHandler;

    private ConnectThread mConnectThread;

    private ConnectedThread mConnectedThread;

    private int mState;

    /**
     * Constructor. Prepares a new BluetoothChat session.
     * 
     * @param context
     *            The UI Activity Context
     * @param handler
     *            A Handler to send messages back to the UI Activity
     */
    public TerminalServiceBT(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = ConnectionState.STATE_NONE;
        mHandler = handler;
    }

    /**
     * Set the current state of the chat connection.
     * 
     * @param state
     *            An integer defining the current connection state.
     */
    private synchronized void setState(int state) {
        if (D) {
            Log.d(TAG, "setState() " + mState + " -> " + state);
        }
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        if (mHandler != null) {
            mHandler.obtainMessage(HandleMessages.MESSAGE_STATE_CHANGE, state,
                    -1).sendToTarget();
        }
    }

    /**
     * @return Return the current connection state.
     */
    public/* synchronized */int getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        if (D) {
            Log.d(TAG, "start");
        }

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(ConnectionState.STATE_NONE);

        // Start the thread to listen on a BluetoothServerSocket
        // if (mSecureAcceptThread == null) {
        // mSecureAcceptThread = new AcceptThread(true);
        // mSecureAcceptThread.start();
        // }
        // if (mInsecureAcceptThread == null) {
        // mInsecureAcceptThread = new AcceptThread(false);
        // mInsecureAcceptThread.start();
        // }
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
        if (D) {
            Log.d(TAG, "connect to: " + device);
        }

        // Cancel any thread attempting to make a connection
        if (mState == ConnectionState.STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        setState(ConnectionState.STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection.
     * 
     * @param socket
     *            The BluetoothSocket on which the connection was made.
     * @param device
     *            The BluetoothDevice that has been connected.
     */
    public synchronized void connected(BluetoothSocket socket,
            BluetoothDevice device) {
        if (D) {
            Log.d(TAG, "connected");
        }

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        // Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        // Bundle bundle = new Bundle();
        // bundle.putString(DEVICE_NAME, device.getName());
        // msg.setData(bundle);
        // mHandler.sendMessage(msg);

        setState(ConnectionState.STATE_CONNECTED);
    }

    public void join() {
        if (D) {
            Log.d(TAG, "join");
        }

        if (mConnectThread != null) {
            try {
                mConnectThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (mConnectedThread != null) {
            try {
                mConnectedThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Stop all threads.
     */
    public synchronized void stop() {
        if (D) {
            Log.d(TAG, "stop");
        }

        mHandler = null;

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Kdyz zastavuju, tak uz nic nikam neposilej.
        setState(ConnectionState.STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner.
     * 
     * @param out
     *            The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != ConnectionState.STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        if (Looper.myLooper() != null && mHandler != null) {
            // Send a failure message back to the Activity
            Message msg = mHandler.obtainMessage(HandleMessages.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString("Toast", "Unable to connect device");
            msg.setData(bundle);
            mHandler.obtainMessage(HandleMessages.MESSAGE_QUIT).sendToTarget();
        }

        // Start the service over to restart none mode
        TerminalServiceBT.this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {

        if (Looper.myLooper() != null && mHandler != null) {
            // Send a failure message back to the Activity
            Message msg = mHandler.obtainMessage(HandleMessages.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString("Toast", "Device connection was lost");
            msg.setData(bundle);
            mHandler.sendMessage(msg);
            mHandler.obtainMessage(HandleMessages.MESSAGE_QUIT).sendToTarget();
        }

        // Start the service over to restart none mode
        TerminalServiceBT.this.start();
    }

    public BluetoothAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * This thread runs while attempting to make an outgoing connection with a
     * device. It runs straight through; the connection either succeeds or
     * fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure) {
                    tmp = device
                            .createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                } else {
                    tmp = device
                            .createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread:");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            getAdapter().cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() "
                            + " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (TerminalServiceBT.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + " socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device. It handles all
     * incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread: ");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    byte[] buffer = SlipInputReader.read(mmInStream);

                    // Send the obtained bytes to the UI Activity
                    if (mHandler != null) {
                        mHandler.obtainMessage(
                                HandleMessages.MESSAGE_TERM_READ,
                                buffer.length, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                    connectionLost();
                    break;
                }
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
                mmOutStream.write(buffer);

                Log.d(">>>term", new String(buffer, "UTF-8"));
                Log.d(">>>", MonetUtils.bytesToHex(buffer));

                // Log.d("<<<term", slip.toString());
                // Log.d("<<<    ", MonetUtils.bytesToHex(slip.toByteArray()));

                // Share the sent message back to the UI Activity
//                if (mHandler != null) {
//                    mHandler.obtainMessage(
//                            HandleMessages.MESSAGE_TERM_WRITE_FINISH, -1, -1,
//                            buffer).sendToTarget();
//                }
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
