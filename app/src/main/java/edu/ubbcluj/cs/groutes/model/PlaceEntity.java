package edu.ubbcluj.cs.groutes.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import edu.ubbcluj.cs.groutes.generalhelper.GeoManager;

public class PlaceEntity {

    private String mName;
    private GeoPoint mLocation;
    private String mAddress;

    public String retId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    private String mId;

    public PlaceEntity(){
        mName = new String("");
        mLocation = new GeoPoint(0, 0);
        mAddress = new String("");
    }
    public PlaceEntity(String pAddress){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String name = dateFormat.format(currentTime);

        mId = name;
        mName = pAddress;
        mAddress = pAddress;
        mLocation = createFromAddress(pAddress);
    }
    public PlaceEntity(String pName, GeoPoint pLocation){
        mName = pName;
        mLocation = pLocation;
    }
    public PlaceEntity(Map<String, Object> pAttributes) {
        mName = (String) pAttributes.get("name");
        mLocation = (GeoPoint) pAttributes.get("location");
        mAddress = (String) pAttributes.get("address");
    }

    public PlaceEntity(Map<String, Object> pAttributes, String pId) {
        mId = pId;
        mName = (String) pAttributes.get("name");
        mLocation = (GeoPoint) pAttributes.get("location");
        mAddress = (String) pAttributes.get("address");
    }

    private GeoPoint createFromAddress(String pAddress){
        return GeoManager.getInstance().getPointFromAddress(pAddress);
    }

    public LatLng returnLocationAsLatLng(){
        return new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
    }


    public String getName() {
        return mName;
    }
    public void setName(String mName) {
        this.mName = mName;
    }

    public String getAddress() {
        return mAddress;
    }
    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public GeoPoint getLocation() {
        return mLocation;
    }
    public void setLocation(GeoPoint mLocation) {
        this.mLocation = mLocation;
    }

}
