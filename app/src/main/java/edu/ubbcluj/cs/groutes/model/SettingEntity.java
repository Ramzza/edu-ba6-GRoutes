package edu.ubbcluj.cs.groutes.model;

import com.google.firebase.firestore.GeoPoint;

import java.util.Map;

import edu.ubbcluj.cs.groutes.activities.SettingsActivity;

public class SettingEntity {
    private int confUseExactUntil = 0;
    private int confTravelMode = 0; // 0 - walking, 1 - driving
    private int confZoom = 19;
    private int confPositionInterval = 1;
    private int confZoomInterval = 5;
    private int confNavHistory = 10;
    private int confLang = 0; // 0 - english, 1 - hu, 2 - ro
    private int confDirectionTime = 2; // 2 - 12:00, 3 - 16:00, 1 - 8:00, 4 - 20:00, 0 - 00:00
    private int confDistanceTime = 0; // 0 - distance, 1 - time

    public SettingEntity(){
    }

    public SettingEntity(Map<String, Object> pAttributes) {
        confUseExactUntil = ((Long) pAttributes.get("confUseExactUntil")).intValue();
        confTravelMode = ((Long) pAttributes.get("confTravelMode")).intValue();
        confZoom = ((Long) pAttributes.get("confZoom")).intValue();
        confPositionInterval = ((Long) pAttributes.get("confPositionInterval")).intValue();
        confZoomInterval = ((Long) pAttributes.get("confZoomInterval")).intValue();
        confNavHistory = ((Long) pAttributes.get("confNavHistory")).intValue();
        confLang = ((Long) pAttributes.get("confLang")).intValue();
        confDirectionTime = ((Long) pAttributes.get("confDirectionTime")).intValue();
        confDistanceTime = ((Long) pAttributes.get("confDistanceTime")).intValue();
    }

    public int getConfUseExactUntil() {
        return confUseExactUntil;
    }

    public void setConfUseExactUntil(int confUseExactUntil) {
        this.confUseExactUntil = confUseExactUntil;
    }

    public int getConfTravelMode() {
        return confTravelMode;
    }

    public void setConfTravelMode(int confTravelMode) {
        this.confTravelMode = confTravelMode;
    }

    public int getConfZoom() {
        return confZoom;
    }

    public void setConfZoom(int confZoom) {
        this.confZoom = confZoom;
    }

    public int getConfPositionInterval() {
        return confPositionInterval;
    }

    public void setConfPositionInterval(int confPositionInterval) {
        this.confPositionInterval = confPositionInterval;
    }

    public int getConfZoomInterval() {
        return confZoomInterval;
    }

    public void setConfZoomInterval(int confZoomInterval) {
        this.confZoomInterval = confZoomInterval;
    }

    public int getConfNavHistory() {
        return confNavHistory;
    }

    public void setConfNavHistory(int confNavHistory) {
        this.confNavHistory = confNavHistory;
    }

    public int getConfLang() {
        return confLang;
    }

    public void setConfLang(int confLang) {
        this.confLang = confLang;
    }

    public int getConfDirectionTime() {
        return confDirectionTime;
    }

    public void setConfDirectionTime(int confDirectionTime) {
        this.confDirectionTime = confDirectionTime;
    }

    public int getConfDistanceTime() {
        return confDistanceTime;
    }

    public void setConfDistanceTime(int confDistanceTime) {
        this.confDistanceTime = confDistanceTime;
    }

}
