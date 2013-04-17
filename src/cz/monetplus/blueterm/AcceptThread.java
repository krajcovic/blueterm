package cz.monetplus.blueterm;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

public class AcceptThread extends Thread {
	private static final String NAME = "BlueTermMonet";

	private static final UUID MY_UUID = UUID
			.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");;

	private static final int REQUEST_ENABLE_BT = 1;

	private final BluetoothServerSocket mmServerSocket;

	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	public AcceptThread() {
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			mmServerSocket = null;
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}

			// Use a temporary object that is later assigned to mmServerSocket,
			// because mmServerSocket is final
			BluetoothServerSocket tmp = null;
			try {
				// MY_UUID is the app's UUID string, also used by the client
				// code
				tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
						NAME, MY_UUID);
			} catch (IOException e) {
			}
			mmServerSocket = tmp;
		}
	}

	// if (mBluetoothAdapter == null) {
	// // Device does not support Bluetooth
	// }

	// if (!mBluetoothAdapter.isEnabled()) {
	// Intent enableBtIntent = new
	// Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	// startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	// }

	private void startActivityForResult(Intent enableBtIntent,
			int requestEnableBt) {
		// TODO Auto-generated method stub

	}

	public void run() {
		BluetoothSocket socket = null;
		// Keep listening until exception occurs or a socket is returned
		while (true) {
			try {
				socket = mmServerSocket.accept();
			} catch (IOException e) {
				break;
			}
			// If a connection was accepted
			if (socket != null) {
				// Do work to manage the connection (in a separate thread)
				manageConnectedSocket(socket);
				try {
					mmServerSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
	}

	private void manageConnectedSocket(BluetoothSocket socket) {
		// TODO Auto-generated method stub

	}

	/** Will cancel the listening socket, and cause the thread to finish */
	public void cancel() {
		try {
			mmServerSocket.close();
		} catch (IOException e) {
		}
	}

}
