package cz.monetplus.mashregister.ingenico;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cz.monetplus.blueterm.MonetBTAPI;
import cz.monetplus.blueterm.TransactionCommand;
import cz.monetplus.blueterm.TransactionIn;
import cz.monetplus.blueterm.TransactionOut;
import cz.monetplus.mashregister.R;
import cz.monetplus.mashregister.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class ServisActivity extends AdActivity {
	private static final int ACTIVITY_INTENT_ID = 33333;

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 33334;

	private static final String TAG = "ServisActivity";

	private TextView mAnswerTextView;
	private TextView blueHwAddress;

	DoTransactionTask transactionTask = null;

	private Menu propertiesMenu;

	private PosCallbackee posCallbackee;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servis);

		super.adAddView();
		mAnswerTextView = (TextView) findViewById(R.id.textAnswer);

		mAnswerTextView.setFocusableInTouchMode(true);
		mAnswerTextView.requestFocus();

		setupButtons();

		Button buttonSelect = (Button) findViewById(R.id.buttonHwSelect);
		buttonSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Launch the DeviceListActivity to see devices and do scan
				Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);

			}
		});

		blueHwAddress = (TextView) findViewById(R.id.textViewHw);

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		blueHwAddress.setText(settings.getString(BT_ADDRESS, getString(R.string.default_select_device)));

		if (blueHwAddress.getText().equals(getString(R.string.default_select_device))) {
			setButtons(false);
		}

		updateTerminalName();

		this.posCallbackee = new PosCallbackee(ServisActivity.this, getApplicationContext());
	}

	private void updateTerminalName() {
		try {
		BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
		if (defaultAdapter != null) {
			BluetoothDevice remoteDevice = defaultAdapter.getRemoteDevice(blueHwAddress.getText().toString());
//			remoteDevice = defaultAdapter.getRemoteDevice("00:00:00:00:00:00");
			if (remoteDevice != null) {
				EditText ettn = (EditText) findViewById(R.id.editTextTerminalName);
				ettn.setText(remoteDevice.getName());
			}
		}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	private void setupButtons() {
		Button pinButton = (Button) findViewById(R.id.buttonGetPin);
		pinButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					EditText ettn = (EditText) findViewById(R.id.editTextTerminalName);
					Toast.makeText(getApplicationContext(), MonetBTAPI.getPin(ettn.getText().toString()),
							Toast.LENGTH_LONG).show();

				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}

			}
		});

		Button connectButton = (Button) findViewById(R.id.buttonConnect);
		connectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					// ShowTransactionOut(new TransactionOu));
					mAnswerTextView.setText("Calling " + TransactionCommand.ONLY_CONNECT);
					TransactionIn transIn = new TransactionIn(blueHwAddress.getText().toString(),
							TransactionCommand.ONLY_CONNECT, posCallbackee);

					transactionTask = new DoTransactionTask();
					transactionTask.execute(transIn);

				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}

			}
		});

		Button disconnectButton = (Button) findViewById(R.id.buttonDisconnect);
		disconnectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAnswerTextView.setText("Calling disconnecting...");
				try {
					MonetBTAPI.doCancel();
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});

		Button updateButton = (Button) findViewById(R.id.buttonMaintenanceUpdate);
		updateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAnswerTextView.setText("Calling disconnecting...");
				try {
					// ShowTransactionOut(new TransactionOu));
					mAnswerTextView.setText("Calling " + TransactionCommand.MAINTENANCE_UPDATE);
					TransactionIn transIn = new TransactionIn(blueHwAddress.getText().toString(),
							TransactionCommand.MAINTENANCE_UPDATE, posCallbackee);

					transactionTask = new DoTransactionTask();
					transactionTask.execute(transIn);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.properties_menu, menu);
		propertiesMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.bt_enabled:
			item.setChecked(true);
			propertiesMenu.findItem(R.id.tcp_enabled).setChecked(false);
			break;
		case R.id.tcp_enabled:
			item.setChecked(true);
			propertiesMenu.findItem(R.id.bt_enabled).setChecked(false);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void ShowTransactionOut(TransactionOut out) {
		if (out != null) {
			final String result = out.toString();
			ServisActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mAnswerTextView.setText(result);
					Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

					if (!posCallbackee.getTicket().isEmpty()) {
						Intent intent = new Intent(getApplicationContext(), TicketListActivity.class);
						Bundle b = new Bundle();
						b.putStringArrayList("ticket", (ArrayList<String>) posCallbackee.getTicket());
						intent.putExtras(b);

						startActivity(intent);
					}
				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_INSECURE:
			if (resultCode == Activity.RESULT_OK) {
				if (data.hasExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS)) {
					blueHwAddress.setText(data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS));

					if (blueHwAddress.getText().length() > 0) {
						setButtons(true);
						updateTerminalName();
					}
				}
			}
			break;
		case ACTIVITY_INTENT_ID:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					mAnswerTextView.setText(data.toString());
				}
			}
			break;
		}
	}

	private void setButtons(boolean enabled) {
		Button button = (Button) findViewById(R.id.buttonConnect);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonDisconnect);
		button.setEnabled(enabled);
	}

	class DoTransactionTask extends AsyncTask<TransactionIn, Void, TransactionOut> {

		@Override
		protected TransactionOut doInBackground(TransactionIn... params) {
			try {
				return MonetBTAPI.doTransaction(ServisActivity.this, params[0]);
			} catch (Exception e) {
				ServisActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "Another thread work with blueterm.", Toast.LENGTH_LONG)
								.show();
					}
				});
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(TransactionOut result) {
			// do the analysis of the returned data of the function
			if (result != null) {
				ShowTransactionOut(result);
			}
			transactionTask = null;
		}
	}

	class DoCancelTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			MonetBTAPI.doCancel();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			ServisActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mAnswerTextView.setText("MonetBTApi is closed. maybe.");

				}
			});
		}

	}

	@Override
	protected void onStart() {
		super.onStart();

		// EasyTracker.getInstance(this).activityStart(this); // Add this
		// method.
	}

	@Override
	protected void onStop() {
		super.onStop();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(BT_ADDRESS, blueHwAddress.getText().toString());

		editor.commit();
		// EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}
