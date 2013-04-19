package cz.monetplus.blueterm;

import java.io.OutputStream;
import java.util.ArrayList;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarmerAdapter extends BaseAdapter {

	private ArrayList<String> mListItems;
	private LayoutInflater mLayoutInflater;

	OutputStream forServer;

	public WarmerAdapter(BluetoothChat context, ArrayList<String> arrayList) {
		mListItems = arrayList;

		// get the layout inflater
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// getCount() represents how many items are in the list
		return mListItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// check to see if the reused view is null or not, if is not null then
		// reuse it
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.list_item, null);
		}

		// get the string item from the position "position" from array list to
		// put it on the TextView
		String stringItem = mListItems.get(position);
		if (stringItem != null) {

			TextView itemName = (TextView) convertView
					.findViewById(R.id.list_item_text_view);

			if (itemName != null) {
				// set the item name on the TextView
				itemName.setText(stringItem);
			}
		}

		// this method must return the view corresponding to the data at the
		// specified position.
		return convertView;
	}
}
