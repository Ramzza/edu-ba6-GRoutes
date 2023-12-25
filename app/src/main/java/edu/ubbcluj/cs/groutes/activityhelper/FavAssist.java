package edu.ubbcluj.cs.groutes.activityhelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.ubbcluj.cs.groutes.activities.FavActivity;
import edu.ubbcluj.cs.groutes.R;
import edu.ubbcluj.cs.groutes.activities.FavTabActivity;
import edu.ubbcluj.cs.groutes.model.PlaceEntity;
import edu.ubbcluj.cs.groutes.model.RouteEntity;
import edu.ubbcluj.cs.groutes.generalhelper.CacheManager;

public class FavAssist {
    private static FavAssist sInstance;

    private int favSize;
    private FavTabActivity mActivity;
    private LinearLayout parentLinearLayout;
    private CacheManager cacheManager;
    private String m_Text;

    public void setTab(int tab) {
        this.tab = tab;
    }

    private int tab = 0;


    public void setFavSize(int historySize) {
        this.favSize = historySize;
    }

    public FavTabActivity getActivity() {
        return mActivity;
    }

    public void setActivity(FavTabActivity mActivity) {
        this.mActivity = mActivity;
        this.parentLinearLayout = mActivity.findViewById(R.id.history_root2);
    }

    public static FavAssist getInstance() {
        if (sInstance == null) {
            sInstance = new FavAssist();
        }

        return sInstance;
    }

    private FavAssist() {
        this.cacheManager = CacheManager.getInstance();
    }

    public void addHistoryItems(String[] items) {

        for (int i = 0; i < items.length; i++) {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.fav_fields, null);
            // Add the new row before the add field button.

            this.setHistoryText(rowView, items[i]);

            parentLinearLayout.addView(rowView, 0);
        }
    }

    private void setHistoryText(View pView, String pText) {
        ViewGroup layoutCont = (ViewGroup) pView;

        TextView text = (TextView) layoutCont.getChildAt(2);

        text.setText(pText);
    }

    public void getFavData(LinearLayout parentLinearLayout) {
        this.parentLinearLayout = parentLinearLayout;
        String[] favEntries;

        ArrayList<RouteEntity> favRoutes = cacheManager.getFavouriteRoutes();

        favEntries = new String[favRoutes.size()];

        for (int i = 0; i < favRoutes.size(); i++) {
            favEntries[i] = favRoutes.get(i).getName();
        }

        addHistoryItems(favEntries);
    }

    public void getFavPlaces(LinearLayout parentLinearLayout) {
        this.parentLinearLayout = parentLinearLayout;
        String[] favEntries;

        ArrayList<PlaceEntity> favPlaces = cacheManager.getFavouritePlaces();

        favEntries = new String[favPlaces.size()];

        for (int i = 0; i < favPlaces.size(); i++) {
            favEntries[i] = favPlaces.get(i).getName();
        }

        addHistoryItems(favEntries);
    }

    public void setSingleRoute(View pButton, boolean goExt) {
        ViewGroup inputField;
        TextView textView;
        String searchString;

        inputField = (ViewGroup) pButton.getParent();
        textView = (TextView) inputField.getChildAt(2);

        searchString = textView.getText().toString();

        RouteEntity route = cacheManager.findFavouriteByName(searchString);
        cacheManager.setSearchRoute(route);

        if (goExt) {
            cacheManager.setExtRoute(route);
        }
    }

    public void deleteRoute(View pButton) {
        ViewGroup inputField;
        TextView textView;
        String searchString;

        inputField = (ViewGroup) pButton.getParent();
        textView = (TextView) inputField.getChildAt(2);

        //parentLinearLayout.removeView(inputField);
        searchString = textView.getText().toString();
        ((ViewGroup) inputField.getParent()).removeView(inputField);

        if (tab == 0) {
            RouteEntity route = cacheManager.findFavouriteByName(searchString);
            cacheManager.deleteRoute("/favouriteRoutes", route);
        } else {
            PlaceEntity place = cacheManager.findFavPlaceByName(searchString);
            cacheManager.deletePlace("/favPlaces", place);
        }


    }

    public void renameFav(final View pButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Change name");

// Set up the input
        final EditText input = new EditText(mActivity);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                TextView fav = (TextView) ((ViewGroup) pButton.getParent()).getChildAt(2);
                String oldName = fav.getText().toString();


                if (tab == 0) {
                    //route
                    if(null != cacheManager.findFavouriteByName(m_Text)){
                        showMessage("Name already exists");
                        return;
                    }
                    cacheManager.updateFav(oldName, m_Text, 1);
                } else {
                    //place
                    if(null != cacheManager.findFavPlaceByName(m_Text)){
                        showMessage("Name already exists");
                        return;
                    }
                    cacheManager.updateFav(oldName, m_Text, 2);
                }

                fav.setText(m_Text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void setSingleLocation(View pButton, boolean goExt) {
        ViewGroup inputField;
        TextView editText;
        String searchString;

        inputField = (ViewGroup) pButton.getParent();
        editText = (TextView) inputField.getChildAt(2);

        searchString = editText.getText().toString();

        PlaceEntity place = cacheManager.findFavPlaceByName(searchString);
        cacheManager.setLocation(place);

        if (goExt) {
            cacheManager.setExtRoute(new RouteEntity(place));
        }
    }

    public void showMessage(String msg){
        Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
    }
}
