package cz.monetplus.blueterm;

import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

// TODO: dodelat settigs http://developer.android.com/reference/android/preference/PreferenceActivity.html
public class SettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

//	final static String KEY_PREF_LOG = "pref_log";
//	final static String KEY_PREF_BLUE_ADDR = "blue_addr";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		// super.onBuildHeaders(target);
		// loadHeadersFromResource(R.xml.preferences, target);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		// Preference connectionPref = findPreference(key);
		// connectionPref.setSummary(sharedPreferences.getString(key, ""));

	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

}
