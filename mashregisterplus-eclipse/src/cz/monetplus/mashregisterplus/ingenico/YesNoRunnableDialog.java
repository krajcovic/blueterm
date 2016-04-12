package cz.monetplus.mashregisterplus.ingenico;

import java.util.concurrent.Callable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification.Action;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

public class YesNoRunnableDialog implements Runnable {

	private static final String TAG = "YesNoCalleableDialog";

	private Boolean isSignOk = null;
	private Activity activity;

	public YesNoRunnableDialog(Activity activity) {
		super();
		this.activity = activity;
	}

	@Override
	public void run() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// Yes button clicked
					setIsSignOk((Boolean.TRUE));
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					setIsSignOk((Boolean.FALSE));
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		builder.setMessage("Souhlasí podpis?").setPositiveButton("Ano", dialogClickListener)
				.setNegativeButton("Ne", dialogClickListener).show();
	}

	public Boolean getIsSignOk() {
		return isSignOk;
	}

	public void setIsSignOk(Boolean isSignOk) {
		this.isSignOk = isSignOk;
	}

}
