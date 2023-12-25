package edu.ubbcluj.cs.groutes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import edu.ubbcluj.cs.groutes.R;
import edu.ubbcluj.cs.groutes.activityhelper.HistoryAssist;

public class HistoryActivity extends AppCompatActivity {
    private HistoryAssist mAssist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        this.mAssist = HistoryAssist.getInstance();
        mAssist.setActivity(this);
        //this.addHistoryFields();
        mAssist.getHistoryData();
    }

    private void addHistoryFields(){
        //this.mAssist.addHistoryItems();
    }

    public void onBtnShowMap(View view){
        Intent intent;

        mAssist.setSingleRoute(view, false);

        intent = new Intent(this, MultiRouteActivity.class);
        startActivity(intent);
    }

    public void onDelete(View view){
        mAssist.deleteRouteFromHistory(view);
    }

    public void onFavouriteButton(View view){
        mAssist.addRouteToFavourites(view);
    }

    public void onBtnSearch(View view){
        Intent intent;

        mAssist.setSingleRoute(view, true);

        intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
}
