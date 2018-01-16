package in.walkwithus.eguide.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.walkwithus.eguide.App;
import in.walkwithus.eguide.activity.EnableSettings;
import in.walkwithus.eguide.activity.MainActivity;

/**
 * Updated by bahwan on 1/15/18.
 * Project name: Eguide
 */

public class GpsSwitchStateReceiver extends BroadcastReceiver{
    public static GpsSwitchStateReceiver gpsSwitchStateReceiver;
    public GpsSwitchStateReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        if (arg1.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            if(!App.get().isGPSEnabled()){
                Intent callGPSSettingIntent = new Intent(context,EnableSettings.class);
                context.startActivity(callGPSSettingIntent);
            }
        }
    }
}
