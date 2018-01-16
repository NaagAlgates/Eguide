package in.walkwithus.eguide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import in.walkwithus.eguide.App;
import in.walkwithus.eguide.R;

/**
 * Updated by bahwan on 1/15/18.
 * Project name: Eguide
 */

public class EnableSettings extends AppCompatActivity {
    private static final String TAG = EnableSettings.class.getSimpleName();
    Button buttonSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_gps);
    }

    public void openSettings(View view) {
        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(callGPSSettingIntent);
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(App.get().isGPSEnabled()){
            Intent callGPSSettingIntent = new Intent(EnableSettings.this,MainActivity.class);
            startActivity(callGPSSettingIntent);
        }
    }
}
