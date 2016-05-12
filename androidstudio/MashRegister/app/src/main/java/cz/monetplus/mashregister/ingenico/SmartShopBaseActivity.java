package cz.monetplus.mashregister.ingenico;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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
public class SmartShopBaseActivity extends AdActivity {
	private static final int ACTIVITY_INTENT_ID = 33333;

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 33334;

	private static final String TAG = "SmartShopBaseActivity";

	// private final ReentrantLock lock = new ReentrantLock();

	private EditText mAmountIdEditText;
	private EditText mCardType;
	private Spinner mCurrencySpinner;
	private EditText mInvoiceIdEditText;
	private CheckBox cbPartialPayment;
	private EditText mTicketNumberEditText;

	private TextView mAnswerTextView;

	private String currentCurrency;
	private TextView blueHwAddress;

	DoTransactionTask transactionTask = null;

	private Menu propertiesMenu;

	private PosCallbackee posCallbackee;

	// private AdView adView;

	// /* Your ad unit id. Replace with your actual ad unit id. */
	// private static final String AD_UNIT_ID =
	// "ca-app-pub-4197154738167514/1390370981";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR); // Add this line
		setContentView(R.layout.activity_smartshop_base);
		getActionBar().show();

		super.adAddView();

		mAmountIdEditText = (EditText) findViewById(R.id.editPrice);
		mCardType = (EditText) findViewById(R.id.editCardType);
		mCurrencySpinner = (Spinner) findViewById(R.id.spinnerCurrency);
		mInvoiceIdEditText = (EditText) findViewById(R.id.editTextInvoice);
		mTicketNumberEditText = (EditText) findViewById(R.id.editTicketId);
		cbPartialPayment = (CheckBox) findViewById(R.id.cbPartial);

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

		blueHwAddress = (TextView) findViewById(R.id.textViewHw);
		setupButtons();

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		blueHwAddress.setText(settings.getString(BT_ADDRESS, getString(R.string.default_select_device)));

		if (blueHwAddress.getText().equals(getString(R.string.default_select_device))) {
			setButtons(false);
		}
		
		this.posCallbackee = new PosCallbackee(SmartShopBaseActivity.this, getApplicationContext());
	}
	
	private void doTransacation(TransactionCommand command) {
		try {
			mAnswerTextView.setText("Calling " + command);			
			posCallbackee.getTicket().clear();
			
			TransactionIn transIn = new TransactionIn(blueHwAddress.getText()
					.toString(), command, posCallbackee);
			transIn.setAmount(Long.valueOf((long) (Double
					.valueOf(mAmountIdEditText.getText().toString()) * 100)));
			
			if(mCardType.getText().toString().length() > 0) {
				transIn.setCardType(Integer.valueOf(mCardType.getText().toString()));
			}
			
			transIn.setCurrency(Integer.valueOf(currentCurrency));
			transIn.setInvoice(mInvoiceIdEditText.getText().toString());
			transIn.setPartialPayment(cbPartialPayment.isChecked());
			transIn.setTicketNumber(mTicketNumberEditText.getText().toString());

			if (transactionTask != null) {
				transactionTask.cancel(true);
				transactionTask = null;
			}

			transactionTask = new DoTransactionTask();
			transactionTask.execute(transIn);

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
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);

			}
		});

		Button temp = (Button) findViewById(R.id.buttonInfo);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransacation(TransactionCommand.SMART_SHOP_GET_APP_INFO);
			}
		});

		temp = (Button) findViewById(R.id.buttonSmartPay);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransacation(TransactionCommand.SMART_SHOP_PAY);
			}

		});
		
		temp = (Button) findViewById(R.id.buttonRecharging);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransacation(TransactionCommand.SMART_SHOP_RECHARGING);
			}

		});

		temp = (Button) findViewById(R.id.buttonReturn);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransacation(TransactionCommand.SMART_SHOP_RETURN);
			}
		});

		temp = (Button) findViewById(R.id.buttonCardState);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransacation(TransactionCommand.SMART_SHOP_STATE);
			}
		});

		temp = (Button) findViewById(R.id.buttonTransactionHandshake);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransacation(TransactionCommand.SMART_SHOP_HANDSHAKE);
			}
		});

		temp = (Button) findViewById(R.id.buttonTip);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransacation(TransactionCommand.SMART_SHOP_TIP);
			}
		});

		temp = (Button) findViewById(R.id.buttonBalancing);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransacation(TransactionCommand.SMART_SHOP_PARAMETRS_CALL);
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
			SmartShopBaseActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mAnswerTextView.setText(result);
					Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

//					if (!posCallbackee.getTicket().isEmpty()) {
//						Intent intent = new Intent(getApplicationContext(), TicketListActivity.class);
//						Bundle b = new Bundle();
//						b.putStringArrayList("ticket", (ArrayList<String>) posCallbackee.getTicket());
//						intent.putExtras(b);
//
//						startActivity(intent);
//					}

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
		Button button = (Button) findViewById(R.id.buttonInfo);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonTransactionHandshake);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonBalancing);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonSmartPay);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonRecharging);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonReturn);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonCardState);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonTip);
		button.setEnabled(enabled);
	}

	class DoTransactionTask extends AsyncTask<TransactionIn, Void, TransactionOut> {

		@Override
		protected TransactionOut doInBackground(TransactionIn... params) {
			try {
				return MonetBTAPI.doTransaction(SmartShopBaseActivity.this, params[0]);
			} catch (Exception e) {
				SmartShopBaseActivity.this.runOnUiThread(new Runnable() {
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
			SmartShopBaseActivity.this.runOnUiThread(new Runnable() {

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

}
