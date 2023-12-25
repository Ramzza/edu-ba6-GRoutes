package edu.ubbcluj.cs.groutes.generalhelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import edu.ubbcluj.cs.groutes.model.PlaceEntity;
import edu.ubbcluj.cs.groutes.model.RouteEntity;
import edu.ubbcluj.cs.groutes.model.SettingEntity;

public class CacheManager {
    private static final String TAG = "grm.CacheManager";
    private static CacheManager sInstance;

    private ArrayList<RouteEntity> mHistory;
    private ArrayList<RouteEntity> mFavouriteRoutes;

    public ArrayList<PlaceEntity> getFavouritePlaces() {
        return favouritePlaces;
    }

    private ArrayList<PlaceEntity> favouritePlaces;

    private RouteEntity mSearch;
    private PlaceEntity mLocation;
    private DatabaseAssist databaseAssist;
    private AppCompatActivity loggedInAct;

    private boolean histLoaded = false;
    private boolean favRouteLoaded = false;
    private boolean favPlaceLoaded = false;
    private boolean settingsLoaded = false;


    private ProgressDialog progress;

    public RouteEntity getExtRoute() {
        return extRoute;
    }

    public void setExtRoute(RouteEntity extRoute) {
        this.extRoute = extRoute;
    }

    private RouteEntity extRoute;

    public static CacheManager getInstance() {
        if (sInstance == null) {
            sInstance = new CacheManager();
        }

        return sInstance;
    }

    private CacheManager() {
        mHistory = new ArrayList<>();
        mFavouriteRoutes = new ArrayList<>();
        databaseAssist = DatabaseAssist.getInstance();
    }


    public void getSavedData(AppCompatActivity context) {
        loggedInAct = context;

        if (histLoaded && favRouteLoaded && favPlaceLoaded && settingsLoaded) {
            return;
        }

        showLoadingSpinner(context);
        cacheHistory();
        cacheFavourites();
        cacheGroups();
        cacheSettings();
    }

    private void cacheHistory() {
        databaseAssist.getDocuments(this, "history");
    }

    private void cacheFavourites() {
        databaseAssist.getDocuments(this, "favouriteRoutes");
        databaseAssist.getDocuments(this, "favPlaces");
    }

    private void cacheGroups() {

    }

    private void cacheSettings() {
        databaseAssist.getDocuments(this, "settings");
    }

    public void addHistory() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        String name = dateFormat.format(currentTime);
        mSearch.setName(name);
        mSearch.setId(name);

        String path = "/users/" + databaseAssist.getUser().getUid() + "/history";
        String docu = mSearch.retId();
        databaseAssist.createDocument(path, docu, mSearch);

        mHistory.add(mSearch);
    }

    public void addRouteToFavourites(RouteEntity pRoute) {
        Iterator<RouteEntity> iterator = mFavouriteRoutes.iterator();

        while (iterator.hasNext()) {
            if (pRoute.retId().equals(iterator.next().retId())) {
                return;
            }
        }

        mFavouriteRoutes.add(pRoute);

        String path = "/users/" + databaseAssist.getUser().getUid() + "/favouriteRoutes";
        String docu = pRoute.retId();

        databaseAssist.createDocument(path, docu, pRoute);
    }

    public void updateSettings(SettingEntity settingEntity) {
        String path = "/users/" + databaseAssist.getUser().getUid() + "/settings";
        String docu = "set";

        databaseAssist.createDocument(path, docu, settingEntity);

    }

    public void addPlaceToFavourites(PlaceEntity pPlace) {
        Iterator<PlaceEntity> iterator = favouritePlaces.iterator();

        while (iterator.hasNext()) {
            if (pPlace.retId().equals(iterator.next().retId())) {
                return;
            }
        }

        favouritePlaces.add(pPlace);

        String path = "/users/" + databaseAssist.getUser().getUid() + "/favPlaces";
        String docu = pPlace.retId();

        databaseAssist.createDocument(path, docu, pPlace);
    }

    public void deleteRouteFromHistory(RouteEntity pRoute) {
        mHistory.remove(pRoute);
        databaseAssist.deleteDocument("/history", pRoute.retId());
    }

    public void deleteRoute(String pPath, RouteEntity pRoute) {
        switch (pPath) {
            case "/history":
                mHistory.remove(pRoute);
                break;
            case "/favouriteRoutes":
                mFavouriteRoutes.remove(pRoute);
                break;
        }

        databaseAssist.deleteDocument(pPath, pRoute.retId());
    }

    public void deletePlace(String pPath, PlaceEntity place) {
        favouritePlaces.remove(place);
        databaseAssist.deleteDocument(pPath, place.retId());
    }

    public int getHistorySize() {
        return mHistory.size();
    }

    public RouteEntity findHistoryByName(String pName) {
        RouteEntity result = null;

        for (int i = 0; i < mHistory.size(); i++) {
            result = mHistory.get(i);

            if (result.getName().equals(pName)) {
                break;
            }

            result = null;
        }

        return result;
    }

    public RouteEntity findFavouriteByName(String pName) {
        RouteEntity result = null;

        for (int i = 0; i < mFavouriteRoutes.size(); i++) {
            result = mFavouriteRoutes.get(i);

            if (result.getName().equals(pName)) {
                break;
            }

            result = null;
        }

        return result;
    }

    public PlaceEntity findFavPlaceByName(String pName) {
        PlaceEntity result = null;

        for (int i = 0; i < favouritePlaces.size(); i++) {
            result = favouritePlaces.get(i);

            if (result.getName().equals(pName)) {
                break;
            }

            result = null;
        }

        return result;
    }

    public void handleQueryResult(QuerySnapshot pResult, String pCaller, boolean pProcess) {
        ArrayList<RouteEntity> routes = new ArrayList<>();
        ArrayList<PlaceEntity> places = new ArrayList<>();

        for (QueryDocumentSnapshot document : pResult) {
            Log.d(TAG, document.getId() + " => " + document.getData() + pResult.getMetadata().toString());

            if (pCaller.equals("favPlaces")) {
                places.add(new PlaceEntity(document.getData(), document.getId()));
            } else if (pCaller.equals("settings")) {
                SettingsManager.getInstance().setEntity(document.getData());
            } else {
                routes.add(new RouteEntity(document.getData(), document.getId()));
            }
        }


        switch (pCaller) {
            case "history":
                mHistory = routes;
                histLoaded = true;
                break;
            case "favouriteRoutes":
                mFavouriteRoutes = routes;
                favRouteLoaded = true;
            case "favPlaces":
                favouritePlaces = places;
                favPlaceLoaded = true;
            case "settings":
                settingsLoaded = true;
        }

        if (histLoaded && favPlaceLoaded && favRouteLoaded && settingsLoaded) {
            dismissLoadingSpinner();
        }

    }

    public ArrayList<RouteEntity> getHistory() {
        return mHistory;
    }

    public void setHistory(ArrayList<RouteEntity> mHistory) {
        this.mHistory = mHistory;
    }

    public RouteEntity getSearchRoute() {
        return mSearch;
    }

    public void setSearchRoute(RouteEntity mSearch) {
        this.mSearch = mSearch;
    }

    public PlaceEntity getLocation() {
        return mLocation;
    }

    public void setLocation(PlaceEntity mLocation) {
        this.mLocation = mLocation;
    }

    public ArrayList<RouteEntity> getFavouriteRoutes() {
        return mFavouriteRoutes;
    }

    public void setFavouriteRoutes(ArrayList<RouteEntity> mFavourites) {
        this.mFavouriteRoutes = mFavourites;
    }

    public void updateFav(String oldName, String newName, int type) {
        if (type == 1) {
            //route
            RouteEntity route = findFavouriteByName(oldName);
            route.setName(newName);

            String path = "/users/" + databaseAssist.getUser().getUid() + "/favouriteRoutes";
            String docu = route.retId();

            databaseAssist.createDocument(path, docu, route);
        } else {
            //place
            PlaceEntity place = findFavPlaceByName(oldName);
            place.setName(newName);

            String path = "/users/" + databaseAssist.getUser().getUid() + "/favPlaces";
            String docu = place.retId();

            databaseAssist.createDocument(path, docu, place);
        }
    }

    private void showLoadingSpinner(Activity pContext) {
        //      pContext.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        progress = new ProgressDialog(pContext);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    private void dismissLoadingSpinner() {
        progress.dismiss();
    }
}
