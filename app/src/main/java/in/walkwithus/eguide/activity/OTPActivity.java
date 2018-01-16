package in.walkwithus.eguide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.alimuzaffar.lib.pin.PinEntryEditText;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import in.walkwithus.eguide.R;
import in.walkwithus.eguide.helpers.AppConstants;
import in.walkwithus.eguide.helpers.Preferences;

/**
 * Updated by bahwan on 1/15/18.
 * Project name: Eguide
 */

public class OTPActivity extends AppCompatActivity {
    private static final String TAG = OTPActivity.class.getSimpleName();
    private boolean isValidOTP = false;
    Preferences preferences;
    PinEntryEditText pinEntry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        pinEntry = (PinEntryEditText) findViewById(R.id.txt_pin_entry);
        preferences = new Preferences();
        isValidOTP = preferences.getBoolean(AppConstants.PREF_KEY_OTP,false);
        if(isValidOTP){
            Intent i = new Intent(OTPActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }
        if (pinEntry != null) {
            pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str) {
                    /*if (str.toString().equals("1234")) {
                    } else {
                        pinEntry.setText(null);
                    }*/
                    DateTime currentTime = DateTime.now( DateTimeZone.UTC );
                    preferences.putString(AppConstants.PREF_OTP_ENTERED_TIME,""+currentTime);
                    Intent i = new Intent(OTPActivity.this,MainActivity.class);
                    startActivity(i);
                    pinEntry.setText(null);
                    finish();
                }
            });
        }
    }
}
