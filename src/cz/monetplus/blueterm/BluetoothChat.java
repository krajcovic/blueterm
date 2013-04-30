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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Timer;

import cz.monetplus.blueterm.bprotocol.BProtocol;
import cz.monetplus.blueterm.bprotocol.BProtocolFactory;
import cz.monetplus.blueterm.bprotocol.BProtocolMessages;
import cz.monetplus.blueterm.bprotocol.BProtocolTag;
import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.server.ServerFrame;
import cz.monetplus.blueterm.server.TCPClient;
import cz.monetplus.blueterm.util.MonetUtils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothChat extends Activity {
	// Debugging
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_TERM_READ = 2;
	public static final int MESSAGE_TERM_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int MESSAGE_CONNECTED = 6;
	public static final int MESSAGE_SERVER_READ = 12;
	public static final int MESSAGE_SERVER_WRITE = 13;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	private static final int REQUEST_PREFERENCE = 4;

	// Layout Views
	private static TextView mTitle;
	private ListView mConversationView;
	private EditText mOutEditText;
	private Button mAppInfoButton;
	private Button mPayButton;
	private Button mHandShakeButton;
	private Button mClearButton;
	private Button mStopButton;
	private static LinearLayout mInputTerminalLayout;
	private static LinearLayout mProgressLayout;

	private EditText mTerminalIdEditText;
	private EditText mAmountIdEditText;
	private EditText mInvoiceIdEditText;

	// Name of the connected device
	private static String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private static ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private TerminalService mChatService = null;

	// TCP client;
	private static TCPClient mTcpClient;

	// in the arrayList we add the messaged received from server
	private ArrayList<String> arrayList;

	private WarmerAdapter mAdapter;
	private boolean isStandalone;

	// private ByteArrayInputStream slipInputFraming;
	private static ByteArrayOutputStream slipOutputpFraming;

	SharedPreferences sharedPref;

	Timer terminalTimer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		// Set up the window layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);

		// Set up the custom title
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		slipOutputpFraming = new ByteArrayOutputStream();
		// slipInputFraming = new ByteArrayInputStream(new byte[512]);

		arrayList = new ArrayList<String>();

		// relate the listView from java to the one created in xml
		// mList = (ListView)findViewById(R.id.list);
		mAdapter = new WarmerAdapter(this, arrayList);
		// mList.setAdapter(mAdapter);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == TerminalService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}
	}

	/**
	 * @param resultCode
	 * @param serverMessage
	 */
	// TODO: vratit zpatky volane aplikaci.
	private void returnResult(String resultCode, String serverMessage) {
		if (isStandalone == false) {
			// Create intent to deliver some kind of result
			// data
			Intent result = new Intent(); // Uri.parse("content://result_uri"));
			result.putExtra("ResultCode", resultCode);
			result.putExtra("ServerMessage", serverMessage);
			setResult(Activity.RESULT_OK, result);
			finish();
		}
	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");

		mInputTerminalLayout = (LinearLayout) findViewById(R.id.input_layout);
		mProgressLayout = (LinearLayout) findViewById(R.id.progres_layout);

		// Initialize the array adapter for the conversation thread
		mConversationArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.message);
		mConversationView = (ListView) findViewById(R.id.in);
		mConversationView.setAdapter(mConversationArrayAdapter);

		// Initialize the compose field with a listener for the return key
		mOutEditText = (EditText) findViewById(R.id.edit_text_out);
		mOutEditText.setOnEditorActionListener(mWriteListener);

		mTerminalIdEditText = (EditText) findViewById(R.id.editTerminalId);
		mAmountIdEditText = (EditText) findViewById(R.id.editAmount);
		mInvoiceIdEditText = (EditText) findViewById(R.id.editInvoice);

		// Initialize the app info button with a listener that for click events
		mAppInfoButton = (Button) findViewById(R.id.button_app_info);
		mAppInfoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showProgressLayout();

				EditText mTerminalIdEditText = (EditText) findViewById(R.id.editTerminalId);
				send2Terminal(SLIPFrame.createFrame(new TerminalFrame(33333,
						BProtocolMessages.getAppInfo(mTerminalIdEditText
								.getText().toString())).createFrame()));
			}
		});

		mPayButton = (Button) findViewById(R.id.button_pay);
		mPayButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showProgressLayout();

				Double value = Double.valueOf(mAmountIdEditText.getText()
						.toString()) * 100;

				send2Terminal(SLIPFrame.createFrame(new TerminalFrame(33333,
						BProtocolMessages.getSale(mTerminalIdEditText.getText()
								.toString(), value.intValue(),
								mInvoiceIdEditText.getText().toString()))
						.createFrame()));
			}
		});
		mPayButton.setFocusableInTouchMode(true);
		mPayButton.requestFocus();

		mHandShakeButton = (Button) findViewById(R.id.button_handshake);
		mHandShakeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showProgressLayout();

				EditText mTerminalIdEditText = (EditText) findViewById(R.id.editTerminalId);
				send2Terminal(SLIPFrame.createFrame(new TerminalFrame(33333,
						BProtocolMessages.getHanshake(mTerminalIdEditText
								.getText().toString())).createFrame()));
			}
		});

		mClearButton = (Button) findViewById(R.id.button_clear);
		mClearButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mConversationArrayAdapter.clear();
			}
		});

		mStopButton = (Button) findViewById(R.id.buttonStop);
		mStopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mInputTerminalLayout.setVisibility(View.VISIBLE);
				mProgressLayout.setVisibility(View.GONE);

				returnResult("61", "Spojeni ukončeno uživatelem");
			}
		});

		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		// TODO: smazat hodnoty, pouze pro debug
		String terminalId = "12345678";
		String amount = "10.00";
		String invoice = "1234";
		isStandalone = true;

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new TerminalService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");

		// Get the intent that started this activity
		Intent intent = getIntent();
		if (intent != null) {
			if (Intent.ACTION_SENDTO.equals(intent.getAction())) {
				isStandalone = false;

				terminalId = intent.getStringExtra("TerminalId");
				amount = intent.getStringExtra("Amount");
				invoice = intent.getStringExtra("Invoice");

				String address = sharedPref.getString(
						getString(R.string.preferences_blue_addr), "");
				if (!address.isEmpty()) {
					connectDevice(address, false);
				}
			}
		}

		mTerminalIdEditText.setText(terminalId);
		mAmountIdEditText.setText(amount);
		mInvoiceIdEditText.setText(invoice);

		setDebugVisibility();
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	private void send2Terminal(byte[] message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != TerminalService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		if (message.length > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			// byte[] send = message.getBytes();
			mChatService.write(message);

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
			mOutEditText.setText(mOutStringBuffer);
		}
	}

	// The action listener for the EditText widget, to listen for the return key
	private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
		public boolean onEditorAction(TextView view, int actionId,
				KeyEvent event) {
			// If the action is a key-up event on the return key, send the
			// message
			if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_UP) {
				String message = view.getText().toString();
				send2Terminal(message.getBytes());
			}
			if (D)
				Log.i(TAG, "END onEditorAction");
			return true;
		}
	};

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		private byte[] idConnect;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case TerminalService.STATE_CONNECTED:
					mTitle.setText(R.string.title_connected_to);
					mTitle.append(mConnectedDeviceName);
					mConversationArrayAdapter.clear();
					break;
				case TerminalService.STATE_CONNECTING:
					mTitle.setText(R.string.title_connecting);
					break;
				case TerminalService.STATE_LISTEN:
				case TerminalService.STATE_NONE:
					mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_SERVER_WRITE: {
				String hex = MonetUtils.bytesToHex((byte[]) msg.obj);
				mConversationArrayAdapter.add("SO: "
						+ hex.substring(0, Math.min(hex.length(), 80)));
			}
				break;

			case MESSAGE_SERVER_READ: {
				String hex = MonetUtils.bytesToHex((byte[]) msg.obj);
				mConversationArrayAdapter.add("SI: "
						+ hex.substring(0, Math.min(hex.length(), 80)));
			}
				break;

			case MESSAGE_TERM_WRITE: {
				{
					String hex = MonetUtils.bytesToHex((byte[]) msg.obj);
					mConversationArrayAdapter.add("TO: "
							+ hex.substring(0, Math.min(hex.length(), 80)));
				}
				break;
			}

			case MESSAGE_CONNECTED: {
				// if (mTcpClient.isConnected()) {
				byte[] status = new byte[1];
				status[0] = (byte) msg.arg1;
				ServerFrame soFrame = new ServerFrame((byte) 0x05, idConnect,
						status);
				TerminalFrame toFrame = new TerminalFrame(
						TerminalPorts.SERVER.getPortNumber(),
						soFrame.createFrame());

				send2Terminal(SLIPFrame.createFrame(toFrame.createFrame()));

				if (status[0] != 0) {
					mInputTerminalLayout.setVisibility(View.VISIBLE);
					mProgressLayout.setVisibility(View.GONE);
				}

				// }
				break;
			}

			case MESSAGE_TERM_READ: {
				byte[] readSlipFrame = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				// String readMessage = new String(readBuf, 0, msg.arg1);
				// String readMessage = MonetUtils.bytesToHex(readSlipFrame,
				// msg.arg1);
				slipOutputpFraming.write(readSlipFrame, 0, msg.arg1);

				// Check
				if (SLIPFrame.isFrame(slipOutputpFraming.toByteArray())) {

					TerminalFrame termFrame = new TerminalFrame(
							SLIPFrame.parseFrame(readSlipFrame));

					if (termFrame != null) {
						switch (termFrame.getPort()) {
						case UNDEFINED:
							Log.d(TAG, "undefined port");
							break;
						case SERVER:
							handeServerMessage(termFrame);
							break;
						case FLEET:
							Log.d(TAG, "fleet data");
							break;
						case MAINTENANCE:
							Log.d(TAG, "maintentace data");
							break;
						case MASTER:
							// Tyhle zpravy zpracovavat, jsou pro tuhle aplikaci
							BProtocolFactory factory = new BProtocolFactory();
							BProtocol bprotocol = factory.deserialize(termFrame
									.getData());

							mConversationArrayAdapter.add("TI: "
									+ bprotocol.toString());

							if (bprotocol.getProtocolType().equals("B2")) {
								mInputTerminalLayout
										.setVisibility(View.VISIBLE);
								mProgressLayout.setVisibility(View.GONE);

								returnResult(
										bprotocol.getTagMap().get(
												BProtocolTag.ResponseCode),
										bprotocol.getTagMap().get(
												BProtocolTag.ServerMessage));
							}

							break;
						default:
							// Nedelej nic, spatne data, format, nebo crc
							Log.e(TAG, "Invalid port");
							break;

						}
					}

				} else {
					Log.e(TAG, "Corrupted data. It's not slip frame.");
				}

				// mConversationArrayAdapter.add(mConnectedDeviceName + ":  "
				// + readMessage);

				// Log.d(TAG, "Len: " + slipOutputpFraming.size());

				break;
			}
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(
						BlueTermApplication.getAppContext(), // getApplicationContext()
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(BlueTermApplication.getAppContext(), // getApplicationContext()
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}

		private void handeServerMessage(TerminalFrame termFrame) {
			// sends the message to the server
			ServerFrame serverFrame = new ServerFrame(termFrame.getData());

			switch (serverFrame.getCommand()) {
			case 0x00: {
				ServerFrame soFrame = new ServerFrame((byte) 0x80,
						serverFrame.getId(), null);
				TerminalFrame toFrame = new TerminalFrame(termFrame.getPort()
						.getPortNumber(), soFrame.createFrame());

				send2Terminal(SLIPFrame.createFrame(toFrame.createFrame()));
				break;
			}
			case 0x01: {
				idConnect = serverFrame.getId();

				int port = MonetUtils.getInt(serverFrame.getData()[4],
						serverFrame.getData()[5]);

				int timeout = MonetUtils.getInt(serverFrame.getData()[6],
						serverFrame.getData()[7]);

				// connect to the server
				new TCPconnectTask(Arrays.copyOfRange(serverFrame.getData(), 0,
						4), port, timeout, serverFrame.getIdInt()).execute("");

				ServerFrame soFrame = new ServerFrame((byte) 0x81,
						serverFrame.getId(), new byte[1]);
				TerminalFrame toFrame = new TerminalFrame(termFrame.getPort()
						.getPortNumber(), soFrame.createFrame());

				send2Terminal(SLIPFrame.createFrame(toFrame.createFrame()));

				break;
			}
			case 0x02: {
				mTcpClient.stopClient();
				break;
			}
			case 0x03: {
				if (mTcpClient != null) {
					try {
						mTcpClient.sendMessage(serverFrame.getData());
					} catch (IOException e) {
						Log.d(TAG, e.getMessage());
					}
				}
				break;
			}
			}

		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, true);
			}
			break;
		case REQUEST_CONNECT_DEVICE_INSECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, false);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occured
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		case REQUEST_PREFERENCE:
			if (resultCode == Activity.RESULT_OK) {
				setDebugVisibility();
			}
		}
	}

	private void setDebugVisibility() {
		String tmp = getString(R.string.preferences_log);
		if (sharedPref.getBoolean(tmp, false)) {
			mConversationView.setVisibility(View.VISIBLE);
		} else {
			mConversationView.setVisibility(View.GONE);
		}
	}

	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);

		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.preferences_blue_addr), address);

		// Commit the edits!
		editor.commit();

		connectDevice(address, secure);
	}

	/**
	 * Get the BluetoothDevice object
	 * 
	 * @param address
	 * @param secure
	 */
	private void connectDevice(String address, boolean secure) {
		// Get the BLuetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, secure);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.secure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			return true;
		case R.id.insecure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent,
					REQUEST_CONNECT_DEVICE_INSECURE);
			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		case R.id.preferences:

			serverIntent = new Intent(this, SettingsActivity.class);
			startActivityForResult(serverIntent, REQUEST_PREFERENCE);

			return true;
		}
		return false;
	}

	private void showProgressLayout() {
		if (mChatService.getState() == TerminalService.STATE_CONNECTED) {
			mInputTerminalLayout.setVisibility(View.GONE);
			mProgressLayout.setVisibility(View.VISIBLE);
			// ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
			// LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			// mProgressLayout.setLayoutParams(params);
		}
	}

	public class TCPconnectTask extends AsyncTask<String, byte[], TCPClient> {

		private byte[] serverIp;
		private int serverPort;
		private int connectionId;
		private int timeout;

		private TCPconnectTask(byte[] serverIp, int serverPort, int timeout,
				int connectionId) {
			super();
			this.serverIp = serverIp;
			this.serverPort = serverPort;
			this.connectionId = connectionId;
			this.timeout = timeout;
		}

		@Override
		protected TCPClient doInBackground(String... message) {

			// we create a TCPClient object and
			mTcpClient = new TCPClient(serverIp, serverPort, timeout, mHandler,
					new TCPClient.OnMessageReceived() {
						@Override
						// here the messageReceived method is implemented
						public void messageReceived(byte[] message) {
							// this method calls the onProgressUpdate
							// publishProgress(message);

							ServerFrame soFrame = new ServerFrame((byte) 0x04,
									connectionId, message);

							TerminalFrame termFrame = new TerminalFrame(
									TerminalPorts.SERVER.getPortNumber(),
									soFrame.createFrame());

							// send to terminal
							send2Terminal(SLIPFrame.createFrame(termFrame
									.createFrame()));
						}
					});
			mTcpClient.run();

			return null;
		}

		@Override
		protected void onProgressUpdate(byte[]... values) {
			super.onProgressUpdate(values);

			// Share the sent message back to the UI Activity
			mHandler.obtainMessage(BluetoothChat.MESSAGE_SERVER_READ, -1, -1,
					values[0]).sendToTarget();

			// in the arrayList we add the messaged received from server
			arrayList.add(new String(values[0]));
			// notify the adapter that the data set has changed. This means that
			// new message received
			// from server was added to the list
			mAdapter.notifyDataSetChanged();
		}
	}

}
