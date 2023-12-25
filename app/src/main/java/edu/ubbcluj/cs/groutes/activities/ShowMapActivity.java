package edu.ubbcluj.cs.groutes.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.ubbcluj.cs.groutes.R;
import edu.ubbcluj.cs.groutes.model.PlaceEntity;
import edu.ubbcluj.cs.groutes.generalhelper.CacheManager;
import edu.ubbcluj.cs.groutes.generalhelper.MsgHelper;
import edu.ubbcluj.cs.groutes.model.RouteEntity;

public class ShowMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "grm.ShowMapActivity";
    private GoogleMap mMap;

    private PlaceEntity place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            PlaceEntity location = CacheManager.getInstance().getLocation();
            place = location;

            LatLng center = new LatLng(location.getLocation().getLatitude(), location.getLocation().getLongitude());

            mMap.addMarker(new MarkerOptions().position(center).title(location.getAddress()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 14));
        }catch (Exception err){
            //MsgHelper.exceptionCatchMsg(err, TAG, this);
            Toast.makeText(this, "Location not found", Toast.LENGTH_LONG).show();

        }
    }

    public void onBtnAddFavPlace(View pButton){
        if(place != null){

            CacheManager.getInstance().addPlaceToFavourites(place);
        }
    }
}
