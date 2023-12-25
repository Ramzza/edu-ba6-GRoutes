package edu.ubbcluj.cs.groutes.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.ubbcluj.cs.groutes.R;
import edu.ubbcluj.cs.groutes.activityhelper.MultiRouteAssist;
import edu.ubbcluj.cs.groutes.generalhelper.CacheManager;
import edu.ubbcluj.cs.groutes.generalhelper.RouteCalculator;
import edu.ubbcluj.cs.groutes.generalhelper.SettingsManager;

public class MultiRouteActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener {

    private static final String TAG = "grm.MultiRouteActivity";

    private GoogleMap mMap;

    com.google.maps.model.LatLng[] origins;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private LinkedList<Circle> circles;
    private boolean isNavOn;

    private int locationsNr;
    private int zoomCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MultiRouteAssist.getInstance().setActivity(this);

        isNavOn = false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        RouteCalculator.getInstance().orderLocations(this);
//        CacheManager.getInstance().addHistory();
        mMap.setOnPolylineClickListener(this);
        MultiRouteAssist.getInstance().drawRoute(mMap);

        //startNavigation();


    }

    public void startNavigation() {
        zoomCounter = SettingsManager.getInstance().getSettingEntity().getConfZoomInterval();
        isNavOn = true;
        ((Button) findViewById(R.id.btnStartNav)).setText(R.string.btnStopNav);
        circles = new LinkedList<>();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//called when loc updated
                Circle circle, circleOld;
                LatLng center, centerOld;
                float distance = 100;
                Location prevLoc = new Location("");
                center = new LatLng(location.getLatitude(), location.getLongitude());

                if (!circles.isEmpty()) {
                    centerOld = circles.getFirst().getCenter();
                    prevLoc.setLatitude(centerOld.latitude);
                    prevLoc.setLongitude(centerOld.longitude);

                    distance = prevLoc.distanceTo(location);
                }


                circle = mMap.addCircle(new CircleOptions()
                        .center(center)
                        .radius(2)
                        .fillColor(Color.BLUE));


                //  if(distance > 10) {
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 17));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(center)      // Sets the center of the map to Mountain View
                        .zoom(mMap.getCameraPosition().zoom)
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
                // }

                if (zoomCounter > SettingsManager.getInstance().getSettingEntity().getConfZoomInterval() && mMap.getCameraPosition().zoom < SettingsManager.getInstance().getSettingEntity().getConfZoom()) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(SettingsManager.getInstance().getSettingEntity().getConfZoom()), 2000, null);
                    zoomCounter = 0;
                }

                zoomCounter++;

                Iterator<Circle> iterator = circles.iterator();

                while (iterator.hasNext()) {
                    iterator.next().setFillColor(Color.RED);
                }

                circles.addFirst(circle);

                if (circles.size() > SettingsManager.getInstance().getSettingEntity().getConfNavHistory()) {
                    circles.getLast().remove();
                    circles.removeLast();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                stopNavigation();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            }

        }

        configureButton();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configureButton();
                } else {

                }
        }
    }

    @SuppressLint("MissingPermission")
    private void configureButton() {
        locationManager.requestLocationUpdates("gps", SettingsManager.getInstance().getSettingEntity().getConfPositionInterval()*1000, 0, locationListener);


    }

    private void stopNavigation() {
        isNavOn = false;
        ((Button) findViewById(R.id.btnStartNav)).setText(R.string.btnStartNav);
        locationManager.removeUpdates(locationListener);
        Iterator<Circle> iterator = circles.iterator();
        Circle circle;

        while (iterator.hasNext()) {
            circle = iterator.next();

            if (circle != circles.getFirst()) {
                circle.remove();
            }
        }

        circles = null;
    }

    public void onBtnNavigation(View view) {
        if (isNavOn) {

            stopNavigation();
        } else {
            startNavigation();
        }
    }

    public void onFavouriteButton(View view) {
        MultiRouteAssist.getInstance().addRouteToFavourites();
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        if (Color.RED == polyline.getColor()) {
            polyline.setColor(Color.BLUE);
            polyline.setZIndex(1);
        } else {
            polyline.setColor(Color.RED);
            polyline.setZIndex((float) 1.5);
        }
    }
}
