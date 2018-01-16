package in.walkwithus.eguide.helpers;

import android.os.Environment;

import com.google.android.gms.maps.model.LatLng;

import in.walkwithus.eguide.BuildConfig;

/**
 * Updated by bahwan on 12/25/17.
 * Project name: Eguide
 */

public interface AppConstants {
    String TAG = AppConstants.class.getSimpleName();

    String FILE_EXTENSION = ".mp3";
    String PREF_KEY_OTP = "otp_entry";
    String PREF_OTP_ENTERED_TIME = "otp_entry_time";
    String PREF_KEY_VERSION_CODE = "version_code";
    String PREF_KEY_STOP_NAME="stop_button_name";
    String PREF_KEY_PAUSE_NAME="pause_button_name";
    String PREF_KEY_DEVICE_TOKEN = "device_token";
    String PREF_KEY_DEVICE_LAT = "device_lat";
    String PREF_KEY_DEVICE_LON = "device_lon";
    String PREF_LAST_PLAYED_FILE = "last_played_file_content";
    String URL_CONFIRM_GCM_RECEIPT = "confirm_gcm_receipt";
    // global topic to receive app wide push notifications
    String TOPIC_GLOBAL = "global";
    //String TARGET_PATH = Environment.getExternalStorageDirectory() +"/"+ BuildConfig.LOCAL_FOLDER;

    // broadcast receiver intent filters
    String REGISTRATION_COMPLETE = "registrationComplete";
    String PUSH_NOTIFICATION = "pushNotification";
    int setMinimumSessionDuration=10000;
    int setSessionTimeoutDuration=1800000;
    int animationDefaultDuration=2000;
    int animationSlowDuration=3000;
    int animationQuickDuration=1000;

    // id to handle the notification in the notification tray
    int NOTIFICATION_ID = 100;
    int NOTIFICATION_ID_BIG_IMAGE = 101;
    int CAMERA_RQ = 6969;
    int PERMISSION_RQ = 84;
    int METRIC_METER=0;
    int METRIC_KILOMETER=1;

    double MINIMUM_CHECK_DISTANCE_METER=30;
}
