package in.walkwithus.eguide.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.walkwithus.eguide.adapter.ContentImageDisplayAdapter;
import in.walkwithus.eguide.helpers.Logger;

/**
 * Updated by bahwan on 1/8/18.
 * Project name: Eguide
 */

public class HeadSetReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context,Intent intent) {
        String TAG="HeadSetReceiver";
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    Logger.d(TAG, "Headset unplugged");
                    break;
                case 1:
                    Logger.d(TAG, "Headset plugged");
                    break;
            }
        }
    }
}
