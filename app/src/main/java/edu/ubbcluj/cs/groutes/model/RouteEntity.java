package edu.ubbcluj.cs.groutes.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ubbcluj.cs.groutes.activities.LoginTabActivity;
import io.opencensus.tags.Tag;

public class RouteEntity {

    final static private String TAG = "grm.RouteEntity";

    private String mName;
    private ArrayList<PlaceEntity> mLocations;
    private ArrayList<Integer> mOrder;
    private com.google.maps.model.LatLng[] mOrigins;
    private Map<Integer, List<LatLng>> mDecodedPath;
    private String mId;

    public TravelMode retTravelMode() {
        return travelMode;
    }

    public void setTravelMode(TravelMode travelMode) {
        this.travelMode = travelMode;
        switch (travelMode) {
            case DRIVING:
                sTravelMode = "Driving";
                break;
            case WALKING:
                sTravelMode = "Walking";
        }
    }

    private void convertStringToTravelMode(String sTravelMode) {
        switch (sTravelMode) {
            case "Driving":
                travelMode = TravelMode.DRIVING;
                break;
            case "Walking":
                travelMode = TravelMode.WALKING;
        }
    }

    private TravelMode travelMode = TravelMode.WALKING;

    public String getsTravelMode() {
        return sTravelMode;
    }

    private String sTravelMode;


    public RouteEntity() {
        mName = new String("");
        mLocations = new ArrayList<>();
    }

    public RouteEntity(PlaceEntity place) {
        mName = new String("");
        mLocations = new ArrayList<>();
        mLocations.add(place);
    }

    public RouteEntity(ArrayList<String> pSearch) {
        init();
        createPlacesFromStrings(pSearch);
        createOrigins();
    }

    public RouteEntity(String pName, ArrayList<PlaceEntity> pPlaces) {
        init();
        mName = pName;
        mLocations = pPlaces;
        createOrigins();
    }

    public RouteEntity(Map<String, Object> pAttributes, String pId) {
        mId = pId;
        mName = (String) pAttributes.get("name");
        mLocations = placeArrayFromMap((ArrayList<Map<String, Object>>) pAttributes.get("locations"));
        mOrder = convertOrderToInt((ArrayList<Long>) pAttributes.get("order"));

        createOrigins();
    }

    private ArrayList<Integer> convertOrderToInt(ArrayList<Long> pOld) {
        ArrayList<Integer> result = new ArrayList<>();
        int aux;

        for (int i = 0; i < pOld.size(); i++) {
            aux = pOld.get(i).intValue();
            result.add(aux);
        }

        return result;
    }

    private ArrayList<PlaceEntity> placeArrayFromMap(ArrayList<Map<String, Object>> pPlaces) {
        ArrayList<PlaceEntity> result = new ArrayList<>();

        for (int i = 0; i < pPlaces.size(); i++) {
            result.add(new PlaceEntity(pPlaces.get(i)));
        }

        return result;
    }

    private void init() {
        mLocations = new ArrayList<>();
    }

    private void createPlacesFromStrings(ArrayList<String> pSearch) {
        for (int i = 0; i < pSearch.size(); i++) {
            mLocations.add(new PlaceEntity(pSearch.get(i)));
        }
    }

    private void createOrigins() {

        mOrigins = new com.google.maps.model.LatLng[mLocations.size()];
        PlaceEntity place;
try {
    for (int i = 0; i < mLocations.size(); i++) {
        place = mLocations.get(i);
        mOrigins[i] = new com.google.maps.model.LatLng(place.getLocation().getLatitude(), place.getLocation().getLongitude());
    }
}catch (Exception e){
    //
}

    }


    public PlaceEntity findPlaceByName(String pName) {
        PlaceEntity result;

        for (int i = 0; i < mLocations.size(); i++) {
            result = mLocations.get(i);
            if (result.getName().equals(pName)) {
                return result;
            }
        }

        return null;
    }

    public com.google.maps.model.LatLng returnDirectionInput(int pIndex) {
        com.google.maps.model.LatLng result = null;

        PlaceEntity place = mLocations.get(pIndex);
        result = new com.google.maps.model.LatLng(place.getLocation().getLatitude(), place.getLocation().getLongitude());

        return result;
    }

    public com.google.maps.model.LatLng[] returnDirectionMatrixInput() {
        return mOrigins;
    }


    public int retSize() {
        return mLocations.size();
    }

    public ArrayList<Integer> getOrder() {
        return mOrder;
    }

    public void setOrder(ArrayList<Integer> mOrder) {
        this.mOrder = mOrder;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public ArrayList<PlaceEntity> getLocations() {
        return mLocations;
    }

    public void setLocations(ArrayList<PlaceEntity> mLocations) {
        this.mLocations = mLocations;
    }

    public Map<Integer, List<LatLng>> retDecodedPath() {
        return mDecodedPath;
    }

    public void setDecodedPath(Map<Integer, List<LatLng>> mDecodedPath) {
        this.mDecodedPath = mDecodedPath;
    }

    public String retId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }
}
