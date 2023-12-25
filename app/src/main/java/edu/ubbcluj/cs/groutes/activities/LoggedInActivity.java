package edu.ubbcluj.cs.groutes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import edu.ubbcluj.cs.groutes.R;
import edu.ubbcluj.cs.groutes.generalhelper.CacheManager;

public class LoggedInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        CacheManager.getInstance().getSavedData(this);
    }

    public void onBtnActivitySearch(View view){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void onBtnActivityHistory(View view){
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void onBtnActivityFav(View view){
        Intent intent = new Intent(this, FavTabActivity.class);
        startActivity(intent);
    }

    public void onBtnSettings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onGroupActivity(View view){
        Intent intent = new Intent(this, GroupsActivity.class);
        startActivity(intent);
    }
}
