package cz.monetplus.mashregister.ingenico;

import java.util.ArrayList;
import java.util.concurrent.Executor;

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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cz.monetplus.blueterm.Balancing;
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
public class MbcaBaseActivity extends AdActivity {

	private static final String TAG = "MbcaBaseActivity";

	// private final ReentrantLock lock = new ReentrantLock();

	private EditText mAmountIdEditText;
	private Spinner mCurrencySpinner;
	private EditText mInvoiceIdEditText;
	// private EditText mTranIdEditText;

	private TextView mAnswerTextView;

	private String currentCurrency;
	private TextView blueHwAddress;

//	private DoTransactionTask transactionTask = null;

	private Menu propertiesMenu;

	private PosCallbackee posCallbackee;

	private String lastAuthcode = null;

	// private AdView adView;

	// /* Your ad unit id. Replace with your actual ad unit id. */
	// private static final String AD_UNIT_ID =
	// "ca-app-pub-4197154738167514/1390370981";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR); // Add this line
		setContentView(R.layout.activity_mbca_base);
		getActionBar().show();

		super.adAddView();

		mAmountIdEditText = (EditText) findViewById(R.id.editPrice);
		mCurrencySpinner = (Spinner) findViewById(R.id.spinnerCurrency);
		mInvoiceIdEditText = (EditText) findViewById(R.id.editTextInvoice);
		// mTranIdEditText = (EditText) findViewById(R.id.editTextTranId);

		mAnswerTextView = (TextView) findViewById(R.id.textAnswer);

		mAnswerTextView.setFocusableInTouchMode(true);
		mAnswerTextView.requestFocus();

		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currency_array,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mCurrencySpinner.setAdapter(adapter);
		mCurrencySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
				currentCurrency = parent.getItemAtPosition(pos).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		setupButtons();
		blueHwAddress = (TextView) findViewById(R.id.textViewHw);

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		blueHwAddress.setText(settings.getString(BT_ADDRESS, getString(R.string.default_select_device)));

		if (blueHwAddress.getText().equals(getString(R.string.default_select_device))) {
			setButtons(false);
		}
		
		this.posCallbackee = new PosCallbackee(MbcaBaseActivity.this, getApplicationContext());

	}
	
	/**
	 * @param command
	 */
	private void doTransaction(TransactionCommand command) {
		try {
			mAnswerTextView.setText("Calling " + command);

			posCallbackee.getTicket().clear();
			TransactionIn transIn = new TransactionIn(blueHwAddress.getText()
					.toString(), command, posCallbackee);
			transIn.setAmount(Long.valueOf((long) (Double
					.valueOf(mAmountIdEditText.getText().toString()) * 100)));
			transIn.setCurrency(Integer.valueOf(currentCurrency));
			transIn.setInvoice(mInvoiceIdEditText.getText().toString());

            transIn.setAlternateId(alternateId);

//			if (transactionTask != null) {
//				transactionTask.cancel(true);
//				transactionTask = null;
//			}

			new DoTransactionTask(new TaskDelegate() {
				@Override
				public void taskCompletionResult(TransactionOut transactionOut) {
					ShowTransactionOut(transactionOut);
				}
			}).executeOnExecutor (AsyncTask.SERIAL_EXECUTOR, transIn);

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * @param command
	 */
	private void doReversal(TransactionCommand command, String authCode) {
		try {
			mAnswerTextView.setText("Calling " + command);
			posCallbackee.getTicket().clear();
			TransactionIn transIn = new TransactionIn(blueHwAddress.getText()
					.toString(), command, posCallbackee);
			if (authCode != null) {
				transIn.setAuthCode(authCode);
			}

//			if (transactionTask != null) {
//				transactionTask.cancel(true);
//				transactionTask = null;
//			}

			new DoTransactionTask(new TaskDelegate() {
				@Override
				public void taskCompletionResult(TransactionOut transactionOut) {
					ShowTransactionOut(transactionOut);
				}
			}).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, transIn);

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private void setupButtons() {

		Button buttonSelect = (Button) findViewById(R.id.buttonHwSelect);
		buttonSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Launch the DeviceListActivity to see devices and do scan
				Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
				startActivityForResult(serverIntent, INTENT_RC_CONNECT_DEVICE_INSECURE);

			}
		});

		Button temp = (Button) findViewById(R.id.buttonInfo);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransaction(TransactionCommand.MBCA_INFO);
			}
		});

		temp = (Button) findViewById(R.id.buttonMbcaPay);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransaction(TransactionCommand.MBCA_PAY);
			}

		});

		temp = (Button) findViewById(R.id.buttonLastTran);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransaction(TransactionCommand.MBCA_LAST_TRAN);
			}

		});

				
		temp = (Button) findViewById(R.id.buttonReversalTransaction);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doReversal(TransactionCommand.MBCA_REVERSAL, lastAuthcode);
			}

		});

		temp = (Button) findViewById(R.id.buttonTransactionHandshake);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransaction(TransactionCommand.MBCA_HANDSHAKE);
			}
		});

		temp = (Button) findViewById(R.id.buttonBalancing);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransaction(TransactionCommand.MBCA_BALANCING);
			}
		});
		
		temp = (Button) findViewById(R.id.buttonParameters);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransaction(TransactionCommand.MBCA_PARAMETERS);
			}
		});

		temp = (Button) findViewById(R.id.buttonTip);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransaction(TransactionCommand.SMART_SHOP_TIP);
			}
		});

		temp = (Button) findViewById(R.id.butAccountInfo);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransaction(TransactionCommand.MBCA_ACCOUNT_INFO);
			}
		});


		temp = (Button) findViewById(R.id.buttonAccountInfoTest);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for(int i=0; i<=9; i++) {
					alternateId = Character.valueOf((char) ('0' + i));
//					alternateId = Character.valueOf((char) ('1'));
					doTransaction(TransactionCommand.MBCA_ACCOUNT_INFO);
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

	private void ShowTransactionOut(TransactionOut out) {
		if (out != null) {
			final String result = out.toString();
			lastAuthcode = out.getAuthCode();

			MbcaBaseActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mAnswerTextView.setText(result);
					Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
					setButtons(true);

				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case INTENT_RC_CONNECT_DEVICE_INSECURE:
			if (resultCode == Activity.RESULT_OK) {
				if (data.hasExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS)) {
					blueHwAddress.setText(data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS));

					if (blueHwAddress.getText().length() > 0) {
						setButtons(true);

					}
				}
			}
			break;
		}
	}

	private void setButtons(boolean enabled) {
		Button button = (Button) findViewById(R.id.buttonInfo);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonTransactionHandshake);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonBalancing);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonMbcaPay);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonLastTran);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonParameters);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonTip);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonReversalTransaction);
		button.setEnabled(lastAuthcode == null ? false : enabled);
	}

	public interface TaskDelegate {
		public void taskCompletionResult(TransactionOut transactionOut);
	}

	class DoTransactionTask extends AsyncTask<TransactionIn, Void, TransactionOut> {

		private TaskDelegate delegate;

		public DoTransactionTask(TaskDelegate delegate) {
			this.delegate = delegate;
		}

		@Override
		protected TransactionOut doInBackground(TransactionIn... params) {
			try {
				return MonetBTAPI.doTransaction(MbcaBaseActivity.this, params[0]);
			} catch (Exception e) {
				MbcaBaseActivity.this.runOnUiThread(new Runnable() {
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
		protected void onPostExecute(TransactionOut transactionOut) {
			// do the analysis of the returned data of the function
			if (transactionOut != null) {
				this.delegate.taskCompletionResult(transactionOut);
			}
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				Log.i(TAG, e.getMessage());
//			}
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
			MbcaBaseActivity.this.runOnUiThread(new Runnable() {

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
//		editor.commit();
		editor.apply();
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

	public void onMenuAlternateIdItemClick(MenuItem item) {
	}

}
