package cz.monetplus.mashregisterplus.ingenico;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class TicketListActivity extends Activity {

	ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup the window
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.ticket_list);

		listView = (ListView) findViewById(R.id.ticket_list);
		Bundle b = getIntent().getExtras();
		List<String> values = b.getStringArrayList("ticket");

		((Button) findViewById(R.id.button_close))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);

		// Assign adapter to ListView
		listView.setAdapter(adapter);

		// ListView Item Click Listener
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ListView Clicked item index
				int itemPosition = position;

				// ListView Clicked item value
				String itemValue = (String) listView
						.getItemAtPosition(position);

				// Show Alert
				Toast.makeText(
						getApplicationContext(),
						"Position :" + itemPosition + "  ListItem : "
								+ itemValue, Toast.LENGTH_LONG).show();

			}

		});
	}
	
	

}
