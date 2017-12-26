package in.walkwithus.eguide.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import in.walkwithus.eguide.R;
import in.walkwithus.eguide.broadcast.ProximityIntentReceiver;
import in.walkwithus.eguide.events.ChangeActionEvent;
import in.walkwithus.eguide.events.ContentIdentified;
import in.walkwithus.eguide.events.ContentInterrupted;
import in.walkwithus.eguide.events.ShowToastEvent;
import in.walkwithus.eguide.helpers.AppConstants;
import in.walkwithus.eguide.helpers.AppHelper;
import in.walkwithus.eguide.helpers.Logger;

/**
 * Updated by bahwan on 12/25/17.
 * Project name: Eguide
 */


public class GPSService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = GPSService.class.getSimpleName();
    private LocationManager locationManager;
    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in Milliseconds
    private static final long POINT_RADIUS = 1000; // in Meters
    private static final long PROX_ALERT_EXPIRATION = -1;
    private static final String PROXY_ALERT_INTENT ="service_location";

    @Override
    public void onCreate() {
        super.onCreate();
        /*try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String CHANNEL_ID = "my_channel_01";
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);

                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("")
                        .setContentText("").build();

                startForeground(1, notification);
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/
        buildGoogleApiClient();
        /*locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATE,
                MINIMUM_DISTANCECHANGE_FOR_UPDATE,
                new MyLocationListener()
	        );*/
        Logger.i(TAG, "onCreate");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i(TAG, "onStartCommand");

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        return START_STICKY;
    }


    @Override
    public void onConnected(Bundle bundle) {
        Logger.i(TAG, "onConnected" + bundle);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            EventBus.getDefault().post(new ShowToastEvent
                    ("Permission Required", true));
            return;
        }
        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (l != null) {
            Logger.i(TAG, "lat " + l.getLatitude());
            Logger.i(TAG, "lng " + l.getLongitude());

        }

        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.i(TAG, "onConnectionSuspended " + i);

    }

    @Override
    public void onLocationChanged(Location location) {
        Logger.i(TAG, "lat " + location.getLatitude());
        Logger.i(TAG, "lng " + location.getLongitude());
        /*EventBus.getDefault().post(new ShowToastEvent
                ("MY location is : " + location.getLatitude() + "," + location.getLongitude(), true));*/
        Location currentLocation=new Location("Current");
        LatLng mLocation = (new LatLng(location.getLatitude(), location.getLongitude()));
        currentLocation.setLatitude(mLocation.latitude);
        currentLocation.setLongitude(mLocation.longitude);
        identifyContent(currentLocation);
        /*Location existingLocation=new Location("Existing");
        Location currentLocation=new Location("Current");
        LatLng mLocation = (new LatLng(location.getLatitude(), location.getLongitude()));
        Preferences preferences = new Preferences();
        String sLatitude = preferences.getString(PREF_KEY_DEVICE_LAT);
        String sLongitude = preferences.getString(PREF_KEY_DEVICE_LON);
        if(sLatitude == null && sLongitude==null) {
            sLatitude = preferences.getString(PREF_KEY_DEVICE_LAT);
            sLongitude = preferences.getString(PREF_KEY_DEVICE_LON);
        }
        Preferences.saveString(PREF_KEY_DEVICE_LAT, "" + mLocation.latitude);
        Preferences.saveString(PREF_KEY_DEVICE_LON, "" + mLocation.longitude);
        existingLocation.setLatitude(Double.parseDouble(sLatitude));
        existingLocation.setLongitude(Double.parseDouble(sLongitude));
        currentLocation.setLatitude(mLocation.latitude);
        currentLocation.setLongitude(mLocation.longitude);
        double distance = AppHelper.calculateDistanceMeters(currentLocation,existingLocation, AppConstants.METRIC_METER);
        Logger.i(TAG+ " distance:",""+distance);
        if(distance>AppConstants.MINIMUM_CHECK_DISTANCE_METER)
            EventBus.getDefault().post(new GetLastKnownLocationEvent(mLocation));*/

    }

    private void identifyContent(Location currentLocation){
        Logger.d(TAG,"identifyContent");
        String[] guideDataArray = getResources().getStringArray(R.array.guide_data);
        for (String aGuideDataArray : guideDataArray) {
            String guideLat[]=aGuideDataArray.split(",");
            if(guideLat.length>0){
                Location guideLocation=new Location("Existing");
                guideLocation.setLatitude(Double.parseDouble(guideLat[0]));
                guideLocation.setLongitude(Double.parseDouble(guideLat[1]));
                Logger.d(TAG+" Received data: ",currentLocation.getLatitude()+","+currentLocation.getLongitude());
                Logger.d(TAG+" Stored data",guideLat[0]+","+guideLat[1]);
                double distance = AppHelper.calculateDistanceMeters(currentLocation,guideLocation, AppConstants.METRIC_METER);
                Logger.i(TAG+ " distance:"+" Moving data: ",""+distance);
                String sMessage = "Received data: "+currentLocation.getLatitude()+","+currentLocation.getLongitude()+"\n\n"+
                "Stored data: "+guideLat[0]+","+guideLat[1]+"\n\n"+"Distance: "+""+distance+" Meters";
                //EventBus.getDefault().post(new ShowToastEvent(sMessage,true));
                if(distance<= AppConstants.MINIMUM_CHECK_DISTANCE_METER) {
                    String sFileName=guideLat[0]+guideLat[1];
                    Logger.d(TAG,sFileName);
                    EventBus.getDefault().post(new ContentIdentified("http://www.samisite.com/sound/cropShadesofGrayMonkees.mp3"));
                    return;
                }else{
                    EventBus.getDefault().post(new ContentInterrupted());
                    EventBus.getDefault().post(new ChangeActionEvent(true,true));
                }
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.i(TAG, "onDestroy");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.i(TAG, "onConnectionFailed ");

    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void startLocationUpdate() {
        initLocationRequest();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

}

