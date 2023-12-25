package edu.ubbcluj.cs.groutes.generalhelper;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.GeoApiContext;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.ubbcluj.cs.groutes.R;

public class GeoManager {
    private static GeoManager sInstance;

    private GeoApiContext mGeoApiContext;
    private Geocoder mGeocoder;

    public static GeoManager getInstance(){
        if(sInstance == null){
            sInstance = new GeoManager();
        }
        return sInstance;
    }

    public void setGeocoder(Geocoder pGeocoder){
        mGeocoder = pGeocoder;
    }

    public GeoPoint getPointFromAddress(String pAddress){
        GeoPoint result = null;
        List<Address> address;

        try {
            address = mGeocoder.getFromLocationName(pAddress, 1);
            result = new GeoPoint(address.get(0).getLatitude(), address.get(0).getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public GeoApiContext getGeoApiContext(){
        if (mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext();
            mGeoApiContext.setQueryRateLimit(3)
                    .setApiKey(Integer.toString(R.string.google_api_key))
                    .setConnectTimeout(1, TimeUnit.SECONDS)
                    .setReadTimeout(1, TimeUnit.SECONDS)
                    .setWriteTimeout(1, TimeUnit.SECONDS);
        }

        return mGeoApiContext;
    }
    public GeoApiContext getGeoContext(Context pContext) {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(pContext.getString(R.string.google_maps_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }
}
