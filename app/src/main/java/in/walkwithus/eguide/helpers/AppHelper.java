package in.walkwithus.eguide.helpers;

import android.location.Location;

import in.walkwithus.eguide.broadcast.ConnectivityReceiver;

/**
 * Updated by bahwan on 12/25/17.
 * Project name: Eguide
 */

public class AppHelper {
    private static String TAG = AppHelper.class.getSimpleName();

    public AppHelper() {
    }
    public static boolean checkConnection() {
        boolean internetConnection= ConnectivityReceiver.isConnected();
        Logger.d(TAG,"Internet Connection : "+internetConnection);
        return internetConnection;
    }
    public static double calculateDistanceMeters(Location locationCurrent, Location locationExisting, int metric){
        double distance=0;
        switch(metric){
            case 0: //meters
                distance=locationExisting.distanceTo(locationCurrent);
                break;
            case 1: //KiloMeters
                distance=locationExisting.distanceTo(locationCurrent)/1000;
                break;
        }
        return distance;
    }
}
