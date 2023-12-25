package edu.ubbcluj.cs.groutes.activityhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import edu.ubbcluj.cs.groutes.activities.HistoryActivity;
import edu.ubbcluj.cs.groutes.R;
import edu.ubbcluj.cs.groutes.model.RouteEntity;
import edu.ubbcluj.cs.groutes.generalhelper.CacheManager;

public class HistoryAssist{
    private static HistoryAssist sInstance;

    private int historySize;
    private HistoryActivity mActivity;
    private LinearLayout parentLinearLayout;
    private CacheManager cacheManager;


    public void setHistorySize(int historySize) {
        this.historySize = historySize;
    }

    public HistoryActivity getActivity() {
        return mActivity;
    }

    public void setActivity(HistoryActivity mActivity) {
        this.mActivity = mActivity;
        this.parentLinearLayout = mActivity.findViewById(R.id.history_root2);
    }

    public static HistoryAssist getInstance(){
        if(sInstance == null){
            sInstance = new HistoryAssist();
        }

        return sInstance;
    }

    private HistoryAssist(){
        this.cacheManager = CacheManager.getInstance();
    }

    public void addHistoryItems(String[] items){
        String[] history;

        //history = mDataManager.getHistoryDocuments();

        for(int i=0; i<items.length; i++){
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.history_fields, null);
            // Add the new row before the add field button.

            this.setHistoryText(rowView, items[i]);

            parentLinearLayout.addView(rowView, 0);
        }
    }

    private void setHistoryText(View pView, String pText){
        ViewGroup layoutCont = (ViewGroup) pView;

        TextView text = (TextView) layoutCont.getChildAt(2);

        text.setText(pText);
    }

    public void getHistoryData(){
        String[] historyEntries;

        ArrayList<RouteEntity> history = cacheManager.getHistory();

        historyEntries = new String[history.size()];

        for(int i=0; i<history.size(); i++){
            historyEntries[i] = history.get(i).getName();
        }

        addHistoryItems(historyEntries);
    }

    public void setSingleRoute(View pButton, boolean goExt){
        ViewGroup inputField;
        TextView textView;
        String searchString;

        inputField = (ViewGroup) pButton.getParent();
        textView = (TextView) inputField.getChildAt(2);

        searchString = textView.getText().toString();

        RouteEntity route = cacheManager.findHistoryByName(searchString);
        cacheManager.setSearchRoute(route);

        if(goExt){
            cacheManager.setExtRoute(route);
        }
    }

    public void addRouteToFavourites(View pButton){
        ViewGroup inputField;
        TextView textView;
        String searchString;

        inputField = (ViewGroup) pButton.getParent();
        textView = (TextView) inputField.getChildAt(2);

        searchString = textView.getText().toString();

        RouteEntity route = cacheManager.findHistoryByName(searchString);
        cacheManager.addRouteToFavourites(route);
    }

    public void deleteRouteFromHistory(View pButton){
        ViewGroup inputField;
        TextView textView;
        String searchString;

        inputField = (ViewGroup) pButton.getParent();
        textView = (TextView) inputField.getChildAt(2);

        parentLinearLayout.removeView(inputField);
        searchString = textView.getText().toString();

        RouteEntity route = cacheManager.findHistoryByName(searchString);
        cacheManager.deleteRouteFromHistory(route);
    }
}
