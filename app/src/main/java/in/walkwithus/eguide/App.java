package in.walkwithus.eguide;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.Assert;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.LocalTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.walkwithus.eguide.broadcast.ConnectivityReceiver;
import in.walkwithus.eguide.events.ChangeActionEvent;
import in.walkwithus.eguide.events.ContentIdentified;
import in.walkwithus.eguide.events.ContentInterrupted;
import in.walkwithus.eguide.events.NoInternetEvent;
import in.walkwithus.eguide.events.PausePlayingEvent;
import in.walkwithus.eguide.events.PlayFileEvent;
import in.walkwithus.eguide.events.ShowNoInternetScreenEvent;
import in.walkwithus.eguide.events.ShowToastEvent;
import in.walkwithus.eguide.events.StopPlayingEvent;
import in.walkwithus.eguide.helpers.AppConstants;
import in.walkwithus.eguide.helpers.AppHelper;
import in.walkwithus.eguide.helpers.Logger;
import in.walkwithus.eguide.helpers.Preferences;
import in.walkwithus.eguide.service.GPSService;

/**
 * Updated by bahwan on 12/25/17.
 * Project name: Eguide
 */

public class App extends Application implements Application.ActivityLifecycleCallbacks {
    private static String TAG = App.class.getSimpleName();
    private static App singletonInstance;
    private static final int INVALID_VERSION_CODE = -1;
    private MediaPlayer mediaPlayer;
    @Override
    public void onCreate() {
        super.onCreate();
        singletonInstance = this;
        checkAppUpdate();
        registerActivityLifecycleCallbacks(this);
        EventBus.getDefault().register(this);
        startAllServices();
        //reAssignPlayer();
    }

    public static App get() {
        return singletonInstance;
    }

    public int getVersionCode() {
        return getPackageInfo().versionCode;
    }
    public PackageInfo getPackageInfo() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /*public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalTime.class, new LocalTimeConverter())
                .create();
    }*/

    private void checkAppUpdate() {
        Preferences prefs = new Preferences();
        int previousVersionCode = prefs.getInt(AppConstants.PREF_KEY_VERSION_CODE, INVALID_VERSION_CODE);
        int thisVersionCode = getVersionCode();
        if (previousVersionCode != thisVersionCode) {
            prefs.putInt(AppConstants.PREF_KEY_VERSION_CODE, thisVersionCode).apply();
            onUpdate(previousVersionCode, thisVersionCode);
        }
    }

    private void onUpdate(int previousVersionCode, int thisVersionCode) {
        if (previousVersionCode == INVALID_VERSION_CODE) {
            // this is the very first launch after a fresh installation
            Logger.e(TAG,"First time install");
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activities.add(activity);
        activityStates.put(activity, ActivityState.Created);
        Logger.d(TAG, "An activity was created: " + activity + ". Activities: " + activities);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        boolean appWasInBackground = appIsInBackground();
        activityStates.put(activity, ActivityState.Started);
        if (appWasInBackground) {
            //onAppEnteredForeground(activity);
            Logger.e(TAG, "appWasInBackground");
            if (!AppHelper.isMyServiceRunning(GPSService.class)) {
                Logger.d(TAG, "Start Service");
                startService(new Intent(this, GPSService.class));
            }
            if (!EventBus.getDefault().isRegistered(this)) {
                mediaPlayer = reAssignPlayer(mediaPlayer);
                EventBus.getDefault().register(this);
            }
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        activityStates.put(activity, ActivityState.Resumed);
        //reAssignPlayer();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        activityStates.put(activity, ActivityState.Paused);
        Logger.e(TAG,"onActivityPaused");
        //stopPlaying();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityStates.put(activity, ActivityState.Stopped);
        if (appIsInBackground()) {
            //onAppEnteredBackground(activity);
            Logger.e(TAG,"appIsInBackground");
            //stopPlaying();
            if(AppHelper.isMyServiceRunning(GPSService.class)){
                Logger.d(TAG,"Stop Service");
                stopService(new Intent(this,   GPSService.class));
            }
            EventBus.getDefault().unregister(this);
            mediaPlayer = stopPlaying(mediaPlayer);
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activities.remove(activity);
        activityStates.remove(activity);
        Logger.d(TAG, "An activity was destroyed: " + activity + ". Activities: " + activities);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        // Unused
    }

    private boolean appIsInBackground() {
        // App is in background if ALL activities are Stopped or Created
        for (ActivityState state : activityStates.values()) {
            if (!(state == ActivityState.Stopped || state == ActivityState.Created)) {
                return false;
            }
        }
        return true;
    }

    /*private void onAppEnteredForeground(Activity activityStarted) {
        EventBus.getDefault().post(new AppEnteredForegroundEvent());
    }

    private void onAppEnteredBackground(Activity activityStopped) {
        EventBus.getDefault().post(new AppEnteredBackgroundEvent());
    }*/

    private enum ActivityState {
        Created,
        Started,
        Resumed,
        Paused,
        Stopped,
    }
    private List<Activity> activities = new ArrayList<>();
    private Map<Activity, ActivityState> activityStates = new HashMap<>();

    @Subscribe
    public void showToast(ShowToastEvent showToastEvent){
        if(showToastEvent.isImportant) {
            //if (BuildConfig.DEBUG) {
            Toast.makeText(this, showToastEvent.displayMessage, Toast.LENGTH_LONG).show();
            //}
        }
    }
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
    @Subscribe
    public void playIdentifiedContent(ContentIdentified contentIdentified){
        String sFileName=contentIdentified.contentDataFile+AppConstants.FILE_EXTENSION;
        Logger.d(TAG,sFileName);
        mediaPlayer = stopPlaying(mediaPlayer);
        mediaPlayer = reAssignPlayer(mediaPlayer);
        if(AppHelper.checkConnection()) {
            EventBus.getDefault().post(new NoInternetEvent(false));
            EventBus.getDefault().post(new PlayFileEvent(contentIdentified.contentDataFile, sFileName, mediaPlayer));
        }else{
            //Show No internet Screen
            EventBus.getDefault().post(new NoInternetEvent(true));
        }
        //playContent(sFileName,mediaPlayer);
    }
    @Subscribe
    public void playingInterrupted(ContentInterrupted contentInterrupted){
        Logger.d(TAG,"ContentInterrupted");
        if(mediaPlayer!=null)
            mediaPlayer = stopPlaying(mediaPlayer);
    }
    @Subscribe
    public void isInternetConnected(NoInternetEvent noInternetEvent){
        Logger.d(TAG,"isInternetConnected");
        if(noInternetEvent.isInternetDown){
            //Show internet Down screen
            EventBus.getDefault().post(new ShowNoInternetScreenEvent());
        }
    }
    @Subscribe
    public void playFile(PlayFileEvent playFileEvent) throws IOException {
        String fileName = playFileEvent.sRawName;
        final MediaPlayer mediaPlayer = playFileEvent.mediaPlayer;
        //fileName = fileName.replace(".","-");
        AssetFileDescriptor descriptor = App.get().getAssets().openFd(fileName+".mp3");
        mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
        //mediaPlayer.setDataSource("http://www.samisite.com/sound/cropShadesofGrayMonkees.mp3");
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                EventBus.getDefault().post(new ChangeActionEvent(false,true));
                Logger.d(TAG,"Stopped");
            }
        });
        /*mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        //EventBus.getDefault().post(new ShowToastEvent("Media Player Prepared",true));
                        mediaPlayer.start();
                        EventBus.getDefault().post(new ChangeActionEvent(false,false));
                        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        if (v != null) {
                            v.vibrate(500);
                        }
                    }
                });
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                //EventBus.getDefault().post(new ShowToastEvent("Buffer, please wait.",true));
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                EventBus.getDefault().post(new ShowToastEvent("MediaPlayer Error",true));
                return false;
            }
        });
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                EventBus.getDefault().post(new ShowToastEvent("MediaPlayer Info Listener",true));
                return false;
            }
        });
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                EventBus.getDefault().post(new ShowToastEvent("MediaPlayer seek completed",true));
            }
        });*/Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (v != null) {
            v.vibrate(500);
        }
        mediaPlayer.prepare();
        //mediaPlayer.prepareAsync();
        //mediaPlayer.setVolume(1f, 1f);
        mediaPlayer.setLooping(false);
        Preferences.saveString(AppConstants.PREF_LAST_PLAYED_FILE,playFileEvent.sRawName);
        mediaPlayer.start();
        EventBus.getDefault().post(new ChangeActionEvent(false,false));


    }
    /*@Subscribe
    public void emptyStopFile(StopPlayingEvent stopPlayingEvent) throws IOException{
        EventBus.getDefault().post(new StopPlayingEvent(mediaPlayer,stopPlayingEvent.isStopped));
    }
    @Subscribe
    public void emptyPauseFile(PausePlayingEvent pausePlayingEvent) throws IOException{
        EventBus.getDefault().post(new StopPlayingEvent(mediaPlayer,pausePlayingEvent.isPaused));
    }
    @Subscribe
    public void stopFile(StopPlayingEvent stopPlayingEvent) throws IOException{
        final MediaPlayer mediaPlayer = stopPlayingEvent.mediaPlayer;
        boolean isPlayerStopped = stopPlayingEvent.isStopped;
        if(isPlayerStopped){
            mediaPlayer.start();
        }else{
            mediaPlayer.stop();
        }
    }
    @Subscribe
    public void pauseFile(PausePlayingEvent pausePlayingEvent) throws IOException{
        final MediaPlayer mediaPlayer = pausePlayingEvent.mediaPlayer;
        boolean isPlayerPaused = pausePlayingEvent.isPaused;
        if(isPlayerPaused){
            mediaPlayer.start();
        }else{
            mediaPlayer.pause();
        }
    }*/

    /*public void playContent(String fileName,final MediaPlayer mediaPlayer){
        try {
            AssetFileDescriptor descriptor = App.get().getAssets().openFd(fileName);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
            });
            mediaPlayer.prepare();
            //mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
            Preferences.saveString(AppConstants.PREF_LAST_PLAYED_FILE,fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    /*public static void logAnalytics(int userID,String activity, String action){

        FirebaseAnalytics firebaseAnalytics;
        firebaseAnalytics = FirebaseAnalytics.getInstance(App.get());
        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, 1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Main Activity");
        //Logs an app event.
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //Sets whether analytics collection is enabled for this app on this device.
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);

        //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds).
        firebaseAnalytics.setMinimumSessionDuration(AppConstants.setMinimumSessionDuration);

        //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).
        firebaseAnalytics.setSessionTimeoutDuration(AppConstants.setSessionTimeoutDuration);

        //Sets the user ID property.
        firebaseAnalytics.setUserId(String.valueOf(userID));

        //Sets a user property to a given value.
        firebaseAnalytics.setUserProperty(activity, action);
    }*/

    public void startAllServices(){
        //startService(new Intent(this, GPSService.class));
    }

    public void stopAllService(){

    }
    private MediaPlayer stopPlaying(MediaPlayer mediaPlayer){
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        return mediaPlayer;
    }
    private MediaPlayer reAssignPlayer(MediaPlayer mediaPlayer){
        if(mediaPlayer==null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        return mediaPlayer;
    }
    public static int getImageDrawable(String imageFileName){
        Assert.assertNotNull(imageFileName);

        return App.get().getResources().getIdentifier(imageFileName,
                "drawable", App.get().getPackageName());
    }
    public boolean isGPSEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    public void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Please enable it.")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
