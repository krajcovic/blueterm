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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cz.monetplus.blueterm.MonetBTAPI;
import cz.monetplus.blueterm.TransactionCommand;
import cz.monetplus.blueterm.TransactionIn;
import cz.monetplus.blueterm.TransactionOut;
import cz.monetplus.blueterm.vprotocol.RechargingType;
import cz.monetplus.mashregister.R;
import cz.monetplus.mashregister.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MvtaBaseActivity extends AdActivity {

	private static final String TAG = "MvtaBaseActivity";

	// private final ReentrantLock lock = new ReentrantLock();

	private EditText mAmountIdEditText;
	private Spinner mCurrencySpinner;
	private Spinner mRechargeTypeSpinner;
	private EditText mInvoiceIdEditText;
	private EditText mTranIdEditText;

	private TextView mAnswerTextView;

	private String currentCurrency;
	private RechargingType rechargingType;
	private TextView blueHwAddress;

	DoTransactionTask transactionTask = null;

	private Menu propertiesMenu;

	private PosCallbackee posCallbackee;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_mvta_base);

		super.adAddView();

		mAmountIdEditText = (EditText) findViewById(R.id.editPrice);
		mCurrencySpinner = (Spinner) findViewById(R.id.spinnerCurrency);
		mRechargeTypeSpinner = (Spinner) findViewById(R.id.spinnerRechargeType);
		mInvoiceIdEditText = (EditText) findViewById(R.id.editTextInvoice);
		mTranIdEditText = (EditText) findViewById(R.id.editTicketId);

		mAnswerTextView = (TextView) findViewById(R.id.textAnswer);

		mAnswerTextView.setFocusableInTouchMode(true);
		mAnswerTextView.requestFocus();

		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.currency_array,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mCurrencySpinner.setAdapter(adapter);
		mCurrencySpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View arg1, int pos, long arg3) {
						currentCurrency = parent.getItemAtPosition(pos)
								.toString();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

		adapter = ArrayAdapter.createFromResource(this,
				R.array.recharge_type_array,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mRechargeTypeSpinner.setAdapter(adapter);
		mRechargeTypeSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View arg1, int pos, long arg3) {
						String string = parent.getItemAtPosition(pos)
								.toString();
						rechargingType = RechargingType.valueOf(string);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

		setupButtons();
		blueHwAddress = (TextView) findViewById(R.id.textViewHw);

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		blueHwAddress.setText(settings.getString(BT_ADDRESS,
				getString(R.string.default_select_device)));

		if (blueHwAddress.getText().equals(
				getString(R.string.default_select_device))) {
			setButtons(false);
		}
		
		this.posCallbackee = new PosCallbackee(MvtaBaseActivity.this,
				getApplicationContext());
	}
	
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
			transIn.setTranId(Long
					.valueOf(mTranIdEditText.getText().toString()));
			transIn.setRechargingType(rechargingType);

			if (transactionTask != null) {
				transactionTask.cancel(true);
				transactionTask = null;
			}

			transactionTask = new DoTransactionTask();
			transactionTask.execute(transIn);

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	private void setupButtons() {

		Button temp = (Button) findViewById(R.id.buttonHwSelect);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Launch the DeviceListActivity to see devices and do scan
				Intent serverIntent = new Intent(getApplicationContext(),
						DeviceListActivity.class);
				startActivityForResult(serverIntent,
                        INTENT_RC_CONNECT_DEVICE_INSECURE);

			}
		});

		temp = (Button) findViewById(R.id.buttonInfoMvta);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransaction(TransactionCommand.MVTA_INFO);
			}
		});

		temp = (Button) findViewById(R.id.buttonRechargingTransactionMvta);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransaction(TransactionCommand.MVTA_RECHARGE);
			}

		});

		temp = (Button) findViewById(R.id.buttonTransactionHandshakeMvta);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransaction(TransactionCommand.MVTA_HANDSHAKE);
			}
		});

		temp = (Button) findViewById(R.id.buttonLastTranMvta);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransaction(TransactionCommand.MVTA_LAST_TRAN);
			}
		});

		temp = (Button) findViewById(R.id.buttonParametersMvta);
		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTransaction(TransactionCommand.MVTA_PARAMETERS);
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

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//
//		switch (item.getItemId()) {
////		case R.id.bt_enabled:
////			item.setChecked(true);
////			propertiesMenu.findItem(R.id.tcp_enabled).setChecked(false);
////			break;
////		case R.id.tcp_enabled:
////			item.setChecked(true);
////			propertiesMenu.findItem(R.id.bt_enabled).setChecked(false);
////			break;
//            case R.id.menuHwAddress:
//                Intent serverIntent = new Intent(getApplicationContext(),
//                        DeviceListActivity.class);
//                startActivityForResult(serverIntent,
//                        REQUEST_CONNECT_DEVICE_INSECURE);
//                break;
//			case R.id.alternateId:
//				break;
//		}
//
//		return super.onOptionsItemSelected(item);
//	}

	private void ShowTransactionOut(TransactionOut out) {
		if (out != null) {
			final String result = out.toString();

			MvtaBaseActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mAnswerTextView.setText(result);

					Toast.makeText(getApplicationContext(), result,
							Toast.LENGTH_LONG).show();

//					if (!posCallbackee.getTicket().isEmpty()) {
//						Intent intent = new Intent(getApplicationContext(),
//								TicketListActivity.class);
//						Bundle b = new Bundle();
//						b.putStringArrayList("ticket",
//								(ArrayList<String>) posCallbackee.getTicket());
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
            case INTENT_RC_CONNECT_DEVICE_INSECURE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.hasExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS)) {
                        blueHwAddress
                                .setText(data
                                        .getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS));

                        if (blueHwAddress.getText().length() > 0) {
                            setButtons(true);

                        }
                    }
                }
                break;
        }
	}

	private void setButtons(boolean enabled) {
		Button button = (Button) findViewById(R.id.buttonInfoMvta);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonTransactionHandshakeMvta);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonRechargingTransactionMvta);
		button.setEnabled(enabled);
        button = (Button) findViewById(R.id.buttonLastTranMvta);
        button.setEnabled(enabled);
        button = (Button) findViewById(R.id.buttonParametersMvta);
        button.setEnabled(enabled);

	}

	class DoTransactionTask extends
			AsyncTask<TransactionIn, Void, TransactionOut> {

		@Override
		protected TransactionOut doInBackground(TransactionIn... params) {
			try {
				return MonetBTAPI.doTransaction(MvtaBaseActivity.this,
						params[0]);
			} catch (Exception e) {
				MvtaBaseActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),
								"Another thread work with blueterm.",
								Toast.LENGTH_LONG).show();
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
			MvtaBaseActivity.this.runOnUiThread(new Runnable() {

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
