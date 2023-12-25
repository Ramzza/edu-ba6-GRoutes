package edu.ubbcluj.cs.groutes.generalhelper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

public class NavHelper {

    private static NavHelper sInstance;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private AppCompatActivity activity;

    public NavHelper getInstance() {
        if (sInstance == null) {
            sInstance = new NavHelper();
        }
        return sInstance;
    }


}
