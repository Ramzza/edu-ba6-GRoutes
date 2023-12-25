package edu.ubbcluj.cs.groutes.activityhelper;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ubbcluj.cs.groutes.activities.MultiRouteActivity;
import edu.ubbcluj.cs.groutes.generalhelper.SettingsManager;
import edu.ubbcluj.cs.groutes.model.PlaceEntity;
import edu.ubbcluj.cs.groutes.model.RouteEntity;
import edu.ubbcluj.cs.groutes.generalhelper.CacheManager;
import edu.ubbcluj.cs.groutes.generalhelper.GeoManager;

public class MultiRouteAssist {
    private static final String TAG = "grm.MultiRouteAssist";
    private static MultiRouteAssist sInstance;

    private MultiRouteActivity mActivity;
    private GoogleMap mMap;
    private RouteEntity mRoute;
    private Map<Integer, List<LatLng>> mDecodedPath;
    private CacheManager cacheManager;

    private boolean firstRoute = true;

    private int mLocationsNr;

    public static MultiRouteAssist getInstance() {
        if (sInstance == null) {
            sInstance = new MultiRouteAssist();
        }
        return sInstance;
    }

    private MultiRouteAssist(){
        cacheManager = CacheManager.getInstance();
    }

    public void drawRoute(GoogleMap pMap) {
        this.mMap = pMap;
        this.mRoute = cacheManager.getSearchRoute();

        this.addMarkers();
        this.drawFullRoute();
    }

    private void addMarkers() {
        PlaceEntity place;

        for(int i=0; i<mRoute.retSize();i++){
            place = mRoute.getLocations().get(i);
            mMap.addMarker(new MarkerOptions().position(place.returnLocationAsLatLng()).title(place.getName()));
        }
    }

    private void drawFullRoute() {

        mRoute = cacheManager.getSearchRoute();

        mLocationsNr = mRoute.retSize();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mRoute.getLocations().get(mRoute.retSize()/2).returnLocationAsLatLng(), 14));

        //kor = utazougynok();

        firstRoute = true;

        drawGraph(mRoute.getOrder());
    }

    private void drawSingleRoute(int indexOrigin, int indexDestination) {
        DirectionsResult result = null;

        try {
            if(mRoute.retDecodedPath() == null){
                result = DirectionsApi.newRequest(GeoManager.getInstance().getGeoContext(mActivity))
                        .mode(mRoute.retTravelMode()).origin(mRoute.returnDirectionInput(indexOrigin))
                        .destination(mRoute.returnDirectionInput(indexDestination))
                        .await();
            }

            addPolyline(result, indexOrigin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setActivity(MultiRouteActivity mActivity) {
        this.mActivity = mActivity;
    }

    private void addPolyline(DirectionsResult results, int pIndexOrigin) {
        List<LatLng> decodedPath;

        if (results != null){
            decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
            mDecodedPath.put(pIndexOrigin, decodedPath);
        }else{
            decodedPath = mDecodedPath.get(pIndexOrigin);
        }

        if(firstRoute && mRoute.retTravelMode() == TravelMode.DRIVING){
            mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(Color.GREEN).clickable(true).zIndex(1));
            firstRoute = false;
        }else{
            mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(Color.BLUE).clickable(true).zIndex(1));
        }

    }

    private void drawGraph(ArrayList<Integer> order){
        mDecodedPath = mRoute.retDecodedPath();
        if(mDecodedPath == null){
            mDecodedPath = new HashMap();
        }

        for(int i=0; i<order.size()-1; i++){
            drawSingleRoute(order.get(i), order.get(i+1));
        }

        if(mRoute.retDecodedPath() == null) {
            mRoute.setDecodedPath(mDecodedPath);
        }
    }

    public void startNavigation(){

    }

    public void addRouteToFavourites(){
        cacheManager.addRouteToFavourites(mRoute);
    }
}
