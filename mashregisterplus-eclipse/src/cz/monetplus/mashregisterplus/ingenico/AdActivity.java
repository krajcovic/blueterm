package cz.monetplus.mashregisterplus.ingenico;

import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;

public class AdActivity extends Activity {

	private AdView adView;

	/* Your ad unit id. Replace with your actual ad unit id. */
	// private static final String AD_UNIT_ID =
	// "ca-app-pub-4197154738167514/1390370981";

	protected static final String PREFS_NAME = "MashRegisterPlus.properties";
	protected static final String BT_ADDRESS = "lastUsedBtAddress";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR); // Add this line
		super.onCreate(savedInstanceState);
	}

	public void adAddView() {
		getActionBar().show();

		// Create an ad.
		// adView = new AdView(this);
		// adView.setAdSize(AdSize.BANNER);
		// adView.setAdUnitId(AD_UNIT_ID);

		// Add the AdView to the view hierarchy. The view will have no size
		// until the ad is loaded.
		// LinearLayout layout = (LinearLayout)
		// findViewById(R.id.linearLayoutAds);
		// layout.addView(adView);

		// Create an ad request. Check logcat output for the hashed device ID to
		// get test ads on a physical device.
		// AdRequest adRequest = new AdRequest.Builder()
		// .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		// .addTestDevice("AAA780CD3E74B3969124CE8589CC2C28").build();

		// Start loading the ad in the background.
		// adView.loadAd(adRequest);

	}

	@Override
	protected void onDestroy() {
		if (adView != null) {
			adView.resume();
		}
		super.onDestroy();

	}

	@Override
	protected void onPause() {
		if (adView != null) {
			adView.resume();
		}

		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (adView != null) {
			adView.resume();
		}
	}
}
