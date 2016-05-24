package cz.monetplus.mashregister.ingenico;

import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import cz.monetplus.mashregister.R;

public class AdActivity extends Activity {

    private AdView adView;

    //protected static final int ACTIVITY_INTENT_ID = 33333;

    // Intent request codes
    protected static final int INTENT_RC_CONNECT_DEVICE_INSECURE = 33334;

    protected static final int INTENT_RC_ALTERNATE_ID = 33335;

	/* Your ad unit id. Replace with your actual ad unit id. */
    // private static final String AD_UNIT_ID =
    // "ca-app-pub-4197154738167514/1390370981";

    protected static final String PREFS_NAME = "MashRegister.properties";
    protected static final String BT_ADDRESS = "lastUsedBtAddress";

    protected Character alternateId = null;

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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuHwAddress:
                startActivityForResult(new Intent(getApplicationContext(),
                                DeviceListActivity.class),
                        INTENT_RC_CONNECT_DEVICE_INSECURE);
                break;
            case R.id.alternateId:
                startActivityForResult(new Intent(getApplicationContext(),
                                AlternateIdActivity.class),
                        INTENT_RC_ALTERNATE_ID);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case INTENT_RC_ALTERNATE_ID:
                alternateId = null;
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        if (data.hasExtra(AlternateIdActivity.EXTRA_ALTERNATE_ID)) {
                            alternateId = data.getStringExtra(AlternateIdActivity.EXTRA_ALTERNATE_ID).charAt(0);
                        }
                    }
                }
                break;
        }
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
