package cz.monetplus.mashregister.ingenico;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Callable;
import android.util.Log;
import android.widget.Toast;
import cz.monetplus.blueterm.PosCallbacks;

public class PosCallbackee implements PosCallbacks {

	private static final String TAG = "PosCallbackee";

	/**
	 * 
	 */
	private Activity activity;

	/**
	 * 
	 */
	private Context context;

	/**
	 * 
	 */
	private List<String> ticket = new ArrayList<String>();

	private static Toast makeText = null;

	/**
	 * @param activity
	 * @param context
	 */
	public PosCallbackee(Activity activity, Context context) {
		this.activity = activity;
		this.context = context;
	}

	@Override
	public Boolean ticketLine(final String line) {
		getTicket().add(line);

		return Boolean.TRUE;
	}

	@Override
	public void ticketFinish() {
		// Ukonci listek
		getTicket().add("*** Ticket finish ***");

		Intent intent = new Intent(context, TicketListActivity.class);
		Bundle b = new Bundle();
		b.putStringArrayList("ticket", (ArrayList<String>) this.getTicket());
		intent.putExtras(b);
		activity.startActivity(intent);

		Log.i(TAG, "Call cut on printer");
	}

	@Override
	public void progress(final String line) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (makeText != null) {
					makeText.cancel();
					makeText = null;
				}
				makeText = Toast.makeText(context, line, Toast.LENGTH_SHORT);
				makeText.show();
			}
		});
		Log.i(TAG, line);
	}

	public void progressToast(final String line) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(context, line, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public Boolean isSignOk() {

		Boolean signValue = Boolean.FALSE;

		// FutureTask<Boolean> futureResult = new FutureTask<Boolean>(new
		// YesNoCalleableDialog(activity));
		// activity.runOnUiThread(futureResult);
		//
		// try {
		// signValue = futureResult.get();
		// } catch (ExecutionException wrappedException) {
		// Throwable cause = wrappedException.getCause();
		// Log.e(TAG, wrappedException.getMessage(), cause);
		// } catch (InterruptedException e) {
		// Log.e(TAG, e.getMessage());
		// }

		YesNoRunnableDialog yesNoRunnableDialog = new YesNoRunnableDialog(activity);
		activity.runOnUiThread(yesNoRunnableDialog);

		// I cannot use notify - wait, becose is some problem with handling
		// notification.
		// TODO: solve it later
		while (yesNoRunnableDialog.getIsSignOk() == null) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}

		return yesNoRunnableDialog.getIsSignOk();
	}

	@Override
	public Boolean isConnectivity() {
		// Zatim se nepouziva
		return Boolean.TRUE;
	}

	public List<String> getTicket() {
		return ticket;
	}

	public void setTicket(List<String> ticket) {
		this.ticket = ticket;
	}
}
