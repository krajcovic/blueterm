package cz.monetplus.mashregister.ingenico;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import cz.monetplus.mashregister.R;

/**
 * Created by krajcovic on 5/24/16.
 */
public class AlternateIdActivity extends Activity implements NumberPicker.OnValueChangeListener {
//    private TextView tv;
    static Dialog d;

    // Return Intent extra
    public static String EXTRA_ALTERNATE_ID = "extra_alternate_id";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternate_id);
//        tv = (TextView) findViewById(R.id.textView1);
        show();
//        Button b = (Button) findViewById(R.id.button11);// on click of button display the dialog
//        b.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                show();
//            }
//        });
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        Log.i("value is", "" + newVal);

    }

    public void show() {

        final Dialog d = new Dialog(AlternateIdActivity.this);
        d.setTitle(R.string.title_alternateId);
        d.setContentView(R.layout.dialog_alterante_id);
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(9); // max value 100
        np.setMinValue(0);   // min value 0
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tv.setText(String.valueOf(np.getValue())); //set the value to textview
                Intent intent = new Intent();
                intent.putExtra(EXTRA_ALTERNATE_ID, String.valueOf(np.getValue()));

                // Set result and finish this Activity
                setResult(Activity.RESULT_OK, intent);
                d.dismiss();
                finish();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED, new Intent());
                d.dismiss(); // dismiss the dialog
                finish();
            }
        });
        d.show();


    }
}
