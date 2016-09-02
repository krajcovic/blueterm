package cz.monetplus.mashregister.ingenico;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.auth0.android.Auth0;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;

import cz.monetplus.mashregister.R;

public class WelcomeActivity extends AdActivity {

//	private Lock lock;
//
//	private LockCallback callback = new AuthenticationCallback() {
//		@Override
//		public void onAuthentication(Credentials credentials) {
//
//		}
//
//		@Override
//		public void onCanceled() {
//			// Login Cancelled response
//		}
//
//		@Override
//		public void onError(LockException error){
//			// Login Error response
//		}
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

//		Auth0 auth0 = new Auth0("eYVNVJf1GM0bMMBjco25FFhnJ89KdGo7", "krajcovic.eu.auth0.com");
//		this.lock = Lock.newBuilder(auth0, callback)
//				// Add parameters to the Lock Builder
//				.build();
//		this.lock.onCreate(this);

		Button buttonMbca = (Button) findViewById(R.id.buttonMbca);
		buttonMbca.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						MbcaBaseActivity.class));

			}
		});

		Button buttonMvta = (Button) findViewById(R.id.buttonMvta);
		buttonMvta.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						MvtaBaseActivity.class));

			}
		});

		Button buttonSmartShop = (Button) findViewById(R.id.buttonSmartShop);
		buttonSmartShop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						SmartShopBaseActivity.class));

			}
		});

		Button buttonServis = (Button) findViewById(R.id.buttonServis);
		buttonServis.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						ServisActivity.class));
			}
		});

//		Button buttonAuth = (Button) findViewById(R.id.buttonAuth);
//		buttonServis.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Activity activity = (Activity) v.getContext();
//				startActivity(lock.newIntent(activity));
//			}
//		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Your own Activity code
//		this.lock.onDestroy(this);
//		this.lock = null;
	}
}
