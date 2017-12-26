package in.walkwithus.eguide.helpers;

import com.google.android.gms.maps.model.LatLng;

/**
 * Updated by bahwan on 12/25/17.
 * Project name: Eguide
 */

class StoreLocation {
    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    LatLng latLng;
    String s;
    public StoreLocation() {
    }

    public StoreLocation(LatLng latLng, String s) {
    }
}
