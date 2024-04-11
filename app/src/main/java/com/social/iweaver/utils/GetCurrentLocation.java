package com.social.iweaver.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import java.io.IOException;
import java.util.List;

public class GetCurrentLocation {

    public static String getCurrentLocation(Context mContext) {
        String calculateLocation = null;
        String country_name = null;
        String city_name = null;
        LocationManager lm = (LocationManager) mContext.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Geocoder geocoder = new Geocoder(mContext);
        for (String provider : lm.getAllProviders()) {
            @SuppressLint("MissingPermission") @SuppressWarnings("ResourceType") Location location = lm.getLastKnownLocation(provider);
            if (location != null) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        country_name = addresses.get(0).getCountryName();
                        city_name = addresses.get(0).getLocality();
                        calculateLocation = city_name + " , " + country_name;
                        return calculateLocation;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}

