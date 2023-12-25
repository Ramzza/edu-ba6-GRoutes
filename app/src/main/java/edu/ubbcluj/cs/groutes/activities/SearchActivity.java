package edu.ubbcluj.cs.groutes.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.maps.model.TravelMode;

import java.util.ArrayList;

import edu.ubbcluj.cs.groutes.R;
import edu.ubbcluj.cs.groutes.activityhelper.SearchAssist;
import edu.ubbcluj.cs.groutes.generalhelper.SettingsManager;
import edu.ubbcluj.cs.groutes.model.PlaceEntity;
import edu.ubbcluj.cs.groutes.generalhelper.CacheManager;
import edu.ubbcluj.cs.groutes.generalhelper.GeoManager;
import edu.ubbcluj.cs.groutes.generalhelper.RouteCalculator;
import edu.ubbcluj.cs.groutes.model.RouteEntity;

public class SearchActivity extends AppCompatActivity {
    private LinearLayout parentLinearLayout;

    private SearchAssist mSearchAssist;
    public static final String INTENT_MESSAGE = "com.example.test.SearchAddrActivity.MESSAGE";
    private String vSearchString;
    private PlaceEntity location;
    private RouteEntity extRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearchAssist = SearchAssist.getInstance();

        GeoManager.getInstance().setGeocoder(new Geocoder(this));

        parentLinearLayout = findViewById(R.id.parent_linear_layout2);



       // if(mSearchAssist.isFirstStarted()){
            ToggleButton toggleButton = findViewById(R.id.btn_travel_mode);
            if(SettingsManager.getInstance().getSettingEntity().getConfTravelMode() == 0){
                toggleButton.setChecked(true);
            }else{
                toggleButton.setChecked(false);
            }
            mSearchAssist.setFirstStarted(false);
       // }


        initFromExt();


    }

    public void initFromExt(){
        RouteEntity route = CacheManager.getInstance().getExtRoute();

        if(route == null){
            onAddSearchField(null);
        }else {
            for(int i=route.retSize()-1; i>=0; i--){

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final ViewGroup rowView = (ViewGroup) inflater.inflate(R.layout.input_field, null);

                TextView textview = (TextView) rowView.getChildAt(2);
                textview.setText(route.getLocations().get(i).getAddress());
                // Add the new row before the add field button.
                parentLinearLayout.addView(rowView, 0);


            }

            CacheManager.getInstance().setExtRoute(null);
        }

    }

    public void onAddSearchField(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.input_field, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, 0);
    }

    public void onDelete(View v) {
        parentLinearLayout.removeView((View) v.getParent());
    }

    public void onBtnSearch(View view){
        Intent intent = new Intent(this, MultiRouteActivity.class);
        TravelMode travelMode;
        ToggleButton toggleButton = findViewById(R.id.btn_travel_mode);

        if(toggleButton.isChecked()){
            travelMode = TravelMode.WALKING;
        }else {
            travelMode = TravelMode.DRIVING;
        }

        if(mSearchAssist.createSearchList(view, travelMode) == -1){
            return;
        }
        RouteCalculator.getInstance().orderLocations(this);
        CacheManager.getInstance().addHistory();

        startActivity(intent);
    }

    public void onBtnTravel(View view){

    }

    public void onBtnShowMap(View view){
        Intent intent;

        mSearchAssist.setSingleLocation(view);

        intent = new Intent(this, ShowMapActivity.class);
        startActivity(intent);
    }

    public void onBtnSearchFav(final View view){
        final ArrayList<PlaceEntity> places = CacheManager.getInstance().getFavouritePlaces();
        String[] placeNames = new String[places.size()];

        for(int i=0; i<places.size();i++){
            placeNames[i] = places.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a locations");
        builder.setItems(placeNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                EditText editText =(EditText) ( (ViewGroup) view.getParent()).getChildAt(2);
                editText.setText(places.get(index).getAddress());
            }
        });
        builder.show();
    }
}
