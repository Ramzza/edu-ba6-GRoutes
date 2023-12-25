package edu.ubbcluj.cs.groutes.activityhelper;

import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;

import edu.ubbcluj.cs.groutes.R;
import edu.ubbcluj.cs.groutes.model.PlaceEntity;
import edu.ubbcluj.cs.groutes.model.RouteEntity;
import edu.ubbcluj.cs.groutes.generalhelper.CacheManager;

public class SearchAssist {
    private static SearchAssist sInstance;

    public boolean isFirstStarted() {
        return firstStarted;
    }

    public void setFirstStarted(boolean firstStarted) {
        this.firstStarted = firstStarted;
    }

    private boolean firstStarted = true;

    private CacheManager cacheManager;
    private ArrayList<String> mSearches;

    public static SearchAssist getInstance() {
        if (sInstance == null) {
            sInstance = new SearchAssist();
        }
        return sInstance;
    }

    private SearchAssist(){
        cacheManager = CacheManager.getInstance();
    }

    public int createSearchList(View pButton, TravelMode travelMode){
        ViewGroup searchLayout;
        ViewGroup inputField;
        EditText editText;
        PlaceEntity place;
        com.google.android.gms.maps.model.LatLng test;
        int err = 0;

        String searchString;
        int numberOfInputs;

        searchLayout = (ViewGroup) pButton.getParent().getParent();
        searchLayout = (ViewGroup) searchLayout.getChildAt(1);
        searchLayout = (ViewGroup) searchLayout.getChildAt(0);

        numberOfInputs = searchLayout.getChildCount();

        mSearches = new ArrayList<>();

        for(int i=0; i<numberOfInputs; i++){
            inputField = (ViewGroup) searchLayout.getChildAt(i);
            editText = (EditText) inputField.getChildAt(2);

            searchString = editText.getText().toString();

            try {
                place = new PlaceEntity(searchString);
                test = place.returnLocationAsLatLng();
            }catch (Exception e){
                editText.setError("Invalid address");
                editText.addTextChangedListener(new TextWatch(editText));
                return -1;
            }

            mSearches.add(searchString);
        }

        RouteEntity route = new RouteEntity(mSearches);
        route.setTravelMode(travelMode);

        cacheManager.setSearchRoute(route);

        return err;
    }

    public static class TextWatch implements TextWatcher{
        private EditText editText;
        public TextWatch(EditText eText){
            editText = eText;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            editText.setError(null);
        }
    }

    public void setSingleLocation(View pButton){
        ViewGroup inputField;
        EditText editText;
        String searchString;

        inputField = (ViewGroup) pButton.getParent();
        editText = (EditText) inputField.getChildAt(2);

        searchString = editText.getText().toString();

        PlaceEntity place = new PlaceEntity(searchString);
        cacheManager.setLocation(place);
    }
}
